package ballistix.common.packet;

import java.util.function.Supplier;

import ballistix.common.tile.TileMissileSilo;
import electrodynamics.api.tile.IPacketServerUpdateTile;
import electrodynamics.prefab.utilities.object.Location;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent.Context;

/**
 * @author aurilisdev Will be removed and dependents will be updated to use
 *         {@link IPacketServerUpdateTile}k
 */
@Deprecated
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
	    ServerWorld world = context.get().getSender().getServerWorld();
	    if (world != null) {
		TileMissileSilo tile = (TileMissileSilo) world.getTileEntity(message.pos);
		if (tile != null) {
		    tile.target = new Location(message.target);
		    tile.frequency = message.frequency;
		}
	    }
	});
	ctx.setPacketHandled(true);
    }

    public static void encode(PacketSetMissileData pkt, PacketBuffer buf) {
	buf.writeBlockPos(pkt.pos);
	buf.writeBlockPos(pkt.target);
	buf.writeInt(pkt.frequency);
    }

    public static PacketSetMissileData decode(PacketBuffer buf) {
	return new PacketSetMissileData(buf.readBlockPos(), buf.readBlockPos(), buf.readInt());
    }
}