package ballistix.common.blast.thread.raycast;

import ballistix.common.blast.thread.HashDistanceBlockPos;
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
		setPriority(MAX_PRIORITY);
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
				Vec3 t = new Vec3(position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5);
				BlockPos tt = new BlockPos(t);
				for (float d = 0.3F; power > 0f; power -= d * 0.75F * 5) {
					double distancesq = Math.pow(t.x() - position.getX(), 2) + Math.pow(t.y() - position.getY(), 2)
							+ Math.pow(t.z() - position.getZ(), 2);
					if (distancesq > explosionRadius * explosionRadius) {
						break;
					}
					BlockPos next = new BlockPos(t);
					if (!next.equals(tt)) {
						tt = next;
						BlockState block = world.getBlockState(tt);
						if (block != Blocks.AIR.defaultBlockState() && block != Blocks.VOID_AIR.defaultBlockState()
								&& block.getDestroySpeed(world, tt) >= 0) {
							power -= mainBlast.callBack.getResistance(world, position, tt, mainBlast.explosionSource, block);
							if (power > 0f) {
								int idistancesq = (int) (Math.pow(tt.getX() - position.getX(), 2) + Math.pow(tt.getY() - position.getY(), 2)
										+ Math.pow(tt.getZ() - position.getZ(), 2));
								mainBlast.results.add(new HashDistanceBlockPos(tt.getX(), tt.getY(), tt.getZ(), idistancesq));
							}
						}
					}
					t = new Vec3(t.x + delta.x, t.y + delta.y, t.z + delta.z);
				}
			}
		}
		mainBlast.underBlasts.remove(this);
	}
}