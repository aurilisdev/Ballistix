package ballistix.common.blast;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlastDebilition extends Blast {

	public BlastDebilition(World world, BlockPos position) {
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
