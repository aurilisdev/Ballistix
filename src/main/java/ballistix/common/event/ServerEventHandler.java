package ballistix.common.event;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

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
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.network.NetworkDirection;

@EventBusSubscriber(modid = Ballistix.ID, bus = Bus.FORGE)
public class ServerEventHandler {

	@SubscribeEvent
	public static void attachOverworldCapability(AttachCapabilitiesEvent<World> event) {
		World world = event.getObject();
		if (!world.getCapability(BallistixCapabilities.SILO_REGISTRY).isPresent() && world.dimension().equals(World.OVERWORLD)) {
			event.addCapability(new ResourceLocation(Ballistix.ID, "siloregistry"), new CapabilitySiloRegistry());
		}
		
		if (!world.getCapability(BallistixCapabilities.ACTIVE_MISSILES).isPresent() && world.dimension().equals(World.OVERWORLD)) {
			event.addCapability(new ResourceLocation(Ballistix.ID, "activemissiles"), new CapabilityActiveMissiles());
		}
		
		if (!world.getCapability(BallistixCapabilities.ACTIVE_BULLETS).isPresent() && world.dimension().equals(World.OVERWORLD)) {
			event.addCapability(new ResourceLocation(Ballistix.ID, "activebullets"), new CapabilityActiveBullets());
		}
		
		if (!world.getCapability(BallistixCapabilities.ACTIVE_RAILGUN_ROUNDS).isPresent() && world.dimension().equals(World.OVERWORLD)) {
			event.addCapability(new ResourceLocation(Ballistix.ID, "activerailgunrounds"), new CapabilityActiveRailgunRounds());
		}
		
		if (!world.getCapability(BallistixCapabilities.ACTIVE_SAMS).isPresent() && world.dimension().equals(World.OVERWORLD)) {
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
	public static void handleAntigravedChunks(WorldTickEvent event) {
		
		if (event.phase == Phase.START) {
			return;
		}

		World level = event.world;

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

			Chunk levelChunk = level.getChunk(chunk.getPos().x, chunk.getPos().z);

			AxisAlignedBB box = new AxisAlignedBB(new BlockPos(chunk.getPos().getMinBlockX(), 0, chunk.getPos().getMinBlockZ()), new BlockPos(chunk.getPos().getMaxBlockX(), levelChunk.getMaxBuildHeight(), chunk.getPos().getMaxBlockZ()).offset(1, 1, 1));

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

				if (entity instanceof ServerPlayerEntity) {
					
					ServerPlayerEntity server = (ServerPlayerEntity) entity;

					NetworkHandler.CHANNEL.sendTo(new PacketPushPlayer(server.getUUID()), server.connection.connection, NetworkDirection.PLAY_TO_CLIENT);

				} else {

					entity.push(entity.getDeltaMovement().x, 0.1 * BallistixConstants.EXPLOSIVE_ANTIGRAVITY_GRAVITYFACTOR, entity.getDeltaMovement().z);

				}

			}
			

			for (int i = 0; i < BallistixConstants.EXPLOSIVE_ANTIGRAVITY_MAXBLOCKCHECKS; i++) {
				int x = boundedNextInt(level.random, 0, 17);
				int y = boundedNextInt(level.random, 0, levelChunk.getMaxBuildHeight() + 1);
				int z = boundedNextInt(level.random, 0, 17);
				BlockPos pos = new BlockPos(chunk.getPos().getMinBlockX() + x, y, chunk.getPos().getMaxBlockZ() + z);

				BlockState state = level.getBlockState(pos);

				if (state.isAir(level, pos) || state.getBlock() instanceof FlowingFluidBlock) {
					continue;
				}

				BlockState above = level.getBlockState(pos.above());

				if (above.isAir(level, pos.above()) || above.getBlock() instanceof FlowingFluidBlock) {

					EntityBallistixFallingBlock movingBlock = new EntityBallistixFallingBlock(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, state);
					movingBlock.setDeltaMovement(0, 0.01 * BallistixConstants.EXPLOSIVE_ANTIGRAVITY_GRAVITYFACTOR, 0);

					level.addFreshEntity(movingBlock);

					level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());

				}

			}

			if (chunk.getTime() < 0) {
				iterator.remove();
			}
			

		}

	}
	
	public static int boundedNextInt(Random rng, int origin, int bound) {
        int r = rng.nextInt();
        if (origin < bound) {
            // It's not case (1).
            final int n = bound - origin;
            final int m = n - 1;
            if ((n & m) == 0) {
                // It is case (2): length of range is a power of 2.
                r = (r & m) + origin;
            } else if (n > 0) {
                // It is case (3): need to reject over-represented candidates.
                for (int u = r >>> 1;
                     u + m - (r = u % n) < 0;
                     u = rng.nextInt() >>> 1)
                    ;
                r += origin;
            }
            else {
                // It is case (4): length of range not representable as long.
                while (r < origin || r >= bound) {
                    r = rng.nextInt();
                }
            }
        }
        return r;
    }

}
