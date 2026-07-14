package ballistix.compatibility.nuclearscience;

import ballistix.common.packet.NetworkHandler;
import ballistix.common.packet.type.client.particle.BlastParticleSpawnType;
import ballistix.common.packet.type.client.particle.PacketSpawnBlastParticle;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkDirection;
import nuclearscience.common.block.BlockIrradiated;
import nuclearscience.registers.NuclearScienceBlocks;

public class RadiationHandler {

	public static void addNuclearExplosiveIrradidatedBlock(BlockPos p, World world) {
		BlockState state = world.getBlockState(p);

		if (BlockIrradiated.isValidPlacement(state)) {
			world.setBlock(p, BlockIrradiated.getIrradiatedBlockstate(state), 3);
			if (world.random.nextFloat() < 1 / 15.0 && world instanceof ServerWorld) {
				ServerWorld serverlevel = (ServerWorld) world;
				serverlevel.getChunkSource().chunkMap.getPlayers(new ChunkPos(p), false).forEach(pl -> NetworkHandler.CHANNEL.sendTo(new PacketSpawnBlastParticle(p, BlastParticleSpawnType.TURNRADIOACTIVE), pl.connection.connection, NetworkDirection.PLAY_TO_CLIENT));
//		Minecraft.getInstance().particleEngine.createParticle(ParticleTypes.ASH, p.getX() + 0.5, p.getY() + 1.5,
//			p.getZ() + 0.5, 0, 0, 0);
			}
		} else if (state.is(BlockTags.LEAVES)) {
			world.setBlock(p, Blocks.AIR.defaultBlockState(), 3);
			if (world.random.nextFloat() < 1 / 15.0 && world instanceof ServerWorld) {
				ServerWorld serverlevel = (ServerWorld) world;
				serverlevel.getChunkSource().chunkMap.getPlayers(new ChunkPos(p), false).forEach(pl -> NetworkHandler.CHANNEL.sendTo(new PacketSpawnBlastParticle(p, BlastParticleSpawnType.LEAVES_BREAKING), pl.connection.connection, NetworkDirection.PLAY_TO_CLIENT));
//		Minecraft.getInstance().particleEngine.createParticle(ParticleTypes.GUST, p.getX() + 0.5,
//			p.getY() + 1.5, p.getZ() + 0.5, 0, 0, 0);
			}
		} else if (state.isAir()) {
			world.setBlock(p, NuclearScienceBlocks.BLOCK_RADIOACTIVEAIR.get().defaultBlockState(), 3);
		}
	}

}
