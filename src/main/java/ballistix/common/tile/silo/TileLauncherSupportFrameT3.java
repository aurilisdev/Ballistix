package ballistix.common.tile.silo;

import ballistix.common.block.subtype.SubtypeBallistixMachine;
import ballistix.registers.BallistixTiles;
import voltaic.api.multiblock.subnodebased.parent.IMultiblockParentBlock;

public class TileLauncherSupportFrameT3 extends TileLauncherSupportFrameT1 {
	
	public TileLauncherSupportFrameT3() {
		super(BallistixTiles.TILE_LAUNCHER_SUPPORT_FRAME_TIER3.get(), 3);
	}

	@Override
	public IMultiblockParentBlock.SubnodeWrapper getSubNodes() {
		return SubtypeBallistixMachine.Subnodes.LAUNCHER_SUPPORT_FRAME_TIER3;
	}

	@Override
	public int getInaccuracy() {
		return 0;
	}
}
