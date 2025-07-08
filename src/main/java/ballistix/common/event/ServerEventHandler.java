package ballistix.common.event;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import ballistix.Ballistix;
import ballistix.api.blast.AntigravedChunk;
import ballistix.api.capability.CapabilityActiveBullets;
import ballistix.api.capability.CapabilityActiveMissiles;
import ballistix.api.capability.CapabilityActiveRailgunRounds;
import ballistix.api.capability.CapabilityActiveSAMs;
import ballistix.api.capability.CapabilityAntigravedChunks;
import ballistix.api.capability.CapabilitySiloRegistry;
import ballistix.api.missile.MissileManager;
import ballistix.common.command.CommandClearAntigravityChunks;
import ballistix.common.command.CommandClearBullets;
import ballistix.common.command.CommandClearMissiles;
import ballistix.common.command.CommandClearRailgunRounds;
import ballistix.common.command.CommandClearSAMs;
import ballistix.common.entity.EntityBallistixFallingBlock;
import ballistix.common.packet.NetworkHandler;
import ballistix.common.packet.type.client.PacketPushPlayer;
import ballistix.common.settings.BallistixConstants;
import ballistix.registers.BallistixCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent.LevelTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.network.NetworkDirection;

@EventBusSubscriber(modid = Ballistix.ID, bus = Bus.FORGE)
public class ServerEventHandler {

	@SubscribeEvent
	public static void attachOverworldCapability(AttachCapabilitiesEvent<Level> event) {
		Level world = event.getObject();
		if (!world.getCapability(BallistixCapabilities.SILO_REGISTRY).isPresent() && world.dimension().equals(Level.OVERWORLD)) {
			event.addCapability(new ResourceLocation(Ballistix.ID, "siloregistry"), new CapabilitySiloRegistry());
		}

		if (!world.getCapability(BallistixCapabilities.ACTIVE_MISSILES).isPresent() && world.dimension().equals(Level.OVERWORLD)) {
			event.addCapability(new ResourceLocation(Ballistix.ID, "activemissiles"), new CapabilityActiveMissiles());
		}

		if (!world.getCapability(BallistixCapabilities.ACTIVE_BULLETS).isPresent() && world.dimension().equals(Level.OVERWORLD)) {
			event.addCapability(new ResourceLocation(Ballistix.ID, "activebullets"), new CapabilityActiveBullets());
		}

		if (!world.getCapability(BallistixCapabilities.ACTIVE_RAILGUN_ROUNDS).isPresent() && world.dimension().equals(Level.OVERWORLD)) {
			event.addCapability(new ResourceLocation(Ballistix.ID, "activerailgunrounds"), new CapabilityActiveRailgunRounds());
		}

		if (!world.getCapability(BallistixCapabilities.ACTIVE_SAMS).isPresent() && world.dimension().equals(Level.OVERWORLD)) {
			event.addCapability(new ResourceLocation(Ballistix.ID, "activesams"), new CapabilityActiveSAMs());
		}

		if (!world.getCapability(BallistixCapabilities.ANTIGRAVED_CHUNKS).isPresent()) {
			event.addCapability(new ResourceLocation(Ballistix.ID, "antigravedchunks"), new CapabilityAntigravedChunks());
		}
	}

	@SubscribeEvent
	public static void registerCommands(RegisterCommandsEvent event) {
		CommandClearMissiles.register(event.getDispatcher());
		CommandClearBullets.register(event.getDispatcher());
		CommandClearRailgunRounds.register(event.getDispatcher());
		CommandClearSAMs.register(event.getDispatcher());
		CommandClearAntigravityChunks.register(event.getDispatcher());
	}

