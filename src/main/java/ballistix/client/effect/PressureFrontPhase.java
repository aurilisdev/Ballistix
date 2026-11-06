package ballistix.client.effect;

import ballistix.client.effect.particle.ParticleKind;
import ballistix.client.shake.CameraShakeManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

/** The shock front sweeping outwards: it kicks up dust along the ground and shakes the camera when it reaches it. */
final class PressureFrontPhase extends BlastPhase {

    private static final int DUST_INTERVAL = 2;
    private static final int BLOCK_CHIP_MAX_AGE = 18;
    private static final int BLOCK_CHIP_EVERY = 6;
    private static final int MAX_DUST_SAMPLES = 66;

    private final double startRadius = core * 0.25;
    private final double travelRange = style.extent.frontRange() * scale;
    private boolean viewerShaken;

    PressureFrontPhase(BlastScene scene) {
	super(scene);
    }

    @Override
    void tick(int age) {
	if (age > endTick())
	    return;
	double progress = age / (double) endTick();
	double radius = startRadius + travelRange * (1 - Math.pow(1 - progress, 1.45));
	shakeViewerOnArrival(radius);
	if (age % DUST_INTERVAL == 0)
	    emitGroundDust(age, radius);
    }

    @Override
    int endTick() {
	return timeline.pressureFrontTicks();
    }

    private void shakeViewerOnArrival(double radius) {
	Entity viewer = Minecraft.getInstance().getCameraEntity();
	if (viewerShaken || viewer == null)
	    return;
	double distance = viewer.getEyePosition().distanceTo(origin);
	if (distance <= radius) {
	    viewerShaken = true;
	    CameraShakeManager.startBlastShake(level, core, distance / (startRadius + travelRange));
	}
    }

    private void emitGroundDust(int age, double radius) {
	int samples = count(Math.min(MAX_DUST_SAMPLES, (int) (14 + radius * 0.43)));
	for (int i = 0; i < samples; i++) {
	    double angle = (i + random.nextDouble()) * Mth.TWO_PI / samples;
	    double distance = radius - random.nextDouble() * Math.min(radius, core * 0.65);
	    double dirX = Math.cos(angle);
	    double dirZ = Math.sin(angle);
	    int x = Mth.floor(origin.x + dirX * distance);
	    int z = Mth.floor(origin.z + dirZ * distance);
	    if (!level.hasChunk(x >> 4, z >> 4))
		continue;
	    int surface = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
	    if (surface <= level.getMinBuildHeight() || Math.abs(surface - origin.y) > 12 + core * 2.3)
		continue;

	    float size = (0.8f + random.nextFloat() * 1.1f) * (0.65f + core * 0.115f);
	    double push = (0.08 + random.nextDouble() * 0.15) * (1 + core * 0.06);
	    Vec3 pos = new Vec3(x + 0.5, surface + size * 0.20, z + 0.5);
	    Vec3 velocity = new Vec3(dirX * push, 0.045 + random.nextDouble() * 0.18, dirZ * push);
	    int color = ARGB32.lerp(random.nextFloat() * 0.35f, palette.dust(), palette.smoke());
	    puff(ParticleKind.DUST, pos, velocity, size, lifetime(38 + random.nextInt(50)), color, palette.dust());
	    if (age <= BLOCK_CHIP_MAX_AGE && i % BLOCK_CHIP_EVERY == 0 && hasFineDetail())
		emitBlockChip(new BlockPos(x, surface - 1, z), pos, dirX * push, dirZ * push);
	}
    }

    private void emitBlockChip(BlockPos ground, Vec3 pos, double pushX, double pushZ) {
	BlockState state = level.getBlockState(ground);
	if (state.isAir() || !state.getFluidState().isEmpty())
	    return;
	Particle chip = Minecraft.getInstance().particleEngine.createParticle(
		new BlockParticleOption(ParticleTypes.BLOCK, state).setPos(ground), pos.x, pos.y, pos.z, pushX * 1.8,
		0.65 + random.nextDouble() * 0.9, pushZ * 1.8);
	if (chip != null)
	    chip.scale(1.8f + core * 0.075f);
    }
}
