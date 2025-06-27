package ballistix.common.blast.tier3;

import ballistix.api.blast.IBlast;
import ballistix.common.blast.util.Blast;
import ballistix.common.block.subtype.SubtypeBlast;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlastRejuvination extends Blast {
	
    public BlastRejuvination(World world, BlockPos position) {
        super(world, position);
    }

    @Override
    public void doPreExplode() {
        if(!world.isClientSide) {
            world.playSound(null, position, SoundEvents.GENERIC_EXPLODE, SoundCategory.BLOCKS, 25, 1);
        }
    }

    @Override
    public IBlast getBlastType() {
        return SubtypeBlast.rejuvination;
    }

}
