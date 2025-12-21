package ballistix.common.blast.util.thread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;

import ballistix.common.settings.BallistixConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import voltaic.Voltaic;
import voltaic.prefab.block.HashDistanceBlockPos;

public class ThreadSimpleBlast extends ThreadBlast {


    private static final HashMap<Pair<Integer, ResourceLocation>, Set<BlockPos>> CACHED_EUCLIDEAN_RESULTS = new HashMap<>();
    private static final Set<Integer> currentlyCalculating = Collections.synchronizedSet(new HashSet<>());

    private final Pair<Integer, ResourceLocation> idPair;

    public ThreadSimpleBlast(Level world, BlockPos position, int range, float energy, Entity source, ResourceLocation id) {
        super(world, position, range, energy, source);
        setName("Simple blast thread");
        this.idPair = new Pair<Integer, ResourceLocation>(range, id);
        setPriority(MAX_PRIORITY);
    }
    public ThreadSimpleBlast(Level world, BlockPos position, int range, float energy, Entity source,
	    ResourceLocation id, boolean sortBasedOnDistance) {
	this(world, position, range, energy, source, id);
	this.sortBasedOnDistance = sortBasedOnDistance;
    }

    private boolean sortBasedOnDistance = false;

    public double strictnessAtEdges = 1.85;

    @Override
    public void run() {
        int explosionRadius = this.explosionRadius;
        Random random = Voltaic.RANDOM;
        runEuclidian(explosionRadius, random);
        super.run();
    }

