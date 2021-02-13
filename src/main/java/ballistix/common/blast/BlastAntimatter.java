package ballistix.common.blast;

import java.util.Iterator;

import ballistix.common.blast.thread.ThreadSimpleBlast;
import ballistix.common.block.SubtypeBlast;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.Explosion.Mode;
import net.minecraft.world.World;

public class BlastAntimatter extends Blast {

	public BlastAntimatter(World world, BlockPos position) {
		super(world, position);
	}

	@Override
	public void doPreExplode() {
		if (!world.isRemote) {
			thread = new ThreadSimpleBlast(world, position, 36, Integer.MAX_VALUE, null);
			thread.start();
		}
	}

	private ThreadSimpleBlast thread;
	private int pertick = -1;

	@Override
	public boolean doExplode(int callCount) {
		if (!world.isRemote) {
			if (thread == null) {
				return true;
			}
			Explosion ex = new Explosion(world, null, null, null, position.getX(), position.getY(), position.getZ(), 36, false, Mode.BREAK);
			if (thread.isComplete) {
				if (pertick == -1) {
					pertick = (int) (thread.results.size() / 70.0 + 1);
				}
				int finished = pertick;
				Iterator<BlockPos> iterator = thread.results.iterator();
				while (iterator.hasNext()) {
					if (finished-- < 0) {
						break;
					}
					BlockPos p = new BlockPos(iterator.next());
					world.getBlockState(p).getBlock().onExplosionDestroy(world, p, ex);
					world.setBlockState(p, Blocks.AIR.getDefaultState(), 2);
					iterator.remove();
				}
				if (thread.results.isEmpty()) {
					attackEntities(50);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void doPostExplode() {
	}

	@Override
	public boolean isInstantaneous() {
		return false;
	}

	@Override
	public SubtypeBlast getBlastType() {
		return SubtypeBlast.antimatter;
	}

}
