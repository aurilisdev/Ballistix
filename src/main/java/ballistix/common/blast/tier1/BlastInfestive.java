package ballistix.common.blast.tier1;

import ballistix.api.blast.IBlast;
import ballistix.common.blast.util.BlastLasting;
import ballistix.common.blast.util.thread.ThreadSimpleBlast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.settings.BallistixConstants;
import ballistix.compatibility.griefdefender.GriefDefenderHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.SilverfishBlock;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Iterator;

public class BlastInfestive extends BlastLasting {

    private ThreadSimpleBlast thread;
    private Iterator<BlockPos> iterator;
    private int pertick = -1;

    public BlastInfestive(World world, BlockPos position) {
        super(world, position);
    }

    @Override
    public void doPreExplode() {
        if (!world.isClientSide) {
            thread = new ThreadSimpleBlast(world, position, (int) BallistixConstants.EXPLOSIVE_INFESTIVE_RADIUS, Integer.MAX_VALUE, null, getBlastType().id());
            thread.start();
            world.playSound(null, position, SoundEvents.GENERIC_EXPLODE, SoundCategory.BLOCKS, 25, 1);
            world.playSound(null, position, SoundEvents.GHAST_HURT, SoundCategory.BLOCKS, 25, 1);
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
            pertick = (int) (thread.results.size() * 1.5 / BallistixConstants.EXPLOSIVE_INFESTIVE_DURATION + 1);
            iterator = thread.results.iterator();
        }
        int finished = pertick;
        while (iterator.hasNext()) {
            if (finished-- < 0) {
                break;
            }
            BlockPos p = new BlockPos(iterator.next()).offset(position);
            BlockState state = world.getBlockState(p);

            if(state.isAir(world, p)) {
                continue;
            }

            if(!SilverfishBlock.isCompatibleHostBlock(state)) {
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

            world.setBlockAndUpdate(p, SilverfishBlock.stateByHostBlock(state.getBlock()));
        }

        return !iterator.hasNext();


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
    public IBlast getBlastType() {
        return SubtypeBlast.infestive;
    }

}
