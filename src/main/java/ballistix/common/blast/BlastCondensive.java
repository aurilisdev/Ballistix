package ballistix.common.blast;

import ballistix.common.block.SubtypeBlast;
import ballistix.common.settings.Constants;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion.Mode;
import net.minecraft.world.World;

public class BlastCondensive extends Blast {

    public BlastCondensive(World world, BlockPos position) {
	super(world, position);
    }

    @Override
    public boolean doExplode(int callCount) {
	if (!world.isRemote) {
	    world.createExplosion(null, position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5,
		    (float) Constants.EXPLOSIVE_CONDENSIVE_SIZE, Mode.BREAK);
	}
	return true;
    }

    @Override
    public SubtypeBlast getBlastType() {
	return SubtypeBlast.condensive;
    }

}
