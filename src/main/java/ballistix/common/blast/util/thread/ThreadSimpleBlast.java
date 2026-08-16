package ballistix.common.blast.util.thread;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import ballistix.common.settings.BallistixConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import voltaic.Voltaic;
import voltaic.prefab.block.HashDistanceBlockPos;

public class ThreadSimpleBlast extends ThreadBlast {

    private static final Map<CacheKey, Set<BlockPos>> CACHED_EUCLIDEAN_RESULTS = new ConcurrentHashMap<>();

    private static final Comparator<BlockPos> BY_CENTER_DISTANCE = Comparator
	    .comparingLong(ThreadSimpleBlast::distanceSq).thenComparingInt(BlockPos::getX)
	    .thenComparingInt(BlockPos::getY).thenComparingInt(BlockPos::getZ);

    private final ResourceLocation id;
    private final boolean sortBasedOnDistance;

    public double strictnessAtEdges = 1.85;

    private record CacheKey(int radius, ResourceLocation id, boolean sorted, long strictness) {
    }

    public ThreadSimpleBlast(Level world, BlockPos position, int range, float energy, Entity source,
	    ResourceLocation id) {
	this(world, position, range, energy, source, id, false);
    }

    public ThreadSimpleBlast(Level world, BlockPos position, int range, float energy, Entity source,
	    ResourceLocation id, boolean sortBasedOnDistance) {
	super(world, position, range, energy, source);
	setName("Simple blast thread");
	this.id = id;
	this.sortBasedOnDistance = sortBasedOnDistance;
    }

    @Override
    public void run() {
	runEuclidian(explosionRadius, Voltaic.RANDOM);
	super.run();
    }

    public void runEuclidian(int explosionRadius, Random random) {
	if (!BallistixConfig.INSTANCE.SHOULD_CACHE_EXPLOSIONS.get()) {
	    results = calculateEuclidean(explosionRadius, random);
	    return;
	}
	CacheKey key = new CacheKey(explosionRadius, id, sortBasedOnDistance,
		Double.doubleToLongBits(strictnessAtEdges));
	results = CACHED_EUCLIDEAN_RESULTS.computeIfAbsent(key, unused -> calculateEuclidean(explosionRadius, random));
    }

    private Set<BlockPos> calculateEuclidean(int explosionRadius, Random random) {
	int radiusSq = explosionRadius * explosionRadius;
	double edgeThreshold = radiusSq * strictnessAtEdges;
	int estimatedSize = (int) Math.min(Integer.MAX_VALUE - 8L,
		(long) (Math.PI * 4.0 / 3.0 * radiusSq * (explosionRadius + 1)));
	ArrayList<BlockPos> positions = new ArrayList<>(Math.max(16, estimatedSize));
	for (int i = -explosionRadius; i <= explosionRadius; i++) {
	    int iSq = i * i;
	    for (int j = 0; j <= explosionRadius; j++) {
		int dist2D = iSq + j * j;
		if (dist2D > radiusSq) {
		    continue;
		}
		int kMax = (int) Math.sqrt(radiusSq - dist2D);
		for (int k = 0; k <= kMax; k++) {
		    int dist3D = dist2D + k * k;
		    if (random.nextFloat() * radiusSq >= edgeThreshold - dist3D) {
			continue;
		    }
		    addPosition(positions, i, k, j, dist3D, random);
		    if (k != 0) {
			addPosition(positions, i, -k, j, dist3D, random);
			if (j != 0) {
			    addPosition(positions, i, -k, -j, dist3D, random);
			}
		    }
		    if (j != 0) {
			addPosition(positions, i, k, -j, dist3D, random);
		    }
		}
	    }
	}
	if (sortBasedOnDistance) {
	    TreeSet<BlockPos> sorted = new TreeSet<>(BY_CENTER_DISTANCE);
	    sorted.addAll(positions);
	    return sorted;
	}
	locallyShuffle(positions, random);
	return new HashSet<>(positions);
    }

    private static void addPosition(ArrayList<BlockPos> positions, int x, int y, int z, int distanceSq, Random random) {
	int hash = (int) Math.max(1, distanceSq - 50 + random.nextFloat() * 100);
	positions.add(new HashDistanceBlockPos(x, y, z, hash));
    }

    private static void locallyShuffle(ArrayList<BlockPos> positions, Random random) {
	int size = positions.size();
	for (int i = 0; i < size; i++) {
	    int from = Math.max(0, i - 10);
	    int to = Math.min(size, i + 11);
	    int newIndex = random.nextInt(from, to);
	    BlockPos swap = positions.get(newIndex);
	    positions.set(newIndex, positions.get(i));
	    positions.set(i, swap);
	}
    }

    private static long distanceSq(BlockPos pos) {
	long x = pos.getX();
	long y = pos.getY();
	long z = pos.getZ();
	return x * x + y * y + z * z;
    }

    public static void clearCache() {
	CACHED_EUCLIDEAN_RESULTS.clear();
    }
}