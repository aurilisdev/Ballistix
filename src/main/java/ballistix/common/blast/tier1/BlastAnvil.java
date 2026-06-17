package ballistix.common.blast.tier1;

import javax.annotation.Nullable;

import ballistix.api.blast.IBlast;
import ballistix.common.blast.util.Blast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.settings.BallistixConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class BlastAnvil extends Blast {

    public BlastAnvil(Level world, BlockPos position, @Nullable Entity owner, @Nullable Entity blastEntity) {
	super(world, position, owner, blastEntity);
    }

    @Override
    public boolean doExplode(int callCount) {
	super.doExplode(callCount);
	hasStarted = true;

	if (!world.isClientSide) {
	    world.playSound(null, position, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 1.0F, 1.0F);
	    for (int i = 0; i < BallistixConfig.INSTANCE.EXPLOSIVE_ANVIL_ANVILSPERBLAST.getAsInt(); i++) {

		float xVel = (world.random.nextBoolean() ? 1.0F : -1.0F) * world.random.nextFloat();
		float yVel = 1.0F;
		float zVel = (world.random.nextBoolean() ? 1.0F : -1.0F) * world.random.nextFloat();
		FallingBlockEntity anvil = new FallingBlockEntity(EntityType.FALLING_BLOCK, world);
		anvil.blockState = Blocks.ANVIL.defaultBlockState();
		anvil.blocksBuilding = true;
		anvil.setPos(position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5);
		anvil.xo = anvil.getX();
		anvil.yo = anvil.getY();
		anvil.zo = anvil.getZ();
		anvil.setStartPos(anvil.blockPosition());
		anvil.setDeltaMovement(xVel, yVel, zVel);
		anvil.dropItem = false;
		world.addFreshEntity(anvil);

	    }
	}

	return true;
    }

    @Override
    public IBlast getBlastType() {
	return SubtypeBlast.anvil;
    }
}
