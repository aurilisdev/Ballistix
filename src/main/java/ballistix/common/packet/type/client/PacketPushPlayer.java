package ballistix.common.packet.type.client;

import java.util.UUID;

import ballistix.common.packet.NetworkHandler;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PacketPushPlayer implements CustomPacketPayload {

    public static final ResourceLocation PACKET_SETSEARCHRADARTRACKEDCLIENT_PACKETID = NetworkHandler
	    .id("packetpushplayer");
    public static final Type<PacketPushPlayer> TYPE = new Type<>(PACKET_SETSEARCHRADARTRACKEDCLIENT_PACKETID);

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketPushPlayer> CODEC = new StreamCodec<>() {

	@Override
	public PacketPushPlayer decode(RegistryFriendlyByteBuf buf) {
	    return new PacketPushPlayer(buf.readUUID());
	}

	@Override
	public void encode(RegistryFriendlyByteBuf buf, PacketPushPlayer packet) {
	    buf.writeUUID(packet.id);
	}
    };

    private final UUID id;

    public PacketPushPlayer(UUID player) {
	id = player;
    }

    public static void handle(PacketPushPlayer message, IPayloadContext context) {
	ClientBarrierMethods.handlePushPlayer(message.id);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
	return TYPE;
    }
}
