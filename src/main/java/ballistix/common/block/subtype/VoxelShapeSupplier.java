package ballistix.common.block.subtype;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

@FunctionalInterface
public interface VoxelShapeSupplier {
    VoxelShape supply(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context);
}