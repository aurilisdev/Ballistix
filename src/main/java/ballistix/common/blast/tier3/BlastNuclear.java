package ballistix.common.blast.tier3;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nullable;

import ballistix.Ballistix;
import ballistix.api.blast.IBlast;
import ballistix.api.blast.IHasCustomRender;
import ballistix.client.effect.BlastEffects.Style;
import ballistix.common.blast.util.Blast;
import ballistix.common.blast.util.BlastLasting;
import ballistix.common.blast.util.thread.ThreadSimpleBlast;
import ballistix.common.blast.util.thread.raycast.ThreadDynamicRaycastBlast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.packet.type.client.particle.BlastParticleSpawnType;
import ballistix.common.packet.type.client.particle.PacketSpawnBlastParticle;
import ballistix.common.settings.BallistixConfig;
import ballistix.compatibility.nuclearscience.RadiationHandler;
import ballistix.registers.BallistixSounds;
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
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.network.PacketDistributor;
import voltaic.api.radiation.RadiationSystem;
import voltaic.api.radiation.SimpleRadiationSource;
import voltaic.prefab.utilities.object.Location;

public class BlastNuclear extends BlastLasting implements IHasCustomRender {

    public BlastNuclear(Level world, BlockPos position, @Nullable Entity owner, @Nullable Entity blastEntity) {
	super(world, position, owner, blastEntity);
    }

    @Override
    public void doPreExplode() {
	if (!world.isClientSide) {
	    threadRay = new ThreadDynamicRaycastBlast(world, position,
		    (int) BallistixConfig.INSTANCE.EXPLOSIVE_NUCLEAR_SIZE.getAsDouble(),
		    (float) BallistixConfig.INSTANCE.EXPLOSIVE_NUCLEAR_ENERGY.getAsDouble(), null);
	    threadSimple = new ThreadSimpleBlast(world, position,
		    (int) BallistixConfig.INSTANCE.EXPLOSIVE_NUCLEAR_RADIATION_RADIUS.getAsDouble(), Integer.MAX_VALUE,
		    null, getBlastType().id());
	    threadSimple.strictnessAtEdges = 1.7;
	    if (BallistixConfig.INSTANCE.SHOULD_MULTITHREAD_RAYTRACING.isTrue()) {
		threadRay.start();
	    } else {
		threadRay.run();
	    }
	    threadSimple.start();
	    Explosion ex = new Explosion(world, null, null, null, position.getX(), position.getY(), position.getZ(),
		    (float) BallistixConfig.INSTANCE.EXPLOSIVE_NUCLEAR_SIZE.getAsDouble() * 3, false,
		    BlockInteraction.DESTROY, ParticleTypes.EXPLOSION, ParticleTypes.EXPLOSION_EMITTER,
		    SoundEvents.GENERIC_EXPLODE);
	    attackEntities((float) BallistixConfig.INSTANCE.EXPLOSIVE_NUCLEAR_SIZE.getAsDouble() * 2, ex);
	}
    }

    private Iterator<BlockPos> cachedIteratorRay;
    private Iterator<BlockPos> cachedIterator;

    private ThreadDynamicRaycastBlast threadRay;
    private ThreadSimpleBlast threadSimple;
    private int pertick = -1;
    private int perticksimple = -1;
    private boolean secondDamage = false;
    private boolean thirdDamage = false;
    private boolean sounded = false;
    private boolean appliedFortronDamage = false;

    @Override
    public boolean shouldRender() {
	return pertick > 0;
    }

