package ballistix.common.blast.thread.raycast;

import electrodynamics.prefab.block.HashDistanceBlockPos;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class ThreadRaySideBlast extends Thread {

    public final ThreadRaycastBlast mainBlast;

    public final Direction direction;
    private final RandomSource random = RandomSource.createThreadSafe();

    private static final float DEFAULT_POWER_DEC = 1.125f;

    public ThreadRaySideBlast(ThreadRaycastBlast threadRaycastBlast, Direction dir) {
	mainBlast = threadRaycastBlast;
	direction = dir;
	setName("Raycast Blast Side Thread");
    }

    @Override
    @SuppressWarnings("java:S2184")
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

		Vec3 delta = new Vec3(x, y, z).normalize();

		float power = explosionEnergy - explosionEnergy * random.nextFloat() / 2;

		Vec3 currentVector = new Vec3(position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5);

		BlockPos currentBlockPos = new BlockPos((int) Math.floor(currentVector.x()),
			(int) Math.floor(currentVector.y()), (int) Math.floor(currentVector.z()));

		while (power > 0.0F) {
		    BlockPos next = new BlockPos((int) Math.floor(currentVector.x()),
			    (int) Math.floor(currentVector.y()), (int) Math.floor(currentVector.z()));
		    if (!next.equals(currentBlockPos) && currentBlockPos != position) {
			currentBlockPos = next;
			BlockState block = world.getBlockState(currentBlockPos);
			if (!block.isAir()) {
			    if (block.getDestroySpeed(world, currentBlockPos) >= 0) {
				power -= Math.max(DEFAULT_POWER_DEC, callback.getResistance(world, position, currentBlockPos,
					explosionSource, block));
				if (power > 0f) {
				    int idistancesq = (int) (Math.pow(currentBlockPos.getX() - position.getX(), 2)
					    + Math.pow(currentBlockPos.getY() - position.getY(), 2)
					    + Math.pow(currentBlockPos.getZ() - position.getZ(), 2));
				    synchronized (mainBlast.resultsSync) {
					mainBlast.resultsSync.add(new HashDistanceBlockPos(currentBlockPos.getX(),
						currentBlockPos.getY(), currentBlockPos.getZ(), idistancesq));
				    }
				}
			    } else {
				power = 0;
				break;
			    }
			}
		    }
		    currentVector = new Vec3(currentVector.x + delta.x, currentVector.y + delta.y,
			    currentVector.z + delta.z);
		    power -= DEFAULT_POWER_DEC;
		}
	    }
	}
	mainBlast.underBlasts.remove(this);
    }
}