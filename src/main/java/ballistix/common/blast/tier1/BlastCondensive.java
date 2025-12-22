package ballistix.common.blast.tier1;

import javax.annotation.Nullable;

import ballistix.api.blast.IHasCustomRender;
import ballistix.common.blast.util.Blast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.settings.BallistixConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlastCondensive extends Blast implements IHasCustomRender {

    public BlastCondensive(Level world, BlockPos position, @Nullable Entity owner, @Nullable Entity blastEntity) {
	super(world, position, owner, blastEntity);
    }

    @Override
    public boolean doExplode(int callCount) {
	super.doExplode(callCount);
	hasStarted = true;
	if (!world.isClientSide) {
	    world.explode(blastEntity, world.damageSources().explosion(blastEntity, owner), null, position.getX() + 0.5,
		    position.getY() + 0.5, position.getZ() + 0.5, (float) BallistixConstants.EXPLOSIVE_CONDENSIVE_SIZE,
		    false, ExplosionInteraction.BLOCK);
	}
	return true;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void produceParticles() {
    }

    @Override
    public SubtypeBlast getBlastType() {
	return SubtypeBlast.condensive;
    }

}
