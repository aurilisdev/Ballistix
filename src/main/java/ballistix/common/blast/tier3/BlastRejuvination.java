package ballistix.common.blast.tier3;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.io.FileUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.Futures;

import ballistix.api.blast.IBlast;
import ballistix.common.blast.util.Blast;
import ballistix.common.block.subtype.SubtypeBlast;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.LoggerChunkProgressListener;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.storage.ServerLevelData;
import voltaic.Voltaic;

public class BlastRejuvination extends Blast {
    public BlastRejuvination(Level world, BlockPos position) {
        super(world, position);
    }

    @Override
    public void doPreExplode() {
        if(!world.isClientSide) {
            world.playSound(null, position, SoundEvents.WITHER_SPAWN, SoundSource.BLOCKS, 25, 1);
        }
    }

    @Override
    public IBlast getBlastType() {
        return SubtypeBlast.rejuvination;
    }
    
    @Override
    public boolean doExplode(int callCount) {
    	if(world.isClientSide) {
            return true;
        }

        // Based upon implementation from World Edit
        // https://github.com/EngineHub/WorldEdit/blob/version/7.3.x/worldedit-neoforge/src/main/java/com/sk89q/worldedit/neoforge/NeoForgeWorld.java#L228

        try {

            Path tempDir = Paths.get("BallistixRejuvinationBlast");
            LevelStorageSource levelStorage = LevelStorageSource.createDefault(tempDir);

            try {

                LevelStorageSource.LevelStorageAccess session = levelStorage.createAccess("BallistixRejuvinationBlast");

                ServerLevel currentWorld = (ServerLevel) world;

                PrimaryLevelData levelProperties = (PrimaryLevelData) currentWorld.getServer().getWorldData().overworldData();

                try {

                    ServerLevel newWorld = new ServerLevel(
                            //
                            currentWorld.getServer(),
                            //
                            Util.backgroundExecutor(),
                            //
                            session,
                            //
                            (ServerLevelData) currentWorld.getLevelData(),
                            //
                            currentWorld.dimension(),
                            //
                            currentWorld.dimensionTypeRegistration(),
                            //
                            new LoggerChunkProgressListener(32),
                            //
                            currentWorld.getChunkSource().getGenerator(),
                            //
                            currentWorld.isDebug(),
                            //
                            levelProperties.worldGenSettings().seed(),
                            //
                            ImmutableList.of(),
                            //
                            false
                            //
                    );

                    ChunkPos pos = new ChunkPos(position);

                    CompletableFuture<ChunkAccess> loadedChunk = newWorld.getChunkSource().getChunkFuture(pos.x, pos.z, ChunkStatus.FEATURES, true).thenApply(either -> either.orThrow());

                    BlockableEventLoop<Runnable> executor = newWorld.getChunkSource().mainThreadProcessor;

                    executor.managedBlock(() -> {
                        if (loadedChunk.isDone() && Futures.getUnchecked(loadedChunk) == null) {
                            return false;
                        }
                        return loadedChunk.isDone();
                    });

                    ChunkAccess newChunk = loadedChunk.getNow(null);
                    ChunkAccess currChunk = currentWorld.getChunk(pos.x, pos.z);

                    BlockPos start = new BlockPos(pos.getMinBlockX(), newWorld.getMinBuildHeight(), pos.getMinBlockZ());
                    BlockPos end = new BlockPos(pos.getMaxBlockX(), newWorld.getMaxBuildHeight(), pos.getMaxBlockZ());

                    BlockPos.betweenClosedStream(start, end).forEach(blockPos -> {

                        BlockState newState = newChunk.getBlockState(blockPos);
                        BlockState currState = currChunk.getBlockState(blockPos);

                        if(newState == currState) {
                            return;
                        }

                        currentWorld.setBlockAndUpdate(blockPos, newState);

                    });


                    while(currentWorld.getServer().pollTask()) {
                        Thread.yield();
                    }

                } catch(Exception e) {
                    e.printStackTrace();
                    Voltaic.LOGGER.info("Rejuvenation Blast at " + position + " has failed.");
                }

            } catch(Exception e) {
                e.printStackTrace();
                Voltaic.LOGGER.info("Rejuvenation Blast at " + position + " has failed.");
            } finally {
                FileUtils.deleteDirectory(new File(tempDir.toUri()));
                if(Files.exists(tempDir)) {
                    Voltaic.LOGGER.info(tempDir + " still exists!");
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
            Voltaic.LOGGER.info("Rejuvenation Blast at " + position + " has failed.");
        }

        return true;
    }

}
