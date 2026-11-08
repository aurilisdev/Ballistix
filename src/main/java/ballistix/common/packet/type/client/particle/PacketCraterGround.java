package ballistix.common.packet.type.client.particle;

import java.util.List;

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

public record PacketCraterGround(BlockPos origin, SubtypeBlast blast, List<BlockPos> floors) implements CustomPacketPayload {

    public static final Type<PacketCraterGround> TYPE = new Type<>(NetworkHandler.id("crater_ground"));
    public static final StreamCodec<ByteBuf, PacketCraterGround> CODEC = StreamCodec.composite(
	    BlockPos.STREAM_CODEC, PacketCraterGround::origin,
	    ByteBufCodecs.STRING_UTF8.map(SubtypeBlast::valueOf, SubtypeBlast::name), PacketCraterGround::blast,
	    BlockPos.STREAM_CODEC.apply(ByteBufCodecs.list(2500)), PacketCraterGround::floors, PacketCraterGround::new);

    public static void send(ServerLevel level, BlockPos origin, SubtypeBlast blast, List<BlockPos> floors) {
	PacketStartBlastEffects.sendToViewers(level, origin, new PacketCraterGround(origin, blast, floors));
    }

    public static void handle(PacketCraterGround packet, IPayloadContext context) {
	ClientBarrierMethods.handleCraterGround(packet.origin, packet.blast, packet.floors);
    }

    @Override
    public Type<PacketCraterGround> type() {
	return TYPE;
    }
}
