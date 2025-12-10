package ballistix.common.blast.tier2;

import java.util.Iterator;

import ballistix.api.blast.IHasCustomRender;
import ballistix.client.particle.ParticleOptionsBlastSmoke;
import ballistix.client.shake.CameraShakeEffect;
import ballistix.client.shake.CameraShakeManager;
import ballistix.common.blast.util.BlastLasting;
import ballistix.common.blast.util.thread.raycast.ThreadDynamicRaycastBlast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.packet.type.client.particle.BlastParticleSpawnType;
import ballistix.common.packet.type.client.particle.PacketSpawnBlastParticle;
import ballistix.common.settings.BallistixConfig;
import ballistix.compatibility.griefdefender.GriefDefenderHandler;
import ballistix.prefab.utils.ParticleUtilities;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import voltaic.prefab.utilities.object.Location;

public class BlastThermobaric extends BlastLasting implements IHasCustomRender {

    public BlastThermobaric(Level world, BlockPos position) {
	super(world, position);
    }

    @Override
    public void doPreExplode() {
	if (!world.isClientSide) {
	    thread = new ThreadDynamicRaycastBlast(world, position,
		    (int) BallistixConfig.INSTANCE.EXPLOSIVE_THERMOBARIC_SIZE.getAsDouble(),
		    (float) BallistixConfig.INSTANCE.EXPLOSIVE_THERMOBARIC_ENERGY.getAsDouble(), null);
	    if (BallistixConfig.INSTANCE.SHOULD_MULTITHREAD_RAYTRACING.isTrue()) {
		thread.start();
	    } else {
		thread.run();
	    }
	    Explosion ex = new Explosion(world, null, null, null, position.getX(), position.getY(), position.getZ(),
		    (float) BallistixConfig.INSTANCE.EXPLOSIVE_THERMOBARIC_SIZE.getAsDouble() * 3, false,
		    BlockInteraction.DESTROY, ParticleTypes.EXPLOSION, ParticleTypes.EXPLOSION_EMITTER,
		    SoundEvents.GENERIC_EXPLODE);
	    attackEntities((float) BallistixConfig.INSTANCE.EXPLOSIVE_THERMOBARIC_SIZE.getAsDouble() * 2, ex);
	}

    }

    private ThreadDynamicRaycastBlast thread;
    private int pertick = -1;
    private Iterator<BlockPos> cachedIterator;

    @Override
    public boolean doExplode(int callCount) {
	super.doExplode(callCount);
	if (thread == null) {
	    return !world.isClientSide;
	}
	if (callCount % 2 == 0) {
	    Explosion ex = new Explosion(world, null, null, null, position.getX(), position.getY(), position.getZ(),
		    (float) BallistixConfig.INSTANCE.EXPLOSIVE_THERMOBARIC_SIZE.getAsDouble() * 3, false,
		    BlockInteraction.DESTROY, ParticleTypes.EXPLOSION, ParticleTypes.EXPLOSION_EMITTER,
		    SoundEvents.GENERIC_EXPLODE);
	    synchronized (thread.finishedBlocks) {
		if (pertick == -1) {
		    hasStarted = true;
		    attackEntities((float) BallistixConfig.INSTANCE.EXPLOSIVE_THERMOBARIC_SIZE.getAsDouble() * 2, ex);
		    world.playSound(null, position, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 25, 1);
		    pertick = (int) (1200 * 45.0
			    / BallistixConfig.INSTANCE.EXPLOSIVE_THERMOBARIC_DURATION.getAsDouble());
		}
		cachedIterator = thread.finishedBlocks.iterator();
		int finished = pertick;
		while (cachedIterator.hasNext()) {
		    if (finished-- < 0) {
			break;
		    }
		    BlockPos p = cachedIterator.next();
		    Block block = world.getBlockState(p).getBlock();
		    switch (griefPreventionMethod) {
		    case NONE:

			BlockState state = Blocks.AIR.defaultBlockState();
			double dis = new Location(p.getX(), 0, p.getZ())
				.distance(new Location(position.getX(), 0, position.getZ()));
			if (world.random.nextFloat() < 1 / (3 * Math.sqrt(dis))) {
			    BlockPos offset = p.relative(Direction.DOWN);
			    if (!thread.results.contains(offset)) {
				state = Blocks.FIRE.defaultBlockState();
			    }
			}
			block.wasExploded(world, p, ex);
			world.setBlock(p, state, 3);
			break;
		    case GRIEF_DEFENDER:
			GriefDefenderHandler.destroyBlock(block, ex, p, world);
			break;
		    case SABER_FACTIONS:
			break;
		    }
		    if (world.random.nextFloat() < 1 / 20.0 && world instanceof ServerLevel serverlevel) {
			serverlevel.getChunkSource().chunkMap.getPlayers(new ChunkPos(p), false)
				.forEach(pl -> PacketDistributor.sendToPlayer(pl,
					new PacketSpawnBlastParticle(p, BlastParticleSpawnType.EXPLOSIVE_BLOCK_BREAK)));
		    }
		    cachedIterator.remove();
		}
		if (!cachedIterator.hasNext() && thread.isComplete) {
		    attackEntities((float) BallistixConfig.INSTANCE.EXPLOSIVE_THERMOBARIC_SIZE.getAsDouble() * 2, ex);
		    return true;
		}
	    }
	}
	return false;
    }

