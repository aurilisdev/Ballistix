package ballistix.common.blast.util.thread;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public abstract class ThreadBlast extends Thread {

	public Set<BlockPos> results = new HashSet<>();

	public final BlockPos position;
	public World level;
	public int explosionRadius;
	public float explosionEnergy;
	public Entity explosionSource;

	public boolean isComplete = false;

	protected ThreadBlast(World world, BlockPos pos, int radius, float energy, Entity source) {
		level = world;
		position = pos;
		explosionRadius = radius;
		explosionEnergy = energy;
		explosionSource = source;
		setPriority(Thread.MAX_PRIORITY);
	}

	@Override
	public void run() {
		isComplete = true;
		super.run();
	}
}