package ballistix.common.packet.type.client.particle;

import java.util.function.Supplier;

import ballistix.common.packet.type.client.ClientBarrierMethods;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import voltaic.api.codec.StreamCodec;

public class PacketSpawnBlastParticle {

	public static final StreamCodec<PacketBuffer, PacketSpawnBlastParticle> CODEC = new StreamCodec<PacketBuffer, PacketSpawnBlastParticle>() {
		
		@Override
		public void encode(PacketBuffer buf, PacketSpawnBlastParticle data) {
			StreamCodec.BLOCK_POS.encode(buf, data.pos);
			buf.writeInt(data.type.ordinal());
		}
		
		@Override
		public PacketSpawnBlastParticle decode(PacketBuffer buf) {
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

	public static void encode(PacketSpawnBlastParticle pkt, PacketBuffer buf) {
		CODEC.encode(buf, pkt);
	}

	public static PacketSpawnBlastParticle decode(PacketBuffer buf) {
		return CODEC.decode(buf);
	}
}