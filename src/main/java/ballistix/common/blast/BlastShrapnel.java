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
	public void doExplode(int callCount) {
	}

	@Override
	public void doPostExplode() {
	}

}
