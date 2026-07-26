package ballistix.common.blast.tier1;

import java.util.Iterator;

import javax.annotation.Nullable;

import ballistix.api.blast.IBlast;
import ballistix.common.blast.util.BlastLasting;
import ballistix.common.blast.util.thread.ThreadSimpleBlast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.settings.BallistixConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.InfestedBlock;
import net.minecraft.world.level.block.state.BlockState;

public class BlastInfestive extends BlastLasting {

    private ThreadSimpleBlast thread;
    private Iterator<BlockPos> iterator;
    private int pertick = -1;

    public BlastInfestive(Level world, BlockPos position, @Nullable Entity owner, @Nullable Entity blastEntity) {
	super(world, position, owner, blastEntity);
    }

    @Override
    public void doPreExplode() {
	if (!world.isClientSide) {
	    thread = new ThreadSimpleBlast(world, position, (int) BallistixConstants.EXPLOSIVE_INFESTIVE_RADIUS,
		    Integer.MAX_VALUE, null, getBlastType().id());
	    thread.start();
	    world.playSound(null, position, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 25, 1);
	    world.playSound(null, position, SoundEvents.GHAST_HURT, SoundSource.BLOCKS, 25, 1);
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

	    if (state.isAir()) {
		continue;
	    }

	    if (!InfestedBlock.isCompatibleHostBlock(state)) {
		continue;
	    }

	    boolean shouldRepulse = canHarmBlock(p);

	    if (!shouldRepulse) {
		continue;
	    }

	    world.setBlockAndUpdate(p, InfestedBlock.infestedStateByHost(state));
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
