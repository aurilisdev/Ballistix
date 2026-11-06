package ballistix.client.effect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * What the effects know about the ground a blast leaves behind. With a supporting server the crater floors are
 * reported as the blast carves them; otherwise ground is sampled from the heightmap around the origin.
 */
final class CraterGround {

    private static final double SAMPLE_RADIUS_FACTOR = 1.75;
    private static final int SAMPLE_REACH_MARGIN = 2;

    private final ClientLevel level;
    private final Vec3 origin;
    private final float core;
    private final double radius;
    private final boolean serverReported;
    private final int lingerTicks;
    private final Map<Long, Integer> indexByColumn = new HashMap<>();
    private final List<BlockPos> floors = new ArrayList<>();
    private int endTick;

    CraterGround(BlastScene scene, boolean serverReported) {
	level = scene.level;
	origin = scene.origin;
	core = scene.core;
	radius = Math.min(scene.radius, scene.style.extent.referenceRadius() * SAMPLE_RADIUS_FACTOR);
	this.serverReported = serverReported;
	lingerTicks = scene.timeline.craterLingerTicks();
	endTick = lingerTicks;
    }

    boolean isServerReported() {
	return serverReported;
    }

    /** Adds newly carved floors, replacing any already known for the same column, and extends the crater dust. */
    void report(List<BlockPos> reported, int age) {
	for (BlockPos floor : reported) {
	    long column = ChunkPos.asLong(floor.getX(), floor.getZ());
	    Integer index = indexByColumn.putIfAbsent(column, floors.size());
	    if (index == null)
		floors.add(floor);
	    else
		floors.set(index, floor);
	}
	if (!reported.isEmpty())
	    endTick = age + lingerTicks;
    }

    int endTick() {
	return endTick;
    }

    int lingerTicks() {
	return lingerTicks;
    }

    double radius() {
	return radius;
    }

    boolean hasNoFloors() {
	return floors.isEmpty();
    }

    int floorCount() {
	return floors.size();
    }

    BlockPos floorAt(int index) {
	return floors.get(index);
    }

    /** Top of the floor block, or NaN if it cannot carry dust. */
    double floorY(BlockPos ground) {
	if (serverReported) {
	    if (!level.hasChunk(ground.getX() >> 4, ground.getZ() >> 4))
		return Double.NaN;
	    // A floor update may arrive ahead of the corresponding block changes.
	    BlockPos above = ground.above();
	    BlockState space = level.getBlockState(above);
	    if (!space.getFluidState().isEmpty() || !space.getCollisionShape(level, above).isEmpty())
		return Double.NaN;
	}
	BlockState state = level.getBlockState(ground);
	if (!state.getFluidState().isEmpty())
	    return Double.NaN;
	VoxelShape shape = state.getCollisionShape(level, ground);
	return shape.isEmpty() ? Double.NaN : ground.getY() + shape.max(Direction.Axis.Y);
    }

    /** Height of the first obstacle above the floor up to maxY, or infinity if the column is open. */
    double ceilingY(BlockPos ground, double maxY) {
	// The heightmap bounds the scan, but a roof does not prevent dust from spawning below it.
	int top = Math.min(Mth.ceil(maxY),
		level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ground.getX(), ground.getZ()) - 1);
	if (top <= ground.getY() + 1)
	    return Double.POSITIVE_INFINITY;
	BlockPos.MutableBlockPos above = new BlockPos.MutableBlockPos();
	for (int y = ground.getY() + 2; y <= top; y++) {
	    above.set(ground.getX(), y, ground.getZ());
	    BlockState state = level.getBlockState(above);
	    if (!state.getFluidState().isEmpty())
		return y;
	    VoxelShape shape = state.getCollisionShape(level, above);
	    if (!shape.isEmpty())
		return y + shape.min(Direction.Axis.Y);
	}
	return Double.POSITIVE_INFINITY;
    }

    /** A random ground block within the crater radius, or null if it falls outside loaded terrain or reach. */
    @Nullable
    BlockPos sampleGround(RandomSource random) {
	double angle = random.nextDouble() * Mth.TWO_PI;
	double offset = radius * 0.9 * Math.sqrt(random.nextDouble());
	int x = Mth.floor(origin.x + Math.cos(angle) * offset);
	int z = Mth.floor(origin.z + Math.sin(angle) * offset);
	if (!level.hasChunk(x >> 4, z >> 4))
	    return null;

	int surface = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
	double reach = radius + SAMPLE_REACH_MARGIN;
	if (surface <= level.getMinBuildHeight() || origin.distanceToSqr(x + 0.5, surface, z + 0.5) > reach * reach)
	    return null;
	return new BlockPos(x, surface - 1, z);
    }

    /** The point on the ground under the blast where a rising column starts. */
    Vec3 stemBase() {
	Vec3 base = null;
	double reach = core * 0.6;
	double nearest = reach * reach;
	for (BlockPos ground : floors) {
	    double dx = ground.getX() + 0.5 - origin.x;
	    double dz = ground.getZ() + 0.5 - origin.z;
	    double distance = dx * dx + dz * dz;
	    if (distance >= nearest)
		continue;
	    double floorY = floorY(ground);
	    if (!Double.isNaN(floorY) && floorY <= origin.y + core * 0.15) {
		base = new Vec3(ground.getX() + 0.5, floorY + 1, ground.getZ() + 0.5);
		nearest = distance;
	    }
	}
	if (base != null)
	    return base;

	int x = Mth.floor(origin.x);
	int z = Mth.floor(origin.z);
	if (level.hasChunk(x >> 4, z >> 4)) {
	    int surface = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
	    if (surface > level.getMinBuildHeight() && origin.y - surface <= radius + SAMPLE_REACH_MARGIN
		    && surface <= origin.y + core * 0.15)
		return new Vec3(origin.x, surface + 1, origin.z);
	}
	return origin;
    }
}
