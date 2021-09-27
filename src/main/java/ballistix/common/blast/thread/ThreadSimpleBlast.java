package ballistix.common.blast.thread;

import java.util.HashMap;
import java.util.HashSet;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ThreadSimpleBlast extends ThreadBlast {
    private static final HashMap<Integer, HashSet<BlockPos>> cachedResults = new HashMap<>();

    public ThreadSimpleBlast(World world, BlockPos position, int range, float energy, Entity source) {
	super(world, position, range, energy, source);
    }

    @Override
    @SuppressWarnings("java:S2184")
    public void run() {
	int explosionRadius = this.explosionRadius;
	long time = System.currentTimeMillis();
//	for (int i = -explosionRadius; i <= explosionRadius; i++) {
//	    int smallest = position.getY() - explosionRadius < 0 ? -explosionRadius - (position.getY() - explosionRadius) : -explosionRadius;
//	    int largest = position.getY() + explosionRadius > 255 ? explosionRadius - (explosionRadius + position.getY() - 255) : explosionRadius;
//	    for (int j = smallest; j <= largest; j++) {
//		for (int k = -explosionRadius; k <= explosionRadius; k++) {
//		    int idistance = i * i + j * j + k * k;
//		    if (idistance <= explosionRadius * explosionRadius
//			    && world.rand.nextFloat() * (explosionRadius * explosionRadius) < explosionRadius * explosionRadius * 1.85 - idistance) {
//			HashDistanceBlockPos pos = new HashDistanceBlockPos(position.getX() + i, position.getY() + j, position.getZ() + k, idistance);
//			BlockState block = world.getBlockState(pos);
//			if (block != Blocks.AIR.getDefaultState() && block != Blocks.VOID_AIR.getDefaultState()
//				&& block.getBlockHardness(world, pos) >= 0) {
//			    results.add(pos);
//			}
//		    }
//		}
//	    }
//	}
	// Code above is old code. This code is cache based causing less calculations on
	// average
	if (!cachedResults.containsKey(explosionRadius)) {
	    HashSet<BlockPos> positions = new HashSet<>();
	    for (int i = -explosionRadius; i <= explosionRadius; i++) {
		for (int j = -explosionRadius; j <= explosionRadius; j++) {
		    for (int k = -explosionRadius; k <= explosionRadius; k++) {
			int idistance = i * i + j * j + k * k;
			if (idistance <= explosionRadius * explosionRadius && world.rand.nextFloat()
				* (explosionRadius * explosionRadius) < explosionRadius * explosionRadius * 1.85 - idistance) {
			    positions.add(new BlockPos(i, j, k));
			}
		    }
		}
	    }
	    cachedResults.put(explosionRadius, positions);
	}
//	for (BlockPos p : cachedResults.get(explosionRadius)) {
//	    int smallest = position.getY() - explosionRadius < 0 ? -explosionRadius - (position.getY() - explosionRadius) : -explosionRadius;
//	    int largest = position.getY() + explosionRadius > 255 ? explosionRadius - (explosionRadius + position.getY() - 255) : explosionRadius;
//	    if (p.getY() >= smallest && p.getY() <= largest) {
//		int idistance = (int) p.distanceSq(0, 0, 0, true);
//		HashDistanceBlockPos pos = new HashDistanceBlockPos(position.getX() + p.getX(), position.getY() + p.getY(),
//			position.getZ() + p.getZ(), idistance);
//		BlockState block = world.getBlockState(pos);
//		if (block != Blocks.AIR.getDefaultState() && block != Blocks.VOID_AIR.getDefaultState() && block.getBlockHardness(world, pos) >= 0) {
//		    results.add(pos);
//		}
//	    }
//	}
	results = (HashSet<BlockPos>) cachedResults.get(explosionRadius).clone();
	System.out.println(System.currentTimeMillis() - time);
	super.run();
    }
}