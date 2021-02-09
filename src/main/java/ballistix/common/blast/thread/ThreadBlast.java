package ballistix.common.blast.thread;

import java.util.HashSet;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class ThreadBlast extends Thread {

	public final HashSet<BlockPos> results = new HashSet<>();

	public final BlockPos position;
	public World worldObj;
	public int explosionRadius;
	public float explosionEnergy;
	public Entity explosionSource;

	public boolean isComplete = false;

	public ThreadBlast(World world, BlockPos pos, int radius, float energy, Entity source) {
		worldObj = world;
		position = pos;
		explosionRadius = radius;
		explosionEnergy = energy;
		explosionSource = source;
		setPriority(Thread.MIN_PRIORITY);
	}

	@Override
	public void run() {
		isComplete = true;
		super.run();
	}
}