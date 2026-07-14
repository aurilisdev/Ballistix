package ballistix.common.packet.type.client.particle;

import java.util.function.Supplier;

import ballistix.common.packet.type.client.ClientBarrierMethods;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;
import voltaic.api.codec.StreamCodec;

public class PacketSpawnBlastParticle {

	public static final StreamCodec<ByteBuf, PacketSpawnBlastParticle> CODEC = new StreamCodec<>() {
		
		@Override
		public void encode(ByteBuf buf, PacketSpawnBlastParticle data) {
			StreamCodec.BLOCK_POS.encode(buf, data.pos);
			buf.writeInt(data.type.ordinal());
		}
		
		@Override
		public PacketSpawnBlastParticle decode(ByteBuf buf) {
			return new PacketSpawnBlastParticle(StreamCodec.BLOCK_POS.decode(buf), BlastParticleSpawnType.values()[buf.readInt()]);
		}
	};

	private final BlockPos pos;
	private final BlastParticleSpawnType type;

	public PacketSpawnBlastParticle(BlockPos pos, BlastParticleSpawnType type) {
		this.pos = pos;
		this.type = type;
	}

	public static void handle(PacketSpawnBlastParticle message, Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {

			ClientBarrierMethods.handlerSpawnBlastParticle(message.pos, message.type);

		});
		ctx.setPacketHandled(true);
	}

	public static void encode(PacketSpawnBlastParticle pkt, FriendlyByteBuf buf) {
		CODEC.encode(buf, pkt);
	}

	public static PacketSpawnBlastParticle decode(FriendlyByteBuf buf) {
		return CODEC.decode(buf);
	}
}