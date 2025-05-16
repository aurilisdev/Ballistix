package ballistix.common.blast.thread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.Sets;

import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.settings.BallistixConstants;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import voltaic.Voltaic;
import voltaic.prefab.block.HashDistanceBlockPos;

public class ThreadSimpleBlast extends ThreadBlast {

    private static final HashSet<BlockPos>[] CACHED_EUCLIDEAN_RESULTS = new HashSet[SubtypeBlast.values().length];
    private static final Set<Integer> currentlyCalculating = Collections.synchronizedSet(new HashSet<>());

    private final int ordinal;

    public ThreadSimpleBlast(World world, BlockPos position, int range, float energy, Entity source, int ordinal) {
        super(world, position, range, energy, source);
        setName("Simple blast thread");
        this.ordinal = ordinal;
        setPriority(MAX_PRIORITY);
    }

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
                if (CACHED_EUCLIDEAN_RESULTS[ordinal] == null) {
                    currentlyCalculating.add(explosionRadius);
                }
            }
            if (CACHED_EUCLIDEAN_RESULTS[ordinal] == null) {
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
                for (int i = 0; i < positions.size(); i++) {
                    int newIndex = boundedNextInt(Voltaic.RANDOM, Math.max(0, i - 10), Math.min(positions.size() - 1, i + 10));
                    BlockPos atNew = positions.get(newIndex);
                    positions.set(newIndex, positions.get(i));
                    positions.set(i, atNew);
                }
                CACHED_EUCLIDEAN_RESULTS[ordinal] = Sets.newHashSet(positions);
            }

            results = CACHED_EUCLIDEAN_RESULTS[ordinal];
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
            for (int i = 0; i < positions.size(); i++) {
                int newIndex = boundedNextInt(Voltaic.RANDOM, Math.max(0, i - 10), Math.min(positions.size() - 1, i + 10));
                BlockPos atNew = positions.get(newIndex);
                positions.set(newIndex, positions.get(i));
                positions.set(i, atNew);
            }
            results = Sets.newHashSet(positions);
        }

    }
    
    public static int boundedNextInt(Random rng, int origin, int bound) {
        int r = rng.nextInt();
        if (origin < bound) {
            // It's not case (1).
            final int n = bound - origin;
            final int m = n - 1;
            if ((n & m) == 0) {
                // It is case (2): length of range is a power of 2.
                r = (r & m) + origin;
            } else if (n > 0) {
                // It is case (3): need to reject over-represented candidates.
                for (int u = r >>> 1;
                     u + m - (r = u % n) < 0;
                     u = rng.nextInt() >>> 1)
                    ;
                r += origin;
            }
            else {
                // It is case (4): length of range not representable as long.
                while (r < origin || r >= bound) {
                    r = rng.nextInt();
                }
            }
        }
        return r;
    }

}

//TODO: Create a thread manager pool thingy so u cant spam threads.