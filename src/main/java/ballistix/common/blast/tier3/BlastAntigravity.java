package ballistix.common.blast.tier3;

import java.util.HashSet;

import ballistix.api.blast.AntigravedChunk;
import ballistix.api.blast.IBlast;
import ballistix.common.blast.util.Blast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.settings.BallistixConfig;
import ballistix.registers.BallistixAttachmentTypes;
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

        for(int i = -BallistixConfig.INSTANCE.EXPLOSIVE_ANTIGRAVITY_CHUNKRADIUS.get(); i < BallistixConfig.INSTANCE.EXPLOSIVE_ANTIGRAVITY_CHUNKRADIUS.get(); i++) {
            for(int j = -BallistixConfig.INSTANCE.EXPLOSIVE_ANTIGRAVITY_CHUNKRADIUS.get(); j < BallistixConfig.INSTANCE.EXPLOSIVE_ANTIGRAVITY_CHUNKRADIUS.get(); j++) {

                BlockPos pos = position.offset(i * 16, 0, j * 16);

                ChunkPos chunkPos = new ChunkPos(pos);

                HashSet<AntigravedChunk> set = world.getData(BallistixAttachmentTypes.ANTIGRAVED_CHUNKS);

                set.add(new AntigravedChunk(chunkPos, BallistixConfig.INSTANCE.EXPLOSIVE_ANTIGRAVITY_CHUNKDURATION.get()));

                world.setData(BallistixAttachmentTypes.ANTIGRAVED_CHUNKS, set);

            }

        }

        return true;
    }

}
