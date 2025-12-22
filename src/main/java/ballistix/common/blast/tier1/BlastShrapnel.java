package ballistix.common.blast.tier1;

import javax.annotation.Nullable;

import ballistix.common.blast.util.Blast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.entity.EntityShrapnel;
import ballistix.common.settings.BallistixConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class BlastShrapnel extends Blast {

    public BlastShrapnel(Level world, BlockPos position, @Nullable Entity owner, @Nullable Entity blastEntity) {
	super(world, position, owner, blastEntity);
    }

    @Override
    public void doPreExplode() {
	if (!world.isClientSide) {
	    world.playSound(null, position, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 25, 1);
	}
    }

    @Override
    public boolean doExplode(int callCount) {
	hasStarted = true;
	for (int i = 0; i < BallistixConstants.EXPLOSIVE_SHRAPNEL_SHRAPNEL_COUNT; i++) {
	    EntityShrapnel shrapnel = new EntityShrapnel(world, owner);
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
