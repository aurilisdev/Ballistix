package ballistix.common.packet;

import java.util.function.Supplier;

import ballistix.common.tile.TileMissileSilo;
import electrodynamics.api.math.Location;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class PacketSetMissileData {

    private final BlockPos target;
    private final BlockPos pos;

    public PacketSetMissileData(BlockPos pos, BlockPos target) {
	this.pos = pos;
	this.target = target;
    }

    public static void handle(PacketSetMissileData message, Supplier<Context> context) {
	Context ctx = context.get();
	ctx.enqueueWork(() -> {
	    ServerWorld world = context.get().getSender().getServerWorld();
	    if (world != null) {
		TileMissileSilo tile = (TileMissileSilo) world.getTileEntity(message.pos);
		if (tile != null) {
		    tile.target = new Location(message.target);
		}
	    }
	});
	ctx.setPacketHandled(true);
    }

    public static void encode(PacketSetMissileData pkt, PacketBuffer buf) {
	buf.writeBlockPos(pkt.pos);
	buf.writeBlockPos(pkt.target);
    }

    public static PacketSetMissileData decode(PacketBuffer buf) {
	return new PacketSetMissileData(buf.readBlockPos(), buf.readBlockPos());
    }
}