package ballistix.common.blast;

import java.util.Iterator;
import java.util.List;

import ballistix.common.blast.thread.ThreadRaycastBlast;
import ballistix.common.blast.thread.ThreadSimpleBlast;
import ballistix.common.block.SubtypeBlast;
import ballistix.common.settings.Constants;
import electrodynamics.common.packet.NetworkHandler;
import electrodynamics.common.packet.PacketSpawnSmokeParticle;
import electrodynamics.prefab.utilities.object.Location;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.Explosion.Mode;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.network.NetworkDirection;

public class BlastNuclear extends Blast implements IHasCustomRenderer {

    public BlastNuclear(World world, BlockPos position) {
	super(world, position);
    }

    @Override
    @Deprecated
    public void doPreExplode() {
	if (!world.isRemote) {
	    threadRay = new ThreadRaycastBlast(world, position, (int) Constants.EXPLOSIVE_NUCLEAR_SIZE, (float) Constants.EXPLOSIVE_NUCLEAR_ENERGY,
		    null);
	    threadSimple = new ThreadSimpleBlast(world, position, (int) (Constants.EXPLOSIVE_NUCLEAR_SIZE * 1.5),
		    (float) Constants.EXPLOSIVE_NUCLEAR_ENERGY, null, true);
	    threadSimple.strictnessAtEdges = 1.7;
	    threadRay.start();
	    threadSimple.start();
	}

    }

    private ThreadRaycastBlast threadRay;
    private ThreadSimpleBlast threadSimple;
    private int pertick = -1;
    private int perticksimple = -1;
    private int particleHeight = 0;

    @Override
    public boolean shouldRender() {
	return pertick > 0;
    }

    @Override
    public boolean doExplode(int callCount) {
	if (!world.isRemote) {
	    if (threadRay == null) {
		return true;
	    }
	    Explosion ex = new Explosion(world, null, null, null, position.getX(), position.getY(), position.getZ(),
		    (float) Constants.EXPLOSIVE_NUCLEAR_SIZE, false, Mode.BREAK);
	    boolean rayDone = false;
	    if (threadRay.isComplete && !rayDone) {
		if (pertick == -1) {
		    pertick = (int) (threadRay.results.size() / Constants.EXPLOSIVE_NUCLEAR_DURATION + 1);
		}
		int finished = pertick;
		Iterator<BlockPos> iterator = threadRay.results.iterator();
		while (iterator.hasNext()) {
		    if (finished-- < 0) {
			break;
		    }
		    BlockPos p = new BlockPos(iterator.next());

		    BlockState state = Blocks.AIR.getDefaultState();
		    double dis = new Location(p.getX(), 0, p.getZ()).distance(new Location(position.getX(), 0, position.getZ()));
		    if (world.rand.nextFloat() < 1 / 5.0 && dis < 15) {
			BlockPos offset = p.offset(Direction.DOWN);
			if (!threadRay.results.contains(offset) && world.rand.nextFloat() < (15.0f - dis) / 15.0f) {
			    state = Blocks.FIRE.getDefaultState();
			}
		    }
		    world.getBlockState(p).getBlock().onExplosionDestroy(world, p, ex);
		    world.setBlockState(p, state, 2 | 16 | 32);
		    if (world.rand.nextFloat() < 1 / 20.0 && world instanceof ServerWorld) {
			((ServerWorld) world).getChunkProvider().chunkManager.getTrackingPlayers(new ChunkPos(p), false)
				.forEach(pl -> NetworkHandler.CHANNEL.sendTo(new PacketSpawnSmokeParticle(p), pl.connection.getNetworkManager(),
					NetworkDirection.PLAY_TO_CLIENT));
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
			    if (i * i + k * k < radius * radius && world.rand.nextFloat() < (particleHeight > 18 ? 0.1 : 0.3)) {
				BlockPos p = position.add(i, particleHeight, k);
				((ServerWorld) world).getChunkProvider().chunkManager.getTrackingPlayers(new ChunkPos(p), false)
					.forEach(pl -> NetworkHandler.CHANNEL.sendTo(new PacketSpawnSmokeParticle(p),
						pl.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT));
			    }
			}
		    }
		    particleHeight++;
		}
		if (threadRay.results.isEmpty()) {
		    attackEntities((float) Constants.EXPLOSIVE_NUCLEAR_SIZE * 2);
		    rayDone = true;
		}
		if (ModList.get().isLoaded("nuclearscience")) {
		    Location source = new Location(position);
		    double range = Constants.EXPLOSIVE_NUCLEAR_SIZE * 4;
		    AxisAlignedBB bb = AxisAlignedBB.withSizeAtOrigin(range, range, range);
		    bb = bb.offset(new Vector3d(source.x(), source.y(), source.z()));
		    List<LivingEntity> list = world.getEntitiesWithinAABB(LivingEntity.class, bb);
		    for (LivingEntity living : list) {
			nuclearscience.api.radiation.RadiationSystem.applyRadiation(living, source, 150000);
		    }
		}
	    }
	    if (ModList.get().isLoaded("nuclearscience")) {
		if (threadSimple.isComplete && rayDone) {
		    if (perticksimple == -1) {
			perticksimple = (int) (threadSimple.results.size() * 1.5 / Constants.EXPLOSIVE_NUCLEAR_DURATION + 1);
		    }
		    int finished = perticksimple;
		    Iterator<BlockPos> iterator = threadSimple.results.iterator();
		    while (iterator.hasNext()) {
			if (finished-- < 0) {
			    break;
			}
			BlockPos p = new BlockPos(iterator.next()).add(position);
			BlockState state = world.getBlockState(p);
			Block block = state.getBlock();
			if (block == Blocks.GRASS_BLOCK || block == Blocks.DIRT) {
			    if (world.rand.nextFloat() < 0.7) {
				world.setBlockState(p, nuclearscience.DeferredRegisters.blockRadioactiveSoil.getDefaultState(), 2 | 16 | 32);
			    }
			} else if (!state.isSolid()) {
			    world.setBlockState(p, Blocks.AIR.getDefaultState(), 2 | 16 | 32);
			}
			iterator.remove();
		    }
		    if (threadSimple.results.isEmpty()) {
			return true;
		    }
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
