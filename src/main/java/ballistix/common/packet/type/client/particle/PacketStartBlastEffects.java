package ballistix.common.packet.type.client.particle;

import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.packet.NetworkHandler;
import ballistix.common.packet.type.client.ClientBarrierMethods;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** Tells clients that a blast with visual effects has detonated, and how large it is. */
public record PacketStartBlastEffects(BlockPos position, SubtypeBlast blast, double radius)
	implements CustomPacketPayload {

    public static final Type<PacketStartBlastEffects> TYPE = new Type<>(NetworkHandler.id("start_blast_effects"));
    public static final StreamCodec<ByteBuf, PacketStartBlastEffects> CODEC = StreamCodec.composite(
	    BlockPos.STREAM_CODEC, PacketStartBlastEffects::position,
	    ByteBufCodecs.STRING_UTF8.map(SubtypeBlast::valueOf, SubtypeBlast::name), PacketStartBlastEffects::blast,
	    ByteBufCodecs.DOUBLE, PacketStartBlastEffects::radius, PacketStartBlastEffects::new);

    public static void send(ServerLevel level, BlockPos position, SubtypeBlast blast, double radius) {
	if (Double.isFinite(radius) && radius > 0)
	    BlastEffectBroadcaster.sendToViewers(level, position, new PacketStartBlastEffects(position, blast, radius));
    }

    public static void handle(PacketStartBlastEffects packet, IPayloadContext context) {
	ClientBarrierMethods.handleStartBlastEffects(packet.position, packet.blast, packet.radius);
    }

    @Override
    public Type<PacketStartBlastEffects> type() {
	return TYPE;
    }
}
