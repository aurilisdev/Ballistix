package ballistix.common.blast.util.thread.raycast;

import java.util.concurrent.atomic.AtomicInteger;

import ballistix.Ballistix;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import modularforcefields.registers.ModularForcefieldsBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import voltaic.prefab.block.HashDistanceBlockPos;

public class DynamicRay {

    private float power;

    private int currentBlockX;
    private int currentBlockY;
    private int currentBlockZ;

    private final int stepX;
    private final int stepY;
    private final int stepZ;

    private double tMaxX;
    private double tMaxY;
    private double tMaxZ;

    private final double tDeltaX;
    private final double tDeltaY;
    private final double tDeltaZ;

    private double lastT;

    private final float powerDecrease;
    private final ThreadDynamicRaycastBlast mainBlast;

    private final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

    public DynamicRay(float currentX, float currentY, float currentZ, float dx, float dy, float dz, float power,
	    float powerDecrease, ThreadDynamicRaycastBlast mainBlast) {

	this.power = power;
	this.powerDecrease = powerDecrease;
	this.mainBlast = mainBlast;

	currentBlockX = Mth.floor(currentX);
	currentBlockY = Mth.floor(currentY);
	currentBlockZ = Mth.floor(currentZ);

	stepX = Float.compare(dx, 0.0F);
	stepY = Float.compare(dy, 0.0F);
	stepZ = Float.compare(dz, 0.0F);

	tDeltaX = stepX == 0 ? Double.POSITIVE_INFINITY : Math.abs(1.0 / dx);
	tDeltaY = stepY == 0 ? Double.POSITIVE_INFINITY : Math.abs(1.0 / dy);
	tDeltaZ = stepZ == 0 ? Double.POSITIVE_INFINITY : Math.abs(1.0 / dz);

	tMaxX = getInitialTMax(currentX, currentBlockX, dx, stepX);
	tMaxY = getInitialTMax(currentY, currentBlockY, dy, stepY);
	tMaxZ = getInitialTMax(currentZ, currentBlockZ, dz, stepZ);
    }

    private static double getInitialTMax(double position, int block, double direction, int step) {
	if (step > 0) {
	    return (block + 1.0 - position) / direction;
	}
	if (step < 0) {
	    return (position - block) / -direction;
	}
	return Double.POSITIVE_INFINITY;
    }

    public boolean tick(final BlockPos position, final Level world, final IResistanceCallback callback,
	    final Entity explosionSource, Long2ObjectOpenHashMap<BlockState> alreadyDestroyed) {

	if (power <= 0.0F || stepX == 0 && stepY == 0 && stepZ == 0) {
	    return true;
	}

	/*
	 * Only advance ONE axis at a time.
	 *
	 * <= gives deterministic tie handling:
	 *
	 * X wins X/Y/Z ties first, then Y, then Z.
	 *
	 * Therefore an exact corner traversal can never jump diagonally from A directly
	 * to D.
	 */
	double nextT;

	if (tMaxX <= tMaxY && tMaxX <= tMaxZ) {
	    nextT = tMaxX;
	    currentBlockX += stepX;
	    tMaxX += tDeltaX;
	} else if (tMaxY <= tMaxZ) {
	    nextT = tMaxY;
	    currentBlockY += stepY;
	    tMaxY += tDeltaY;
	} else {
	    nextT = tMaxZ;
	    currentBlockZ += stepZ;
	    tMaxZ += tDeltaZ;
	}

	/*
	 * At an exact corner the next axis may have the exact same t value. That means
	 * the intermediate voxel has zero geometric thickness along the ray, so don't
	 * charge travel attenuation twice.
	 */
	double travelled = nextT - lastT;

	if (travelled > 0.0) {
	    power -= powerDecrease * (float) travelled;
	}

	lastT = nextT;

	if (power <= 0.0F) {
	    return true;
	}

	long key = BlockPos.asLong(currentBlockX, currentBlockY, currentBlockZ);

	BlockState block = alreadyDestroyed.get(key);

	mutablePos.set(currentBlockX, currentBlockY, currentBlockZ);

	if (block != null) {
	    if (!block.isAir() && block.getDestroySpeed(world, mutablePos) >= 0.0F) {
		power -= Math.max(powerDecrease,
			callback.getResistance(world, position, mutablePos, explosionSource, block));
	    }

	    return power <= 0.0F;
	}

	block = world.getBlockState(mutablePos);

	if (Ballistix.MFFS_LOADED && block.is(ModularForcefieldsBlocks.BLOCK_FORTRONFIELD.get())) {
	    BlockPos fieldPos = new BlockPos(currentBlockX, currentBlockY, currentBlockZ);

	    mainBlast.fortronRayHits.computeIfAbsent(fieldPos, pos -> new AtomicInteger()).incrementAndGet();

	    return true;
	}

	if (!block.isAir()) {
	    if (block.getDestroySpeed(world, mutablePos) < 0.0F) {
		return true;
	    }

	    alreadyDestroyed.put(key, block);

	    power -= Math.max(powerDecrease,
		    callback.getResistance(world, position, mutablePos, explosionSource, block));

	    if (power > 0.0F) {
		int diffX = currentBlockX - position.getX();
		int diffY = currentBlockY - position.getY();
		int diffZ = currentBlockZ - position.getZ();

		int distanceSq = diffX * diffX + diffY * diffY + diffZ * diffZ;

		mainBlast.intermediateResults
			.add(new HashDistanceBlockPos(currentBlockX, currentBlockY, currentBlockZ, distanceSq));
	    }
	}

	return power <= 0.0F;
    }
}