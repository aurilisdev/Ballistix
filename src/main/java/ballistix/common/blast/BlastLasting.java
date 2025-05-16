package ballistix.common.blast;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BlastLasting extends Blast {
	public boolean isDoneClient = false;

	protected BlastLasting(World world, BlockPos position) {
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
