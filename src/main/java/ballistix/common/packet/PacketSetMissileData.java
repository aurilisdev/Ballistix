package ballistix.common.packet;

import java.util.function.Supplier;

import ballistix.common.tile.TileMissileSilo;
import electrodynamics.prefab.utilities.object.Location;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.network.NetworkEvent.Context;

// TODO: Could be removed
public class PacketSetMissileData {

	private final BlockPos target;
	private final BlockPos pos;
	private final int frequency;

	public PacketSetMissileData(BlockPos pos, BlockPos target, Integer frequency) {
		this.pos = pos;
		this.target = target;
		this.frequency = frequency;
	}

	public static void handle(PacketSetMissileData message, Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			ServerLevel world = context.get().getSender().getLevel();
			if (world != null) {
				TileMissileSilo tile = (TileMissileSilo) world.getBlockEntity(message.pos);
				if (tile != null) {
					tile.target = new Location(message.target);
					tile.setFrequency(message.frequency);
				}
			}
		});
		ctx.setPacketHandled(true);
	}

	public static void encode(PacketSetMissileData pkt, FriendlyByteBuf buf) {
		buf.writeBlockPos(pkt.pos);
		buf.writeBlockPos(pkt.target);
		buf.writeInt(pkt.frequency);
	}

	public static PacketSetMissileData decode(FriendlyByteBuf buf) {
		return new PacketSetMissileData(buf.readBlockPos(), buf.readBlockPos(), buf.readInt());
	}
}