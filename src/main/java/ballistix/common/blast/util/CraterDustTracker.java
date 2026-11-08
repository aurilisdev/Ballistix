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

final class CraterDustTracker {

    private static final int COVERED_FLOOR_SEARCH_DEPTH = 12;

    private final ServerLevel level;
    private final BlockPos origin;
    private final SubtypeBlast blast;
    private final int stride;
    private final double radius;
    private final Map<Long, Integer> floorHeights = new HashMap<>();
    private final Map<Long, BlockPos> pendingFloors = new LinkedHashMap<>();
    private final BlockPos.MutableBlockPos floorProbe = new BlockPos.MutableBlockPos();

    CraterDustTracker(ServerLevel level, BlockPos origin, SubtypeBlast blast, double radius) {
	this.level = level;
	this.origin = origin.immutable();
	this.blast = blast;
	this.radius = radius;
	// At most 49 by 49 sampled columns, even for a larger configured blast.
	stride = Math.max(1, Mth.ceil(radius / 24));
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
	if (floorY < floorHeights.getOrDefault(column, Integer.MAX_VALUE)) {
	    floorHeights.put(column, floorY);
	    pendingFloors.put(column, new BlockPos(x, floorY, z));
	}
    }

    private int findCoveredFloor(BlockPos removed) {
	// Follow the opening made by the blast without choosing the surface above an intact roof.
	int minY = Math.max(level.getMinBuildHeight(), removed.getY() - COVERED_FLOOR_SEARCH_DEPTH);
	for (int y = removed.getY() - 1; y >= minY; y--) {
	    floorProbe.set(removed.getX(), y, removed.getZ());
	    BlockState state = level.getBlockState(floorProbe);
	    if (!state.getFluidState().isEmpty())
		break;
	    if (state.blocksMotion())
		return y;
	}
	return level.getMinBuildHeight() - 1;
    }

    void flush() {
	if (pendingFloors.isEmpty())
	    return;
	PacketCraterGround.send(level, origin, blast, List.copyOf(pendingFloors.values()));
	pendingFloors.clear();
    }
}
