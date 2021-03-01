package ballistix.common.blast.thread;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ThreadSimpleBlast extends ThreadBlast {

    public ThreadSimpleBlast(World world, BlockPos position, int range, float energy, Entity source) {
	super(world, position, range, energy, source);
    }

    @Override
    public void run() {
	int explosionRadius = this.explosionRadius;
	for (int i = -explosionRadius; i <= explosionRadius; i++) {
	    for (int j = -explosionRadius; j <= explosionRadius; j++) {
		for (int k = -explosionRadius; k <= explosionRadius; k++) {
		    int idistance = i * i + j * j + k * k;
		    if (idistance <= explosionRadius * explosionRadius && world.rand.nextFloat()
			    * (explosionRadius * explosionRadius) < explosionRadius * explosionRadius * 1.85
				    - idistance) {
			HashDistanceBlockPos pos = new HashDistanceBlockPos(position.getX() + i, position.getY() + j,
				position.getZ() + k, idistance);
			BlockState block = world.getBlockState(pos);
			if (block != Blocks.AIR.getDefaultState() && block != Blocks.VOID_AIR.getDefaultState()
				&& block.getBlockHardness(world, pos) >= 0) {
			    results.add(pos);
			}
		    }
		}
	    }
	}
	super.run();
    }
}