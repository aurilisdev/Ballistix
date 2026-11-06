package ballistix.common.blast.tier2;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nullable;

import ballistix.Ballistix;
import ballistix.api.blast.IHasCustomRender;
import ballistix.client.effect.BlastEffects.Style;
import ballistix.common.blast.util.Blast;
import ballistix.common.blast.util.BlastLasting;
import ballistix.common.blast.util.thread.raycast.ThreadDynamicRaycastBlast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.packet.type.client.particle.BlastParticleSpawnType;
import ballistix.common.packet.type.client.particle.PacketSpawnBlastParticle;
import ballistix.common.settings.BallistixConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import voltaic.prefab.utilities.object.Location;

public class BlastThermobaric extends BlastLasting implements IHasCustomRender {

    public BlastThermobaric(Level world, BlockPos position, @Nullable Entity owner, @Nullable Entity blastEntity) {
	super(world, position, owner, blastEntity);
    }

    @Override
    public void doPreExplode() {
	if (!world.isClientSide) {
	    ThreadDynamicRaycastBlast thread = this.thread = new ThreadDynamicRaycastBlast(world, position,
		    (int) BallistixConfig.INSTANCE.EXPLOSIVE_THERMOBARIC_SIZE.getAsDouble(),
		    (float) BallistixConfig.INSTANCE.EXPLOSIVE_THERMOBARIC_ENERGY.getAsDouble(), null);
	    if (BallistixConfig.INSTANCE.SHOULD_MULTITHREAD_RAYTRACING.isTrue()) {
		thread.start();
	    } else {
		thread.run();
	    }
	    Explosion ex = new Explosion(world, blastEntity, world.damageSources().explosion(blastEntity, owner), null,
		    position.getX(), position.getY(), position.getZ(),
		    (float) BallistixConfig.INSTANCE.EXPLOSIVE_THERMOBARIC_SIZE.getAsDouble() * 3, false,
		    BlockInteraction.DESTROY, ParticleTypes.EXPLOSION, ParticleTypes.EXPLOSION_EMITTER,
		    SoundEvents.GENERIC_EXPLODE);
	    attackEntities((float) BallistixConfig.INSTANCE.EXPLOSIVE_THERMOBARIC_SIZE.getAsDouble() * 2, ex);
	}
    }

    private int pertick = -1;
    private boolean appliedFortronDamage = false;

    private @Nullable ThreadDynamicRaycastBlast thread;

    @Override
    public boolean doExplode(int callCount) {
	super.doExplode(callCount);
	ThreadDynamicRaycastBlast thread = this.thread;
	if (thread == null) {
	    return !world.isClientSide;
	}
	if (callCount % 2 == 0) {
	    Explosion ex = new Explosion(world, blastEntity, world.damageSources().explosion(blastEntity, owner), null,
		    position.getX(), position.getY(), position.getZ(),
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
		Iterator<BlockPos> cachedIterator = thread.finishedBlocks.iterator();
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
				.forEach(pl -> PacketDistributor.sendToPlayer(pl,
					new PacketSpawnBlastParticle(p, BlastParticleSpawnType.EXPLOSIVE_BLOCK_BREAK)));
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
		    attackEntities((float) BallistixConfig.INSTANCE.EXPLOSIVE_THERMOBARIC_SIZE.getAsDouble() * 2, ex);
		    return true;
		}
	    }
	}
	return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void produceParticles() {
	playDetonationEffects(Style.THERMOBARIC, BallistixConfig.INSTANCE.EXPLOSIVE_THERMOBARIC_SIZE.getAsDouble());
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
	ThreadDynamicRaycastBlast thread = this.thread;
	return thread == null || thread.isComplete;
    }

    @Override
    public boolean shouldRender() {
	return pertick > 0;
    }

}
