package ballistix.common.packet.type.client;

import java.util.UUID;
import java.util.function.Supplier;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import voltaic.api.codec.StreamCodec;

public class PacketPushPlayer {

    public static final StreamCodec<PacketBuffer, PacketPushPlayer> CODEC = new StreamCodec<PacketBuffer, PacketPushPlayer>() {

        @Override
        public PacketPushPlayer decode(PacketBuffer buf) {
            return new PacketPushPlayer(buf.readUUID());
        }

        @Override
        public void encode(PacketBuffer buf, PacketPushPlayer packet) {
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

    public static void encode(PacketPushPlayer pkt, PacketBuffer buf) {
		CODEC.encode(buf, pkt);
	}

	public static PacketPushPlayer decode(PacketBuffer buf) {
		return CODEC.decode(buf);
	}
}
