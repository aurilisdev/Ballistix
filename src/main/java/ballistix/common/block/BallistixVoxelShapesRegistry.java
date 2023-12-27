package ballistix.common.block;

import ballistix.registers.BallistixBlocks;
import electrodynamics.common.block.voxelshapes.VoxelShapeRegistry;
import net.minecraft.block.Block;
import net.minecraft.util.Direction;

public class BallistixVoxelShapesRegistry {
	
	/**
	 * By convention this will be in alphabetical order
	 */
	public static void init() {

		/* MISSILE SILO */

		VoxelShapeRegistry.registerShape(BallistixBlocks.blockMissileSilo, Block.box(0, 0, 0, 16, 1, 16), Direction.SOUTH);

	}

}