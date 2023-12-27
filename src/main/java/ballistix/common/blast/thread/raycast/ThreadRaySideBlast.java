package ballistix.common.blast.thread.raycast;

import java.util.Random;

import electrodynamics.prefab.block.HashDistanceBlockPos;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;

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
		World world = mainBlast.level;
		int iMin = -explosionRadius, iMax = explosionRadius, jMax = explosionRadius, jMin = -explosionRadius;
		Vector3i orientation = direction.getNormal();
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
				Vector3d delta = new Vector3d(x, y, z).normalize();
				float power = mainBlast.explosionEnergy - mainBlast.explosionEnergy * new Random().nextFloat() / 2;
				Vector3d currentVector = new Vector3d(position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5);
				BlockPos currentBlockPos = new BlockPos(currentVector);
				for (float d = 0.3F; power > 0f; power -= d * 0.75F * 5) {
					BlockPos next = new BlockPos(currentVector);
					if (!next.equals(currentBlockPos)) {
						currentBlockPos = next;
						BlockState block = world.getBlockState(currentBlockPos);
						if (block != Blocks.AIR.defaultBlockState() && block != Blocks.CAVE_AIR.defaultBlockState() && block != Blocks.VOID_AIR.defaultBlockState()) {
							if (block.getDestroySpeed(world, currentBlockPos) >= 0) {
								power -= Math.max(1, mainBlast.callBack.getResistance(world, position, currentBlockPos, mainBlast.explosionSource, block));
								if (power > 0f) {
									int idistancesq = (int) (Math.pow(currentBlockPos.getX() - position.getX(), 2) + Math.pow(currentBlockPos.getY() - position.getY(), 2) + Math.pow(currentBlockPos.getZ() - position.getZ(), 2));
									synchronized (mainBlast.resultsSync) {
										mainBlast.resultsSync.add(new HashDistanceBlockPos(currentBlockPos.getX(), currentBlockPos.getY(), currentBlockPos.getZ(), idistancesq));
									}
								}
							} else {
								power = 0;
							}
						}
					}
					currentVector = new Vector3d(currentVector.x + delta.x, currentVector.y + delta.y, currentVector.z + delta.z);
				}
			}
		}
		mainBlast.underBlasts.remove(this);
	}
}