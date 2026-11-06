package ballistix.common.packet.type.client.particle;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.registration.NetworkRegistry;

/** Sends blast effect payloads to the players close enough to see them and able to receive them. */
final class BlastEffectBroadcaster {

    private static final double VIEW_DISTANCE_SQUARED = 512 * 512;

    private BlastEffectBroadcaster() {
    }

    static void sendToViewers(ServerLevel level, BlockPos position, CustomPacketPayload payload) {
	Vec3 centre = Vec3.atCenterOf(position);
	for (ServerPlayer player : level.players()) {
	    if (player.distanceToSqr(centre) <= VIEW_DISTANCE_SQUARED
		    && NetworkRegistry.hasChannel(player.connection, payload.type().id())) {
		PacketDistributor.sendToPlayer(player, payload);
	    }
	}
    }
}
