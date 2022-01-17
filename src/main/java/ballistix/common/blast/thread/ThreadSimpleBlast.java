package ballistix.common.blast.thread;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class ThreadSimpleBlast extends ThreadBlast {
	private static final HashMap<Integer, HashSet<BlockPos>> cachedResults = new HashMap<>();
	private static final HashMap<Integer, HashSet<BlockPos>> cachedEuclideanResults = new HashMap<>();
	private boolean euclideanDistanceBased;

	public ThreadSimpleBlast(Level world, BlockPos position, int range, float energy, Entity source, boolean euclideanDistanceBased) {
		super(world, position, range, energy, source);
		this.euclideanDistanceBased = euclideanDistanceBased;
		setName("Simple blast thread");
	}

	public double strictnessAtEdges = 1.85;

	@Override
	@SuppressWarnings("java:S2184")
	public void run() {
		int explosionRadius = this.explosionRadius;
		Random random = new Random();
		if (!(euclideanDistanceBased ? cachedEuclideanResults : cachedResults).containsKey(explosionRadius)) {
			HashSet<BlockPos> positions = new HashSet<>();
			for (int i = -explosionRadius; i <= explosionRadius; i++) {
				for (int j = -explosionRadius; j <= explosionRadius; j++) {
					for (int k = -explosionRadius; k <= explosionRadius; k++) {
						int idistance = i * i + j * j + k * k;
						if (idistance <= explosionRadius * explosionRadius && random.nextFloat()
								* (explosionRadius * explosionRadius) < explosionRadius * explosionRadius * strictnessAtEdges - idistance) {
							positions.add(euclideanDistanceBased ? new HashDistanceBlockPos(i, j, k, (int) (idistance - 5 + random.nextFloat() * 10))
									: new BlockPos(i, j, k));
						}
					}
				}
			}
			(euclideanDistanceBased ? cachedEuclideanResults : cachedResults).put(explosionRadius, positions);
		}
		results = (euclideanDistanceBased ? cachedEuclideanResults : cachedResults).get(explosionRadius);
		super.run();
	}
}
//TODO: Create a thread manager pool thingy so u cant spam threads.