package ballistix.common.blast.tier1;

import ballistix.api.blast.IBlast;
import ballistix.common.blast.util.Blast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.entity.EntityBallistixFallingBlock;
import ballistix.common.settings.BallistixConstants;
import net.minecraft.block.Blocks;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlastAnvil extends Blast {

    public BlastAnvil(World world, BlockPos position) {
        super(world, position);
    }

    @Override
    public boolean doExplode(int callCount) {
        super.doExplode(callCount);
        hasStarted = true;

        if (!world.isClientSide) {
            world.playSound(null, position, SoundEvents.GENERIC_EXPLODE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            for (int i = 0; i < BallistixConstants.EXPLOSIVE_ANVIL_ANVILSPERBLAST; i++) {

                float xVel = (world.random.nextBoolean() ? 1.0F : -1.0F) * world.random.nextFloat();
                float yVel = 1.0F;
                float zVel = (world.random.nextBoolean() ? 1.0F : -1.0F) * world.random.nextFloat();
                EntityBallistixFallingBlock anvil = new EntityBallistixFallingBlock(world, position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5, Blocks.ANVIL.defaultBlockState());
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
