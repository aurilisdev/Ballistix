package ballistix.common.blast.util;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.packet.type.client.particle.PacketCraterGround;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;

final class CraterDustTracker {

    private final ServerLevel level;
    private final BlockPos origin;
    private final SubtypeBlast blast;
    private final int stride;
    private final double radius;
    private final Map<Long, BlockPos> pendingFloors = new LinkedHashMap<>();

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
	// A break below an intact roof does not expose a new surface.
	if (surface > removed.getY() || surface <= level.getMinBuildHeight())
	    return;

	pendingFloors.put(ChunkPos.asLong(x, z), new BlockPos(x, surface - 1, z));
    }

    void flush() {
	if (pendingFloors.isEmpty())
	    return;
	PacketCraterGround.send(level, origin, blast, List.copyOf(pendingFloors.values()));
	pendingFloors.clear();
    }
}
