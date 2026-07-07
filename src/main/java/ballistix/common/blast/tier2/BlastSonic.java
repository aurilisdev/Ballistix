package ballistix.common.blast.tier2;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import ballistix.api.blast.IBlast;
import ballistix.common.blast.util.BlastLasting;
import ballistix.common.blast.util.thread.ThreadSimpleBlast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.entity.EntityBallistixFallingBlock;
import ballistix.common.settings.BallistixConstants;
import ballistix.registers.BallistixSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class BlastSonic extends BlastLasting {

    private ThreadSimpleBlast thread;
    private Iterator<BlockPos> iterator;
    private int pertick = -1;

    public BlastSonic(Level world, BlockPos position, @Nullable Entity owner, @Nullable Entity blastEntity) {
	super(world, position, owner, blastEntity);
    }

    @Override
    public IBlast getBlastType() {
	return SubtypeBlast.sonic;
    }

    @Override
    public void doPreExplode() {
	if (!world.isClientSide) {
	    thread = new ThreadSimpleBlast(world, position, (int) BallistixConstants.EXPLOSIVE_SONIC_RADIUS,
		    Integer.MAX_VALUE, null, getBlastType().id(), true);
	    thread.start();
	    world.playSound(null, position, BallistixSounds.SOUND_SONICEXPLOSION.get(), SoundSource.BLOCKS, 25, 1);
	}
    }

    @Override
    public boolean doExplode(int callCount) {
	hasStarted = true;
	super.doExplode(callCount);
	if (thread == null) {
	    return !world.isClientSide;
	}
	if (world.isClientSide || !thread.isComplete) {
	    return false;
	}
	if (pertick == -1) {
	    hasStarted = true;
	    pertick = (int) (thread.results.size() / BallistixConstants.EXPLOSIVE_SONIC_DURATION + 1);
	    iterator = thread.results.iterator();
	}
	int finished = pertick;
	while (iterator.hasNext()) {
	    if (finished-- < 0) {
		break;
	    }
	    BlockPos p = new BlockPos(iterator.next()).offset(position);
	    BlockState state = world.getBlockState(p);

	    if (state.isAir() || state.getDestroySpeed(world, p) < 0
		    || state.getDestroySpeed(world, p) > BallistixConstants.EXPLOSIVE_SONIC_MAXHARDNESS
		    || state.liquid()) {
		continue;
	    }
	    if (!canBreakBlockState(world, state, p, owner)) {
		continue;
	    }
	    double deltaX = p.getX() - position.getX();
	    double deltaY = Math.max(p.getY() - position.getY(), 1);
	    double deltaZ = p.getZ() - position.getZ();

	    double inverseMag = Mth.fastInvSqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

	    double velX = deltaX * inverseMag * BallistixConstants.EXPLOSIVE_SONIC_VELOCITY;
	    double velY = Math.abs(deltaY) * inverseMag * BallistixConstants.EXPLOSIVE_SONIC_VELOCITY;
	    double velZ = deltaZ * inverseMag * BallistixConstants.EXPLOSIVE_SONIC_VELOCITY;

	    EntityBallistixFallingBlock movingBlock = new EntityBallistixFallingBlock(world, p.getX() + 0.5,
		    p.getY() + 0.5, p.getZ() + 0.5, state, thread.results, owner);
	    movingBlock.setDeltaMovement(velX * 0.33, velY * 3, velZ * 0.33);
	    world.setBlock(p, state.getFluidState().createLegacyBlock(), 3);
	    if (world.random.nextFloat() < 1.0 / 3.0) {
		world.addFreshEntity(movingBlock);
	    }
	}

	if (iterator.hasNext()) {
	    float x = position.getX();
	    float y = position.getY();
	    float z = position.getZ();

	    float size = (float) BallistixConstants.EXPLOSIVE_SONIC_RADIUS;
	    float doubleSize = size * 2.0F;

	    int x0 = Mth.floor(x - (double) doubleSize - 1.0D);
	    int x1 = Mth.floor(x + (double) doubleSize + 1.0D);
	    int y0 = Mth.floor(y - (double) doubleSize - 1.0D);
	    int y1 = Mth.floor(y + (double) doubleSize + 1.0D);
	    int z0 = Mth.floor(z - (double) doubleSize - 1.0D);
	    int z1 = Mth.floor(z + (double) doubleSize + 1.0D);

	    List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class,
		    new AABB(x0, y0, z0, x1, y1, z1));

	    for (LivingEntity entity : entities) {

		if (!canHarmEntity(entity)) {
		    continue;
		}

		double deltaX = entity.getX() - position.getX();
		double deltaY = entity.getY() - position.getY();
		double deltaZ = entity.getZ() - position.getZ();

		double inverseMag = Mth.fastInvSqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

		double velX = deltaX * inverseMag * BallistixConstants.EXPLOSIVE_SONIC_VELOCITY;
		double velY = Math.abs(deltaY) * inverseMag * BallistixConstants.EXPLOSIVE_SONIC_VELOCITY;
		double velZ = deltaZ * inverseMag * BallistixConstants.EXPLOSIVE_SONIC_VELOCITY;
		entity.push(velX, velY, velZ);
	    }
	} else {
	    return true;
	}

	return false;

    }

    @Override
    public boolean isInstantaneous() {
	return false;
    }

    @Override
    public boolean isDoneCalculating() {
	if (world.isClientSide) {
	    return shouldRenderCustomClient;
	}
	return thread == null || thread.isComplete;
    }
}
