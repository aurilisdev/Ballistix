package ballistix.common.blast.util.thread.raycast;

import java.util.HashSet;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import voltaic.prefab.block.HashDistanceBlockPos;

public class ThreadRaySideBlast extends Thread {

	public final ThreadRaycastBlast mainBlast;

	public final Direction direction;
	private final RandomSource random = RandomSource.createThreadSafe();

	private static final float DEFAULT_POWER_DEC = 1.125f;

	public ThreadRaySideBlast(ThreadRaycastBlast threadRaycastBlast, Direction dir) {
		mainBlast = threadRaycastBlast;
		direction = dir;
		setName("Raycast Blast Side Thread");
		setPriority(MAX_PRIORITY);
	}

	@Override
	public void run() {
		final int explosionRadius = mainBlast.explosionRadius;
		final BlockPos position = mainBlast.position;
		final Level world = mainBlast.level;
		final int iMin = -explosionRadius, iMax = explosionRadius, jMax = explosionRadius, jMin = -explosionRadius;
		final Vec3i orientation = direction.getNormal();
		final float explosionEnergy = mainBlast.explosionEnergy;
		final IResistanceCallback callback = mainBlast.callBack;
		final Entity explosionSource = mainBlast.explosionSource;

		final boolean xNotZero = orientation.getX() != 0;
		final boolean yNotZero = orientation.getY() != 0;
		final boolean zNotZero = orientation.getZ() != 0;

		final int expX = orientation.getX() * explosionRadius;
		final int expY = orientation.getY() * explosionRadius;
		final int expZ = orientation.getZ() * explosionRadius;
		HashSet<HashDistanceBlockPos> toadd = new HashSet<>();

		for (int i = iMin; i < iMax; i++) {
			for (int j = jMin; j < jMax; j++) {

				int x = 0, y = 0, z = 0;

				if (xNotZero) {
					x = expX;
					y += i;
					z += j;
				} else if (yNotZero) {
					x += i;
					y = expY;
					z += j;
				} else if (zNotZero) {
					x += i;
					y += j;
					z = expZ;
				}

				float power = explosionEnergy - explosionEnergy * random.nextFloat() / 2;

				BlockPos currentBlockPos = new BlockPos(position);

				float currentX = position.getX() + 0.5F;
				float currentY = position.getY() + 0.5F;
				float currentZ = position.getZ() + 0.5F;

				float len = Mth.sqrt(x * x + y * y + z * z); // from net.minecraft.util.Mth
				float invLen = len == 0.0F ? 0.0F : 1.0F / len;
				float dx = x * invLen;
				float dy = y * invLen;
				float dz = z * invLen;

				while (power > 0.0F) {
					BlockPos next = new BlockPos((int) Math.floor(currentX), (int) Math.floor(currentY),
							(int) Math.floor(currentZ));
					if (!next.equals(currentBlockPos) && currentBlockPos != position) {
						currentBlockPos = next;
						BlockState block = world.getBlockState(currentBlockPos);
						if (!block.isAir()) {
							if (block.getDestroySpeed(world, currentBlockPos) >= 0) {
								power -= Math.max(DEFAULT_POWER_DEC, callback.getResistance(world, position,
										currentBlockPos, explosionSource, block));
								if (power > 0f) {
									int idistancesq = (int) (Math.pow(currentBlockPos.getX() - position.getX(), 2)
											+ Math.pow(currentBlockPos.getY() - position.getY(), 2)
											+ Math.pow(currentBlockPos.getZ() - position.getZ(), 2));
									toadd.add(new HashDistanceBlockPos(currentBlockPos.getX(), currentBlockPos.getY(),
											currentBlockPos.getZ(), idistancesq));
								}
							} else {
								power = 0;
								break;
							}
						}
					}
					currentX += dx;
					currentY += dy;
					currentZ += dz;
					power -= DEFAULT_POWER_DEC;
				}
			}
		}
		synchronized (mainBlast.resultsSync) {
			mainBlast.resultsSync.addAll(toadd);
		}
		mainBlast.underBlasts.remove(this);
	}
}