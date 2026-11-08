package ballistix.common.packet.type.client;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import ballistix.api.radar.IDetected;
import ballistix.client.effect.BlastEffects;
import ballistix.client.effect.BlastEffects.Style;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.packet.type.client.particle.BlastParticleSpawnType;
import ballistix.common.settings.BallistixConfig;
import ballistix.common.tile.radar.TileSearchRadar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class ClientBarrierMethods {
    public static void handleSetSearchRadarTrackedClient(HashSet<IDetected.Detected> detected, BlockPos tilePos) {
	ClientLevel level = Minecraft.getInstance().level;
	if (level == null) {
	    return;
	}
	if (level.getBlockEntity(tilePos) instanceof TileSearchRadar radar) {
	    radar.detections.clear();
	    radar.detections.addAll(detected);
	}
    }

    public static void handleStartBlastEffects(BlockPos position, SubtypeBlast blast, double radius) {
	ClientLevel level = Minecraft.getInstance().level;
	if (level == null)
	    return;

	Style style = blastStyle(blast);
	if (style != null)
	    BlastEffects.start(level, Vec3.atCenterOf(position), style, radius);
    }

    public static void handleCraterGround(BlockPos origin, SubtypeBlast blast, List<BlockPos> floors) {
	ClientLevel level = Minecraft.getInstance().level;
	Style style = blastStyle(blast);
	if (level != null && style != null)
	    BlastEffects.updateCraterGround(level, Vec3.atCenterOf(origin), style, floors);
    }

    private static Style blastStyle(SubtypeBlast blast) {
	return switch (blast) {
	    case condensive -> Style.CONDENSIVE;
	    case breaching -> Style.BREACHING;
	    case obsidian -> Style.OBSIDIAN;
	    case thermobaric -> Style.THERMOBARIC;
	    case nuclear -> Style.NUCLEAR;
	    case antimatter -> Style.ANTIMATTER;
	    case largeantimatter -> Style.LARGE_ANTIMATTER;
	    default -> null;
	};
    }

    public static void handlerSpawnBlastParticle(BlockPos p, BlastParticleSpawnType type) {
	ClientLevel level = Minecraft.getInstance().level;
	if (level == null) {
	    return;
	}
	switch (type) {
	case EXPLOSIVE_BLOCK_BREAK:
	    Minecraft.getInstance().particleEngine.createParticle(ParticleTypes.EXPLOSION, p.getX() + 0.5,
		    p.getY() + 0.5, p.getZ() + 0.5, 0, 0, 0);
	    break;
	case LEAVES_BREAKING:
	    Minecraft.getInstance().particleEngine.createParticle(ParticleTypes.GUST, p.getX() + 0.5, p.getY() + 0.5,
		    p.getZ() + 0.5, 0, 0, 0);
	    break;
	case TURNRADIOACTIVE:
	    Minecraft.getInstance().particleEngine.createParticle(ParticleTypes.ASH, p.getX() + 0.5, p.getY() + 1.25,
		    p.getZ() + 0.5, 0, 0, 0);
	    break;
	default:
	    break;

	}
    }

    public static void handlePushPlayer(UUID id) {

	ClientLevel world = Minecraft.getInstance().level;

	if (world == null) {
	    return;
	}

	Player player = world.getPlayerByUUID(id);

	if (player == null || player.isCreative()) {
	    return;
	}
	player.push(0, player.getGravity() * BallistixConfig.INSTANCE.EXPLOSIVE_ANTIGRAVITY_GRAVITYFACTOR.get(), 0);

    }
}
