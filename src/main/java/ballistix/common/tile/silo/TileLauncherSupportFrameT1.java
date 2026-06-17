package ballistix.common.tile.silo;

import ballistix.api.silo.ILauncherSupportFrame;
import ballistix.common.block.subtype.SubtypeBallistixMachine;
import ballistix.registers.BallistixTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import voltaic.api.multiblock.subnodebased.TileMultiSubnode;
import voltaic.api.multiblock.subnodebased.parent.IMultiblockParentBlock;
import voltaic.api.multiblock.subnodebased.parent.IMultiblockParentTile;
import voltaic.prefab.tile.GenericTile;

public class TileLauncherSupportFrameT1 extends GenericTile implements IMultiblockParentTile, ILauncherSupportFrame {
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

    @Override
    public int getInaccuracy() {
	return 30;
    }

}
