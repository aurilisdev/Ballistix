package ballistix.common.event;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import ballistix.References;
import ballistix.api.capability.BallistixCapabilities;
import ballistix.api.capability.CapabilityActiveBullets;
import ballistix.api.capability.CapabilityActiveMissiles;
import ballistix.api.capability.CapabilityActiveRailgunRounds;
import ballistix.api.capability.CapabilityActiveSAMs;
import ballistix.api.capability.CapabilitySiloRegistry;
import ballistix.common.command.CommandClearBullets;
import ballistix.common.command.CommandClearMissiles;
import ballistix.common.command.CommandClearRailgunRounds;
import ballistix.common.command.CommandClearSAMs;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = References.ID, bus = Bus.FORGE)
public class ServerEventHandler {

	private static final ConcurrentHashMap<ServerWorld, HashSet<Integer>> VALID_UUIDS = new ConcurrentHashMap<>();

	@SubscribeEvent
	public static void attachOverworldCapability(AttachCapabilitiesEvent<World> event) {
		World world = event.getObject();
		if (!world.getCapability(BallistixCapabilities.SILO_REGISTRY).isPresent() && world.dimension().equals(World.OVERWORLD)) {
			event.addCapability(new ResourceLocation(References.ID, "siloregistry"), new CapabilitySiloRegistry());
		}
		if (!world.getCapability(BallistixCapabilities.ACTIVE_MISSILES).isPresent() && world.dimension().equals(World.OVERWORLD)) {
			event.addCapability(new ResourceLocation(References.ID, "activemissiles"), new CapabilityActiveMissiles());
		}
		
		if (!world.getCapability(BallistixCapabilities.ACTIVE_BULLETS).isPresent() && world.dimension().equals(World.OVERWORLD)) {
			event.addCapability(new ResourceLocation(References.ID, "activebullets"), new CapabilityActiveBullets());
		}
		
		if (!world.getCapability(BallistixCapabilities.ACTIVE_RAILGUN_ROUNDS).isPresent() && world.dimension().equals(World.OVERWORLD)) {
			event.addCapability(new ResourceLocation(References.ID, "activerailgunrounds"), new CapabilityActiveRailgunRounds());
		}
		
		if (!world.getCapability(BallistixCapabilities.ACTIVE_SAMS).isPresent() && world.dimension().equals(World.OVERWORLD)) {
			event.addCapability(new ResourceLocation(References.ID, "activesams"), new CapabilityActiveSAMs());
		}
	}
	
	@SubscribeEvent
	public static void registerCommands(RegisterCommandsEvent event) {
		CommandClearMissiles.register(event.getDispatcher());
		CommandClearBullets.register(event.getDispatcher());
		CommandClearRailgunRounds.register(event.getDispatcher());
		CommandClearSAMs.register(event.getDispatcher());
	}

	@SubscribeEvent
	public static void handleScanner(ServerTickEvent event) {
		for (Entry<ServerWorld, HashSet<Integer>> en : VALID_UUIDS.entrySet()) {
			Iterator<Integer> it = en.getValue().iterator();
			while (it.hasNext()) {
				int uuid = it.next();
				Entity ent = en.getKey().getEntity(uuid);
				if (ent == null || ent.isAlive()) {
					it.remove();
				}
			}
		}
	}
	
	public static void addTracked(ServerWorld world, int uuid) {
		HashSet<Integer> levelTracked = VALID_UUIDS.getOrDefault(world, new HashSet<>());
		levelTracked.add(uuid);
		VALID_UUIDS.put(world, levelTracked);
	}
	
	public static boolean isTracked(ServerWorld world, int uuid) {
		return VALID_UUIDS.getOrDefault(world, new HashSet<>()).contains(uuid);
	}
	
	public static Set<Entry<ServerWorld, HashSet<Integer>>> getTrackedData(){
		return VALID_UUIDS.entrySet();
	}

}