    private boolean hasShaken = false;

    @Override
    @OnlyIn(Dist.CLIENT)
    public void produceParticles() {
	double x = position.getX() + 0.5;
	double y = position.getY() + 0.5;
	double z = position.getZ() + 0.5;

	double initialSpeed = 0.8;

	if (ticksSinceBlastStart <= 5) {
	    // Fireball
	    ParticleOptions particle = new ParticleOptionsBlastSmoke().setParameters(1.0f, 1.0f, 1.0f, 4f, -0.045f, 750,
		    true, true, 40, 0.95);
	    ParticleUtilities.spawnParticleSphere(particle, x, y, z, 25, 50, 90, initialSpeed, true);

	    // Centersmokes
	    initialSpeed = 1; // Increase/decrease to taste
	    particle = new ParticleOptionsBlastSmoke().setParameters(0.8f, 0.8f, 0.8f, 4f, 0.033f, 750, true, 0.95);
	    ParticleUtilities.spawnParticleSphere(particle, x, y, z, 25, 0, 20, initialSpeed, true);

	    // Centersmokes
	    initialSpeed = 1; // Increase/decrease to taste
	    particle = new ParticleOptionsBlastSmoke().setParameters(0.8f, 0.8f, 0.8f, 4f, -0.033f, 750, true, 0.95);
	    ParticleUtilities.spawnParticleSphere(particle, x, y, z, 25, 0, 20, initialSpeed, true);
	}
	double spawnSize = 3;
	double endSize = BallistixConfig.INSTANCE.EXPLOSIVE_THERMOBARIC_SIZE.getAsDouble() * 7.5;
	int diff = (int) (endSize - spawnSize);
	if (ticksSinceBlastStart > diff)
	    return;
	double size = ParticleUtilities.progressGroundShockwave(world, x, z, ticksSinceBlastStart * 2 / (double) diff,
		spawnSize, endSize, 0.15);
	if (hasShaken)
	    return;
	Vec3 pos = new Vec3(x, y, z);
	double realDistance = Minecraft.getInstance().player.position().distanceTo(pos);
	double dist = Mth.abs((float) (realDistance - size));
	if (dist < 3) {
	    hasShaken = true;
	    CameraShakeEffect effect = CameraShakeManager.createBlastSourcedEffect(
		    BallistixConfig.INSTANCE.EXPLOSIVE_THERMOBARIC_DURATION.getAsDouble() / 1.5, endSize,
		    world.getGameTime(), pos);
	    CameraShakeManager.addShake(effect);
	}
    }

    @Override
    public boolean isInstantaneous() {
	return false;
    }

    @Override
    public SubtypeBlast getBlastType() {
	return SubtypeBlast.thermobaric;
    }
    // TODO: Finish block model

    @Override
    public boolean isDoneCalculating() {
	if (world.isClientSide) {
	    return shouldRenderCustomClient;
	}
	return thread == null || thread.isComplete;
    }

    @Override
    public boolean shouldRender() {
	return pertick > 0;
    }

}
