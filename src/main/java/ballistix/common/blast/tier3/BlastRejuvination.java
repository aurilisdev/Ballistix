package ballistix.common.blast.tier3;

import ballistix.api.blast.IBlast;
import ballistix.common.blast.util.Blast;
import ballistix.common.block.subtype.SubtypeBlast;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;

public class BlastRejuvination extends Blast {
    public BlastRejuvination(Level world, BlockPos position) {
        super(world, position);
    }

    @Override
    public void doPreExplode() {
        if(!world.isClientSide) {
            world.playSound(null, position, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 25, 1);
        }
    }

    @Override
    public IBlast getBlastType() {
        return SubtypeBlast.rejuvination;
    }

}
