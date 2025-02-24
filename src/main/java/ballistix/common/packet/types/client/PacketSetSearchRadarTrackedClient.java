package ballistix.common.packet.types.client;

import java.util.HashSet;
import java.util.function.Supplier;

import ballistix.api.radar.IDetected;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent.Context;

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

	public static void encode(PacketSetSearchRadarTrackedClient pkt, FriendlyByteBuf buf) {
		buf.writeInt(pkt.detected.size());
		for (IDetected.Detected detected : pkt.detected) {
			buf.writeDouble(detected.position().x);
			buf.writeDouble(detected.position().y);
			buf.writeDouble(detected.position().z);
			buf.writeItem(new ItemStack(detected.getItem()));
			buf.writeBoolean(detected.showBearing());
		}
		buf.writeBlockPos(pkt.tilePos);
	}

	public static PacketSetSearchRadarTrackedClient decode(FriendlyByteBuf buf) {
		HashSet<IDetected.Detected> detected = new HashSet<>();

		int size = buf.readInt();
		for (int i = 0; i < size; i++) {

			detected.add(new IDetected.Detected(new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()), buf.readItem().getItem(), buf.readBoolean()));

		}

		return new PacketSetSearchRadarTrackedClient(detected, buf.readBlockPos());
	}
}
