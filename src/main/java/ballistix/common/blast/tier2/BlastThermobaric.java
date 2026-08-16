package ballistix.common.blast.tier2;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nullable;

import ballistix.Ballistix;
import ballistix.api.blast.IHasCustomRender;
import ballistix.client.particle.ParticleOptionsBlastSmoke;
import ballistix.client.shake.CameraShakeEffect;
import ballistix.client.shake.CameraShakeManager;
import ballistix.common.blast.util.Blast;
import ballistix.common.blast.util.BlastLasting;
import ballistix.common.blast.util.thread.raycast.ThreadDynamicRaycastBlast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.packet.NetworkHandler;
import ballistix.common.packet.type.client.particle.BlastParticleSpawnType;
import ballistix.common.packet.type.client.particle.PacketSpawnBlastParticle;
import ballistix.common.settings.BallistixConstants;
import ballistix.prefab.utils.ParticleUtilities;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkDirection;
import voltaic.prefab.utilities.object.Location;

public class BlastThermobaric extends BlastLasting implements IHasCustomRender {

    public BlastThermobaric(Level world, BlockPos position, @Nullable Entity owner, @Nullable Entity blastEntity) {
	super(world, position, owner, blastEntity);
    }

    @Override
    public void doPreExplode() {
	if (!world.isClientSide) {
	    thread = new ThreadDynamicRaycastBlast(world, position, (int) BallistixConstants.EXPLOSIVE_THERMOBARIC_SIZE,
		    (float) BallistixConstants.EXPLOSIVE_THERMOBARIC_ENERGY, null);
	    if (BallistixConstants.SHOULD_MULTITHREAD_RAYTRACING) {
		thread.start();
	    } else {
		thread.run();
	    }
	    Explosion ex = new Explosion(world, null, null, null, position.getX(), position.getY(), position.getZ(),
		    (float) BallistixConstants.EXPLOSIVE_THERMOBARIC_SIZE, false, BlockInteraction.DESTROY);
	    attackEntities((float) BallistixConstants.EXPLOSIVE_THERMOBARIC_SIZE * 2, ex);
	}

    }

    private ThreadDynamicRaycastBlast thread;
    private int pertick = -1;
    private Iterator<BlockPos> cachedIterator;
    private boolean appliedFortronDamage = false;

    @Override
    public boolean doExplode(int callCount) {
	super.doExplode(callCount);
	if (thread == null) {
	    return !world.isClientSide;
	}
	if (callCount % 2 == 0) {
	    Explosion ex = new Explosion(world, blastEntity, world.damageSources().explosion(blastEntity, owner), null,
		    position.getX(), position.getY(), position.getZ(),
		    (float) BallistixConstants.EXPLOSIVE_THERMOBARIC_SIZE, false, BlockInteraction.DESTROY);
	    synchronized (thread.finishedBlocks) {
		if (pertick == -1) {
		    hasStarted = true;
		    attackEntities((float) BallistixConstants.EXPLOSIVE_THERMOBARIC_SIZE * 2, ex);
		    world.playSound(null, position, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 25, 1);
		    pertick = (int) (1200 * 45.0 / BallistixConstants.EXPLOSIVE_THERMOBARIC_DURATION);
		}
		cachedIterator = thread.finishedBlocks.iterator();
		int finished = pertick;
		while (cachedIterator.hasNext()) {
		    if (finished-- < 0) {
			break;
		    }
		    BlockPos p = cachedIterator.next();
		    BlockState state = world.getBlockState(p);
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
		    world.setBlock(p, toPlace,
			    Block.UPDATE_NEIGHBORS | Block.UPDATE_CLIENTS | Block.UPDATE_SUPPRESS_DROPS);
		    if (world.random.nextFloat() < 1 / 20.0 && world instanceof ServerLevel serverlevel) {
			serverlevel.getChunkSource().chunkMap.getPlayers(new ChunkPos(p), false)
				.forEach(pl -> NetworkHandler.CHANNEL.sendTo(
					new PacketSpawnBlastParticle(p, BlastParticleSpawnType.EXPLOSIVE_BLOCK_BREAK),
					pl.connection.connection, NetworkDirection.PLAY_TO_CLIENT));
		    }
		    cachedIterator.remove();
		}
		if (thread.isComplete && !appliedFortronDamage) {
		    if (world instanceof ServerLevel serverLevel && Ballistix.MFFS_LOADED) {
			for (Entry<BlockPos, AtomicInteger> entry : thread.fortronRayHits.entrySet()) {
			    long raysHit = entry.getValue().get();
			    double damagePercentage = raysHit / (double) thread.totalRayCount;
			    Blast.damageFortronField(serverLevel, entry.getKey(), damagePercentage * 0.1);
			    // Hitting the wall of an infinitely large forcefield would here then yield 5
			    // percent damage.
			}
		    }
		    appliedFortronDamage = true;
		}
		if (!cachedIterator.hasNext() && thread.isComplete) {
		    attackEntities((float) BallistixConstants.EXPLOSIVE_THERMOBARIC_SIZE * 2, ex);
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
	double endSize = BallistixConstants.EXPLOSIVE_THERMOBARIC_SIZE * 7.5;
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
		    BallistixConstants.EXPLOSIVE_THERMOBARIC_DURATION / 1.5, endSize, world.getGameTime(), pos);
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
