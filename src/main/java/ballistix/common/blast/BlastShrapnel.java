package ballistix.common.blast;

import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.entity.EntityShrapnel;
import ballistix.common.settings.Constants;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlastShrapnel extends Blast {

	public BlastShrapnel(World world, BlockPos position) {
		super(world, position);
	}

	@Override
	public boolean doExplode(int callCount) {
		hasStarted = true;
		for (int i = 0; i < Constants.EXPLOSIVE_SHRAPNEL_SHRAPNEL_COUNT; i++) {
			EntityShrapnel shrapnel = new EntityShrapnel(world);
			float yaw = world.random.nextFloat() * 360;
			float pitch = world.random.nextFloat() * 90 - 75;
			shrapnel.moveTo(position.getX(), position.getY(), position.getZ(), yaw, pitch);
			shrapnel.shootFromRotation(null, pitch, yaw, 0.0F, 2f, 0.0F);
			world.addFreshEntity(shrapnel);
		}
		return true;
	}

	@Override
	public SubtypeBlast getBlastType() {
		return SubtypeBlast.shrapnel;
	}

}