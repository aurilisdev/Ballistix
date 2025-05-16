package ballistix.common.tile.silo;

import ballistix.api.silo.ILauncherSupportFrame;
import ballistix.common.block.subtype.SubtypeBallistixMachine;
import ballistix.registers.BallistixTiles;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import voltaic.api.multiblock.subnodebased.TileMultiSubnode;
import voltaic.api.multiblock.subnodebased.parent.IMultiblockParentBlock;
import voltaic.api.multiblock.subnodebased.parent.IMultiblockParentTile;
import voltaic.prefab.tile.GenericTile;

public class TileLauncherSupportFrameT1 extends GenericTile implements IMultiblockParentTile, ILauncherSupportFrame {
	
	public TileLauncherSupportFrameT1() {
		this(BallistixTiles.TILE_LAUNCHER_SUPPORT_FRAME_TIER1.get(), 1);
	}

	public TileLauncherSupportFrameT1(TileEntityType<?> type, int tier) {
		super(type);
	}

	@Override
	public IMultiblockParentBlock.SubnodeWrapper getSubNodes() {
		return SubtypeBallistixMachine.Subnodes.LAUNCHER_SUPPORT_FRAME_TIER1;
	}

	@Override
	public void onSubnodeDestroyed(TileMultiSubnode tileMultiSubnode) {
		level.destroyBlock(worldPosition, true);
	}

	@Override
	public Direction getFacingDirection() {
		return getFacing();
	}

	@Override
	public int getInaccuracy() {
		return 30;
	}

}
