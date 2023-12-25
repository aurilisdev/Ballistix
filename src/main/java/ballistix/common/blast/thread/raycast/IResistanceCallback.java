package ballistix.common.blast.thread.raycast;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IResistanceCallback {
	float getResistance(World world, BlockPos position, BlockPos targetPosition, Entity source, BlockState block);
}