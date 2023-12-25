package ballistix.common.blast.thread.raycast;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import ballistix.common.blast.thread.ThreadBlast;
import electrodynamics.prefab.block.HashDistanceBlockPos;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.Explosion.Mode;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;

public class ThreadRaycastBlast extends ThreadBlast {
	public IResistanceCallback callBack;
	public HashSet<ThreadRaySideBlast> underBlasts = new HashSet<>();
	public Set<BlockPos> resultsSync = Collections.synchronizedSet(new HashSet<BlockPos>());
	public boolean locked = false;

	public ThreadRaycastBlast(World world, BlockPos position, int range, float energy, Entity source, IResistanceCallback cb) {
		super(world, position, range, energy, source);
		callBack = cb;
		setName("RaycastBlast Main Thread");
	}

	public ThreadRaycastBlast(World world, BlockPos position, int range, float energy, Entity source) {
		this(world, position, range, energy, source, (world1, pos, targetPosition, source1, block) -> {
			float resistance = 0;

			if (block.getFluidState() != Fluids.EMPTY.defaultFluidState() || block instanceof IFluidBlock) {
				resistance = 0.25f;
			} else {
				resistance = block.getExplosionResistance(world1, position, new Explosion(world, source, null, null, position.getX(), position.getY(), position.getZ(), range, false, Mode.BREAK));
				if (resistance > 200) {
					resistance = 0.75f * (float) Math.sqrt(resistance);
				}
			}

			return resistance;
		});

	}

	@Override
	@SuppressWarnings("java:S2184")
	public void run() {
		results.add(new HashDistanceBlockPos(position.getX(), position.getY(), position.getZ(), 0));
		for (Direction dir : Direction.values()) {
			ThreadRaySideBlast sideBlast = new ThreadRaySideBlast(this, dir);
			sideBlast.start();
			underBlasts.add(sideBlast);
		}
		while (!underBlasts.isEmpty()) {
			try {
				sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		}
		super.run();
	}
}