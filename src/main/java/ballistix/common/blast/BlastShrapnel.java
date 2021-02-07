package ballistix.common.blast;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlastShrapnel extends Blast {

	public BlastShrapnel(World world, BlockPos position) {
		super(world, position);
	}

	@Override
	public void doPreExplode() {
	}

	@Override
	public boolean doExplode(int callCount) {
		return true;
	}

	@Override
	public void doPostExplode() {
	}

}
