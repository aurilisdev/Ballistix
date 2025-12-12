package ballistix.common.blast.tierother;

import ballistix.api.blast.IHasCustomRender;
import ballistix.common.blast.util.Blast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.settings.BallistixConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class BlastObsidian extends Blast implements IHasCustomRender {

    public BlastObsidian(Level world, BlockPos position, Entity owner) {
	super(world, position, owner);
    }

    @Override
    public boolean doExplode(int callCount) {
	super.doExplode(callCount);
	hasStarted = true;
	if (!world.isClientSide) {
	    world.explode(null, position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5,
		    (float) BallistixConfig.INSTANCE.EXPLOSIVE_OBSIDIAN_SIZE.getAsDouble(), ExplosionInteraction.BLOCK);
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
