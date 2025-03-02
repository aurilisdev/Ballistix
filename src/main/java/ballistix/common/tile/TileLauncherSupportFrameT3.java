package ballistix.common.tile;

import ballistix.registers.BallistixTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TileLauncherSupportFrameT3 extends TileLauncherSupportFrameT1 {
	public TileLauncherSupportFrameT3(BlockPos pos, BlockState state) {
		super(BallistixTiles.TILE_LAUNCHER_SUPPORT_FRAME_TIER3.get(), pos, state, 1);
	}
}
