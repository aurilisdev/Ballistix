package ballistix.common.blast.util.thread;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public abstract class ThreadBlast extends Thread {

	public Set<BlockPos> results = new HashSet<>();

	public final BlockPos position;
	public Level level;
	public int explosionRadius;
	public float explosionEnergy;
	public Entity explosionSource;

	public boolean isComplete = false;

	protected ThreadBlast(Level world, BlockPos pos, int radius, float energy, Entity source) {
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