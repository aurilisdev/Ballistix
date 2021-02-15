package ballistix.common.block;

import java.util.HashSet;

import ballistix.common.tile.TileMissileSilo;
import electrodynamics.common.block.BlockGenericMachine;
import electrodynamics.common.multiblock.IMultiblockNode;
import electrodynamics.common.multiblock.IMultiblockTileNode;
import electrodynamics.common.multiblock.Subnode;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class BlockMissileSilo extends BlockGenericMachine implements IMultiblockNode {
	public static final HashSet<Subnode> subnodes = new HashSet<>();
	static {
		int radius = 1;
		for (int i = -radius; i <= radius; i++) {
			for (int k = 0; k <= 1; k++) {
				for (int j = -radius; j <= radius; j++) {
					if (!(i == 0 && j == 0 && k == 0)) {
						subnodes.add(new Subnode(new BlockPos(i, k, j), VoxelShapes.fullCube()));
					}
				}
			}
		}
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new TileMissileSilo();
	}

	@Override
	public boolean hasMultiBlock() {
		return true;
	}

	@Override
	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
		return isValidMultiblockPlacement(state, worldIn, pos, subnodes);
	}

	@Deprecated
	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile instanceof IMultiblockTileNode) {
			IMultiblockTileNode multi = (IMultiblockTileNode) tile;
			multi.onNodeReplaced(worldIn, pos, false);
		}
		super.onReplaced(state, worldIn, pos, newState, isMoving);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile instanceof IMultiblockTileNode) {
			IMultiblockTileNode multi = (IMultiblockTileNode) tile;
			multi.onNodePlaced(worldIn, pos, state, placer, stack);
		}
	}
}
