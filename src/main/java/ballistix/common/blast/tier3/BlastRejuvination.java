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
import net.minecraft.block.BlockState;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.concurrent.ThreadTaskExecutor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.chunk.listener.LoggingChunkStatusListener;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.IServerWorldInfo;
import net.minecraft.world.storage.SaveFormat;
import net.minecraft.world.storage.ServerWorldInfo;
import voltaic.Voltaic;

public class BlastRejuvination extends Blast {
	
    public BlastRejuvination(World world, BlockPos position) {
        super(world, position);
    }

    @Override
    public void doPreExplode() {
        if(!world.isClientSide) {
            world.playSound(null, position, SoundEvents.WITHER_SPAWN, SoundCategory.BLOCKS, 25, 1);
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
            SaveFormat levelStorage = SaveFormat.createDefault(tempDir);

            try {

                SaveFormat.LevelSave session = levelStorage.createAccess("BallistixRejuvinationBlast");

                ServerWorld currentWorld = (ServerWorld) world;

                ServerWorldInfo levelProperties = (ServerWorldInfo) currentWorld.getServer().getWorldData().overworldData();

                try {

                    ServerWorld newWorld = new ServerWorld(
                            //
                            currentWorld.getServer(),
                            //
                            Util.backgroundExecutor(),
                            //
                            session,
                            //
                            (IServerWorldInfo) currentWorld.getLevelData(),
                            //
                            currentWorld.dimension(),
                            //
                            currentWorld.dimensionType(),
                            //
                            new LoggingChunkStatusListener(32),
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

                    CompletableFuture<IChunk> loadedChunk = newWorld.getChunkSource().getChunkFuture(pos.x, pos.z, ChunkStatus.FEATURES, true).thenApply(either -> either.orThrow());

                    ThreadTaskExecutor<Runnable> executor = newWorld.getChunkSource().mainThreadProcessor;

                    executor.managedBlock(() -> {
                        if (loadedChunk.isDone() && Futures.getUnchecked(loadedChunk) == null) {
                            return false;
                        }
                        return loadedChunk.isDone();
                    });

                    IChunk newChunk = loadedChunk.getNow(null);
                    IChunk currChunk = currentWorld.getChunk(pos.x, pos.z);

                    BlockPos start = new BlockPos(pos.getMinBlockX(), 0, pos.getMinBlockZ());
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
