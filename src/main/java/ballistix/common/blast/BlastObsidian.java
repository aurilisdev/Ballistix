package ballistix.common.blast;

import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.settings.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.Level;

public class BlastObsidian extends Blast {

	public BlastObsidian(Level world, BlockPos position) {
		super(world, position);
	}

	@Override
	public boolean doExplode(int callCount) {
		hasStarted = true;
		if (!world.isClientSide) {
			world.explode(null, position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5, (float) Constants.EXPLOSIVE_OBSIDIAN_SIZE, BlockInteraction.BREAK);
		}
		return true;
	}

	@Override
	public SubtypeBlast getBlastType() {
		return SubtypeBlast.obsidian;
	}

}