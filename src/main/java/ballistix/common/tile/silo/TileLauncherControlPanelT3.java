package ballistix.common.tile.silo;

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

    @Override
    public void setTargetFromDesignator(BlockPos target) {
	setTarget(target);
    }
}
