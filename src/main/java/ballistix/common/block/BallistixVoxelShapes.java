package ballistix.common.block;

import java.util.stream.Stream;

import electrodynamics.common.block.voxelshapes.VoxelShapeProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;

public class BallistixVoxelShapes {

	public static void init() {

	}

	public static final VoxelShapeProvider LAUNCHER_CONTROL_PANEL_TIER1 = VoxelShapeProvider.createDirectional(
			//
			Direction.NORTH,
			//
			Stream.of(
					//
					Block.box(4, 0, 4, 12, 1, 12),
					//
					Block.box(6, 0, 6.75, 10, 13.25, 8.75),
					//
					Block.box(2.5, 7.5, 4.6, 13.5, 16.5, 9.6),
					//
					Block.box(4, 0, 12, 12, 1, 16)
			//
			).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get()
	//
	);

	public static final VoxelShapeProvider LAUNCHER_CONTROL_PANEL_TIER2 = VoxelShapeProvider.createDirectional(
			//
			Direction.NORTH,
			//
			Stream.of(
					//
					Block.box(4, 0, 4, 12, 1, 12),
					//
					Block.box(4, 0, 6.5, 12, 7.5, 11.5),
					//
					Block.box(2.5, 7.65, 4.6, 13.5, 16.65, 9.6),
					//
					Block.box(4, 0, 12, 12, 1, 16)
			//
			).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get()
//
	);

	public static final VoxelShapeProvider LAUNCHER_CONTROL_PANEL_TIER3 = VoxelShapeProvider.createDirectional(
			//
			Direction.NORTH,
			//
			Stream.of(
					//
					Block.box(4, 0, 4, 12, 1, 12),
					//
					Block.box(4, 0, 5.325, 11.975, 7.525, 11.475),
					//
					//
					Block.box(2.5, 7.65, 4.6, 13.5, 16.65, 9.6),
					//
					Block.box(4, 0, 12, 12, 1, 16)
			//
			).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get()
//
	);

	public static final VoxelShapeProvider LAUNCHER_PLATFORM_TIER1 = VoxelShapeProvider.createDirectional(
			//
			Direction.NORTH,
			//
			Stream.of(
					//
					Block.box(1, 5, 7, 2, 16, 9),
					//
					Block.box(2, 5, 6, 3, 16, 10),
					//
					Block.box(13, 5, 6, 14, 16, 10),
					//
					Block.box(14, 5, 7, 15, 16, 9),
					//
					Block.box(0, 0, 3, 16, 1, 13),
					//
					Block.box(3, 1, 3, 13, 2, 13),
					//
					Block.box(13, 1, 5, 16, 5, 11),
					//
					Block.box(0, 1, 5, 3, 5, 11),
					//
					Block.box(3, 0.025, 0, 13, 1.025, 16)
			//
			).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get()
//
	);

	public static final VoxelShapeProvider LAUNCHER_PLATFORM_TIER2 = VoxelShapeProvider.createDirectional(
			//
			Direction.NORTH,
			//
			Stream.of(
					//
					Block.box(0, 5, 7, 1, 16, 9),
					//
					Block.box(1, 5, 6, 2, 16, 10),
					//
					Block.box(14, 5, 6, 15, 16, 10),
					//
					Block.box(15, 5, 7, 16, 16, 9),
					//
					Block.box(0, 0, 3, 16, 1, 13),
					//
					Block.box(3, 1, 3, 13, 2, 13),
					//
					//
					Block.box(14, 1, 5, 16, 5, 11),
					//
					Block.box(0, 1, 5, 2, 5, 11),
					//
					Block.box(3, 0.025, 0, 13, 1.025, 16)
			//
			).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get()
//
	);

	public static final VoxelShapeProvider LAUNCHER_PLATFORM_TIER3 = LAUNCHER_PLATFORM_TIER2;

	public static final VoxelShapeProvider LAUNCHER_SUPPORTFRAME_TIER1 = VoxelShapeProvider.createDirectional(
			//
			Direction.NORTH,
			//
			Shapes.join(Block.box(4, 0, 8, 12, 2, 16), Block.box(5, 2, 9, 11, 16, 15), BooleanOp.OR)
//
	);

	public static final VoxelShapeProvider LAUNCHER_SUPPORTFRAME_TIER2 = LAUNCHER_SUPPORTFRAME_TIER1;

	public static final VoxelShapeProvider LAUNCHER_SUPPORTFRAME_TIER3 = VoxelShapeProvider.createDirectional(
			//
			Direction.NORTH,
			//
			Stream.of(
					//
					Block.box(5, 2, 9, 11, 16, 15),
					//
					Block.box(4, 0, 8, 12, 2, 16),
					//
					Block.box(4.75, 15, 8.75, 11.25, 16, 15.25),
					//
					Block.box(4.75, 10, 8.75, 11.25, 11, 15.25),
					//
					Block.box(4.75, 5, 8.75, 11.25, 6, 15.25)
			//
			).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get()
//
	);

	public static final VoxelShapeProvider RADAR = VoxelShapeProvider.createDirectional(
			//
			Direction.NORTH,
			//
			Stream.of(
					//
					Block.box(0, 0, 0, 16, 5, 16),
					//
					Block.box(4, 5, 3.5, 12, 10, 12.5),
					//
					Block.box(6, 10, 6, 10, 11, 10)
			//
			).reduce(Shapes::or).get()
//
	);

	public static final VoxelShapeProvider FIRE_CONTROL_RADAR = VoxelShapeProvider.createDirectional(
			//
			Direction.NORTH,
			//
			Stream.of(
					//
					Block.box(0, 0, 0, 16, 5, 16),
					//
					Block.box(4, 5, 3.5, 12, 10, 12.5),
					//
					Block.box(6, 10, 6, 10, 11, 10)
			//
			).reduce(Shapes::or).get()
//
	);

	public static final VoxelShapeProvider ESM_TOWER = VoxelShapeProvider.createOmni(
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
			).reduce(Shapes::or).get()
//
	);

	public static final VoxelShapeProvider SAM_TURRET = VoxelShapeProvider.createOmni(
			//
			Shapes.or(Block.box(0, 0, 0, 16, 5, 16), Block.box(2.5, 5, 2.5, 13.5, 6, 13.5))
//
	);

	public static final VoxelShapeProvider CIWS_TURRET = VoxelShapeProvider.createOmni(
			//
			Shapes.or(Block.box(0, 0, 0, 16, 5, 16), Block.box(2.5, 5, 2.5, 13.5, 6, 13.5))
//
	);

	public static final VoxelShapeProvider LASER_TURRET = VoxelShapeProvider.createOmni(
			//
			Shapes.or(Block.box(0, 0, 0, 16, 5, 16), Block.box(2.5, 5, 2.5, 13.5, 6, 13.5))
//
	);

	public static final VoxelShapeProvider RAILGUN_TURRET = VoxelShapeProvider.createOmni(
			//
			Shapes.or(Block.box(0, 0, 0, 16, 5, 16), Block.box(2.5, 5, 2.5, 13.5, 6, 13.5))
	//
	);
}