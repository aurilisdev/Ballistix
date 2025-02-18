package ballistix.common.blast.thread;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import com.google.common.collect.Sets;

import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.settings.Constants;
import electrodynamics.Electrodynamics;
import electrodynamics.prefab.block.HashDistanceBlockPos;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class ThreadSimpleBlast extends ThreadBlast {

	private static final HashSet<BlockPos>[] CACHED_EUCLIDEAN_RESULTS = new HashSet[SubtypeBlast.values().length];

	private final int ordinal;

	public ThreadSimpleBlast(Level world, BlockPos position, int range, float energy, Entity source, int ordinal) {
		super(world, position, range, energy, source);
		setName("Simple blast thread");
		this.ordinal = ordinal;
	}

	public double strictnessAtEdges = 1.85;

	@Override
	@SuppressWarnings("java:S2184")
	public void run() {
		int explosionRadius = this.explosionRadius;
		Random random = Electrodynamics.RANDOM;
		runEuclidian(explosionRadius, random);
		super.run();
	}

	public void runEuclidian(int explosionRadius, Random random) {

		if (Constants.SHOULD_CACHE_EXPLOSIONS) {

			if (CACHED_EUCLIDEAN_RESULTS[ordinal] == null) {

				ArrayList<BlockPos> positions = new ArrayList<>();
				for (int i = -explosionRadius; i <= explosionRadius; i++) {
					for (int j = -explosionRadius; j <= explosionRadius; j++) {
						for (int k = -explosionRadius; k <= explosionRadius; k++) {
							int idistance = i * i + j * j + k * k;
							if (idistance <= explosionRadius * explosionRadius && random.nextFloat() * (explosionRadius * explosionRadius) < explosionRadius * explosionRadius * strictnessAtEdges - idistance) {
								positions.add(new HashDistanceBlockPos(i, j, k, (int) Math.max(1, idistance - 50 + random.nextFloat() * 100)));
							}
						}
					}
				}
				Random rand = Electrodynamics.RANDOM;
				for (int i = 0; i < positions.size(); i++) {
					int newIndex = rand.nextInt(Math.max(0, i - 10), Math.min(positions.size() - 1, i + 10));
					BlockPos atNew = positions.get(newIndex);
					positions.set(newIndex, positions.get(i));
					positions.set(i, atNew);
				}
				CACHED_EUCLIDEAN_RESULTS[ordinal] = Sets.newHashSet(positions);

			}

			results = CACHED_EUCLIDEAN_RESULTS[ordinal];

		} else {
			ArrayList<BlockPos> positions = new ArrayList<>();
			for (int i = -explosionRadius; i <= explosionRadius; i++) {
				for (int j = -explosionRadius; j <= explosionRadius; j++) {
					for (int k = -explosionRadius; k <= explosionRadius; k++) {
						int idistance = i * i + j * j + k * k;
						if (idistance <= explosionRadius * explosionRadius && random.nextFloat() * (explosionRadius * explosionRadius) < explosionRadius * explosionRadius * strictnessAtEdges - idistance) {
							positions.add(new HashDistanceBlockPos(i, j, k, (int) Math.max(1, idistance - 50 + random.nextFloat() * 100)));
						}
					}
				}
			}
			Random rand = Electrodynamics.RANDOM;
			for (int i = 0; i < positions.size(); i++) {
				int newIndex = rand.nextInt(Math.max(0, i - 10), Math.min(positions.size() - 1, i + 10));
				BlockPos atNew = positions.get(newIndex);
				positions.set(newIndex, positions.get(i));
				positions.set(i, atNew);
			}
			results = Sets.newHashSet(positions);
		}

	}

}
//TODO: Create a thread manager pool thingy so u cant spam threads.