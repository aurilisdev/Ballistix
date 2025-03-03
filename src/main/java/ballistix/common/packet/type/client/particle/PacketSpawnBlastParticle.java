package ballistix.common.packet.type.client.particle;

import ballistix.common.packet.NetworkHandler;
import ballistix.common.packet.type.client.ClientBarrierMethods;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PacketSpawnBlastParticle implements CustomPacketPayload {

    public static final ResourceLocation PACKET_SPAWNSMOKEPARTICLE_PACKETID = NetworkHandler
	    .id("packetspawnblastparticle");
    public static final Type<PacketSpawnBlastParticle> TYPE = new Type<>(PACKET_SPAWNSMOKEPARTICLE_PACKETID);
    public static final StreamCodec<ByteBuf, PacketSpawnBlastParticle> CODEC = StreamCodec.composite(
	    BlockPos.STREAM_CODEC, instance0 -> instance0.pos, ByteBufCodecs.STRING_UTF8,
	    instance0 -> instance0.type.name(), PacketSpawnBlastParticle::new);

    private final BlockPos pos;
    private final BlastParticleSpawnType type;

    private PacketSpawnBlastParticle(BlockPos pos, String type) {
	this.pos = pos;
	this.type = BlastParticleSpawnType.valueOf(type);
    }

    public PacketSpawnBlastParticle(BlockPos pos, BlastParticleSpawnType type) {
	this.pos = pos;
	this.type =type;
    }

    public static void handle(PacketSpawnBlastParticle message, IPayloadContext context) {
	ClientBarrierMethods.handlerSpawnBlastParticle(message.pos, message.type);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
	return TYPE;
    }
}