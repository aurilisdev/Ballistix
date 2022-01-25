package ballistix.common.blast.thread.raycast;

import electrodynamics.prefab.block.HashDistanceBlockPos;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class ThreadRaySideBlast extends Thread {

	public ThreadRaycastBlast mainBlast;

	public Direction direction;

	public ThreadRaySideBlast(ThreadRaycastBlast threadRaycastBlast, Direction dir) {
		mainBlast = threadRaycastBlast;
		direction = dir;
		setName("Raycast Blast Side Thread");
	}

	@Override
	@SuppressWarnings("java:S2184")
	public void run() {
		int explosionRadius = mainBlast.explosionRadius;
		BlockPos position = mainBlast.position;
		Level world = mainBlast.level;
		int iMin = -explosionRadius, iMax = explosionRadius, jMax = explosionRadius, jMin = -explosionRadius;
		Vec3i orientation = direction.getNormal();
		for (int i = iMin; i < iMax; i++) {
			for (int j = jMin; j < jMax; j++) {
				int x = 0, y = 0, z = 0;
				if (orientation.getX() != 0) {
					x = orientation.getX() * explosionRadius;
					y += i;
					z += j;
				} else if (orientation.getY() != 0) {
					x += i;
					y = orientation.getY() * explosionRadius;
					z += j;
				} else if (orientation.getZ() != 0) {
					x += i;
					y += j;
					z = orientation.getZ() * explosionRadius;
				}
				Vec3 delta = new Vec3(x, y, z).normalize();
				float power = mainBlast.explosionEnergy - mainBlast.explosionEnergy * world.random.nextFloat() / 2;
				Vec3 currentVector = new Vec3(position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5);
				BlockPos currentBlockPos = new BlockPos(currentVector);
				for (float d = 0.3F; power > 0f; power -= d * 0.75F * 5) {
					BlockPos next = new BlockPos(currentVector);
					if (!next.equals(currentBlockPos)) {
						currentBlockPos = next;
						BlockState block = world.getBlockState(currentBlockPos);
						if (block != Blocks.AIR.defaultBlockState() && block != Blocks.CAVE_AIR.defaultBlockState() && block != Blocks.VOID_AIR.defaultBlockState() && block.getDestroySpeed(world, currentBlockPos) >= 0) {
							power -= Math.max(1, mainBlast.callBack.getResistance(world, position, currentBlockPos, mainBlast.explosionSource, block));
							if (power > 0f) {
								int idistancesq = (int) (Math.pow(currentBlockPos.getX() - position.getX(), 2) + Math.pow(currentBlockPos.getY() - position.getY(), 2) + Math.pow(currentBlockPos.getZ() - position.getZ(), 2));
								synchronized (mainBlast.resultsSync) {
									mainBlast.resultsSync.add(new HashDistanceBlockPos(currentBlockPos.getX(), currentBlockPos.getY(), currentBlockPos.getZ(), idistancesq));
								}
							}
						}
					}
					currentVector = new Vec3(currentVector.x + delta.x, currentVector.y + delta.y, currentVector.z + delta.z);
				}
			}
		}
		mainBlast.underBlasts.remove(this);
	}
}