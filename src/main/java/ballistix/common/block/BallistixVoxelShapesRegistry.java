package ballistix.common.block;

import java.util.stream.Stream;

import ballistix.registers.BallistixBlocks;
import electrodynamics.common.block.voxelshapes.VoxelShapeRegistry;
import net.minecraft.block.Block;
import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.VoxelShapes;

public class BallistixVoxelShapesRegistry {
	
	/**
	 * By convention this will be in alphabetical order
	 */
	public static void init() {

		/* MISSILE SILO */

		VoxelShapeRegistry.registerShape(BallistixBlocks.blockMissileSilo, Block.box(0, 0, 0, 16, 1, 16), Direction.SOUTH);
		
		VoxelShapeRegistry.registerShape(BallistixBlocks.blockRadar, 
	            //
	            Stream.of(
	                    //
	                    Block.box(0, 0, 0, 16, 5, 16),
	                    //
	                    Block.box(4, 5, 3.5, 12, 10, 12.5),
	                    //
	                    Block.box(6, 10, 6, 10, 11, 10)
	                    //
	            ).reduce(VoxelShapes::or).get(),
	            //
	            Direction.NORTH
	    );

		VoxelShapeRegistry.registerShape(BallistixBlocks.blockFireControlRadar, 
	            //
	            Stream.of(
	                    //
	                    Block.box(0, 0, 0, 16, 5, 16),
	                    //
	                    Block.box(4, 5, 3.5, 12, 10, 12.5),
	                    //
	                    Block.box(6, 10, 6, 10, 11, 10)
	                    //
	            ).reduce(VoxelShapes::or).get(),
	            //
	            Direction.NORTH
	    );

		VoxelShapeRegistry.registerShape(BallistixBlocks.blockEsmTower, 
	            //
	            Stream.of(
	                    //
	                    Block.box(0, 0, 0, 16, 1, 1),
	                    //
	                    Block.box(0, 0, 15, 16, 1, 16),
	                    //
	                    Block.box(15, 0, 1, 16, 1, 15),
	                    //
	                    Block.box(4, 0, 4, 12, 1, 12),
	                    //
	                    Block.box(0, 0, 1, 1, 1, 15),
	                    //
	                    Block.box(0, 1, 0, 16, 2, 16),
	                    //
	                    Block.box(2, 2, 2, 14, 3, 14),
	                    //
	                    Block.box(3, 3, 3, 13, 7, 13),
	                    //
	                    Block.box(6, 7, 6, 10, 16, 10)
	                    //
	            ).reduce(VoxelShapes::or).get(),
	            //
	            Direction.SOUTH
	    );

		VoxelShapeRegistry.registerShape(BallistixBlocks.blockSamTurret, 
	            //
	            VoxelShapes.or(Block.box(0, 0, 0, 16, 5, 16), Block.box(2.5, 5, 2.5, 13.5, 6, 13.5)),
	            //
	            Direction.SOUTH
	    );

		VoxelShapeRegistry.registerShape(BallistixBlocks.blockCiwsTurret, 
	            //
	            VoxelShapes.or(Block.box(0, 0, 0, 16, 5, 16), Block.box(2.5, 5, 2.5, 13.5, 6, 13.5)),
	            //
	            Direction.SOUTH
	    );

		VoxelShapeRegistry.registerShape(BallistixBlocks.blockLaserTurret, 
	            //
	            VoxelShapes.or(Block.box(0, 0, 0, 16, 5, 16), Block.box(2.5, 5, 2.5, 13.5, 6, 13.5)),
	            //
	            Direction.SOUTH
	    );

		VoxelShapeRegistry.registerShape(BallistixBlocks.blockRailgunTurret, 
	            //
	            VoxelShapes.or(Block.box(0, 0, 0, 16, 5, 16), Block.box(2.5, 5, 2.5, 13.5, 6, 13.5)),
	            //
	            Direction.SOUTH
	    );

	}

}