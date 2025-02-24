package ballistix.common.block;

import ballistix.common.tile.TileESMTower;
import electrodynamics.api.multiblock.Subnode;
import electrodynamics.api.multiblock.parent.IMultiblockParentBlock;
import electrodynamics.api.multiblock.parent.IMultiblockParentTile;
import electrodynamics.prefab.block.GenericMachineBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;

public class BlockESMTower extends GenericMachineBlock implements IMultiblockParentBlock {

	public static final Subnode[] SUBNODES = { new Subnode(new BlockPos(0, 1, 0), Shapes.or(Block.box(6, 0, 6, 10, 13, 10), Block.box(5, 13, 5, 11, 16, 11))), new Subnode(new BlockPos(0, 2, 0), Block.box(5, 0, 5, 11, 16, 11)) };

	public BlockESMTower() {
		super(TileESMTower::new);
	}

	@Override
	public boolean hasMultiBlock() {
		return true;
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {

		return isValidMultiblockPlacement(state, worldIn, pos, SUBNODES);
	}

	@Override
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (worldIn.getBlockEntity(pos) instanceof IMultiblockParentTile multi) {
			multi.onNodeReplaced(worldIn, pos, false);
		}
		super.onRemove(state, worldIn, pos, newState, isMoving);
	}

	@Override
	public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(worldIn, pos, state, placer, stack);
		if (worldIn.getBlockEntity(pos) instanceof IMultiblockParentTile multi) {
			multi.onNodePlaced(worldIn, pos, state, placer, stack);
		}
	}
	
	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.INVISIBLE;
	}

}
