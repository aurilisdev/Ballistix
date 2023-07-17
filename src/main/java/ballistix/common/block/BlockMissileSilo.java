package ballistix.common.block;

import ballistix.common.tile.TileMissileSilo;
import electrodynamics.api.multiblock.Subnode;
import electrodynamics.api.multiblock.parent.IMultiblockParentBlock;
import electrodynamics.api.multiblock.parent.IMultiblockParentTile;
import electrodynamics.prefab.block.GenericMachineBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockMissileSilo extends GenericMachineBlock implements IMultiblockParentBlock {

	public BlockMissileSilo() {
		super(TileMissileSilo::new);
	}

	public static final Subnode[] SUBNODES_SOUTH = new Subnode[14];

	public static final Subnode[] SUBNODES_NORTH = new Subnode[14];

	public static final Subnode[] SUBNODES_EAST = new Subnode[14];

	public static final Subnode[] SUBNODES_WEST = new Subnode[14];

	public static final VoxelShape[] SHAPES = new VoxelShape[14];

	static {

		// Hand-coded but required :|
		// And yes if it looks painful its because it was :D

		// 1st layer
		SHAPES[0] = Shapes.or(Block.box(0, 0, 0, 16, 1, 16), Block.box(1, 1, 1, 7, 2, 16), Block.box(7, 1, 1, 16, 2, 7),
				Block.box(7, 2, 15, 9.5, 4, 16), Block.box(8, 2, 14, 10.5, 4, 15), Block.box(9, 2, 13, 11.5, 4, 14), Block.box(10, 2, 12, 12.5, 4, 13), Block.box(11, 2, 11, 13.5, 4, 12), Block.box(12, 2, 10, 14.5, 4, 11), Block.box(13, 2, 9, 15.5, 4, 10), Block.box(14, 2, 8, 16, 4, 9), Block.box(15, 2, 7, 16, 4, 8),
				Block.box(7, 8, 15, 9.5, 10, 16), Block.box(8, 8, 14, 10.5, 10, 15), Block.box(9, 8, 13, 11.5, 10, 14), Block.box(10, 8, 12, 12.5, 10, 13), Block.box(11, 8, 11, 13.5, 10, 12), Block.box(12, 8, 10, 14.5, 10, 11), Block.box(13, 8, 9, 15.5, 10, 10), Block.box(14, 8, 8, 16, 10, 9), Block.box(15, 8, 7, 16, 10, 8),
				Block.box(7, 14, 15, 9.5, 16, 16), Block.box(8, 14, 14, 10.5, 16, 15), Block.box(9, 14, 13, 11.5, 16, 14), Block.box(10, 14, 12, 12.5, 16, 13), Block.box(11, 14, 11, 13.5, 16, 12), Block.box(12, 14, 10, 14.5, 16, 11), Block.box(13, 14, 9, 15.5, 16, 10), Block.box(14, 14, 8, 16, 16, 9), Block.box(15, 14, 7, 16, 16, 8));
		
		SHAPES[1] = Shapes.or(Block.box(0, 0, 0, 16, 1, 16), Block.box(0, 1, 1, 16, 2, 7),
				Block.box(0, 2, 8, 0.5, 4, 9), Block.box(0, 2, 7, 1.5, 4, 8), Block.box(0, 2, 6, 2.5, 4, 7), Block.box(1, 2, 5, 3.5, 4, 6), Block.box(2, 2, 4, 4.5, 4, 5), Block.box(3, 2, 3, 5.5, 4, 4), Block.box(4, 2, 2, 6.5, 4, 3),
				Block.box(0, 8, 8, 0.5, 10, 9), Block.box(0, 8, 7, 1.5, 10, 8), Block.box(0, 8, 6, 2.5, 10, 7), Block.box(1, 8, 5, 3.5, 10, 6), Block.box(2, 8, 4, 4.5, 10, 5), Block.box(3, 8, 3, 5.5, 10, 4), Block.box(4, 8, 2, 6.5, 10, 3),
				Block.box(0, 14, 8, 0.5, 16, 9), Block.box(0, 14, 7, 1.5, 16, 8), Block.box(0, 14, 6, 2.5, 16, 7), Block.box(1, 14, 5, 3.5, 16, 6), Block.box(2, 14, 4, 4.5, 16, 5), Block.box(3, 14, 3, 5.5, 16, 4), Block.box(4, 14, 2, 6.5, 16, 3),
				Block.box(15.8, 2, 8, 16, 4, 9), Block.box(14.8, 2, 7, 16, 4, 8), Block.box(13.8, 2, 6, 16, 4, 7), Block.box(12.8, 2, 5, 15.3, 4, 6), Block.box(11.8, 2, 4, 14.3, 4, 5), Block.box(10.8, 2, 3, 13.3, 4, 4), Block.box(9.8, 2, 2, 12.3, 4, 3), Block.box(8.8, 2, 1, 11.3, 4, 2),
				Block.box(15.8, 8, 8, 16, 10, 9), Block.box(14.8, 8, 7, 16, 10, 8), Block.box(13.8, 8, 6, 16, 10, 7), Block.box(12.8, 8, 5, 15.3, 10, 6), Block.box(11.8, 8, 4, 14.3, 10, 5), Block.box(10.8, 8, 3, 13.3, 10, 4), Block.box(9.8, 8, 2, 12.3, 10, 3), Block.box(8.8, 8, 1, 11.3, 10, 2),
				Block.box(15.8, 14, 8, 16, 16, 9), Block.box(14.8, 14, 7, 16, 16, 8), Block.box(13.8, 14, 6, 16, 16, 7), Block.box(12.8, 14, 5, 15.3, 16, 6), Block.box(11.8, 14, 4, 14.3, 16, 5), Block.box(10.8, 14, 3, 13.3, 16, 4), Block.box(9.8, 14, 2, 12.3, 16, 3), Block.box(8.8, 14, 1, 11.3, 16, 2),
				Block.box(5, 0, 0, 11, 16, 4));
		
		SHAPES[2] = Shapes.or(Block.box(0, 0, 0, 16, 1, 16), Block.box(9, 1, 1, 15, 2, 16), Block.box(0, 1, 1, 9, 2, 7),
				Block.box(6.8, 2, 15, 9.3, 4, 16), Block.box(5.8, 2, 14, 8.3, 4, 15), Block.box(4.8, 2, 13, 7.3, 4, 14), Block.box(3.8, 2, 12, 6.3, 4, 13), Block.box(2.8, 2, 11, 5.3, 4, 12), Block.box(1.8, 2, 10, 4.3, 4, 11), Block.box(0.8, 2, 9, 3.3, 4, 10), Block.box(0, 2, 8, 2.3, 4, 9), Block.box(0, 2, 7, 1.3, 4, 8), Block.box(0, 2, 6, 0.3, 4, 7),
				Block.box(6.8, 8, 15, 9.3, 10, 16), Block.box(5.8, 8, 14, 8.3, 10, 15), Block.box(4.8, 8, 13, 7.3, 10, 14), Block.box(3.8, 8, 12, 6.3, 10, 13), Block.box(2.8, 8, 11, 5.3, 10, 12), Block.box(1.8, 8, 10, 4.3, 10, 11), Block.box(0.8, 8, 9, 3.3, 10, 10), Block.box(0, 8, 8, 2.3, 10, 9), Block.box(0, 8, 7, 1.3, 10, 8), Block.box(0, 8, 6, 0.3, 10, 7),
				Block.box(6.8, 14, 15, 9.3, 16, 16), Block.box(5.8, 14, 14, 8.3, 16, 15), Block.box(4.8, 14, 13, 7.3, 16, 14), Block.box(3.8, 14, 12, 6.3, 16, 13), Block.box(2.8, 14, 11, 5.3, 16, 12), Block.box(1.8, 14, 10, 4.3, 16, 11), Block.box(0.8, 14, 9, 3.3, 16, 10), Block.box(0, 14, 8, 2.3, 16, 9), Block.box(0, 14, 7, 1.3, 16, 8), Block.box(0, 14, 6, 0.3, 16, 7));
		
		SHAPES[3] = Shapes.or(Block.box(0, 0, 0, 16, 1, 16), Block.box(1, 1, 0, 7, 2, 16),
				Block.box(6, 2, 0, 8.5, 4, 1), Block.box(5, 2, 1, 7.5, 4, 2), Block.box(4, 2, 2, 6.5, 4, 3), Block.box(3, 2, 3, 5.5, 4, 4), Block.box(2, 2, 4, 4.5, 4, 5), Block.box(1, 2, 5, 3.5, 4, 6), 
				Block.box(6, 8, 0, 8.5, 10, 1), Block.box(5, 8, 1, 7.5, 10, 2), Block.box(4, 8, 2, 6.5, 10, 3), Block.box(3, 8, 3, 5.5, 10, 4), Block.box(2, 8, 4, 4.5, 10, 5), Block.box(1, 8, 5, 3.5, 10, 6),
				Block.box(6, 14, 0, 8.5, 16, 1), Block.box(5, 14, 1, 7.5, 16, 2), Block.box(4, 14, 2, 6.5, 16, 3), Block.box(3, 14, 3, 5.5, 16, 4), Block.box(2, 14, 4, 4.5, 16, 5), Block.box(1, 14, 5, 3.5, 16, 6),
				Block.box(0, 0, 5, 3, 16, 11), Block.box(3, 0, 5.8, 4, 15.5, 10.2), Block.box(10, 0, 6, 14, 16, 10));
		
		SHAPES[4] = Shapes.or(Block.box(0, 0, 0, 16, 1, 16), Block.box(9, 0, 1, 15, 2, 16),
				Block.box(7.8, 2, 0, 10.3, 4, 1), Block.box(8.8, 2, 1, 11.3, 4, 2), Block.box(9.8, 2, 2, 12.3, 4, 3), Block.box(10.8, 2, 3, 13.3, 4, 4), Block.box(11.8, 2, 4, 14.3, 4, 5), Block.box(12.8, 2, 5, 15.3, 4, 6), 
				Block.box(7.8, 8, 0, 10.3, 10, 1), Block.box(8.8, 8, 1, 11.3, 10, 2), Block.box(9.8, 8, 2, 12.3, 10, 3), Block.box(10.8, 8, 3, 13.3, 10, 4), Block.box(11.8, 8, 4, 14.3, 10, 5), Block.box(12.8, 8, 5, 15.3, 10, 6),
				Block.box(7.8, 14, 0, 10.3, 16, 1), Block.box(8.8, 14, 1, 11.3, 16, 2), Block.box(9.8, 14, 2, 12.3, 16, 3), Block.box(10.8, 14, 3, 13.3, 16, 4), Block.box(11.8, 14, 4, 14.3, 16, 5), Block.box(12.8, 14, 5, 15.3, 16, 6),
				Block.box(13, 0, 5, 16, 16, 11), Block.box(12, 0, 5.8, 13, 15.5, 10.2), Block.box(2, 0, 6, 6, 16, 10));
		
		SHAPES[5] = Shapes.or(Block.box(0, 0, 0, 16, 1, 16), Block.box(1, 1, 0, 7, 2, 15), Block.box(7, 1, 9, 16, 2, 15));
		
		SHAPES[6] = Shapes.or(Block.box(0, 0, 0, 16, 1, 16), Block.box(0, 1, 9, 16, 2, 15), Block.box(2, 1, 6.5, 13, 10, 14));
		
		SHAPES[7] = Shapes.or(Block.box(0, 0, 0, 16, 1, 16), Block.box(9, 1, 1, 15, 2, 15), Block.box(0, 1, 9, 9, 2, 15));

		// 2nd layer
		SHAPES[8] = Shapes.or(Block.box(7, 4, 15, 9.5, 6, 16), Block.box(8, 4, 14, 10.5, 6, 15), Block.box(9, 4, 13, 11.5, 6, 14), Block.box(10, 4, 12, 12.5, 6, 13), Block.box(11, 4, 11, 13.5, 6, 12), Block.box(12, 4, 10, 14.5, 6, 11), Block.box(13, 4, 9, 15.5, 6, 10), Block.box(14, 4, 8, 16, 6, 9), Block.box(15, 4, 7, 16, 6, 8),
				Block.box(7, 10, 15, 9.5, 12, 16), Block.box(8, 10, 14, 10.5, 12, 15), Block.box(9, 10, 13, 11.5, 12, 14), Block.box(10, 10, 12, 12.5, 12, 13), Block.box(11, 10, 11, 13.5, 12, 12), Block.box(12, 10, 10, 14.5, 12, 11), Block.box(13, 10, 9, 15.5, 12, 10), Block.box(14, 10, 8, 16, 12, 9), Block.box(15, 10, 7, 16, 12, 8));
		
		SHAPES[9] = Shapes.or(Block.box(0, 4, 8, 0.5, 6, 9), Block.box(0, 4, 7, 1.5, 6, 8), Block.box(0, 4, 6, 2.5, 6, 7), Block.box(1, 4, 5, 3.5, 6, 6), Block.box(2, 4, 4, 4.5, 6, 5), Block.box(3, 4, 3, 5.5, 6, 4), Block.box(4, 4, 2, 6.5, 6, 3),
				Block.box(0, 10, 8, 0.5, 12, 9), Block.box(0, 10, 7, 1.5, 12, 8), Block.box(0, 10, 6, 2.5, 12, 7), Block.box(1, 10, 5, 3.5, 12, 6), Block.box(2, 10, 4, 4.5, 12, 5), Block.box(3, 10, 3, 5.5, 12, 4), Block.box(4, 10, 2, 6.5, 12, 3),
				Block.box(15.8, 10, 8, 16, 12, 9), Block.box(14.8, 10, 7, 16, 12, 8), Block.box(13.8, 10, 6, 16, 12, 7), Block.box(12.8, 10, 5, 15.3, 12, 6), Block.box(11.8, 10, 4, 14.3, 12, 5), Block.box(10.8, 10, 3, 13.3, 12, 4), Block.box(9.8, 10, 2, 12.3, 12, 3), Block.box(8.8, 10, 1, 11.3, 12, 2),
				Block.box(15.8, 4, 8, 16, 6, 9), Block.box(14.8, 4, 7, 16, 6, 8), Block.box(13.8, 4, 6, 16, 6, 7), Block.box(12.8, 4, 5, 15.3, 6, 6), Block.box(11.8, 4, 4, 14.3, 6, 5), Block.box(10.8, 4, 3, 13.3, 6, 4), Block.box(9.8, 4, 2, 12.3, 6, 3), Block.box(8.8, 4, 1, 11.3, 6, 2),
				Block.box(5, 0, 0, 11, 16, 4));
		
		SHAPES[10] = Shapes.or(Block.box(6.8, 4, 15, 9.3, 6, 16), Block.box(5.8, 4, 14, 8.3, 6, 15), Block.box(4.8, 4, 13, 7.3, 6, 14), Block.box(3.8, 4, 12, 6.3, 6, 13), Block.box(2.8, 4, 11, 5.3, 6, 12), Block.box(1.8, 4, 10, 4.3, 6, 11), Block.box(0.8, 4, 9, 3.3, 6, 10), Block.box(0, 4, 8, 2.3, 6, 9), Block.box(0, 4, 7, 1.3, 6, 8), Block.box(0, 4, 6, 0.3, 6, 7),
				Block.box(6.8, 10, 15, 9.3, 12, 16), Block.box(5.8, 10, 14, 8.3, 12, 15), Block.box(4.8, 10, 13, 7.3, 12, 14), Block.box(3.8, 10, 12, 6.3, 12, 13), Block.box(2.8, 10, 11, 5.3, 12, 12), Block.box(1.8, 10, 10, 4.3, 12, 11), Block.box(0.8, 10, 9, 3.3, 12, 10), Block.box(0, 10, 8, 2.3, 12, 9), Block.box(0, 10, 7, 1.3, 12, 8), Block.box(0, 10, 6, 0.3, 12, 7));
		
		SHAPES[11] = Shapes.or(Block.box(6, 4, 0, 8.5, 6, 1), Block.box(5, 4, 1, 7.5, 6, 2), Block.box(4, 4, 2, 6.5, 6, 3), Block.box(3, 4, 3, 5.5, 6, 4), Block.box(2, 4, 4, 4.5, 6, 5), Block.box(1, 4, 5, 3.5, 6, 6), 
				Block.box(6, 10, 0, 8.5, 12, 1), Block.box(5, 10, 1, 7.5, 12, 2), Block.box(4, 10, 2, 6.5, 12, 3), Block.box(3, 10, 3, 5.5, 12, 4), Block.box(2, 10, 4, 4.5, 12, 5), Block.box(1, 10, 5, 3.5, 12, 6), 
				Block.box(0, 0, 5, 3, 16, 11), Block.box(3, 0, 5.8, 4, 15.5, 10.2), Block.box(10, 0, 6, 14, 7, 10), Block.box(14, 1.3, 7, 16, 2.8, 9));
		
		SHAPES[12] = Shapes.or(Block.box(0, 1.3, 7, 5, 2.8, 9), Block.box(11, 1.3, 7, 16, 2.8, 9));
		
		SHAPES[13] = Shapes.or(Block.box(7.8, 4, 0, 10.3, 6, 1), Block.box(8.8, 4, 1, 11.3, 6, 2), Block.box(9.8, 4, 2, 12.3, 6, 3), Block.box(10.8, 4, 3, 13.3, 6, 4), Block.box(11.8, 4, 4, 14.3, 6, 5), Block.box(12.8, 4, 5, 15.3, 6, 6), 
				Block.box(7.8, 10, 0, 10.3, 12, 1), Block.box(8.8, 10, 1, 11.3, 12, 2), Block.box(9.8, 10, 2, 12.3, 12, 3), Block.box(10.8, 10, 3, 13.3, 12, 4), Block.box(11.8, 10, 4, 14.3, 12, 5), Block.box(12.8, 10, 5, 15.3, 12, 6), 
				Block.box(13, 0, 5, 16, 16, 11), Block.box(12, 0, 5.8, 13, 15.5, 10.2), Block.box(2, 0, 6, 6, 7, 10), Block.box(0, 1.3, 7, 2, 2.8, 9));

		/* SOUTH */

		// 1st layer
		SUBNODES_SOUTH[0] = new Subnode(new BlockPos(-1, 0, -1), SHAPES[0]);
		SUBNODES_SOUTH[1] = new Subnode(new BlockPos(0, 0, -1), SHAPES[1]);
		SUBNODES_SOUTH[2] = new Subnode(new BlockPos(1, 0, -1), SHAPES[2]);
		SUBNODES_SOUTH[3] = new Subnode(new BlockPos(-1, 0, 0), SHAPES[3]);
		SUBNODES_SOUTH[4] = new Subnode(new BlockPos(1, 0, 0), SHAPES[4]);
		SUBNODES_SOUTH[5] = new Subnode(new BlockPos(-1, 0, 1), SHAPES[5]);
		SUBNODES_SOUTH[6] = new Subnode(new BlockPos(0, 0, 1), SHAPES[6]);
		SUBNODES_SOUTH[7] = new Subnode(new BlockPos(1, 0, 1), SHAPES[7]);

		// 2nd layer
		SUBNODES_SOUTH[8] = new Subnode(new BlockPos(-1, 1, -1), SHAPES[8]);
		SUBNODES_SOUTH[9] = new Subnode(new BlockPos(0, 1, -1), SHAPES[9]);
		SUBNODES_SOUTH[10] = new Subnode(new BlockPos(1, 1, -1), SHAPES[10]);
		SUBNODES_SOUTH[11] = new Subnode(new BlockPos(-1, 1, 0), SHAPES[11]);
		SUBNODES_SOUTH[12] = new Subnode(new BlockPos(0, 1, 0), SHAPES[12]);
		SUBNODES_SOUTH[13] = new Subnode(new BlockPos(1, 1, 0), SHAPES[13]);

		/* NORTH */

		// 1st layer
		SUBNODES_NORTH[0] = new Subnode(new BlockPos(-1, 0, -1), rotate(Direction.NORTH, SHAPES[7]));
		SUBNODES_NORTH[1] = new Subnode(new BlockPos(0, 0, -1), rotate(Direction.NORTH, SHAPES[6]));
		SUBNODES_NORTH[2] = new Subnode(new BlockPos(1, 0, -1), rotate(Direction.NORTH, SHAPES[5]));
		SUBNODES_NORTH[3] = new Subnode(new BlockPos(-1, 0, 0), rotate(Direction.NORTH, SHAPES[4]));
		SUBNODES_NORTH[4] = new Subnode(new BlockPos(1, 0, 0), rotate(Direction.NORTH, SHAPES[3]));
		SUBNODES_NORTH[5] = new Subnode(new BlockPos(-1, 0, 1), rotate(Direction.NORTH, SHAPES[2]));
		SUBNODES_NORTH[6] = new Subnode(new BlockPos(0, 0, 1), rotate(Direction.NORTH, SHAPES[1]));
		SUBNODES_NORTH[7] = new Subnode(new BlockPos(1, 0, 1), rotate(Direction.NORTH, SHAPES[0]));

		// 2nd layer
		SUBNODES_NORTH[8] = new Subnode(new BlockPos(-1, 1, 0), rotate(Direction.NORTH, SHAPES[13]));
		SUBNODES_NORTH[9] = new Subnode(new BlockPos(0, 1, 0), rotate(Direction.NORTH, SHAPES[12]));
		SUBNODES_NORTH[10] = new Subnode(new BlockPos(1, 1, 0), rotate(Direction.NORTH, SHAPES[11]));
		SUBNODES_NORTH[11] = new Subnode(new BlockPos(-1, 1, 1), rotate(Direction.NORTH, SHAPES[10]));
		SUBNODES_NORTH[12] = new Subnode(new BlockPos(0, 1, 1), rotate(Direction.NORTH, SHAPES[9]));
		SUBNODES_NORTH[13] = new Subnode(new BlockPos(1, 1, 1), rotate(Direction.NORTH, SHAPES[8]));

		/* EAST */

		// 1st layer
		SUBNODES_EAST[0] = new Subnode(new BlockPos(-1, 0, -1), rotate(Direction.EAST, SHAPES[2]));
		SUBNODES_EAST[1] = new Subnode(new BlockPos(-1, 0, 0), rotate(Direction.EAST, SHAPES[1]));
		SUBNODES_EAST[2] = new Subnode(new BlockPos(-1, 0, 1), rotate(Direction.EAST, SHAPES[0]));
		SUBNODES_EAST[3] = new Subnode(new BlockPos(0, 0, -1), rotate(Direction.EAST, SHAPES[4]));
		SUBNODES_EAST[4] = new Subnode(new BlockPos(0, 0, 1), rotate(Direction.EAST, SHAPES[3]));
		SUBNODES_EAST[5] = new Subnode(new BlockPos(1, 0, -1), rotate(Direction.EAST, SHAPES[7]));
		SUBNODES_EAST[6] = new Subnode(new BlockPos(1, 0, 0), rotate(Direction.EAST, SHAPES[6]));
		SUBNODES_EAST[7] = new Subnode(new BlockPos(1, 0, 1), rotate(Direction.EAST, SHAPES[5]));

		// 2nd layer
		SUBNODES_EAST[8] = new Subnode(new BlockPos(-1, 1, -1), rotate(Direction.EAST, SHAPES[10]));
		SUBNODES_EAST[9] = new Subnode(new BlockPos(-1, 1, 0), rotate(Direction.EAST, SHAPES[9]));
		SUBNODES_EAST[10] = new Subnode(new BlockPos(-1, 1, 1), rotate(Direction.EAST, SHAPES[8]));
		SUBNODES_EAST[11] = new Subnode(new BlockPos(0, 1, -1), rotate(Direction.EAST, SHAPES[13]));
		SUBNODES_EAST[12] = new Subnode(new BlockPos(0, 1, 0), rotate(Direction.EAST, SHAPES[12]));
		SUBNODES_EAST[13] = new Subnode(new BlockPos(0, 1, 1), rotate(Direction.EAST, SHAPES[11]));

		/* WEST */

		// 1st layer
		SUBNODES_WEST[0] = new Subnode(new BlockPos(-1, 0, -1), rotate(Direction.WEST, SHAPES[5]));
		SUBNODES_WEST[1] = new Subnode(new BlockPos(-1, 0, 0), rotate(Direction.WEST, SHAPES[6]));
		SUBNODES_WEST[2] = new Subnode(new BlockPos(-1, 0, 1), rotate(Direction.WEST, SHAPES[7]));
		SUBNODES_WEST[3] = new Subnode(new BlockPos(0, 0, -1), rotate(Direction.WEST, SHAPES[3]));
		SUBNODES_WEST[4] = new Subnode(new BlockPos(0, 0, 1), rotate(Direction.WEST, SHAPES[4]));
		SUBNODES_WEST[5] = new Subnode(new BlockPos(1, 0, -1), rotate(Direction.WEST, SHAPES[0]));
		SUBNODES_WEST[6] = new Subnode(new BlockPos(1, 0, 0), rotate(Direction.WEST, SHAPES[1]));
		SUBNODES_WEST[7] = new Subnode(new BlockPos(1, 0, 1), rotate(Direction.WEST, SHAPES[2]));

		// 2nd layer
		SUBNODES_WEST[8] = new Subnode(new BlockPos(0, 1, -1), rotate(Direction.WEST, SHAPES[11]));
		SUBNODES_WEST[9] = new Subnode(new BlockPos(0, 1, 0), rotate(Direction.WEST, SHAPES[12]));
		SUBNODES_WEST[10] = new Subnode(new BlockPos(0, 1, 1), rotate(Direction.WEST, SHAPES[13]));
		SUBNODES_WEST[11] = new Subnode(new BlockPos(1, 1, -1), rotate(Direction.WEST, SHAPES[8]));
		SUBNODES_WEST[12] = new Subnode(new BlockPos(1, 1, 0), rotate(Direction.WEST, SHAPES[9]));
		SUBNODES_WEST[13] = new Subnode(new BlockPos(1, 1, 1), rotate(Direction.WEST, SHAPES[10]));

	}

	private static VoxelShape rotate(Direction to, VoxelShape shape) {
		VoxelShape[] buffer = new VoxelShape[] { shape, Shapes.empty() };

		int times = (to.get2DDataValue() - Direction.SOUTH.get2DDataValue() + 4) % 4;
		for (int i = 0; i < times; i++) {
			buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = Shapes.or(buffer[1], Shapes.create(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)));
			buffer[0] = buffer[1];
			buffer[1] = Shapes.empty();
		}

		return buffer[0];
	}

	@Override
	public boolean hasMultiBlock() {
		return true;
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {

		Subnode[] nodes = switch (state.getValue(GenericMachineBlock.FACING)) {
		case EAST -> SUBNODES_EAST;
		case WEST -> SUBNODES_WEST;
		case NORTH -> SUBNODES_NORTH;
		case SOUTH -> SUBNODES_SOUTH;
		default -> SUBNODES_SOUTH;

		};

		return isValidMultiblockPlacement(state, worldIn, pos, nodes);
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
}
