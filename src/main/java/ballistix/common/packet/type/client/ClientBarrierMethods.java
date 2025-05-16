package ballistix.common.packet.type.client;

import java.util.HashSet;

import ballistix.api.radar.IDetected;
import ballistix.common.packet.type.client.particle.BlastParticleSpawnType;
import ballistix.common.tile.radar.TileSearchRadar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class ClientBarrierMethods {
	
    public static void handleSetSearchRadarTrackedClient(HashSet<IDetected.Detected> detected, BlockPos tilePos) {
    	TileEntity tileentity = Minecraft.getInstance().level.getBlockEntity(tilePos);
        if(tileentity instanceof TileSearchRadar) {
        	TileSearchRadar radar = (TileSearchRadar) tileentity;
            radar.detections.clear();
            radar.detections.addAll(detected);
        }
    }
    
    public static void handlerSpawnBlastParticle(BlockPos p, BlastParticleSpawnType type) {
		ClientWorld world = Minecraft.getInstance().level;
		if (world == null) {
			return;
		}
		switch (type) {
		case EXPLOSIVE_BLOCK_BREAK:
			Minecraft.getInstance().particleEngine.createParticle(ParticleTypes.EXPLOSION, p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5, 0, 0, 0);
			break;
		case LEAVES_BREAKING:
			Minecraft.getInstance().particleEngine.createParticle(ParticleTypes.CLOUD, p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5, 0, 0, 0);
			break;
		case TURNRADIOACTIVE:
			Minecraft.getInstance().particleEngine.createParticle(ParticleTypes.ASH, p.getX() + 0.5, p.getY() + 1.25, p.getZ() + 0.5, 0, 0, 0);
			break;
		default:
			break;

		}
	}
    
}
