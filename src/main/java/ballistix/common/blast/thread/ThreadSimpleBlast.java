package ballistix.common.blast.thread;

import java.util.HashMap;
import java.util.HashSet;

import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class ThreadSimpleBlast extends ThreadBlast {
    private static final HashMap<Integer, HashSet<BlockPos>> cachedResults = new HashMap<>();
    private static final HashMap<Integer, HashSet<BlockPos>> cachedHashedResults = new HashMap<>();
    private boolean useHashedResults;

    public ThreadSimpleBlast(Level world, BlockPos position, int range, float energy, Entity source, boolean useHashedResults) {
	super(world, position, range, energy, source);
	this.useHashedResults = useHashedResults;
    }

    public double strictnessAtEdges = 1.85;

    @Override
    @SuppressWarnings("java:S2184")
    public void run() {
	int explosionRadius = this.explosionRadius;
	if (!(useHashedResults ? cachedHashedResults : cachedResults).containsKey(explosionRadius)) {
	    HashSet<BlockPos> positions = new HashSet<>();
	    for (int i = -explosionRadius; i <= explosionRadius; i++) {
		for (int j = -explosionRadius; j <= explosionRadius; j++) {
		    for (int k = -explosionRadius; k <= explosionRadius; k++) {
			int idistance = i * i + j * j + k * k;
			if (idistance <= explosionRadius * explosionRadius && world.random.nextFloat()
				* (explosionRadius * explosionRadius) < explosionRadius * explosionRadius * strictnessAtEdges - idistance) {
			    positions.add(useHashedResults ? new HashDistanceBlockPos(i, j, k, idistance) : new BlockPos(i, j, k));
			}
		    }
		}
	    }
	    (useHashedResults ? cachedHashedResults : cachedResults).put(explosionRadius, positions);
	}
	results = (HashSet<BlockPos>) (useHashedResults ? cachedHashedResults : cachedResults).get(explosionRadius).clone();
	super.run();
    }
}