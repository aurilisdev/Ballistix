package ballistix.common.packet.type.client;

import java.util.HashSet;

import ballistix.api.radar.IDetected;
import ballistix.common.packet.NetworkHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PacketSetSearchRadarTrackedClient implements CustomPacketPayload {

    public static final ResourceLocation PACKET_SETSEARCHRADARTRACKEDCLIENT_PACKETID = NetworkHandler
	    .id("packetsetsearchradartrackedclient");
    public static final Type<PacketSetSearchRadarTrackedClient> TYPE = new Type<>(
	    PACKET_SETSEARCHRADARTRACKEDCLIENT_PACKETID);

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketSetSearchRadarTrackedClient> CODEC = new StreamCodec<>() {

	private static final StreamCodec<ByteBuf, Vec3> VEC_3_STREAM_CODEC = new StreamCodec<>() {
	    @Override
	    public Vec3 decode(ByteBuf buffer) {
		return new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
	    }

	    @Override
	    public void encode(ByteBuf buffer, Vec3 value) {
		buffer.writeDouble(value.x);
		buffer.writeDouble(value.y);
		buffer.writeDouble(value.z);
	    }
	};

	@Override
	public PacketSetSearchRadarTrackedClient decode(RegistryFriendlyByteBuf buf) {
	    HashSet<IDetected.Detected> detected = new HashSet<>();

	    int size = buf.readInt();
	    for (int i = 0; i < size; i++) {

		detected.add(new IDetected.Detected(VEC_3_STREAM_CODEC.decode(buf),
			ItemStack.STREAM_CODEC.decode(buf).getItem(), buf.readBoolean()));
	    }

	    return new PacketSetSearchRadarTrackedClient(detected, BlockPos.STREAM_CODEC.decode(buf));
	}

	@Override
	public void encode(RegistryFriendlyByteBuf buf, PacketSetSearchRadarTrackedClient packet) {
	    buf.writeInt(packet.detected.size());
	    for (IDetected.Detected detected : packet.detected) {
		VEC_3_STREAM_CODEC.encode(buf, detected.getPosition());
		ItemStack.STREAM_CODEC.encode(buf, new ItemStack(detected.getItem()));
		buf.writeBoolean(detected.showBearing());
	    }
	    BlockPos.STREAM_CODEC.encode(buf, packet.tilePos);
	}
    };

    private final HashSet<IDetected.Detected> detected;
    private final BlockPos tilePos;

    public PacketSetSearchRadarTrackedClient(HashSet<IDetected.Detected> detected, BlockPos tilePos) {
	this.detected = detected;
	this.tilePos = tilePos;
    }

    public static void handle(PacketSetSearchRadarTrackedClient message, IPayloadContext context) {
	ClientBarrierMethods.handleSetSearchRadarTrackedClient(message.detected, message.tilePos);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
	return TYPE;
    }
}
