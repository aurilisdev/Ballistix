package ballistix.common.tile;

import ballistix.registers.BallistixTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TileLauncherControlPanelT2 extends TileLauncherControlPanelT1 {

	public TileLauncherControlPanelT2(BlockPos pos, BlockState state) {
		super(BallistixTiles.TILE_LAUNCHER_CONTROL_PANEL_TIER2.get(), pos, state, 2);
	}
}
