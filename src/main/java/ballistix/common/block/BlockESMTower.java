package ballistix.common.block;

import ballistix.common.tile.TileESMTower;
import electrodynamics.api.multiblock.Subnode;
import electrodynamics.api.multiblock.parent.IMultiblockParentBlock;
import electrodynamics.api.multiblock.parent.IMultiblockParentTile;
import electrodynamics.prefab.block.GenericMachineBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class BlockESMTower extends GenericMachineBlock implements IMultiblockParentBlock {

	public static final Subnode[] SUBNODES = { new Subnode(new BlockPos(0, 1, 0), VoxelShapes.or(Block.box(6, 0, 6, 10, 13, 10), Block.box(5, 13, 5, 11, 16, 11))), new Subnode(new BlockPos(0, 2, 0), Block.box(5, 0, 5, 11, 16, 11)) };

	public BlockESMTower() {
		super(world -> new TileESMTower());
	}

	@Override
	public boolean hasMultiBlock() {
		return true;
	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos) {

		return isValidMultiblockPlacement(state, worldIn, pos, SUBNODES);
	}

	@Override
	public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		TileEntity tile = worldIn.getBlockEntity(pos);
		if (tile instanceof IMultiblockParentTile) {
			IMultiblockParentTile multi = (IMultiblockParentTile) tile;
			multi.onNodeReplaced(worldIn, pos, false);
		}
		super.onRemove(state, worldIn, pos, newState, isMoving);
	}

	@Override
	public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(worldIn, pos, state, placer, stack);
		TileEntity tile = worldIn.getBlockEntity(pos);
		if (tile instanceof IMultiblockParentTile) {
			IMultiblockParentTile multi = (IMultiblockParentTile) tile;
			multi.onNodePlaced(worldIn, pos, state, placer, stack);
		}
	}
	
	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.INVISIBLE;
	}

}
