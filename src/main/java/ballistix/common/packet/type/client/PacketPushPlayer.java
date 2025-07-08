package ballistix.common.packet.type.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;
import voltaic.api.codec.StreamCodec;

import java.util.UUID;
import java.util.function.Supplier;

public class PacketPushPlayer {

    public static final StreamCodec<FriendlyByteBuf, PacketPushPlayer> CODEC = new StreamCodec<>() {

        @Override
        public PacketPushPlayer decode(FriendlyByteBuf buf) {
            return new PacketPushPlayer(buf.readUUID());
        }

        @Override
        public void encode(FriendlyByteBuf buf, PacketPushPlayer packet) {
            buf.writeUUID(packet.id);
        }
    };

    private final UUID id;

    public PacketPushPlayer(UUID player) {
        id = player;
    }

    public static void handle(PacketPushPlayer message, Supplier<Context> context) {
    	Context ctx = context.get();
		ctx.enqueueWork(() -> {

			ClientBarrierMethods.handlePushPlayer(message.id);

		});
		ctx.setPacketHandled(true);
    }

    public static void encode(PacketPushPlayer pkt, FriendlyByteBuf buf) {
		CODEC.encode(buf, pkt);
	}

	public static PacketPushPlayer decode(FriendlyByteBuf buf) {
		return CODEC.decode(buf);
	}
}
