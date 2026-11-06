package ballistix.common.blast.util;

import javax.annotation.Nullable;

import ballistix.common.block.subtype.SubtypeBlast;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public abstract class BlastLasting extends Blast {

    /** A blast first seen later than this has been running too long for its opening effects to make sense. */
    private static final int LAST_CLIENT_EFFECT_TICK = 24;
    private static final int CRATER_REPORT_INTERVAL = 10;

    protected BlastLasting(Level world, BlockPos position, @Nullable Entity owner, @Nullable Entity blastEntity) {
	super(world, position, owner, blastEntity);
    }

    public int ticksSinceBlastStart;
    private boolean startedClientEffects;
    @Nullable
    private CraterFloorTracker craterFloors;

    /** Announces the detonation and starts tracking the floors this blast exposes while it carves. */
    protected final void startCraterEffects() {
	sendDetonationEffects();
	double radius = effectRadius();
	if (world instanceof ServerLevel level && getBlastType() instanceof SubtypeBlast type
		&& Double.isFinite(radius) && radius > 0) {
	    craterFloors = new CraterFloorTracker(level, position, type, radius);
	}
    }

    protected final void recordCraterRemoval(BlockPos removed, BlockState removedState) {
	CraterFloorTracker lCraterFloors = craterFloors;
	if (lCraterFloors != null && removedState.blocksMotion())
	    lCraterFloors.recordRemoval(removed);
    }

    protected final void reportCraterFloors(int callCount, boolean carvingDone) {
	CraterFloorTracker lCraterFloors = craterFloors;
	if (lCraterFloors != null && (carvingDone || callCount % CRATER_REPORT_INTERVAL == 0))
	    lCraterFloors.flush();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    protected void startClientEffects() {
	if (startedClientEffects || ticksSinceBlastStart < 1 || ticksSinceBlastStart > LAST_CLIENT_EFFECT_TICK)
	    return;
	startedClientEffects = true;
	super.startClientEffects();
    }

    @Override
    public boolean doExplode(int callCount) {
	if (isDoneCalculating()) {
	    ticksSinceBlastStart++;
	}
	return super.doExplode(callCount);
    }

    public abstract boolean isDoneCalculating();

}
