package ballistix.common.tile.silo;

import ballistix.common.block.subtype.SubtypeBallistixMachine;
import ballistix.common.settings.BallistixConstants;
import ballistix.registers.BallistixTiles;
import voltaic.api.multiblock.subnodebased.parent.IMultiblockParentBlock;

public class TileLauncherPlatformT2 extends TileLauncherPlatformT1 {
	
	public TileLauncherPlatformT2() {
		super(BallistixTiles.TILE_LAUNCHER_PLATFORM_TIER2.get());
	}

	@Override
	public IMultiblockParentBlock.SubnodeWrapper getSubNodes() {
		return SubtypeBallistixMachine.Subnodes.LAUNCHER_PLATFORM_TIER2;
	}

	@Override
	public int getRange() {
		return BallistixConstants.LAUNCHER_PLATFORM_RANGE_T2;
	}

	@Override
	public int getTier() {
		return 2;
	}

}
