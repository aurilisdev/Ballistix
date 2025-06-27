package ballistix.common.blast.tier3;

import ballistix.api.blast.IBlast;
import ballistix.common.blast.util.Blast;
import ballistix.common.block.subtype.SubtypeBlast;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.status.ChunkStatus;

public class BlastRejuvination extends Blast {
    public BlastRejuvination(Level world, BlockPos position) {
        super(world, position);
    }

    @Override
    public void doPreExplode() {
        if(!world.isClientSide) {
            world.playSound(null, position, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 25, 1);
        }
    }

    @Override
    public IBlast getBlastType() {
        return SubtypeBlast.rejuvination;
    }

    @Override
    public boolean doExplode(int callCount) {
        super.doExplode(callCount);

        if(world.isClientSide) {
            return true;
        }

        ChunkPos pos = new ChunkPos(position);

        ServerChunkCache cache = (ServerChunkCache) world.getChunkSource();

        for(ChunkStatus status : ChunkStatus.getStatusList()) {
            cache.chunkMap.scheduleGenerationTask(status, pos);
        }

        cache.chunkMap.runGenerationTasks();

        cache.save(true);

        //cache.chunkMap.getPlayers(pos, false).forEach(pl -> cache.broadcastAndSend(pl, new ClientboundLevelChunkWithLightPacket(cache.chunkMap.getChunkToSend(pos.toLong()), world.getLightEngine(), null, null)));

        return true;
    }
}
