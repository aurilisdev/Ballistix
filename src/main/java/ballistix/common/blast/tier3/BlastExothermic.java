package ballistix.common.blast.tier3;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import ballistix.api.blast.IBlast;
import ballistix.api.blast.IHasCustomRender;
import ballistix.common.blast.util.BlastLasting;
import ballistix.common.blast.util.thread.ThreadSimpleBlast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.settings.BallistixConstants;
import ballistix.compatibility.griefdefender.GriefDefenderHandler;
import ballistix.registers.BallistixSounds;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class BlastExothermic extends BlastLasting implements IHasCustomRender {

    private ThreadSimpleBlast thread;
    private Iterator<BlockPos> iterator;
    private int pertick = -1;

    public BlastExothermic(World world, BlockPos position) {
        super(world, position);
    }

    @Override
    public void doPreExplode() {
        if (!world.isClientSide) {
            thread = new ThreadSimpleBlast(world, position, (int) BallistixConstants.EXPLOSIVE_EXOTHERMIC_RADIUS, Integer.MAX_VALUE, null, getBlastType().id());
            thread.start();
            world.playSound(null, position, BallistixSounds.SOUND_ENDOTHERMICBEAM.get(), SoundCategory.BLOCKS, 25, 1);
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
            pertick = (int) (thread.results.size() * 1.5 / BallistixConstants.EXPLOSIVE_EXOTHERMIC_DURATION + 1);
            iterator = thread.results.iterator();
            world.playSound(null, position, SoundEvents.GENERIC_EXPLODE, SoundCategory.BLOCKS, 25.0F, 1.0F);
            world.explode(null, position.getX(), position.getY(), position.getZ(), (float) BallistixConstants.EXPLOSIVE_EXOTHERMIC_RADIUS * 0.85F, Explosion.Mode.DESTROY);
        }
        int finished = pertick;
        while (iterator.hasNext()) {
            if (finished-- < 0) {
                break;
            }
            BlockPos p = new BlockPos(iterator.next()).offset(position);
            BlockState state = world.getBlockState(p);

            if(state.isAir(world, p) || !(state.getBlock() instanceof FlowingFluidBlock) && (state.getDestroySpeed(world, p) < 0 || state.getDestroySpeed(world, p) > BallistixConstants.EXPLOSIVE_EXOTHERMIC_MAXHARDNESS)) {
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

            if(!shouldRepulse) {
                continue;
            }

            if(state.getBlock() instanceof FlowingFluidBlock) {
                world.setBlockAndUpdate(p, Blocks.AIR.defaultBlockState());
                continue;
            }

            double chance = world.random.nextDouble();

            if(chance <= BallistixConstants.EXPLOSIVE_EXOTHERMIC_CHANCE_FOR_LAVA) {

                world.setBlockAndUpdate(p, Blocks.LAVA.defaultBlockState());

            } else if (chance <= BallistixConstants.EXPLOSIVE_EXOTHERMIC_CHANCE_TO_BURN) {

                int selection = boundedNextInt(world.random, 0, 6);

                BlockState newState = null;

                switch(selection) {
                    case 1:
                        newState = Blocks.NETHERRACK.defaultBlockState();
                        break;
                    case 2 :
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

                if(newState == null) {
                    continue;
                }

                world.setBlockAndUpdate(p, newState);

            } else {
                continue;
            }
        }

        if(!iterator.hasNext()) {
            float x = position.getX();
            float y = position.getY();
            float z = position.getZ();

            float size = (float) BallistixConstants.EXPLOSIVE_SONIC_RADIUS;
            float doubleSize = size * 2.0F;

            int x0 = MathHelper.floor(x - (double) doubleSize - 1.0D);
            int x1 = MathHelper.floor(x + (double) doubleSize + 1.0D);
            int y0 = MathHelper.floor(y - (double) doubleSize - 1.0D);
            int y1 = MathHelper.floor(y + (double) doubleSize + 1.0D);
            int z0 = MathHelper.floor(z - (double) doubleSize - 1.0D);
            int z1 = MathHelper.floor(z + (double) doubleSize + 1.0D);

            List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(x0, y0, z0, x1, y1, z1));

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

                double inverseMag = MathHelper.fastInvSqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

                double velX = deltaX * inverseMag * BallistixConstants.EXPLOSIVE_ENDOTHERMIC_VELOCITY;
                double velY = deltaY * inverseMag * BallistixConstants.EXPLOSIVE_ENDOTHERMIC_VELOCITY;
                double velZ = deltaZ * inverseMag * BallistixConstants.EXPLOSIVE_ENDOTHERMIC_VELOCITY;
                entity.addEffect(new EffectInstance(Effects.CONFUSION, 10000));
                entity.setRemainingFireTicks(10000);
                //entity.addEffect(new MobEffectInstance(BallistixEffects.FROSTBITE, 10000));
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
    
    public static int boundedNextInt(Random rng, int origin, int bound) {
        int r = rng.nextInt();
        if (origin < bound) {
            // It's not case (1).
            final int n = bound - origin;
            final int m = n - 1;
            if ((n & m) == 0) {
                // It is case (2): length of range is a power of 2.
                r = (r & m) + origin;
            } else if (n > 0) {
                // It is case (3): need to reject over-represented candidates.
                for (int u = r >>> 1;
                     u + m - (r = u % n) < 0;
                     u = rng.nextInt() >>> 1)
                    ;
                r += origin;
            }
            else {
                // It is case (4): length of range not representable as long.
                while (r < origin || r >= bound) {
                    r = rng.nextInt();
                }
            }
        }
        return r;
    }
    
}
