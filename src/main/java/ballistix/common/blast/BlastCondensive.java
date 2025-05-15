package ballistix.common.blast;

import ballistix.api.blast.IHasCustomRender;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.settings.BallistixConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlastCondensive extends Blast implements IHasCustomRender {

    public BlastCondensive(Level world, BlockPos position) {
	super(world, position);
    }

    @Override
    public boolean doExplode(int callCount) {
	super.doExplode(callCount);
	hasStarted = true;
	if (!world.isClientSide) {
	    world.explode(null, position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5,
		    (float) BallistixConstants.EXPLOSIVE_CONDENSIVE_SIZE, BlockInteraction.BREAK);
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
