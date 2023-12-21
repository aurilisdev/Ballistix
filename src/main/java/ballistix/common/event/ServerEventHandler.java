package ballistix.common.event;

import ballistix.References;
import ballistix.api.capability.BallistixCapabilities;
import ballistix.api.capability.CapabilitySiloRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = References.ID, bus = Bus.FORGE)
public class ServerEventHandler {

	@SubscribeEvent
	public static void attachOverworldCapability(AttachCapabilitiesEvent<Level> event) {
		Level world = event.getObject();
		if (!world.getCapability(BallistixCapabilities.SILO_REGISTRY).isPresent() && world.dimension().equals(Level.OVERWORLD)) {
			event.addCapability(new ResourceLocation(References.ID, "siloregistry"), new CapabilitySiloRegistry());
		}
	}
	
}