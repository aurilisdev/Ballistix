package ballistix.common.blast.tier3;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import ballistix.api.blast.IBlast;
import ballistix.api.blast.IHasCustomRender;
import ballistix.common.blast.util.BlastLasting;
import ballistix.common.blast.util.thread.ThreadSimpleBlast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.entity.EntityBallistixFallingBlock;
import ballistix.common.settings.BallistixConstants;
import ballistix.registers.BallistixEffects;
import ballistix.registers.BallistixSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class BlastEndothermic extends BlastLasting implements IHasCustomRender {

    private ThreadSimpleBlast thread;
    private Iterator<BlockPos> iterator;
    private int pertick = -1;

    public BlastEndothermic(Level world, BlockPos position, @Nullable Entity owner, @Nullable Entity blastEntity) {
	super(world, position, owner, blastEntity);
    }

    @Override
    public void doPreExplode() {
	if (!world.isClientSide) {
	    thread = new ThreadSimpleBlast(world, position, (int) BallistixConstants.EXPLOSIVE_ENDOTHERMIC_RADIUS,
		    Integer.MAX_VALUE, null, getBlastType().id(), true);
	    thread.start();
	    world.playSound(null, position, BallistixSounds.SOUND_ENDOTHERMICBEAM.get(), SoundSource.BLOCKS, 25, 1);
	}
    }

    @Override
    public boolean doExplode(int callCount) {
	hasStarted = true;
	super.doExplode(callCount);
	if (thread == null) {
	    return !world.isClientSide;
	}
	if (world.isClientSide || !thread.isComplete || ticksSinceBlastStart < 20) {
	    return false;
	}
	if (pertick == -1) {
	    hasStarted = true;
	    pertick = (int) (thread.results.size() * 1.5 / BallistixConstants.EXPLOSIVE_ENDOTHERMIC_DURATION + 1);
	    iterator = thread.results.iterator();
	}
	int finished = pertick;
	while (iterator.hasNext()) {
	    if (finished-- < 0) {
		break;
	    }
	    BlockPos p = new BlockPos(iterator.next()).offset(position);
	    BlockState state = world.getBlockState(p);

	    if (state.isAir() || !(state.getBlock() instanceof LiquidBlock) && (state.getDestroySpeed(world, p) < 0
		    || state.getDestroySpeed(world, p) > BallistixConstants.EXPLOSIVE_ENDOTHERMIC_MAXHARDNESS)) {
		continue;
	    }

	    if (!canBreakBlockState(world, state, p, owner)) {
		continue;
	    }

	    if (state.getBlock() instanceof LiquidBlock) {
		state = Blocks.PACKED_ICE.defaultBlockState();
	    } else {
		state = Blocks.SNOW_BLOCK.defaultBlockState();
	    }

	    double deltaX = p.getX() - position.getX();
	    double deltaY = p.getY() - position.getY();
	    double deltaZ = p.getZ() - position.getZ();

	    double inverseMag = Mth.fastInvSqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

	    double velX = deltaX * inverseMag * BallistixConstants.EXPLOSIVE_ENDOTHERMIC_VELOCITY;
	    double velY = Math.abs(deltaY) * inverseMag * BallistixConstants.EXPLOSIVE_ENDOTHERMIC_VELOCITY;
	    double velZ = deltaZ * inverseMag * BallistixConstants.EXPLOSIVE_ENDOTHERMIC_VELOCITY;

	    EntityBallistixFallingBlock movingBlock = new EntityBallistixFallingBlock(world, p.getX() + 0.5,
		    p.getY() + 0.5, p.getZ() + 0.5, state, thread.results, owner);
	    movingBlock.setDeltaMovement(velX * 0.5, velY * 3, velZ * 0.5);
	    world.setBlock(p, state.getFluidState().createLegacyBlock(), 3);
	    if (world.random.nextFloat() < 1.0 / 6.0) {
		world.addFreshEntity(movingBlock);
	    }
	}

	if (!iterator.hasNext()) {
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

		double velX = deltaX * inverseMag * BallistixConstants.EXPLOSIVE_ENDOTHERMIC_VELOCITY;
		double velY = deltaY * inverseMag * BallistixConstants.EXPLOSIVE_ENDOTHERMIC_VELOCITY;
		double velZ = deltaZ * inverseMag * BallistixConstants.EXPLOSIVE_ENDOTHERMIC_VELOCITY;
		entity.setTicksFrozen(10000);
		entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10000));
		entity.addEffect(new MobEffectInstance(BallistixEffects.FROSTBITE.get(), 10000));
		entity.setDeltaMovement(entity.getDeltaMovement().add(velX, velY, velZ));
	    }

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

    @Override
    public boolean shouldRender() {
	return ticksSinceBlastStart < 20;
    }

    @Override
    public void produceParticles() {

    }

    @Override
    public IBlast getBlastType() {
	return SubtypeBlast.endothermic;
    }
}
