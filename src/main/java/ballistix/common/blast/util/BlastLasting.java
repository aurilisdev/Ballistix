package ballistix.common.blast.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public abstract class BlastLasting extends Blast {

    protected BlastLasting(Level world, BlockPos position, Entity owner) {
	super(world, position, owner);
    }

    public int ticksSinceBlastStart;

    @Override
    public boolean doExplode(int callCount) {
	if (isDoneCalculating()) {
	    ticksSinceBlastStart++;
	}
	return super.doExplode(callCount);
    }

    public abstract boolean isDoneCalculating();

}