    public void runEuclidian(int explosionRadius, Random random) {
        if (BallistixConstants.SHOULD_CACHE_EXPLOSIONS) {
            synchronized (currentlyCalculating) {
                while (currentlyCalculating.contains(explosionRadius)) {
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }
                if (CACHED_EUCLIDEAN_RESULTS.get(idPair) == null) {
                    currentlyCalculating.add(explosionRadius);
                }
            }
            if (CACHED_EUCLIDEAN_RESULTS.get(idPair) == null) {
                int rSqrd = explosionRadius * explosionRadius;
                ArrayList<BlockPos> positions = new ArrayList<>((int) (Math.PI * 4.0 / 3.0 * rSqrd * (explosionRadius + 1)));
                for (int i = -explosionRadius; i <= explosionRadius; i++) {
                    for (int j = 0; j <= explosionRadius; j++) {
                        int dist2D = i * i + j * j;
                        if (dist2D <= rSqrd) {
                            int kMax = (int) Math.floor(Math.sqrt(rSqrd - dist2D));
                            for (int k = 0; k <= kMax; k++) {
                                int dist3D = dist2D + k * k;
                                if (random.nextFloat() * rSqrd < rSqrd * strictnessAtEdges - dist3D) {
                                    positions.add(new HashDistanceBlockPos(i, k, j, (int) Math.max(1, dist3D - 50 + random.nextFloat() * 100)));
                                    if (k != 0) {
                                        positions.add(new HashDistanceBlockPos(i, -k, j, (int) Math.max(1, dist3D - 50 + random.nextFloat() * 100)));
                                        if (j != 0) {
                                            positions.add(new HashDistanceBlockPos(i, -k, -j, (int) Math.max(1, dist3D - 50 + random.nextFloat() * 100)));
                                        }
                                    }
                                    if (j != 0) {
                                        positions.add(new HashDistanceBlockPos(i, k, -j, (int) Math.max(1, dist3D - 50 + random.nextFloat() * 100)));
                                    }
                                }
                            }
                        }
                    }
                }
                // Sort
                Random rand = Voltaic.RANDOM;
                for (int i = 0; i < positions.size(); i++) {
                    int newIndex = rand.nextInt(Math.max(0, i - 10), Math.min(positions.size() - 1, i + 10));
                    BlockPos atNew = positions.get(newIndex);
                    positions.set(newIndex, positions.get(i));
                    positions.set(i, atNew);
                }
		if (sortBasedOnDistance) {

		    Comparator<BlockPos> byCenterDistance = Comparator.comparingLong(pos -> {
			long x = pos.getX();
			long y = pos.getY();
			long z = pos.getZ();
			// distance from (0.5, 0.5, 0.5) equals x² + y² + z² for integer block coords
			return x * x + y * y + z * z;
		    });
		    byCenterDistance = byCenterDistance.thenComparingInt(BlockPos::getX)
			    .thenComparingInt(BlockPos::getY).thenComparingInt(BlockPos::getZ);

		    // turn results into a sorted set, closest to farthest
		    Set<BlockPos> sorted = new TreeSet<>(byCenterDistance);
		    sorted.addAll(positions);
		    CACHED_EUCLIDEAN_RESULTS.put(idPair, sorted);

		} else {
		    CACHED_EUCLIDEAN_RESULTS.put(idPair, Sets.newHashSet(positions));
		}
            }

            results = CACHED_EUCLIDEAN_RESULTS.get(idPair);
            synchronized (currentlyCalculating) {
                currentlyCalculating.remove(explosionRadius);
            }
        } else {
            int rSqrd = explosionRadius * explosionRadius;
            ArrayList<BlockPos> positions = new ArrayList<>((int) (Math.PI * 4.0 / 3.0 * rSqrd * (explosionRadius + 1)));
            for (int i = -explosionRadius; i <= explosionRadius; i++) {
                for (int j = 0; j <= explosionRadius; j++) {
                    int dist2D = i * i + j * j;
                    if (dist2D <= rSqrd) {
                        int kMax = (int) Math.floor(Math.sqrt(rSqrd - dist2D));
                        for (int k = 0; k <= kMax; k++) {
                            int dist3D = dist2D + k * k;
                            if (random.nextFloat() * rSqrd < rSqrd * strictnessAtEdges - dist3D) {
                                positions.add(new HashDistanceBlockPos(i, k, j, (int) Math.max(1, dist3D - 50 + random.nextFloat() * 100)));
                                if (k != 0) {
                                    positions.add(new HashDistanceBlockPos(i, -k, j, (int) Math.max(1, dist3D - 50 + random.nextFloat() * 100)));
                                    if (j != 0) {
                                        positions.add(new HashDistanceBlockPos(i, -k, -j, (int) Math.max(1, dist3D - 50 + random.nextFloat() * 100)));
                                    }
                                }
                                if (j != 0) {
                                    positions.add(new HashDistanceBlockPos(i, k, -j, (int) Math.max(1, dist3D - 50 + random.nextFloat() * 100)));
                                }
                            }
                        }
                    }
                }
            }
	    // Sort
	    Random rand = Voltaic.RANDOM;
	    for (int i = 0; i < positions.size(); i++) {
		int newIndex = rand.nextInt(Math.max(0, i - 10), Math.min(positions.size() - 1, i + 10));
		BlockPos atNew = positions.get(newIndex);
		positions.set(newIndex, positions.get(i));
		positions.set(i, atNew);
	    }
	    results = Sets.newHashSet(positions);
	    if (sortBasedOnDistance) {
		Comparator<BlockPos> byCenterDistance = Comparator.comparingLong(pos -> {
		    long x = pos.getX();
		    long y = pos.getY();
		    long z = pos.getZ();
		    // distance from (0.5, 0.5, 0.5) equals x² + y² + z² for integer block coords
		    return x * x + y * y + z * z;
		});
		byCenterDistance = byCenterDistance.thenComparingInt(BlockPos::getX).thenComparingInt(BlockPos::getY)
			.thenComparingInt(BlockPos::getZ);

		// turn results into a sorted set, closest to farthest
		Set<BlockPos> sorted = new TreeSet<>(byCenterDistance);
		sorted.addAll(positions);
		results = sorted;
	    }
        }

    }

}

//TODO: Create a thread manager pool thingy so u cant spam threads.