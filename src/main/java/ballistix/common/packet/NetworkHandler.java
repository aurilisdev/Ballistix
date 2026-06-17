package ballistix.common.packet;

import java.util.Optional;

import ballistix.Ballistix;
import ballistix.common.packet.type.client.PacketPushPlayer;
import ballistix.common.packet.type.client.PacketSetSearchRadarTrackedClient;
import ballistix.common.packet.type.client.particle.PacketSpawnBlastParticle;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
	    new ResourceLocation(Ballistix.ID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals,
	    PROTOCOL_VERSION::equals);
    private static int disc = 0;

    public static void init() {
	CHANNEL.registerMessage(disc++, PacketSetSearchRadarTrackedClient.class,
		PacketSetSearchRadarTrackedClient::encode, PacketSetSearchRadarTrackedClient::decode,
		PacketSetSearchRadarTrackedClient::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
	CHANNEL.registerMessage(disc++, PacketSpawnBlastParticle.class, PacketSpawnBlastParticle::encode,
		PacketSpawnBlastParticle::decode, PacketSpawnBlastParticle::handle,
		Optional.of(NetworkDirection.PLAY_TO_CLIENT));
	CHANNEL.registerMessage(disc++, PacketPushPlayer.class, PacketPushPlayer::encode, PacketPushPlayer::decode,
		PacketPushPlayer::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }
}
