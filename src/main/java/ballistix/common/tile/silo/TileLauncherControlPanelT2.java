package ballistix.common.tile.silo;

import ballistix.registers.BallistixTiles;

public class TileLauncherControlPanelT2 extends TileLauncherControlPanelT1 {

	public TileLauncherControlPanelT2() {
		super(BallistixTiles.TILE_LAUNCHER_CONTROL_PANEL_TIER2.get());
	}

	@Override
	public int getTier() {
		return 2;
	}
}
