package ballistix.common.blast.tier3;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import ballistix.Ballistix;
import ballistix.api.blast.IBlast;
import ballistix.api.blast.IMovingBlast;
import ballistix.common.blast.util.Blast;
import ballistix.common.blast.util.thread.ThreadSimpleBlast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.settings.BallistixConstants;
import ballistix.registers.BallistixSounds;
import modularforcefields.common.world.FortronFieldData;
import modularforcefields.common.world.FortronProtectionRegion;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import voltaic.prefab.utilities.WorldUtils;

public class BlastDarkmatter extends Blast implements IMovingBlast {

    public BlastDarkmatter(Level world, BlockPos position, @Nullable Entity owner, @Nullable Entity blastEntity) {
	super(world, position, owner, blastEntity);
    }

    @Override
    public void doPreExplode() {
	if (!world.isClientSide) {
	    thread = new ThreadSimpleBlast(world, position, (int) BallistixConstants.EXPLOSIVE_DARKMATTER_RADIUS,
		    Integer.MAX_VALUE, null, getBlastType().id());
	    thread.start();
	    if (!isRepeating) {
		world.playSound(null, position, BallistixSounds.SOUND_DARKMATTER.get(), SoundSource.BLOCKS, 1, 1);
	    }
	}
    }

    private List<FortronProtectionRegion> protectionRegions = Collections.emptyList();

    private final Map<Long, ArrayDeque<BlockPos>> blockedFortronBlocks = new HashMap<>();
    private final ArrayDeque<BlockPos> retryFortronBlocks = new ArrayDeque<>();
    public ThreadSimpleBlast thread;
    private int callAtStart = -1;
    private int pertick = -1;
    public boolean canceled = false;

    private Iterator<BlockPos> cachedIterator;

