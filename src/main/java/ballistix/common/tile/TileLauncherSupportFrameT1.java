package ballistix.common.tile;

import ballistix.common.block.subtype.SubtypeBallistixMachine;
import ballistix.registers.BallistixTiles;
import electrodynamics.api.multiblock.subnodebased.parent.IMultiblockParentBlock;
import electrodynamics.api.multiblock.subnodebased.parent.IMultiblockParentTile;
import electrodynamics.common.tile.TileMultiSubnode;
import electrodynamics.prefab.tile.GenericTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TileLauncherSupportFrameT1 extends GenericTile implements IMultiblockParentTile {
	public TileLauncherSupportFrameT1(BlockPos pos, BlockState state) {
		this(BallistixTiles.TILE_LAUNCHER_SUPPORT_FRAME_TIER1.get(), pos, state, 1);
	}

	public TileLauncherSupportFrameT1(BlockEntityType<?> type, BlockPos pos, BlockState state, int tier) {
		super(type, pos, state);
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

}
