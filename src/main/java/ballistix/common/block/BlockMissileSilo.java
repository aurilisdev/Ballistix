package ballistix.common.block;

import ballistix.common.tile.TileMissileSilo;
import electrodynamics.common.block.BlockGenericMachine;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class BlockMissileSilo extends BlockGenericMachine {
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new TileMissileSilo();
	}

}
