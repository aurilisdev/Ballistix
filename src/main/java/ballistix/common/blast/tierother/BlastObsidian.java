package ballistix.common.blast.tierother;

import ballistix.api.blast.IHasCustomRender;
import ballistix.common.blast.util.Blast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.settings.BallistixConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlastObsidian extends Blast implements IHasCustomRender {

    public BlastObsidian(Level world, BlockPos position) {
        super(world, position);
    }

    @Override
    public boolean doExplode(int callCount) {
        super.doExplode(callCount);
        hasStarted = true;
        if (!world.isClientSide) {
            world.explode(null, position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5, (float) BallistixConstants.EXPLOSIVE_OBSIDIAN_SIZE, ExplosionInteraction.BLOCK);
        } else {
            produceParticles();
        }
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void produceParticles() {
    }

    @Override
    public SubtypeBlast getBlastType() {
        return SubtypeBlast.obsidian;
    }

}
