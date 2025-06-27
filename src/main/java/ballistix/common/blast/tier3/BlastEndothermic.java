package ballistix.common.blast.tier3;

import ballistix.api.blast.IBlast;
import ballistix.common.blast.util.Blast;
import ballistix.common.block.subtype.SubtypeBlast;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlastEndothermic extends Blast {
	
    public BlastEndothermic(World world, BlockPos position) {
        super(world, position);
    }

    @Override
    public IBlast getBlastType() {
        return SubtypeBlast.endothermic;
    }
}
