package ballistix.common.event;

import ballistix.Ballistix;
import ballistix.api.capability.CapabilityActiveBullets;
import ballistix.api.capability.CapabilityActiveMissiles;
import ballistix.api.capability.CapabilityActiveRailgunRounds;
import ballistix.api.capability.CapabilityActiveSAMs;
import ballistix.api.capability.CapabilitySiloRegistry;
import ballistix.common.command.CommandClearBullets;
import ballistix.common.command.CommandClearMissiles;
import ballistix.common.command.CommandClearRailgunRounds;
import ballistix.common.command.CommandClearSAMs;
import ballistix.registers.BallistixCapabilities;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

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
	}
	
	@SubscribeEvent
	public static void registerCommands(RegisterCommandsEvent event) {
		CommandClearMissiles.register(event.getDispatcher());
		CommandClearBullets.register(event.getDispatcher());
		CommandClearRailgunRounds.register(event.getDispatcher());
		CommandClearSAMs.register(event.getDispatcher());
	}

}
