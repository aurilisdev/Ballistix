package ballistix.common.packet.types.client;

import java.util.HashSet;

import ballistix.api.radar.IDetected;
import ballistix.common.tile.radar.TileSearchRadar;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class ClientBarrierMethods {
    public static void handleSetSearchRadarTrackedClient(HashSet<IDetected.Detected> detected, BlockPos tilePos) {
    	TileEntity tile = Minecraft.getInstance().level.getBlockEntity(tilePos);
        if(tile instanceof TileSearchRadar) {
        	TileSearchRadar radar = (TileSearchRadar) tile;
            radar.detections.clear();
            radar.detections.addAll(detected);
        }
    }
}
