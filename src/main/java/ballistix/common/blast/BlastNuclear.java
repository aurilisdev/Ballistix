package ballistix.common.blast;

import java.util.Iterator;

import ballistix.common.blast.thread.ThreadRaycastBlast;
import ballistix.common.block.SubtypeBlast;
import ballistix.common.settings.Constants;
import electrodynamics.api.math.Location;
import electrodynamics.packet.NetworkHandler;
import electrodynamics.packet.PacketSpawnSmokeParticle;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.Explosion.Mode;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkDirection;

public class BlastNuclear extends Blast implements IHasCustomRenderer {

    public BlastNuclear(World world, BlockPos position) {
	super(world, position);
    }

    @Override
    public void doPreExplode() {
	if (!world.isRemote) {
	    thread = new ThreadRaycastBlast(world, position, (int) Constants.EXPLOSIVE_NUCLEAR_SIZE,
		    (float) Constants.EXPLOSIVE_NUCLEAR_ENERGY, null);
	    thread.start();
	}

    }

    private ThreadRaycastBlast thread;
    private int pertick = -1;
    private int particleHeight = 0;

    @Override
    public boolean shouldRender() {
	return pertick > 0;
    }

    @Override
    public boolean doExplode(int callCount) {
	if (!world.isRemote) {
	    if (thread == null) {
		return true;
	    }
	    Explosion ex = new Explosion(world, null, null, null, position.getX(), position.getY(), position.getZ(),
		    (float) Constants.EXPLOSIVE_NUCLEAR_SIZE, false, Mode.BREAK);
	    if (thread.isComplete) {
		if (pertick == -1) {
		    pertick = (int) (thread.results.size() / Constants.EXPLOSIVE_NUCLEAR_DURATION + 1);
		}
		int finished = pertick;
		Iterator<BlockPos> iterator = thread.results.iterator();
		while (iterator.hasNext()) {
		    if (finished-- < 0) {
			break;
		    }
		    BlockPos p = new BlockPos(iterator.next());

		    BlockState state = Blocks.AIR.getDefaultState();
		    double dis = new Location(p.getX(), 0, p.getZ())
			    .distance(new Location(position.getX(), 0, position.getZ()));
		    if (dis < 15) {
			BlockPos offset = p.offset(Direction.DOWN);
			if (!world.isAirBlock(offset) && world.rand.nextFloat() < (15.0f - dis) / 15.0f) {
			    state = Blocks.FIRE.getDefaultState();
			}
		    }
		    world.getBlockState(p).getBlock().onExplosionDestroy(world, p, ex);
		    world.setBlockState(p, state, 2 | 16 | 32);
		    if (world.rand.nextFloat() < 1 / 10.0 && world instanceof ServerWorld) {
			((ServerWorld) world).getChunkProvider().chunkManager.getTrackingPlayers(new ChunkPos(p), false)
				.forEach(pl -> NetworkHandler.CHANNEL.sendTo(new PacketSpawnSmokeParticle(p),
					pl.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT));
		    }
		    iterator.remove();
		}
		if (particleHeight < 23) {
		    int radius = 2;
		    if (particleHeight > 18) {
			radius = 25 + 20 - particleHeight;
		    }
		    if (particleHeight > 20) {
			radius = 25 - 20 + particleHeight;
		    }
		    for (int i = -radius; i <= radius; i++) {
			for (int k = -radius; k <= radius; k++) {
			    if (i * i + k * k < radius * radius
				    && world.rand.nextFloat() < (particleHeight > 18 ? 0.2 : 0.6)) {
				BlockPos p = position.add(i, particleHeight, k);
				((ServerWorld) world).getChunkProvider().chunkManager
					.getTrackingPlayers(new ChunkPos(p), false)
					.forEach(pl -> NetworkHandler.CHANNEL.sendTo(new PacketSpawnSmokeParticle(p),
						pl.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT));
			    }
			}
		    }
		    particleHeight++;
		}
		if (thread.results.isEmpty()) {
		    attackEntities((float) Constants.EXPLOSIVE_NUCLEAR_SIZE * 2);
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
	return SubtypeBlast.nuclear;
    }

}
