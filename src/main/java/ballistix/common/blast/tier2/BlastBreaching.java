package ballistix.common.blast.tier2;

import java.util.Iterator;

import javax.annotation.Nullable;

import ballistix.api.blast.IHasCustomRender;
import ballistix.client.particle.ParticleOptionsBlastSmoke;
import ballistix.client.shake.CameraShakeEffect;
import ballistix.client.shake.CameraShakeManager;
import ballistix.common.blast.util.BlastLasting;
import ballistix.common.blast.util.thread.raycast.ThreadDynamicRaycastBlast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.settings.BallistixConstants;
import ballistix.prefab.utils.ParticleUtilities;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import voltaic.prefab.utilities.object.Location;

public class BlastBreaching extends BlastLasting implements IHasCustomRender {

    private ThreadDynamicRaycastBlast thread;
    private Iterator<BlockPos> iterator;
    private int pertick = -1;

    public BlastBreaching(Level world, BlockPos position, @Nullable Entity owner, @Nullable Entity blastEntity) {
	super(world, position, owner, blastEntity);
    }

    @Override
    public void doPreExplode() {
	if (!world.isClientSide) {
	    thread = new ThreadDynamicRaycastBlast(world, position, (int) BallistixConstants.EXPLOSIVE_BREACHING_SIZE,
		    (float) BallistixConstants.EXPLOSIVE_BREACHING_ENERGY, null);
	    thread.start();
	    world.explode(blastEntity, DamageSource.explosion(owner instanceof LivingEntity ent ? ent : null), null,
		    position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5,
		    (float) BallistixConstants.EXPLOSIVE_BREACHING_SIZE, true, BlockInteraction.DESTROY);
	    world.playSound(null, position, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 25, 1);
	}
    }

    @Override
    public boolean doExplode(int callCount) {
	hasStarted = true;
	super.doExplode(callCount);
	if ((thread == null) || world.isClientSide || !thread.isComplete) {
	    return ticksSinceBlastStart > BallistixConstants.EXPLOSIVE_BREACHING_SIZE * 3;
	}

	if (pertick == -1) {
	    hasStarted = true;
	    pertick = (int) (thread.results.size() * 1.5 / BallistixConstants.EXPLOSIVE_BREACHING_DURATION + 1);
	    iterator = thread.results.iterator();
	}
	Explosion ex = new Explosion(world, blastEntity,
		DamageSource.explosion(owner instanceof LivingEntity ent ? ent : null), null, position.getX(),
		position.getY(), position.getZ(), (float) BallistixConstants.EXPLOSIVE_NUCLEAR_SIZE * 3, false,
		BlockInteraction.DESTROY);
	int finished = pertick;
	while (iterator.hasNext()) {
	    if (finished-- < 0) {
		break;
	    }
	    BlockPos p = new BlockPos(iterator.next()).offset(position);
	    BlockState state = world.getBlockState(p);

	    if (state.isAir()) {
		continue;
	    }

	    if (!canBreakBlockState(world, state, p, owner)) {
		continue;
	    }
	    Block block = state.getBlock();
	    BlockState toPlace = Blocks.AIR.defaultBlockState();
	    double dis = new Location(p.getX(), 0, p.getZ())
		    .distance(new Location(position.getX(), 0, position.getZ()));
	    if (world.random.nextFloat() < 1 / (3 * Math.sqrt(dis))) {
		BlockPos offset = p.relative(Direction.DOWN);
		if (!thread.results.contains(offset)) {
		    toPlace = Blocks.FIRE.defaultBlockState();
		}
	    }
	    block.wasExploded(world, p, ex);
	    world.setBlock(p, toPlace, Block.UPDATE_NEIGHBORS | Block.UPDATE_CLIENTS | Block.UPDATE_SUPPRESS_DROPS);

	}

	return ticksSinceBlastStart > BallistixConstants.EXPLOSIVE_BREACHING_SIZE * 3;
    }

    @Override
    public boolean isInstantaneous() {
	return false;
    }

    @Override
    public SubtypeBlast getBlastType() {
	return SubtypeBlast.breaching;
    }

    private boolean hasShaken;

    @Override
    @OnlyIn(Dist.CLIENT)
    public void produceParticles() {
	double x = position.getX() + 0.5;
	double y = position.getY() - 2;
	double z = position.getZ() + 0.5;
	if (ticksSinceBlastStart == 1) {
	    double initialSpeed = 0.4;
	    // Fireball
	    ParticleOptions particle = new ParticleOptionsBlastSmoke().setParameters(1.0f, 1.0f, 1.0f, 1f, -0.045f, 200,
		    true, true, 20, 0.95);
	    ParticleUtilities.spawnParticleSphere(particle, x, y, z, 100, 40, 90, initialSpeed, true);

	    // Centersmokes
	    initialSpeed = 0.4;
	    particle = new ParticleOptionsBlastSmoke().setParameters(1.0f, 1.0f, 1.0f, 1f, 0.033f, 200, true, 0.95);
	    ParticleUtilities.spawnParticleSphere(particle, x, y, z, 100, 0, 20, initialSpeed, true);

	    // Centersmokes
	    initialSpeed = 0.4;
	    particle = new ParticleOptionsBlastSmoke().setParameters(1.0f, 1.0f, 1.0f, 1f, -0.033f, 200, true, 0.95);
	    ParticleUtilities.spawnParticleSphere(particle, x, y, z, 100, 0, 20, initialSpeed, true);
	}
	// Shockwave
	double spawnSize = 3;
	double endSize = BallistixConstants.EXPLOSIVE_BREACHING_SIZE * 5;
	int diff = (int) (endSize - spawnSize);
	if (ticksSinceBlastStart > diff)
	    return;
	double size = ParticleUtilities.progressGroundShockwave(world, x, z, ticksSinceBlastStart / (double) diff,
		spawnSize, endSize, 0.1);
	if (hasShaken)
	    return;
	Vec3 pos = new Vec3(x, y, z);
	double realDistance = Minecraft.getInstance().player.position().distanceTo(pos);
	double dist = Mth.abs((float) (realDistance - size));
	if (dist < 3) {
	    hasShaken = true;
	    CameraShakeEffect effect = CameraShakeManager.createBlastSourcedEffect(20.0, endSize, world.getGameTime(),
		    pos);
	    CameraShakeManager.addShake(effect);
	}
    }

    @Override
    public boolean isDoneCalculating() {
	if (world.isClientSide) {
	    return shouldRenderCustomClient;
	}
	return true;
    }
}
