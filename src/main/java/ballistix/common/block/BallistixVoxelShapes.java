package ballistix.common.block;

import java.util.stream.Stream;

import electrodynamics.common.block.voxelshapes.VoxelShapeProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;

public class BallistixVoxelShapes {

    public static void init() {

    }

    public static final VoxelShapeProvider LAUNCHER_CONTROL_PANEL = VoxelShapeProvider.createOmni(Block.box(0, 0, 0, 16, 16, 16));

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
