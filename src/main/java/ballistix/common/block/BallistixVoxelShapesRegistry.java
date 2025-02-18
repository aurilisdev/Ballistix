package ballistix.common.block;

import java.util.stream.Stream;

import ballistix.registers.BallistixBlocks;
import electrodynamics.common.block.voxelshapes.VoxelShapes;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;

public class BallistixVoxelShapesRegistry {

	/**
	 * By convention this will be in alphabetical order
	 */
	public static void init() {

		/* MISSILE SILO */

		VoxelShapes.registerShape(BallistixBlocks.blockMissileSilo, Block.box(0, 0, 0, 16, 1, 16), Direction.SOUTH);
		
		VoxelShapes.registerShape(BallistixBlocks.blockRadar, 
	            //
	            Stream.of(
	                    //
	                    Block.box(0, 0, 0, 16, 5, 16),
	                    //
	                    Block.box(4, 5, 3.5, 12, 10, 12.5),
	                    //
	                    Block.box(6, 10, 6, 10, 11, 10)
	                    //
	            ).reduce(Shapes::or).get(),
	            //
	            Direction.NORTH
	    );

		VoxelShapes.registerShape(BallistixBlocks.blockFireControlRadar, 
	            //
	            Stream.of(
	                    //
	                    Block.box(0, 0, 0, 16, 5, 16),
	                    //
	                    Block.box(4, 5, 3.5, 12, 10, 12.5),
	                    //
	                    Block.box(6, 10, 6, 10, 11, 10)
	                    //
	            ).reduce(Shapes::or).get(),
	            //
	            Direction.NORTH
	    );

		VoxelShapes.registerShape(BallistixBlocks.blockEsmTower, 
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
	            ).reduce(Shapes::or).get(),
	            //
	            Direction.SOUTH
	    );

		VoxelShapes.registerShape(BallistixBlocks.blockSamTurret, 
	            //
	            Shapes.or(Block.box(0, 0, 0, 16, 5, 16), Block.box(2.5, 5, 2.5, 13.5, 6, 13.5)),
	            //
	            Direction.SOUTH
	    );

		VoxelShapes.registerShape(BallistixBlocks.blockCiwsTurret, 
	            //
	            Shapes.or(Block.box(0, 0, 0, 16, 5, 16), Block.box(2.5, 5, 2.5, 13.5, 6, 13.5)),
	            //
	            Direction.SOUTH
	    );

		VoxelShapes.registerShape(BallistixBlocks.blockLaserTurret, 
	            //
	            Shapes.or(Block.box(0, 0, 0, 16, 5, 16), Block.box(2.5, 5, 2.5, 13.5, 6, 13.5)),
	            //
	            Direction.SOUTH
	    );

		VoxelShapes.registerShape(BallistixBlocks.blockRailgunTurret, 
	            //
	            Shapes.or(Block.box(0, 0, 0, 16, 5, 16), Block.box(2.5, 5, 2.5, 13.5, 6, 13.5)),
	            //
	            Direction.SOUTH
	    );

	}

}
