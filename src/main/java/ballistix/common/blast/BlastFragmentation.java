package ballistix.common.blast;

import ballistix.common.block.SubtypeBlast;
import ballistix.common.entity.EntityShrapnel;
import ballistix.common.settings.Constants;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlastFragmentation extends Blast {

    public BlastFragmentation(World world, BlockPos position) {
	super(world, position);
    }

    @Override
    public boolean doExplode(int callCount) {
	for (int i = 0; i < Constants.EXPLOSIVE_FRAGMENTATION_SHRAPNEL_COUNT; i++) {
	    EntityShrapnel shrapnel = new EntityShrapnel(world);
	    float yaw = world.rand.nextFloat() * 360;
	    float pitch = world.rand.nextFloat() * 90 - 75;
	    shrapnel.setLocationAndAngles(position.getX(), position.getY() + 1.0, position.getZ(), yaw, pitch);
	    shrapnel.func_234612_a_(null, pitch, yaw, 0.0F, 0.5f, 0.0F);
	    shrapnel.isExplosive = true;
	    shrapnel.addVelocity(0, 0.7f, 0);
	    world.addEntity(shrapnel);
	}
	return true;
    }

    @Override
    public SubtypeBlast getBlastType() {
	return SubtypeBlast.fragmentation;
    }

}
