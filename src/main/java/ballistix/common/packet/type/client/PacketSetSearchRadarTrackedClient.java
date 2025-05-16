package ballistix.common.packet.type.client;

import java.util.HashSet;
import java.util.function.Supplier;

import ballistix.api.radar.IDetected;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import voltaic.api.codec.StreamCodec;

public class PacketSetSearchRadarTrackedClient {
	
	public static final StreamCodec<PacketBuffer, PacketSetSearchRadarTrackedClient> CODEC = new StreamCodec<PacketBuffer, PacketSetSearchRadarTrackedClient>() {

        private final StreamCodec<PacketBuffer, Vector3d> VEC_3_STREAM_CODEC = new StreamCodec<PacketBuffer, Vector3d>() {
            @Override
            public Vector3d decode(PacketBuffer buffer) {
                return new Vector3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
            }

            @Override
            public void encode(PacketBuffer buffer, Vector3d value) {
                buffer.writeDouble(value.x);
                buffer.writeDouble(value.y);
                buffer.writeDouble(value.z);
            }
        };

        @Override
        public PacketSetSearchRadarTrackedClient decode(PacketBuffer buf) {
            HashSet<IDetected.Detected> detected = new HashSet<>();

            int size = buf.readInt();
            for (int i = 0; i < size; i++) {

                detected.add(new IDetected.Detected(VEC_3_STREAM_CODEC.decode(buf), StreamCodec.ITEM_STACK.decode(buf).getItem(), buf.readBoolean()));
            }


            return new PacketSetSearchRadarTrackedClient(detected, StreamCodec.BLOCK_POS.decode(buf));
        }

        @Override
        public void encode(PacketBuffer buf, PacketSetSearchRadarTrackedClient packet) {
            buf.writeInt(packet.detected.size());
            for (IDetected.Detected detected : packet.detected) {
                VEC_3_STREAM_CODEC.encode(buf, detected.getPosition());
                StreamCodec.ITEM_STACK.encode(buf, new ItemStack(detected.getItem()));
                buf.writeBoolean(detected.showBearing());
            }
            StreamCodec.BLOCK_POS.encode(buf, packet.tilePos);
        }
    };

	private final HashSet<IDetected.Detected> detected;
	private final BlockPos tilePos;

	public PacketSetSearchRadarTrackedClient(HashSet<IDetected.Detected> detected, BlockPos tilePos) {
		this.detected = detected;
		this.tilePos = tilePos;
	}

	public static void handle(PacketSetSearchRadarTrackedClient message, Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {

			ClientBarrierMethods.handleSetSearchRadarTrackedClient(message.detected, message.tilePos);

		});
		ctx.setPacketHandled(true);
	}

	public static void encode(PacketSetSearchRadarTrackedClient pkt, PacketBuffer buf) {
		CODEC.encode(buf, pkt);
	}

	public static PacketSetSearchRadarTrackedClient decode(PacketBuffer buf) {
		return CODEC.decode(buf);
	}
}
