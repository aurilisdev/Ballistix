package ballistix.common.blast;

import ballistix.common.block.SubtypeBlast;
import ballistix.common.entity.EntityShrapnel;
import ballistix.common.settings.Constants;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlastShrapnel extends Blast {

	public BlastShrapnel(World world, BlockPos position) {
		super(world, position);
	}

	@Override
	public void doPreExplode() {
	}

	@Override
	public boolean doExplode(int callCount) {
		for (int i = 0; i < Constants.EXPLOSIVE_SHRAPNEL_SHRAPNEL_COUNT; i++) {
			EntityShrapnel shrapnel = new EntityShrapnel(world, 0, 0, 0);
			float yaw = world.rand.nextFloat() * 360;
			float pitch = world.rand.nextFloat() * 90 - 75;
			shrapnel.setLocationAndAngles(position.getX(), position.getY(), position.getZ(), yaw, pitch);
			shrapnel.func_234612_a_(null, pitch, yaw, 0.0F, 2f, 0.0F);
			world.addEntity(shrapnel);
		}
		return true;
	}

	@Override
	public void doPostExplode() {
	}

	@Override
	public SubtypeBlast getBlastType() {
		return SubtypeBlast.shrapnel;
	}

}
