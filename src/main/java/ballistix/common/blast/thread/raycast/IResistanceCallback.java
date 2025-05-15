package ballistix.common.blast.thread.raycast;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IResistanceCallback {
	float getResistance(Level world, BlockPos position, BlockPos targetPosition, Entity source, BlockState block);
}
