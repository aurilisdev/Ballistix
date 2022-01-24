package ballistix.common.blast.thread.raycast;

import java.util.HashSet;

import ballistix.common.blast.thread.HashDistanceBlockPos;
import ballistix.common.blast.thread.ThreadBlast;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.IFluidBlock;

public class ThreadRaycastBlast extends ThreadBlast {
	public IResistanceCallback callBack;
	public HashSet<ThreadRaySideBlast> underBlasts = new HashSet<>();

	public ThreadRaycastBlast(Level world, BlockPos position, int range, float energy, Entity source, IResistanceCallback cb) {
		super(world, position, range, energy, source);
		callBack = cb;
		setName("RaycastBlast Main Thread");
	}

	public ThreadRaycastBlast(Level world, BlockPos position, int range, float energy, Entity source) {
		this(world, position, range, energy, source, (world1, pos, targetPosition, source1, block) -> {
			float resistance = 0;

			if (block.getFluidState() != Fluids.EMPTY.defaultFluidState() || block instanceof IFluidBlock) {
				resistance = 0.25f;
			} else {
				resistance = block.getExplosionResistance(world1, position, new Explosion(world, source, null, null, position.getX(), position.getY(), position.getZ(), range, false, BlockInteraction.BREAK));
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
			}
		}
		super.run();
	}
}