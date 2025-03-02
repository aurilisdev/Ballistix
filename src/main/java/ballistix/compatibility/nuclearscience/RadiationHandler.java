package ballistix.compatibility.nuclearscience;

import ballistix.common.packet.type.client.particle.BlastParticleSpawnType;
import ballistix.common.packet.type.client.particle.PacketSpawnBlastParticle;
import ballistix.common.settings.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;
import nuclearscience.api.radiation.RadiationSystem;
import nuclearscience.api.radiation.SimpleRadiationSource;
import nuclearscience.common.block.BlockIrradiated;
import nuclearscience.registers.NuclearScienceBlocks;

public class RadiationHandler {

    public static void addNuclearExplosionRadiation(Level world, BlockPos location) {
	RadiationSystem.addRadiationSource(world, new SimpleRadiationSource(150000.0, 2,
		(int) (Constants.EXPLOSIVE_NUCLEAR_SIZE), false, 86400 * 20, location, false));
    }

    public static void addNuclearExplosiveIrradidatedBlock(BlockPos p, Level world) {
	BlockState state = world.getBlockState(p);

	if (BlockIrradiated.isValidPlacement(state)) {
	    world.setBlock(p, BlockIrradiated.getIrradiatedBlockstate(state), 3);
	    if (world.random.nextFloat() < 1 / 15.0 && world instanceof ServerLevel serverlevel) {
		serverlevel.getChunkSource().chunkMap.getPlayers(new ChunkPos(p), false).forEach(pl -> PacketDistributor
			.sendToPlayer(pl, new PacketSpawnBlastParticle(p, BlastParticleSpawnType.TURNRADIOACTIVE)));
//		Minecraft.getInstance().particleEngine.createParticle(ParticleTypes.ASH, p.getX() + 0.5, p.getY() + 1.5,
//			p.getZ() + 0.5, 0, 0, 0);
	    }
	} else if (state.is(BlockTags.LEAVES)) {
	    world.setBlock(p, Blocks.AIR.defaultBlockState(), 3);
	    if (world.random.nextFloat() < 1 / 15.0 && world instanceof ServerLevel serverlevel) {
		serverlevel.getChunkSource().chunkMap.getPlayers(new ChunkPos(p), false).forEach(pl -> PacketDistributor
			.sendToPlayer(pl, new PacketSpawnBlastParticle(p, BlastParticleSpawnType.LEAVES_BREAKING)));
//		Minecraft.getInstance().particleEngine.createParticle(ParticleTypes.GUST, p.getX() + 0.5,
//			p.getY() + 1.5, p.getZ() + 0.5, 0, 0, 0);
	    }
	} else if (state.isAir()) {
	    world.setBlock(p, NuclearScienceBlocks.BLOCK_RADIOACTIVEAIR.get().defaultBlockState(), 3);
	}
    }

}
