package ballistix.common.packet;

import java.util.Optional;

import ballistix.References;
import ballistix.common.packet.types.client.PacketSetSearchRadarTrackedClient;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(References.ID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
	private static int disc = 0;

	public static void init() {
		CHANNEL.registerMessage(disc++, PacketSetSearchRadarTrackedClient.class, PacketSetSearchRadarTrackedClient::encode, PacketSetSearchRadarTrackedClient::decode, PacketSetSearchRadarTrackedClient::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
	}
}