package ballistix.common.blast;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion.Mode;
import net.minecraft.world.World;

public class BlastChemical extends Blast {

	public BlastChemical(World world, BlockPos position) {
		super(world, position);
	}

	@Override
	public boolean isInstantaneous() {
		return false;
	}

	@Override
	public void doPreExplode() {
	}

	@Override
	public boolean doExplode(int callCount) {
		float size = callCount / 10.0f;
		if (!world.isRemote) {
			world.createExplosion(null, position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5, size, Mode.BREAK);
		}
		return callCount > 100;
	}

	@Override
	public void doPostExplode() {
	}

}
