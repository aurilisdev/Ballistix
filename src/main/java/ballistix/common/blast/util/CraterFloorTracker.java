package ballistix.common.blast.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.packet.type.client.particle.PacketCraterGround;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * Tracks the ground a blast exposes as it carves, and reports the lowest floor found in each sampled column to the
 * clients that are showing the blast's effects.
 */
final class CraterFloorTracker {

    private static final int SAMPLED_COLUMNS_PER_RADIUS = 24;
    private static final int COVERED_FLOOR_SEARCH_DEPTH = 12;

    private final ServerLevel level;
    private final BlockPos origin;
    private final SubtypeBlast blast;
    private final double radius;
    /** Spacing of the sampled columns, so a larger blast does not report ever more of them. */
    private final int stride;
    private final Map<Long, Integer> lowestFloors = new HashMap<>();
    private final Map<Long, BlockPos> unreportedFloors = new LinkedHashMap<>();
    private final BlockPos.MutableBlockPos probe = new BlockPos.MutableBlockPos();

    CraterFloorTracker(ServerLevel level, BlockPos origin, SubtypeBlast blast, double radius) {
	this.level = level;
	this.origin = origin.immutable();
	this.blast = blast;
	this.radius = radius;
	stride = Math.max(1, Mth.ceil(radius / SAMPLED_COLUMNS_PER_RADIUS));
    }

    void recordRemoval(BlockPos removed) {
	int x = removed.getX();
	int z = removed.getZ();
	int dx = x - origin.getX();
	int dz = z - origin.getZ();
	if (dx % stride != 0 || dz % stride != 0 || Math.abs(dx) > radius || Math.abs(dz) > radius)
	    return;

	int surface = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
	int floorY = surface <= removed.getY() ? surface - 1 : findCoveredFloor(removed);
	if (floorY < level.getMinBuildHeight())
	    return;

	long column = ChunkPos.asLong(x, z);
	if (floorY < lowestFloors.getOrDefault(column, Integer.MAX_VALUE)) {
	    lowestFloors.put(column, floorY);
	    unreportedFloors.put(column, new BlockPos(x, floorY, z));
	}
    }

    /** Follows the opening the blast made downwards, rather than reporting the surface above an intact roof. */
    private int findCoveredFloor(BlockPos removed) {
	int minY = Math.max(level.getMinBuildHeight(), removed.getY() - COVERED_FLOOR_SEARCH_DEPTH);
	for (int y = removed.getY() - 1; y >= minY; y--) {
	    probe.set(removed.getX(), y, removed.getZ());
	    BlockState state = level.getBlockState(probe);
	    if (!state.getFluidState().isEmpty())
		break;
	    if (state.blocksMotion())
		return y;
	}
	return level.getMinBuildHeight() - 1;
    }

    void flush() {
	if (unreportedFloors.isEmpty())
	    return;
	PacketCraterGround.send(level, origin, blast, List.copyOf(unreportedFloors.values()));
	unreportedFloors.clear();
    }
}
