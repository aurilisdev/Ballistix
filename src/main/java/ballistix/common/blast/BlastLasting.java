package ballistix.common.blast;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public abstract class BlastLasting extends Blast {
	public boolean isDoneClient = false;

	protected BlastLasting(Level world, BlockPos position) {
		super(world, position);
	}

	protected int ticksSinceBlastStart;

	@Override
	public boolean doExplode(int callCount) {
		if (isDoneCalculating()) {
			ticksSinceBlastStart++;
		}
		return super.doExplode(callCount);
	}

	public abstract boolean isDoneCalculating();

}