    @Override
    public boolean doExplode(int callCount) {
	if (world.isClientSide) {
	    return false;
	}

	hasStarted = true;
	if (thread == null || canceled) {
	    return true;
	}

	Explosion ex = new Explosion(world, blastEntity,
		DamageSource.explosion(owner instanceof LivingEntity ent ? ent : null), null, position.getX(),
		position.getY(), position.getZ(), (float) BallistixConstants.EXPLOSIVE_DARKMATTER_RADIUS, false,
		Explosion.BlockInteraction.DESTROY);

	if (thread.isComplete) {
	    if (callAtStart == -1) {
		callAtStart = callCount;
	    }

	    if (pertick == -1) {
		pertick = (int) (thread.results.size()
			/ (isRepeating ? BallistixConstants.EXPLOSIVE_DARKMATTER_REPEATDURATION
				: BallistixConstants.EXPLOSIVE_DARKMATTER_DURATION));

		cachedIterator = thread.results.iterator();
	    }

	    /*
	     * Dark matter needs current field data every tick. Once a field dies,
	     * previously protected blocks must become available for destruction.
	     */
	    if (Ballistix.MFFS_LOADED && world instanceof ServerLevel serverLevel) {
		int radius = (int) BallistixConstants.EXPLOSIVE_DARKMATTER_RADIUS;
		protectionRegions = FortronFieldData.get(serverLevel).getProtectionRegions(position, radius);
	    } else {
		protectionRegions = Collections.emptyList();
	    }

	    Set<Long> damagedThisTick = new HashSet<>();
	    Set<Long> activeProjectors = new HashSet<>();

	    for (FortronProtectionRegion region : protectionRegions) {
		activeProjectors.add(region.getProjectorId());
	    }

	    /*
	     * Any field that disappeared releases all blocks that it previously protected.
	     * Those blocks are retried below.
	     *
	     * Fields that still exist take 1% damage once this tick.
	     */
	    Iterator<Map.Entry<Long, ArrayDeque<BlockPos>>> blockedIterator = blockedFortronBlocks.entrySet()
		    .iterator();

	    while (blockedIterator.hasNext()) {
		Map.Entry<Long, ArrayDeque<BlockPos>> entry = blockedIterator.next();
		long projectorId = entry.getKey();

		if (activeProjectors.contains(projectorId)) {
		    if (!entry.getValue().isEmpty() && damagedThisTick.add(projectorId)
			    && world instanceof ServerLevel serverLevel) {
			Blast.damageFortronProjector(serverLevel, projectorId, 0.01);
		    }
		} else {
		    retryFortronBlocks.addAll(entry.getValue());
		    blockedIterator.remove();
		}
	    }

	    int finished = pertick;

	    /*
	     * First destroy blocks that were protected by a field which has since been
	     * destroyed.
	     */
	    while (!retryFortronBlocks.isEmpty()) {
		if (finished-- < 0) {
		    break;
		}

		BlockPos offset = retryFortronBlocks.removeFirst();
		BlockPos p = offset.offset(position);

		FortronProtectionRegion blockingRegion = null;

		for (FortronProtectionRegion region : protectionRegions) {
		    if (region.separates(position, p)) {
			blockingRegion = region;
			break;
		    }
		}

		/*
		 * The block may still be protected by another overlapping/nested field. Move it
		 * to that projector's blocked queue instead.
		 */
		if (blockingRegion != null) {
		    long projectorId = blockingRegion.getProjectorId();

		    blockedFortronBlocks.computeIfAbsent(projectorId, id -> new ArrayDeque<>()).addLast(offset);

		    if (damagedThisTick.add(projectorId) && world instanceof ServerLevel serverLevel) {
			Blast.damageFortronProjector(serverLevel, projectorId, 0.01);
		    }

		    continue;
		}

		BlockState state = world.getBlockState(p);
		Block block = state.getBlock();

		if (state.getDestroySpeed(world, p) >= 0) {
		    if (canBreakBlockState(world, state, p, owner)) {
			block.wasExploded(world, p, ex);
			world.setBlock(p, Blocks.AIR.defaultBlockState(),
				Block.UPDATE_NEIGHBORS | Block.UPDATE_CLIENTS | Block.UPDATE_SUPPRESS_DROPS);
		    }
		}
	    }

	    /*
	     * Process new dark matter blocks. Protected blocks are stored instead of
	     * discarded so they can be destroyed after the field dies.
	     */
	    while (cachedIterator.hasNext()) {
		if (finished-- < 0) {
		    break;
		}

		BlockPos offset = cachedIterator.next();
		BlockPos p = offset.offset(position);

		FortronProtectionRegion blockingRegion = null;

		for (FortronProtectionRegion region : protectionRegions) {
		    if (region.separates(position, p)) {
			blockingRegion = region;
			break;
		    }
		}

		if (blockingRegion != null) {
		    long projectorId = blockingRegion.getProjectorId();

		    blockedFortronBlocks.computeIfAbsent(projectorId, id -> new ArrayDeque<>()).addLast(offset);

		    /*
		     * Only one 1% hit per projector per tick, regardless of how many blocks
		     * encounter that field this tick.
		     */
		    if (damagedThisTick.add(projectorId) && world instanceof ServerLevel serverLevel) {
			Blast.damageFortronProjector(serverLevel, projectorId, 0.01);
		    }

		    continue;
		}

		BlockState state = world.getBlockState(p);
		Block block = state.getBlock();

		if (state.getDestroySpeed(world, p) >= 0) {
		    if (canBreakBlockState(world, state, p, owner)) {
			block.wasExploded(world, p, ex);
			world.setBlock(p, Blocks.AIR.defaultBlockState(),
				Block.UPDATE_NEIGHBORS | Block.UPDATE_CLIENTS | Block.UPDATE_SUPPRESS_DROPS);
		    }
		}
	    }

	    /*
	     * Do not finish merely because the original iterator is exhausted. Dark matter
	     * may still be chewing through a forcefield or catching up on blocks that the
	     * forcefield protected earlier.
	     */
	    if (!cachedIterator.hasNext()) {
		WorldUtils.clearChunkCache();
		return true;
	    }
	}

	float x = position.getX();
	float y = position.getY();
	float z = position.getZ();

	float size = (float) BallistixConstants.EXPLOSIVE_DARKMATTER_RADIUS;

	float doubleSize = size * 2.0F;

	int x0 = Mth.floor(x - (double) doubleSize - 1.0D);
	int x1 = Mth.floor(x + (double) doubleSize + 1.0D);
	int y0 = Mth.floor(y - (double) doubleSize - 1.0D);
	int y1 = Mth.floor(y + (double) doubleSize + 1.0D);
	int z0 = Mth.floor(z - (double) doubleSize - 1.0D);
	int z1 = Mth.floor(z + (double) doubleSize + 1.0D);

	List<Entity> entities = world.getEntities(null, new AABB(x0, y0, z0, x1, y1, z1));

	for (Entity entity : entities) {

	    if (!canHarmEntity(entity)) {
		continue;
	    }

	    double deltaX = entity.getX() - x;
	    double deltaY = (entity instanceof PrimedTnt ? entity.getY() : entity.getEyeY()) - y;
	    double deltaZ = entity.getZ() - z;
	    double deltaDistance = Mth.sqrt((float) (deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ));
	    if (deltaDistance == 0.0D) {
		continue;
	    }
	    deltaX = deltaX / deltaDistance;
	    deltaY = deltaY / deltaDistance;
	    deltaZ = deltaZ / deltaDistance;
	    double d11 = (-0.2 - (callCount - callAtStart) / 150.0) / deltaDistance;
	    entity.push(deltaX * d11, deltaY * d11, deltaZ * d11);
	    if (entity instanceof ServerPlayer serverplayerentity) {
		if (!serverplayerentity.isCreative()) {
		    serverplayerentity.connection.send(new ClientboundExplodePacket(x, y, z, size, new ArrayList<>(),
			    new Vec3(deltaX * d11, deltaY * d11, deltaZ * d11)));
		}
	    } else if (entity instanceof FallingBlockEntity) {
		entity.remove(RemovalReason.DISCARDED);
	    }
	}
	attackEntities((float) ((callCount - callAtStart) / 75.0), ex);
	if (world.random.nextFloat() < 0.5) {
	    world.explode(null, position.getX(), position.getY(), position.getZ(), 2, BlockInteraction.NONE);
	}
	return false;
    }

    @Override
    public boolean isInstantaneous() {
	return false;
    }

    @Override
    public IBlast getBlastType() {
	return SubtypeBlast.darkmatter;
    }

    @Override
    public int movementTicks() {
	return BallistixConstants.EXPLOSIVE_DARKMATTER_MOVEMENTTICKS;
    }

    @Override
    public int persistenceTicks() {
	return BallistixConstants.EXPLOSIVE_DARKMATTER_PERSISTANCE;
    }

}
