package ballistix.common.event;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import ballistix.Ballistix;
import ballistix.api.blast.AntigravedChunk;
import ballistix.api.missile.MissileManager;
import ballistix.common.command.CommandClearAntigravityChunks;
import ballistix.common.command.CommandClearBullets;
import ballistix.common.command.CommandClearMissiles;
import ballistix.common.command.CommandClearRailgunRounds;
import ballistix.common.command.CommandClearSAMs;
import ballistix.common.entity.EntityBallistixFallingBlock;
import ballistix.common.packet.type.client.PacketPushPlayer;
import ballistix.common.settings.BallistixConfig;
import ballistix.registers.BallistixAttachmentTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = Ballistix.ID, bus = EventBusSubscriber.Bus.GAME)
public class ServerEventHandler {

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        CommandClearMissiles.register(event.getDispatcher());
        CommandClearBullets.register(event.getDispatcher());
        CommandClearRailgunRounds.register(event.getDispatcher());
        CommandClearSAMs.register(event.getDispatcher());
        CommandClearAntigravityChunks.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void handleAntigravedChunks(LevelTickEvent.Post event) {

        Level level = event.getLevel();

        if(level.isClientSide() || !level.hasData(BallistixAttachmentTypes.ANTIGRAVED_CHUNKS)) {
            return;
        }

        HashSet<AntigravedChunk> chunks = level.getData(BallistixAttachmentTypes.ANTIGRAVED_CHUNKS);

        Iterator<AntigravedChunk> iterator = chunks.iterator();

        AntigravedChunk chunk;

        while(iterator.hasNext()) {
            chunk = iterator.next();

            if(!level.getChunkSource().hasChunk(chunk.getPos().x, chunk.getPos().z)) {
                continue;
            }

            chunk.decrementTime();

            ChunkAccess levelChunk = level.getChunk(chunk.getPos().x, chunk.getPos().z);

            AABB box = AABB.encapsulatingFullBlocks(new BlockPos(chunk.getPos().getMinBlockX(), levelChunk.getMinBuildHeight(), chunk.getPos().getMinBlockZ()), new BlockPos(chunk.getPos().getMaxBlockX(), levelChunk.getMaxBuildHeight(), chunk.getPos().getMaxBlockZ()));

            MissileManager.getMissilesForLevel(level.dimension()).forEach(missile -> {
                if(!missile.getBoundingBox().intersects(box) || missile.position.y > BallistixConfig.INSTANCE.EXPLOSIVE_ANTIGRAVITY_MAXHEIGHT.get()) {
                    return;
                }
                missile.position = missile.position.add(0, 0.04, 0);
            });

            MissileManager.getBulletsForLevel(level.dimension()).forEach(bullet -> {
                if(!bullet.getBoundingBox().intersects(box) || bullet.position.y > BallistixConfig.INSTANCE.EXPLOSIVE_ANTIGRAVITY_MAXHEIGHT.get()) {
                    return;
                }
                bullet.position = bullet.position.add(0, 0.04, 0);
            });

            MissileManager.getRailgunRoundsForLevel(level.dimension()).forEach(round -> {
                if(!round.getBoundingBox().intersects(box) || round.position.y > BallistixConfig.INSTANCE.EXPLOSIVE_ANTIGRAVITY_MAXHEIGHT.get()) {
                    return;
                }
                round.position = round.position.add(0, 0.04, 0);
            });

            MissileManager.getSAMsForLevel(level.dimension()).forEach(missile -> {
                if(!missile.getBoundingBox().intersects(box) || missile.position.y > BallistixConfig.INSTANCE.EXPLOSIVE_ANTIGRAVITY_MAXHEIGHT.get()) {
                    return;
                }
                missile.position = missile.position.add(0, 0.04, 0);
            });

            List<Entity> entities = level.getEntitiesOfClass(Entity.class, box);

            for(Entity entity : entities) {

                if(!entity.isAlive() || entity.getY() > BallistixConfig.INSTANCE.EXPLOSIVE_ANTIGRAVITY_MAXHEIGHT.get()) {
                    continue;
                }

                if(entity instanceof ServerPlayer server) {

                    PacketDistributor.sendToPlayer(server, new PacketPushPlayer(server.getUUID()));


                } else {

                    entity.push(entity.getDeltaMovement().x, entity.getGravity() * BallistixConfig.INSTANCE.EXPLOSIVE_ANTIGRAVITY_GRAVITYFACTOR.get(), entity.getDeltaMovement().z);

                }

            }

            for(int i = 0; i < BallistixConfig.INSTANCE.EXPLOSIVE_ANTIGRAVITY_MAXBLOCKCHECKS.get(); i++) {
                int x = level.random.nextIntBetweenInclusive(0, 16);
                int y = level.random.nextIntBetweenInclusive(levelChunk.getMinBuildHeight(), levelChunk.getMaxBuildHeight());
                int z = level.random.nextIntBetweenInclusive(0, 16);
                BlockPos pos = new BlockPos(chunk.getPos().getMinBlockX() + x, y, chunk.getPos().getMaxBlockZ() + z);

                BlockState state = level.getBlockState(pos);

                if(state.isAir() || state.liquid()) {
                    continue;
                }

                BlockState above = level.getBlockState(pos.above());

                if(above.isAir() || above.liquid()) {

                    EntityBallistixFallingBlock movingBlock = new EntityBallistixFallingBlock(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, state);
                    movingBlock.setDeltaMovement(0, movingBlock.getGravity() * BallistixConfig.INSTANCE.EXPLOSIVE_ANTIGRAVITY_GRAVITYFACTOR.get(), 0);

                    level.addFreshEntity(movingBlock);

                    level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());

                }


            }

            if(chunk.getTime() < 0) {
                iterator.remove();
            }

        }

        level.setData(BallistixAttachmentTypes.ANTIGRAVED_CHUNKS, chunks);


    }

}
