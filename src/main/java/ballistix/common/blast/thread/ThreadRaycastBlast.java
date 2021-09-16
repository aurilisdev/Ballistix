package ballistix.common.blast.thread;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.Explosion.Mode;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;

public class ThreadRaycastBlast extends ThreadBlast {

    public interface IResistanceCallback {
	float getResistance(World world, BlockPos position, BlockPos targetPosition, Entity source, BlockState block);
    }

    public IResistanceCallback callBack;

    public ThreadRaycastBlast(World world, BlockPos position, int range, float energy, Entity source, IResistanceCallback cb) {
	super(world, position, range, energy, source);
	callBack = cb;
    }

    public ThreadRaycastBlast(World world, BlockPos position, int range, float energy, Entity source) {
	this(world, position, range, energy, source, (world1, pos, targetPosition, source1, block) -> {
	    float resistance = 0;

	    if (block.getFluidState() != Fluids.EMPTY.getDefaultState() || block instanceof IFluidBlock) {
		resistance = 0.25f;
	    } else {
		resistance = block.getExplosionResistance(world1, position,
			new Explosion(world, source, null, null, position.getX(), position.getY(), position.getZ(), range, false, Mode.BREAK));
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

		Vector3d delta = new Vector3d(Math.sin(theta) * Math.cos(phi), Math.cos(theta), Math.sin(theta) * Math.sin(phi));
		float power = explosionEnergy - explosionEnergy * world.rand.nextFloat() / 2;
		Vector3d t = new Vector3d(position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5);
		BlockPos tt = new BlockPos(t);
		for (float d = 0.3F; power > 0f; power -= d * 0.75F * 10) {
		    double distancesq = Math.pow(t.getX() - position.getX(), 2) + Math.pow(t.getY() - position.getY(), 2)
			    + Math.pow(t.getZ() - position.getZ(), 2);
		    if (distancesq > explosionRadius * explosionRadius) {
			break;
		    }
		    BlockPos next = new BlockPos(t);
		    if (!next.equals(tt)) {
			tt = next;
			BlockState block = world.getBlockState(tt);
			if (block != Blocks.AIR.getDefaultState() && block != Blocks.VOID_AIR.getDefaultState()
				&& block.getBlockHardness(world, tt) >= 0) {
			    power -= callBack.getResistance(world, position, tt, explosionSource, block);
			    if (power > 0f) {
				int idistancesq = (int) (Math.pow(tt.getX() - position.getX(), 2) + Math.pow(tt.getY() - position.getY(), 2)
					+ Math.pow(tt.getZ() - position.getZ(), 2));
				results.add(new HashDistanceBlockPos(tt.getX(), tt.getY(), tt.getZ(), idistancesq));
			    }
			}
		    }
		    t = new Vector3d(t.x + delta.x, t.y + delta.y, t.z + delta.z);
		}
	    }
	}
	super.run();
    }
}