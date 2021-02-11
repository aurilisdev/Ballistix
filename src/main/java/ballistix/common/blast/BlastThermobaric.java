package ballistix.common.blast;

import java.util.Iterator;

import ballistix.common.blast.thread.ThreadRaycastBlast;
import ballistix.common.block.SubtypeBlast;
import electrodynamics.packet.NetworkHandler;
import electrodynamics.packet.PacketSpawnSmokeParticle;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.Explosion.Mode;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkDirection;

public class BlastThermobaric extends Blast {

	public BlastThermobaric(World world, BlockPos position) {
		super(world, position);
	}

	@Override
	public void doPreExplode() {
		if (!world.isRemote) {
			thread = new ThreadRaycastBlast(world, position, 20, 45, null);
			thread.start();
		}

	}

	private ThreadRaycastBlast thread;
	private int pertick = -1;

	@Override
	public boolean doExplode(int callCount) {
		if (!world.isRemote) {
			if (thread == null) {
				return true;
			}
			Explosion ex = new Explosion(world, null, position.getX(), position.getY(), position.getZ(), 25, true, Mode.BREAK);
			if (thread.isComplete) {
				if (pertick == -1) {
					pertick = (int) (thread.results.size() / 15.0 + 1);
				}
				int finished = pertick;
				Iterator<BlockPos> iterator = thread.results.iterator();
				while (iterator.hasNext()) {
					if (finished-- < 0) {
						break;
					}
					BlockPos p = new BlockPos(iterator.next());
					world.getBlockState(p).getBlock().onExplosionDestroy(world, p, ex);
					world.setBlockState(p, Blocks.AIR.getDefaultState(), 2);
					if (world.rand.nextFloat() < 1 / 10.0) {
						if (world instanceof ServerWorld) {
							((ServerWorld) world).getChunkProvider().chunkManager.getTrackingPlayers(new ChunkPos(p), false).forEach(pl -> {
								NetworkHandler.CHANNEL.sendTo(new PacketSpawnSmokeParticle(p), pl.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
							});
						}
					}
					iterator.remove();
				}
				if (thread.results.isEmpty()) {
					attackEntities(15);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void doPostExplode() {
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
