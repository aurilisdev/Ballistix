package ballistix.common.blast.tier3;

import java.util.Iterator;
import java.util.List;

import ballistix.api.blast.IBlast;
import ballistix.api.blast.IHasCustomRender;
import ballistix.common.blast.util.BlastLasting;
import ballistix.common.blast.util.thread.ThreadSimpleBlast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.settings.BallistixConfig;
import ballistix.compatibility.griefdefender.GriefDefenderHandler;
import ballistix.registers.BallistixSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class BlastExothermic extends BlastLasting implements IHasCustomRender {

    private ThreadSimpleBlast thread;
    private Iterator<BlockPos> iterator;
    private int pertick = -1;

    public BlastExothermic(Level world, BlockPos position) {
	super(world, position);
    }

    @Override
    public void doPreExplode() {
	if (!world.isClientSide) {
	    thread = new ThreadSimpleBlast(world, position,
		    (int) BallistixConfig.INSTANCE.EXPLOSIVE_EXOTHERMIC_RADIUS.getAsDouble(), Integer.MAX_VALUE, null,
		    getBlastType().id());
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
	    pertick = (int) (thread.results.size() * 1.5 / BallistixConfig.INSTANCE.EXPLOSIVE_EXOTHERMIC_DURATION.get()
		    + 1);
	    iterator = thread.results.iterator();
	    world.playSound(null, position, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 25.0F, 1.0F);
	    world.explode(null, position.getX(), position.getY(), position.getZ(),
		    (float) BallistixConfig.INSTANCE.EXPLOSIVE_EXOTHERMIC_RADIUS.getAsDouble() * 0.85F,
		    Level.ExplosionInteraction.BLOCK);
	}
	int finished = pertick;
	while (iterator.hasNext()) {
	    if (finished-- < 0) {
		break;
	    }
	    BlockPos p = new BlockPos(iterator.next()).offset(position);
	    BlockState state = world.getBlockState(p);

	    if (state.isAir() || (!(state.getBlock() instanceof LiquidBlock)
		    && (state.getDestroySpeed(world, p) < 0 || state.getDestroySpeed(world,
			    p) > BallistixConfig.INSTANCE.EXPLOSIVE_EXOTHERMIC_MAXHARDNESS.get()))) {
		continue;
	    }

	    boolean shouldRepulse = true;

	    switch (griefPreventionMethod) {
	    case NONE:
		break;
	    case GRIEF_DEFENDER:
		shouldRepulse = GriefDefenderHandler.shouldHarmBlock(p);
		break;
	    case SABER_FACTIONS:
		break;
	    }

	    if (!shouldRepulse) {
		continue;
	    }

	    if (state.getBlock() instanceof LiquidBlock) {
		world.setBlockAndUpdate(p, Blocks.AIR.defaultBlockState());
		continue;
	    }

	    double chance = world.random.nextDouble();

	    if (chance <= BallistixConfig.INSTANCE.EXPLOSIVE_EXOTHERMIC_CHANCE_FOR_LAVA.get()) {

		world.setBlockAndUpdate(p, Blocks.LAVA.defaultBlockState());

	    } else if (chance <= BallistixConfig.INSTANCE.EXPLOSIVE_EXOTHERMIC_CHANCE_TO_BURN.get()) {

		int selection = world.random.nextIntBetweenInclusive(0, 5);

		BlockState newState = null;

		switch (selection) {
		case 1:
		    newState = Blocks.NETHERRACK.defaultBlockState();
		    break;
		case 2:
		    newState = Blocks.NETHER_BRICKS.defaultBlockState();
		    break;
		case 3:
		    newState = Blocks.SOUL_SAND.defaultBlockState();
		    break;
		case 4:
		    newState = Blocks.SOUL_SOIL.defaultBlockState();
		    break;
		case 5:
		    newState = Blocks.OBSIDIAN.defaultBlockState();
		    break;
		}

		if (newState == null) {
		    continue;
		}

		world.setBlockAndUpdate(p, newState);

	    } else {
		continue;
	    }
	}

	if (!iterator.hasNext()) {
	    float x = position.getX();
	    float y = position.getY();
	    float z = position.getZ();

	    float size = (float) BallistixConfig.INSTANCE.EXPLOSIVE_SONIC_RADIUS.getAsDouble();
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

		switch (griefPreventionMethod) {
		case GRIEF_DEFENDER:
		    if (!GriefDefenderHandler.shouldEntityBeHarmed(entity)) {
			continue;
		    }
		    break;
		default:
		    break;
		}

		double deltaX = entity.getX() - position.getX();
		double deltaY = entity.getY() - position.getY();
		double deltaZ = entity.getZ() - position.getZ();

		double inverseMag = Mth.fastInvSqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

		double velX = deltaX * inverseMag * BallistixConfig.INSTANCE.EXPLOSIVE_ENDOTHERMIC_VELOCITY.get();
		double velY = deltaY * inverseMag * BallistixConfig.INSTANCE.EXPLOSIVE_ENDOTHERMIC_VELOCITY.get();
		double velZ = deltaZ * inverseMag * BallistixConfig.INSTANCE.EXPLOSIVE_ENDOTHERMIC_VELOCITY.get();
		entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 10000));
		entity.igniteForTicks(10000);
		// entity.addEffect(new MobEffectInstance(BallistixEffects.FROSTBITE, 10000));
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
	return SubtypeBlast.exothermic;
    }
}
