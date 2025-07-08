package ballistix.common.blast.tier3;

import ballistix.api.blast.AntigravedChunk;
import ballistix.api.blast.IBlast;
import ballistix.common.blast.util.Blast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.settings.BallistixConstants;
import ballistix.registers.BallistixCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

public class BlastAntigravity extends Blast {
    public BlastAntigravity(Level world, BlockPos position) {
        super(world, position);
    }

    @Override
    public IBlast getBlastType() {
        return SubtypeBlast.antigravity;
    }

    @Override
    public void doPreExplode() {
        if(!world.isClientSide) {
            world.playSound(null, position, SoundEvents.END_PORTAL_SPAWN, SoundSource.BLOCKS, 25, 1);
        }
    }

    @Override
    public boolean doExplode(int callCount) {
        super.doExplode(callCount);

        if(world.isClientSide()) {
            return false;
        }

        for(int i = -BallistixConstants.EXPLOSIVE_ANTIGRAVITY_CHUNKRADIUS; i < BallistixConstants.EXPLOSIVE_ANTIGRAVITY_CHUNKRADIUS; i++) {
            for(int j = -BallistixConstants.EXPLOSIVE_ANTIGRAVITY_CHUNKRADIUS; j < BallistixConstants.EXPLOSIVE_ANTIGRAVITY_CHUNKRADIUS; j++) {

                BlockPos pos = position.offset(i * 16, 0, j * 16);

                ChunkPos chunkPos = new ChunkPos(pos);
                
                world.getCapability(BallistixCapabilities.ANTIGRAVED_CHUNKS).ifPresent(cap -> {
                	cap.activeChunks.add(new AntigravedChunk(chunkPos, BallistixConstants.EXPLOSIVE_ANTIGRAVITY_CHUNKDURATION));
                });

            }

        }

        return true;
    }

}