    @Override
    public boolean doExplode(int callCount) {
	super.doExplode(callCount);
	if (threadRay == null) {
	    return !world.isClientSide;
	}
	Explosion ex = new Explosion(world, blastEntity, world.damageSources().explosion(blastEntity, owner), null,
		position.getX(), position.getY(), position.getZ(),
		(float) BallistixConfig.INSTANCE.EXPLOSIVE_NUCLEAR_SIZE.getAsDouble() * 3, false,
		BlockInteraction.DESTROY, ParticleTypes.EXPLOSION, ParticleTypes.EXPLOSION_EMITTER,
		SoundEvents.GENERIC_EXPLODE);
	if (callCount % 2 == 0) {
	    synchronized (threadRay.finishedBlocks) {
		if (pertick == -1) {
		    hasStarted = true;
		    startCraterEffects(BallistixConfig.INSTANCE.EXPLOSIVE_NUCLEAR_SIZE.getAsDouble());
		    attackEntities((float) BallistixConfig.INSTANCE.EXPLOSIVE_NUCLEAR_SIZE.getAsDouble() * 2, ex);
		    world.playSound(null, position, BallistixSounds.SOUND_NUCLEAREXPLOSION.get(), SoundSource.BLOCKS,
			    25, 1);
		    pertick = (int) (2400.0 * 360.0
			    / BallistixConfig.INSTANCE.EXPLOSIVE_NUCLEAR_DURATION.getAsDouble());
		}
		int finished = pertick;
		cachedIteratorRay = threadRay.finishedBlocks.iterator();
		while (cachedIteratorRay.hasNext()) {
		    if (finished-- < 0) {
			break;
		    }
		    BlockPos p = cachedIteratorRay.next();
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
			if (!threadRay.results.contains(offset)) {
			    toPlace = Blocks.FIRE.defaultBlockState();
			}
		    }
		    block.wasExploded(world, p, ex);
		    if (world.setBlock(p, toPlace,
			    Block.UPDATE_NEIGHBORS | Block.UPDATE_CLIENTS | Block.UPDATE_SUPPRESS_DROPS)
			    && state.blocksMotion())
			recordCraterRemoval(p);
		    if (world instanceof ServerLevel serverlevel) {
			if (!sounded) {
			    serverlevel.getChunkSource().chunkMap.getPlayers(new ChunkPos(p), false).forEach(player -> {
				serverlevel.playSound(null, player.getX(), player.getY(), player.getZ(),
					BallistixSounds.SOUND_NUCLEAREXPLOSION.get(), // Change to your sound event
					SoundSource.PLAYERS, 25, 1.0F);
			    });
			    sounded = true;
			}
			if (world.random.nextFloat() < 1 / 20.0) {
			    serverlevel.getChunkSource().chunkMap.getPlayers(new ChunkPos(p), false)
				    .forEach(pl -> PacketDistributor.sendToPlayer(pl, new PacketSpawnBlastParticle(p,
					    BlastParticleSpawnType.EXPLOSIVE_BLOCK_BREAK)));
			}
		    }
		    cachedIteratorRay.remove();
		}
	    }
	}
	if (threadRay.isComplete && !secondDamage) {
	    attackEntities((float) BallistixConfig.INSTANCE.EXPLOSIVE_NUCLEAR_SIZE.getAsDouble() * 2, ex);
	    secondDamage = true;
	}
	if (threadRay.isComplete && !appliedFortronDamage) {
	    if (world instanceof ServerLevel serverLevel && Ballistix.MFFS_LOADED) {
		for (Entry<BlockPos, AtomicInteger> entry : threadRay.fortronRayHits.entrySet()) {
		    long raysHit = entry.getValue().get();
		    double damagePercentage = raysHit / (double) threadRay.totalRayCount;
		    Blast.damageFortronField(serverLevel, entry.getKey(), damagePercentage * 0.3333);
		    // Hitting the wall of an infinitely large forcefield would here then yield 16
		    // percent damage.
		}
	    }
	    appliedFortronDamage = true;
	}
	boolean done = false;
	if (threadSimple.isComplete && callCount % 2 == 0) {
	    if (!thirdDamage) {
		attackEntities((float) BallistixConfig.INSTANCE.EXPLOSIVE_NUCLEAR_SIZE.getAsDouble() * 2, ex);
		thirdDamage = true;
	    }

	    if (canSpawnParticle(position)) {
		int radiationDurationTicks = (int) (BallistixConfig.INSTANCE.EXPLOSIVE_NUCLEAR_RADIATION_DURATION_REAL_DAYS
			.get() * 24 * 60 * 60 * 20);
		RadiationSystem.addRadiationSource(world,
			new SimpleRadiationSource(150000.0, 2,
				(int) BallistixConfig.INSTANCE.EXPLOSIVE_NUCLEAR_RADIATION_RADIUS.getAsDouble(), true,
				radiationDurationTicks, position, false, false));
	    }
	    if (perticksimple == -1) {
		cachedIterator = threadSimple.results.iterator();
	    }
	    perticksimple = (int) (4 * Math.PI * 0.5
		    * Math.clamp(callCount * callCount, 0,
			    (int) (BallistixConfig.INSTANCE.EXPLOSIVE_NUCLEAR_RADIATION_RADIUS.getAsDouble()
				    * BallistixConfig.INSTANCE.EXPLOSIVE_NUCLEAR_RADIATION_RADIUS.getAsDouble())))
		    * 4;
	    int finished = perticksimple;
	    while (cachedIterator.hasNext()) {
		if (finished-- < 0) {
		    break;
		}

		BlockPos pos = cachedIterator.next().offset(position);

		if (!canHarmBlock(pos)) {
		    continue;
		}
		if (ModList.get().isLoaded(Ballistix.NUCLEAR_SCIENCE_ID) && pos.distSqr(position)
			/ (BallistixConfig.INSTANCE.EXPLOSIVE_NUCLEAR_RADIATION_RADIUS.getAsDouble()
				* BallistixConfig.INSTANCE.EXPLOSIVE_NUCLEAR_RADIATION_RADIUS.getAsDouble()) < 0.6
					+ 0.2 * world.random.nextDouble()) {
		    RadiationHandler.addNuclearExplosiveIrradidatedBlock(pos, world);
		    BlockState at = world.getBlockState(pos);
		    if (at.is(Tags.Blocks.GLASS_BLOCKS)) {
			world.setBlock(pos, Blocks.AIR.defaultBlockState(),
				Block.UPDATE_NEIGHBORS | Block.UPDATE_CLIENTS | Block.UPDATE_SUPPRESS_DROPS);
		    } else if (world.random.nextFloat() < 0.2) {
			Direction dir = Direction.getRandom(world.random);
			if (at.isFlammable(world, pos, dir)) {
			    world.setBlock(pos.relative(dir), Blocks.FIRE.defaultBlockState(),
				    Block.UPDATE_NEIGHBORS | Block.UPDATE_CLIENTS | Block.UPDATE_SUPPRESS_DROPS);
			}
		    }
		}
	    }
	    if (!cachedIterator.hasNext()) {
		done = ticksSinceBlastStart > 1500;
	    }
	}
	flushCraterEffects(callCount, done);
	return done;

    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void produceParticles() {
	playDetonationEffects(Style.NUCLEAR, BallistixConfig.INSTANCE.EXPLOSIVE_NUCLEAR_SIZE.getAsDouble());
    }

    @Override
    public boolean isDoneCalculating() {
	if (world.isClientSide) {
	    return shouldRenderCustomClient;
	}
	return threadRay == null || threadRay.isComplete;
    }

    @Override
    public boolean isInstantaneous() {
	return false;
    }

    @Override
    public IBlast getBlastType() {
	return SubtypeBlast.nuclear;
    }

}
