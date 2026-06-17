package ballistix.common.block;

import java.util.stream.Stream;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import voltaic.common.block.voxelshapes.VoxelShapeProvider;

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

    public static final VoxelShapeProvider AIR_RAID_SIREN = VoxelShapeProvider.createDirectional(
	    ///
	    Direction.NORTH,
	    //
	    Stream.of(
		    //
		    Stream.of(
			    //
			    Block.box(2, 0, 2, 14, 1, 14),
			    //
			    Block.box(4, 1, 4, 12, 3, 12),
			    //
			    Block.box(5, 3, 5, 11, 6, 11),
			    //
			    Block.box(4, 6, 5, 12, 7, 11),
			    //
			    Block.box(4, 7, 4, 12, 13, 12),
			    //
			    Block.box(4, 13, 5, 12, 14, 11)
		    //
		    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
		    //
		    Stream.of(
			    //
			    Block.box(1, 5, 3, 4, 15, 13),
			    //
			    Block.box(0, 6, 13, 4, 14, 14),
			    //
			    Block.box(0, 6, 2, 4, 14, 3),
			    //
			    Block.box(0, 15, 4, 4, 16, 12),
			    //
			    Block.box(0, 4, 4, 4, 5, 12),
			    //
			    Block.box(0, 5, 3, 1, 15, 4),
			    //
			    Block.box(0, 5, 12, 1, 15, 13),
			    //
			    Block.box(0, 5, 4, 1, 6, 12),
			    //
			    Block.box(0, 14, 4, 1, 15, 12),
			    //
			    Block.box(0, 6, 4, 1, 7, 5),
			    //
			    Block.box(0, 13, 4, 1, 14, 5),
			    //
			    Block.box(0, 13, 11, 1, 14, 12),
			    //
			    Block.box(0, 6, 11, 1, 7, 12)
		    //
		    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
		    //
		    Stream.of(
			    //
			    Block.box(12, 5, 3, 15, 15, 13),
			    //
			    Block.box(12, 6, 13, 16, 14, 14),
			    //
			    Block.box(12, 6, 2, 16, 14, 3),
			    //
			    Block.box(12, 15, 4, 16, 16, 12),
			    //
			    Block.box(12, 4, 4, 16, 5, 12),
			    //
			    Block.box(15, 5, 3, 16, 15, 4),
			    //
			    Block.box(15, 5, 12, 16, 15, 13),
			    //
			    Block.box(15, 5, 4, 16, 6, 12),
			    //
			    Block.box(15, 14, 4, 16, 15, 12),
			    //
			    Block.box(15, 6, 11, 16, 7, 12),
			    //
			    Block.box(15, 13, 11, 16, 14, 12),
			    //
			    Block.box(15, 13, 4, 16, 14, 5),
			    //
			    Block.box(15, 6, 4, 16, 7, 5)
		    //
		    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get()
	    //
	    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get());
}