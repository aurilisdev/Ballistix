package ballistix.common.blast;

import java.util.Iterator;

import ballistix.common.blast.thread.ThreadRaycastBlast;
import ballistix.common.block.SubtypeBlast;
import ballistix.common.settings.Constants;
import electrodynamics.common.packet.NetworkHandler;
import electrodynamics.common.packet.PacketSpawnSmokeParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.fmllegacy.network.NetworkDirection;

public class BlastThermobaric extends Blast {

    public BlastThermobaric(Level world, BlockPos position) {
	super(world, position);
    }

    @Override
    public void doPreExplode() {
	hasStarted = true;
	if (!world.isClientSide) {
	    thread = new ThreadRaycastBlast(world, position, (int) Constants.EXPLOSIVE_THERMOBARIC_SIZE,
		    (float) Constants.EXPLOSIVE_THERMOBARIC_ENERGY, null);
	    thread.start();
	}

    }

    private ThreadRaycastBlast thread;
    private int pertick = -1;

    @Override
    public boolean doExplode(int callCount) {
	if (!world.isClientSide) {
	    if (thread == null) {
		return true;
	    }
	    Explosion ex = new Explosion(world, null, null, null, position.getX(), position.getY(), position.getZ(),
		    (float) Constants.EXPLOSIVE_THERMOBARIC_SIZE, false, BlockInteraction.BREAK);
	    if (thread.isComplete) {
		if (pertick == -1) {
		    pertick = (int) (thread.results.size() / Constants.EXPLOSIVE_THERMOBARIC_DURATION + 1);
		}
		int finished = pertick;
		Iterator<BlockPos> iterator = thread.results.iterator();
		while (iterator.hasNext()) {
		    if (finished-- < 0) {
			break;
		    }
		    BlockPos p = new BlockPos(iterator.next());
		    world.getBlockState(p).getBlock().wasExploded(world, p, ex);
		    world.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
		    if (world.random.nextFloat() < 1 / 10.0 && world instanceof ServerLevel serverlevel) {
			serverlevel.getChunkSource().chunkMap.getPlayers(new ChunkPos(p), false).forEach(pl -> NetworkHandler.CHANNEL
				.sendTo(new PacketSpawnSmokeParticle(p), pl.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT));
		    }
		    iterator.remove();
		}
		if (thread.results.isEmpty()) {
		    attackEntities((float) Constants.EXPLOSIVE_THERMOBARIC_SIZE * 2);
		    return true;
		}
	    }
	}
	return false;
    }

    @Override
    public boolean isInstantaneous() {
	return false;
    }

    @Override
    public SubtypeBlast getBlastType() {
	return SubtypeBlast.thermobaric;
    }

}
