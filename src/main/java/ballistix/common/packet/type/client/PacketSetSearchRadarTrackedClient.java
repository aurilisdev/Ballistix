package ballistix.common.packet.type.client;

import java.util.HashSet;
import java.util.function.Supplier;

import ballistix.api.radar.IDetected;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent.Context;
import voltaic.api.codec.StreamCodec;

public class PacketSetSearchRadarTrackedClient {

    public static final StreamCodec<FriendlyByteBuf, PacketSetSearchRadarTrackedClient> CODEC = new StreamCodec<>() {

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
	public PacketSetSearchRadarTrackedClient decode(FriendlyByteBuf buf) {
	    HashSet<IDetected.Detected> detected = new HashSet<>();

	    int size = buf.readInt();
	    for (int i = 0; i < size; i++) {

		detected.add(new IDetected.Detected(VEC_3_STREAM_CODEC.decode(buf),
			StreamCodec.ITEM_STACK.decode(buf).getItem(), buf.readBoolean()));
	    }

	    return new PacketSetSearchRadarTrackedClient(detected, StreamCodec.BLOCK_POS.decode(buf));
	}

	@Override
	public void encode(FriendlyByteBuf buf, PacketSetSearchRadarTrackedClient packet) {
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

    public static void encode(PacketSetSearchRadarTrackedClient pkt, FriendlyByteBuf buf) {
	CODEC.encode(buf, pkt);
    }

    public static PacketSetSearchRadarTrackedClient decode(FriendlyByteBuf buf) {
	return CODEC.decode(buf);
    }
}
