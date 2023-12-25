package ballistix.common.event;

import ballistix.References;
import ballistix.api.capability.BallistixCapabilities;
import ballistix.api.capability.CapabilitySiloRegistry;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = References.ID, bus = Bus.FORGE)
public class ServerEventHandler {

	@SubscribeEvent
	public static void attachOverworldCapability(AttachCapabilitiesEvent<World> event) {
		World world = event.getObject();
		if (!world.getCapability(BallistixCapabilities.SILO_REGISTRY).isPresent() && world.dimension().equals(World.OVERWORLD)) {
			event.addCapability(new ResourceLocation(References.ID, "siloregistry"), new CapabilitySiloRegistry());
		}
	}

}