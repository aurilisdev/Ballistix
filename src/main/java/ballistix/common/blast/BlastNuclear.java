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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fmllegacy.network.NetworkDirection;

public class BlastNuclear extends Blast implements IHasCustomRenderer {

    public BlastNuclear(Level world, BlockPos position) {
	super(world, position);
    }

    @Override
    public void doPreExplode() {
	if (!world.isClientSide) {
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
	if (!world.isClientSide) {
	    if (threadRay == null) {
		return true;
	    }
	    Explosion ex = new Explosion(world, null, null, null, position.getX(), position.getY(), position.getZ(),
		    (float) Constants.EXPLOSIVE_NUCLEAR_SIZE, false, BlockInteraction.BREAK);
	    boolean rayDone = false;
	    if (threadRay.isComplete && !rayDone) {
		hasStarted = true;
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

		    BlockState state = Blocks.AIR.defaultBlockState();
		    double dis = new Location(p.getX(), 0, p.getZ()).distance(new Location(position.getX(), 0, position.getZ()));
		    if (world.random.nextFloat() < 1 / 5.0 && dis < 15) {
			BlockPos offset = p.relative(Direction.DOWN);
			if (!threadRay.results.contains(offset) && world.random.nextFloat() < (15.0f - dis) / 15.0f) {
			    state = Blocks.FIRE.defaultBlockState();
			}
		    }
		    world.getBlockState(p).getBlock().wasExploded(world, p, ex);
		    world.setBlock(p, state, 2);
		    if (world.random.nextFloat() < 1 / 20.0 && world instanceof ServerLevel serverlevel) {
			serverlevel.getChunkSource().chunkMap.getPlayers(new ChunkPos(p), false).forEach(pl -> NetworkHandler.CHANNEL
				.sendTo(new PacketSpawnSmokeParticle(p), pl.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT));
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
			    if (i * i + k * k < radius * radius && world.random.nextFloat() < (particleHeight > 18 ? 0.1 : 0.3)) {
				BlockPos p = position.offset(i, particleHeight, k);
				((ServerLevel) world).getChunkSource().chunkMap.getPlayers(new ChunkPos(p), false)
					.forEach(pl -> NetworkHandler.CHANNEL.sendTo(new PacketSpawnSmokeParticle(p), pl.connection.getConnection(),
						NetworkDirection.PLAY_TO_CLIENT));
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
		    AABB bb = AABB.ofSize(new Vec3(source.x(), source.y(), source.z()), range, range, range);
		    List<LivingEntity> list = world.getEntitiesOfClass(LivingEntity.class, bb);
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
			BlockPos p = new BlockPos(iterator.next()).offset(position);
			BlockState state = world.getBlockState(p);
			Block block = state.getBlock();
			if (block == Blocks.GRASS_BLOCK || block == Blocks.DIRT) {
			    if (world.random.nextFloat() < 0.7) {
				world.setBlock(p, nuclearscience.DeferredRegisters.blockRadioactiveSoil.defaultBlockState(), 2);
			    }
			} else if (state.getMaterial() == Material.LEAVES) {
			    world.setBlock(p, Blocks.AIR.defaultBlockState(), 2 | 16 | 32);
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
