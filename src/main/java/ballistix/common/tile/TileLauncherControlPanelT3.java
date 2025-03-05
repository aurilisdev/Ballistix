package ballistix.common.tile;

import ballistix.registers.BallistixTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TileLauncherControlPanelT3 extends TileLauncherControlPanelT1 {

	public TileLauncherControlPanelT3(BlockPos pos, BlockState state) {
		super(BallistixTiles.TILE_LAUNCHER_CONTROL_PANEL_TIER3.get(), pos, state);
	}

	@Override
	public int getTier() {
		return 3;
	}
}
