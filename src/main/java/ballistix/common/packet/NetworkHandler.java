package ballistix.common.packet;

import ballistix.Ballistix;
import ballistix.References;
import ballistix.common.packet.type.client.PacketSetSearchRadarTrackedClient;
import ballistix.common.packet.type.client.particle.PacketSpawnBlastParticle;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = References.ID, bus = EventBusSubscriber.Bus.MOD)
public class NetworkHandler {
	private static final String PROTOCOL_VERSION = "1";

	@SubscribeEvent
	public static void registerPackets(final RegisterPayloadHandlersEvent event) {

		final PayloadRegistrar registry = event.registrar(References.ID).versioned(PROTOCOL_VERSION).optional();

		// CLIENTBOUND

		registry.playToClient(PacketSetSearchRadarTrackedClient.TYPE, PacketSetSearchRadarTrackedClient.CODEC, PacketSetSearchRadarTrackedClient::handle);
	        registry.playToClient(PacketSpawnBlastParticle.TYPE, PacketSpawnBlastParticle.CODEC, PacketSpawnBlastParticle::handle);

	}

	public static ResourceLocation id(String name) {
		return Ballistix.rl(name);
	}
}
