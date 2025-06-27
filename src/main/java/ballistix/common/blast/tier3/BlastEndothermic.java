package ballistix.common.blast.tier3;

import ballistix.api.blast.IBlast;
import ballistix.common.blast.util.Blast;
import ballistix.common.block.subtype.SubtypeBlast;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class BlastEndothermic extends Blast {
    public BlastEndothermic(Level world, BlockPos position) {
        super(world, position);
    }

    @Override
    public IBlast getBlastType() {
        return SubtypeBlast.endothermic;
    }
}
