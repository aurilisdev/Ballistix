package ballistix.common.blast;

import java.util.Iterator;

import ballistix.common.blast.thread.ThreadSimpleBlast;
import ballistix.common.blast.thread.raycast.ThreadRaycastBlast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.settings.Constants;
import ballistix.registers.BallistixSounds;
import electrodynamics.api.sound.SoundAPI;
import electrodynamics.common.packet.NetworkHandler;
import electrodynamics.common.packet.types.client.PacketSpawnSmokeParticle;
import electrodynamics.prefab.utilities.object.Location;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.Explosion.Mode;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.network.NetworkDirection;
import nuclearscience.api.radiation.RadiationSystem;
import nuclearscience.registers.NuclearScienceBlocks;

public class BlastNuclear extends Blast implements IHasCustomRenderer {

	public BlastNuclear(World world, BlockPos position) {
		super(world, position);
	}

	@Override
	public void doPreExplode() {
		if (!world.isClientSide) {
			threadRay = new ThreadRaycastBlast(world, position, (int) Constants.EXPLOSIVE_NUCLEAR_SIZE, (float) Constants.EXPLOSIVE_NUCLEAR_ENERGY, null);
			threadSimple = new ThreadSimpleBlast(world, position, (int) (Constants.EXPLOSIVE_NUCLEAR_SIZE * 2), Integer.MAX_VALUE, null, true);
			threadSimple.strictnessAtEdges = 1.7;
			threadRay.start();
			threadSimple.start();
		} else {
			SoundAPI.playSound(BallistixSounds.SOUND_NUCLEAREXPLOSION.get(), SoundCategory.BLOCKS, 1, 1, position);
		}
	}

	private Iterator<BlockPos> cachedIteratorRay;
	private Iterator<BlockPos> cachedIterator;

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
			Explosion ex = new Explosion(world, null, null, null, position.getX(), position.getY(), position.getZ(), (float) Constants.EXPLOSIVE_NUCLEAR_SIZE, false, Mode.BREAK);
			boolean rayDone = false;
			if (threadRay.isComplete && !rayDone) {
				hasStarted = true;
				synchronized (threadRay.resultsSync) {
					if (pertick == -1) {
						pertick = (int) (threadRay.resultsSync.size() / Constants.EXPLOSIVE_NUCLEAR_DURATION + 1);
						cachedIteratorRay = threadRay.resultsSync.iterator();
					}
					int finished = pertick;
					while (cachedIteratorRay.hasNext()) {
						if (finished-- < 0) {
							break;
						}
						BlockPos p = new BlockPos(cachedIteratorRay.next());

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
						if (world.random.nextFloat() < 1 / 20.0 && world instanceof ServerWorld) {
							ServerWorld serverlevel = (ServerWorld) world;
							serverlevel.getChunkSource().chunkMap.getPlayers(new ChunkPos(p), false).forEach(pl -> NetworkHandler.CHANNEL.sendTo(new PacketSpawnSmokeParticle(p), pl.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT));
						}
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
									((ServerWorld) world).getChunkSource().chunkMap.getPlayers(new ChunkPos(p), false).forEach(pl -> NetworkHandler.CHANNEL.sendTo(new PacketSpawnSmokeParticle(p), pl.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT));
								}
							}
						}
						particleHeight++;
					}
					if (!cachedIteratorRay.hasNext()) {
						rayDone = true;
					}
					if (ModList.get().isLoaded("nuclearscience")) {
						RadiationSystem.emitRadiationFromLocation(world, new Location(position), Constants.EXPLOSIVE_NUCLEAR_SIZE * 4, 150000);
					}
				}
			}
			if (ModList.get().isLoaded("nuclearscience")) {
				if (threadSimple.isComplete && rayDone) {
					if (perticksimple == -1) {
						cachedIterator = threadSimple.results.iterator();
						perticksimple = (int) (threadSimple.results.size() * 1.5 / Constants.EXPLOSIVE_NUCLEAR_DURATION + 1);
					}
					int finished = perticksimple;
					while (cachedIterator.hasNext()) {
						if (finished-- < 0) {
							break;
						}
						BlockPos p = new BlockPos(cachedIterator.next()).offset(position);
						BlockState state = world.getBlockState(p);
						Block block = state.getBlock();
						if (block == Blocks.GRASS_BLOCK || block == Blocks.DIRT) {
							if (world.random.nextFloat() < 0.7) {
								world.setBlock(p, NuclearScienceBlocks.blockRadioactiveSoil.defaultBlockState(), 2 | 16 | 32);
							}
						} else if (state.getMaterial() == Material.LEAVES) {
							world.setBlock(p, Blocks.AIR.defaultBlockState(), 2 | 16 | 32);
						} else if (state.getBlock() == Blocks.AIR || state.getBlock() == Blocks.CAVE_AIR) {
							world.setBlock(p, NuclearScienceBlocks.blockRadioactiveAir.defaultBlockState(), 2 | 16 | 32);
						}
					}
					if (!cachedIterator.hasNext()) {
						attackEntities((float) Constants.EXPLOSIVE_NUCLEAR_SIZE * 2);
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
	// TODO: Finish block model
}