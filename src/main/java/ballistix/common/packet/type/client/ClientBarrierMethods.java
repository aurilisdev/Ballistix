package ballistix.common.packet.type.client;

import java.util.HashSet;

import ballistix.api.radar.IDetected;
import ballistix.common.tile.radar.TileSearchRadar;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

public class ClientBarrierMethods {
    public static void handleSetSearchRadarTrackedClient(HashSet<IDetected.Detected> detected, BlockPos tilePos) {
        if(Minecraft.getInstance().level.getBlockEntity(tilePos) instanceof TileSearchRadar radar) {
            radar.detections.clear();
            radar.detections.addAll(detected);
        }
    }
}
