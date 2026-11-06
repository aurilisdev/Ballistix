package ballistix.common.packet.type.client.particle;

import ballistix.api.blast.IBlast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.packet.NetworkHandler;
import ballistix.common.packet.type.client.ClientBarrierMethods;
import ballistix.common.settings.BallistixConfig;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.NetworkRegistry;

public record PacketStartBlastEffects(BlockPos position, SubtypeBlast blast, double radius) implements CustomPacketPayload {

    public static final Type<PacketStartBlastEffects> TYPE = new Type<>(NetworkHandler.id("start_blast_effects"));
    public static final StreamCodec<ByteBuf, PacketStartBlastEffects> CODEC = StreamCodec.composite(
	    BlockPos.STREAM_CODEC, PacketStartBlastEffects::position,
	    ByteBufCodecs.STRING_UTF8.map(SubtypeBlast::valueOf, SubtypeBlast::name), PacketStartBlastEffects::blast,
	    ByteBufCodecs.DOUBLE, PacketStartBlastEffects::radius, PacketStartBlastEffects::new);

    private static final double VIEW_DISTANCE_SQUARED = 512 * 512;

    public static void send(ServerLevel level, BlockPos position, IBlast blast) {
	if (!(blast instanceof SubtypeBlast subtype))
	    return;

	BallistixConfig config = BallistixConfig.INSTANCE;
	double radius = switch (subtype) {
	    case condensive -> config.EXPLOSIVE_CONDENSIVE_SIZE.getAsDouble();
	    case breaching -> config.EXPLOSIVE_BREACHING_SIZE.getAsDouble();
	    case obsidian -> config.EXPLOSIVE_OBSIDIAN_SIZE.getAsDouble();
	    case thermobaric -> config.EXPLOSIVE_THERMOBARIC_SIZE.getAsDouble();
	    case nuclear -> config.EXPLOSIVE_NUCLEAR_SIZE.getAsDouble();
	    case antimatter -> config.EXPLOSIVE_ANTIMATTER_RADIUS.getAsDouble();
	    case largeantimatter -> config.EXPLOSIVE_LARGEANTIMATTER_RADIUS.getAsDouble();
	    default -> 0;
	};
	if (!Double.isFinite(radius) || radius <= 0)
	    return;

	PacketStartBlastEffects packet = new PacketStartBlastEffects(position, subtype, radius);
	Vec3 centre = Vec3.atCenterOf(position);
	for (ServerPlayer player : level.players()) {
	    if (player.distanceToSqr(centre) <= VIEW_DISTANCE_SQUARED
		    && NetworkRegistry.hasChannel(player.connection, TYPE.id())) {
		PacketDistributor.sendToPlayer(player, packet);
	    }
	}
    }

    public static void handle(PacketStartBlastEffects packet, IPayloadContext context) {
	ClientBarrierMethods.handleStartBlastEffects(packet.position, packet.blast, packet.radius);
    }

    @Override
    public Type<PacketStartBlastEffects> type() {
	return TYPE;
    }
}
