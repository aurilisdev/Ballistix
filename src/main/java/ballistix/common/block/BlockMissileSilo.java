package ballistix.common.block;

import java.util.HashSet;

import ballistix.common.tile.TileMissileSilo;
import electrodynamics.common.block.BlockGenericMachine;
import electrodynamics.common.multiblock.IMultiblockNode;
import electrodynamics.common.multiblock.IMultiblockTileNode;
import electrodynamics.common.multiblock.Subnode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;

public class BlockMissileSilo extends BlockGenericMachine implements IMultiblockNode {
    public static final HashSet<Subnode> subnodes = new HashSet<>();
    static {
	int radius = 1;
	for (int i = -radius; i <= radius; i++) {
	    for (int k = 0; k <= 1; k++) {
		for (int j = -radius; j <= radius; j++) {
		    if (!(i == 0 && j == 0 && k == 0)) {
			subnodes.add(new Subnode(new BlockPos(i, k, j), Shapes.block()));
		    }
		}
	    }
	}
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
	return new TileMissileSilo(pos, state);
    }

    @Override
    public boolean hasMultiBlock() {
	return true;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
	return isValidMultiblockPlacement(state, worldIn, pos, subnodes);
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
	BlockEntity tile = worldIn.getBlockEntity(pos);
	if (tile instanceof IMultiblockTileNode multi) {
	    multi.onNodeReplaced(worldIn, pos, false);
	}
	super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
	super.setPlacedBy(worldIn, pos, state, placer, stack);
	BlockEntity tile = worldIn.getBlockEntity(pos);
	if (tile instanceof IMultiblockTileNode multi) {
	    multi.onNodePlaced(worldIn, pos, state, placer, stack);
	}
    }
}
