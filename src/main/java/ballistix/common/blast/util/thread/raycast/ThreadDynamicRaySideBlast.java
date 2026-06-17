package ballistix.common.blast.util.thread.raycast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ThreadDynamicRaySideBlast extends Thread {

    public final ThreadDynamicRaycastBlast mainBlast;

    public final Direction direction;
    private final RandomSource random = RandomSource.createThreadSafe();
    private ArrayList<DynamicRay> rays = new ArrayList<>();

    private static final float DEFAULT_POWER_DEC = 1.125f;

    public ThreadDynamicRaySideBlast(ThreadDynamicRaycastBlast threadRaycastBlast, Direction dir) {
	mainBlast = threadRaycastBlast;
	direction = dir;
	setName("Raycast Blast Side Thread");
	setPriority(MAX_PRIORITY);
	initializeRays();
    }

    private void initializeRays() {
	int explosionRadius = mainBlast.explosionRadius;
	BlockPos position = mainBlast.position;

	int iMin = -explosionRadius, iMax = explosionRadius, jMax = explosionRadius, jMin = -explosionRadius;
	Vec3i orientation = direction.getNormal();
	float explosionEnergy = mainBlast.explosionEnergy;

	boolean xNotZero = orientation.getX() != 0;
	boolean yNotZero = orientation.getY() != 0;
	boolean zNotZero = orientation.getZ() != 0;

	int expX = orientation.getX() * explosionRadius;
	int expY = orientation.getY() * explosionRadius;
	int expZ = orientation.getZ() * explosionRadius;

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
		rays.add(new DynamicRay(currentX, currentY, currentZ, dx, dy, dz, power, currentBlockPos,
			DEFAULT_POWER_DEC, mainBlast));

	    }
	}
    }

    @Override
    public void run() {
	Level world = mainBlast.level;
	IResistanceCallback callback = mainBlast.callBack;
	Entity explosionSource = mainBlast.explosionSource;
	BlockPos position = mainBlast.position;

	while (!rays.isEmpty()) {
	    Iterator<DynamicRay> it = rays.iterator();
	    HashMap<BlockPos, BlockState> positions = new HashMap<>();
	    while (it.hasNext()) {
		DynamicRay ray = it.next();
		if (ray.tick(position, world, callback, explosionSource, positions))
		    it.remove();
	    }
	}
	mainBlast.underBlasts.remove(this);
    }
}