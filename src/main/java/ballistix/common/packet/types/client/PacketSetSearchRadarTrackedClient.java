package ballistix.common.packet.types.client;

import java.util.HashSet;
import java.util.function.Supplier;

import ballistix.api.radar.IDetected;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class PacketSetSearchRadarTrackedClient {

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
		buf.writeInt(pkt.detected.size());
		for (IDetected.Detected detected : pkt.detected) {
			buf.writeDouble(detected.getPosition().x);
			buf.writeDouble(detected.getPosition().y);
			buf.writeDouble(detected.getPosition().z);
			buf.writeItem(new ItemStack(detected.getItem()));
			buf.writeBoolean(detected.showBearing());
		}
		buf.writeBlockPos(pkt.tilePos);
	}

	public static PacketSetSearchRadarTrackedClient decode(PacketBuffer buf) {
		HashSet<IDetected.Detected> detected = new HashSet<>();

		int size = buf.readInt();
		for (int i = 0; i < size; i++) {

			detected.add(new IDetected.Detected(new Vector3d(buf.readDouble(), buf.readDouble(), buf.readDouble()), buf.readItem().getItem(), buf.readBoolean()));

		}

		return new PacketSetSearchRadarTrackedClient(detected, buf.readBlockPos());
	}
}
