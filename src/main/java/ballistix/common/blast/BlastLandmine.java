package ballistix.common.blast;

import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.entity.EntityShrapnel;
import ballistix.common.settings.BallistixConstants;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlastLandmine extends Blast {

	public BlastLandmine(World world, BlockPos position) {
		super(world, position);
	}

	@Override
	public void doPreExplode() {
		if(!world.isClientSide) {
			world.playSound(null, position, SoundEvents.GENERIC_EXPLODE, SoundCategory.BLOCKS, 25, 1);
		}
	}

	@Override
	public boolean doExplode(int callCount) {
		hasStarted = true;
		for (int i = 0; i < BallistixConstants.EXPLOSIVE_FRAGMENTATION_SHRAPNEL_COUNT; i++) {
			EntityShrapnel shrapnel = new EntityShrapnel(world);
			float yaw = world.random.nextFloat() * 360;
			float pitch = world.random.nextFloat() * 90 - 75;
			shrapnel.moveTo(position.getX(), position.getY() + 1.0, position.getZ(), yaw, pitch);
			shrapnel.shootFromRotation(null, pitch, yaw, 0.0F, 0.5f, 0.0F);
			shrapnel.isExplosive = true;
			shrapnel.push(0, 0.7f, 0);
			world.addFreshEntity(shrapnel);
		}
		return true;
	}

	@Override
	public SubtypeBlast getBlastType() {
		return SubtypeBlast.landmine;
	}

}