	@SubscribeEvent
	public static void handleAntigravedChunks(LevelTickEvent event) {

		if (event.phase == Phase.START) {
			return;
		}

		Level level = event.level;

		if (level.isClientSide()) {
			return;
		}

		LazyOptional<CapabilityAntigravedChunks> lazy = level.getCapability(BallistixCapabilities.ANTIGRAVED_CHUNKS);

		if (!lazy.isPresent()) {
			return;
		}

		HashSet<AntigravedChunk> chunks = lazy.resolve().get().activeChunks;

		Iterator<AntigravedChunk> iterator = chunks.iterator();

		AntigravedChunk chunk;

		while (iterator.hasNext()) {
			chunk = iterator.next();

			if (!level.getChunkSource().hasChunk(chunk.getPos().x, chunk.getPos().z)) {
				continue;
			}

			chunk.decrementTime();

			ChunkAccess levelChunk = level.getChunk(chunk.getPos().x, chunk.getPos().z);

			AABB box = new AABB(new BlockPos(chunk.getPos().getMinBlockX(), levelChunk.getMinBuildHeight(), chunk.getPos().getMinBlockZ()), new BlockPos(chunk.getPos().getMaxBlockX(), levelChunk.getMaxBuildHeight(), chunk.getPos().getMaxBlockZ()).offset(1, 1, 1));

			MissileManager.getMissilesForLevel(level.dimension()).forEach(missile -> {
				if (!missile.getBoundingBox().intersects(box) || missile.position.y > BallistixConstants.EXPLOSIVE_ANTIGRAVITY_MAXHEIGHT) {
					return;
				}
				missile.position = missile.position.add(0, 0.04, 0);
			});

			MissileManager.getBulletsForLevel(level.dimension()).forEach(bullet -> {
				if (!bullet.getBoundingBox().intersects(box) || bullet.position.y > BallistixConstants.EXPLOSIVE_ANTIGRAVITY_MAXHEIGHT) {
					return;
				}
				bullet.position = bullet.position.add(0, 0.04, 0);
			});

			MissileManager.getRailgunRoundsForLevel(level.dimension()).forEach(round -> {
				if (!round.getBoundingBox().intersects(box) || round.position.y > BallistixConstants.EXPLOSIVE_ANTIGRAVITY_MAXHEIGHT) {
					return;
				}
				round.position = round.position.add(0, 0.04, 0);
			});

			MissileManager.getSAMsForLevel(level.dimension()).forEach(missile -> {
				if (!missile.getBoundingBox().intersects(box) || missile.position.y > BallistixConstants.EXPLOSIVE_ANTIGRAVITY_MAXHEIGHT) {
					return;
				}
				missile.position = missile.position.add(0, 0.04, 0);
			});

			List<Entity> entities = level.getEntitiesOfClass(Entity.class, box);

			for (Entity entity : entities) {

				if (!entity.isAlive() || entity.getY() > BallistixConstants.EXPLOSIVE_ANTIGRAVITY_MAXHEIGHT) {
					continue;
				}

				if (entity instanceof ServerPlayer server) {

					NetworkHandler.CHANNEL.sendTo(new PacketPushPlayer(server.getUUID()), server.connection.connection, NetworkDirection.PLAY_TO_CLIENT);

				} else {

					entity.push(entity.getDeltaMovement().x, 0.1 * BallistixConstants.EXPLOSIVE_ANTIGRAVITY_GRAVITYFACTOR, entity.getDeltaMovement().z);

				}

			}

			for (int i = 0; i < BallistixConstants.EXPLOSIVE_ANTIGRAVITY_MAXBLOCKCHECKS; i++) {
				int x = level.random.nextIntBetweenInclusive(0, 16);
				int y = level.random.nextIntBetweenInclusive(levelChunk.getMinBuildHeight(), levelChunk.getMaxBuildHeight());
				int z = level.random.nextIntBetweenInclusive(0, 16);
				BlockPos pos = new BlockPos(chunk.getPos().getMinBlockX() + x, y, chunk.getPos().getMaxBlockZ() + z);

				BlockState state = level.getBlockState(pos);

				if (state.isAir() || state.liquid()) {
					continue;
				}

				BlockState above = level.getBlockState(pos.above());

				if (above.isAir() || above.liquid()) {

					EntityBallistixFallingBlock movingBlock = new EntityBallistixFallingBlock(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, state);
					movingBlock.setDeltaMovement(0, 0.04 * BallistixConstants.EXPLOSIVE_ANTIGRAVITY_GRAVITYFACTOR, 0);

					level.addFreshEntity(movingBlock);

					level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());

				}

			}

			if (chunk.getTime() < 0) {
				iterator.remove();
			}

		}

	}

}
