package ballistix.common.block;

import ballistix.registers.BallistixBlocks;
import electrodynamics.common.block.voxelshapes.VoxelShapes;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;

public class BallistixVoxelShapesRegistry {
	
	/**
	 * By convention this will be in alphabetical order
	 */
	public static void init() {
		
		/* MISSILE SILO */
		
		VoxelShapes.registerShape(BallistixBlocks.blockMissileSilo, Block.box(0, 0, 0, 16, 1, 16), Direction.SOUTH);
		
	}

}
