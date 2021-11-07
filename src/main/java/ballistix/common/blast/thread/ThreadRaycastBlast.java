package ballistix.common.blast.thread;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.IFluidBlock;

public class ThreadRaycastBlast extends ThreadBlast {

    public interface IResistanceCallback {
	float getResistance(Level world, BlockPos position, BlockPos targetPosition, Entity source, BlockState block);
    }

    public IResistanceCallback callBack;

    public ThreadRaycastBlast(Level world, BlockPos position, int range, float energy, Entity source, IResistanceCallback cb) {
	super(world, position, range, energy, source);
	callBack = cb;
    }

    public ThreadRaycastBlast(Level world, BlockPos position, int range, float energy, Entity source) {
	this(world, position, range, energy, source, (world1, pos, targetPosition, source1, block) -> {
	    float resistance = 0;

	    if (block.getFluidState() != Fluids.EMPTY.defaultFluidState() || block instanceof IFluidBlock) {
		resistance = 0.25f;
	    } else {
		resistance = block.getExplosionResistance(world1, position, new Explosion(world, source, null, null, position.getX(), position.getY(),
			position.getZ(), range, false, BlockInteraction.BREAK));
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
	int steps = (int) Math.ceil(Math.PI / Math.atan(1.0D / explosionRadius));
	for (int phi_n = 0; phi_n < 2 * steps; phi_n++) {
	    for (int theta_n = 0; theta_n < steps; theta_n++) {
		double phi = Math.PI * 2 / steps * phi_n;
		double theta = Math.PI / steps * theta_n;

		Vec3 delta = new Vec3(Math.sin(theta) * Math.cos(phi), Math.cos(theta), Math.sin(theta) * Math.sin(phi));
		float power = explosionEnergy - explosionEnergy * world.random.nextFloat() / 2;
		Vec3 t = new Vec3(position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5);
		BlockPos tt = new BlockPos(t);
		for (float d = 0.3F; power > 0f; power -= d * 0.75F * 10) {
		    double distancesq = Math.pow(t.x() - position.getX(), 2) + Math.pow(t.y() - position.getY(), 2)
			    + Math.pow(t.z() - position.getZ(), 2);
		    if (distancesq > explosionRadius * explosionRadius) {
			break;
		    }
		    BlockPos next = new BlockPos(t);
		    if (!next.equals(tt)) {
			tt = next;
			BlockState block = world.getBlockState(tt);
			if (block != Blocks.AIR.defaultBlockState() && block != Blocks.VOID_AIR.defaultBlockState()
				&& block.getDestroySpeed(world, tt) >= 0) {
			    power -= callBack.getResistance(world, position, tt, explosionSource, block);
			    if (power > 0f) {
				int idistancesq = (int) (Math.pow(tt.getX() - position.getX(), 2) + Math.pow(tt.getY() - position.getY(), 2)
					+ Math.pow(tt.getZ() - position.getZ(), 2));
				results.add(new HashDistanceBlockPos(tt.getX(), tt.getY(), tt.getZ(), idistancesq));
			    }
			}
		    }
		    t = new Vec3(t.x + delta.x, t.y + delta.y, t.z + delta.z);
		}
	    }
	}
	super.run();
    }
}