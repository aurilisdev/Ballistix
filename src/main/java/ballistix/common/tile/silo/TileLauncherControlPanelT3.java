package ballistix.common.tile.silo;

import ballistix.registers.BallistixTiles;
import net.minecraft.util.math.BlockPos;

public class TileLauncherControlPanelT3 extends TileLauncherControlPanelT1 {

	public TileLauncherControlPanelT3() {
		super(BallistixTiles.TILE_LAUNCHER_CONTROL_PANEL_TIER3.get());
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
