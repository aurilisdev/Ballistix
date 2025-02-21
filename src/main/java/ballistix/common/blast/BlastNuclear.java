package ballistix.common.blast;

import java.util.Iterator;

import ballistix.References;
import ballistix.api.blast.IHasCustomRender;
import ballistix.client.particle.ParticleOptionBlastSmoke;
import ballistix.common.blast.thread.ThreadSimpleBlast;
import ballistix.common.blast.thread.raycast.ThreadRaycastBlast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.packet.type.client.particle.BlastParticleSpawnType;
import ballistix.common.packet.type.client.particle.PacketSpawnBlastParticle;
import ballistix.common.settings.Constants;
import ballistix.compatibility.griefdefender.GriefDefenderHandler;
import ballistix.compatibility.nuclearscience.RadiationHandler;
import ballistix.registers.BallistixSounds;
import electrodynamics.prefab.utilities.object.Location;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.network.PacketDistributor;

public class BlastNuclear extends BlastCalculating implements IHasCustomRender {

	public BlastNuclear(Level world, BlockPos position) {
		super(world, position);
	}

	@Override
	public void doPreExplode() {
		if (!world.isClientSide) {
			threadRay = new ThreadRaycastBlast(world, position, (int) Constants.EXPLOSIVE_NUCLEAR_SIZE,
					(float) Constants.EXPLOSIVE_NUCLEAR_ENERGY, null);
			threadSimple = new ThreadSimpleBlast(world, position, (int) (Constants.EXPLOSIVE_NUCLEAR_SIZE * 2.5),
					Integer.MAX_VALUE, null, getBlastType().ordinal());
			threadSimple.strictnessAtEdges = 1.7;
			threadRay.start();
			threadSimple.start();
		}
	}

	private Iterator<BlockPos> cachedIteratorRay;
	private Iterator<BlockPos> cachedIterator;

	private ThreadRaycastBlast threadRay;
	private ThreadSimpleBlast threadSimple;
	private int pertick = -1;
	private int perticksimple = -1;

	@Override
	public boolean shouldRender() {
		return pertick > 0;
	}

	@Override
	public boolean doExplode(int callCount) {
		super.doExplode(callCount);
		if (threadRay == null) {
			return !world.isClientSide;
		}
		Explosion ex = new Explosion(world, null, null, null, position.getX(), position.getY(), position.getZ(),
				(float) Constants.EXPLOSIVE_NUCLEAR_SIZE, false, BlockInteraction.DESTROY, ParticleTypes.EXPLOSION,
				ParticleTypes.EXPLOSION_EMITTER, SoundEvents.GENERIC_EXPLODE);
		boolean rayDone = false;
		boolean addRadiation = false;
		if (ticksSinceBlastStart == 1) {
			world.playSound(null, position, BallistixSounds.SOUND_NUCLEAREXPLOSION.get(), SoundSource.BLOCKS, 25, 1);
		}
		if (threadRay.isComplete && !rayDone && callCount % 2 == 0) {

			synchronized (threadRay.resultsSync) {
				if (pertick == -1) {
					hasStarted = true;
					pertick = (int) (threadRay.resultsSync.size() / Constants.EXPLOSIVE_NUCLEAR_DURATION * 2 + 1);
					cachedIteratorRay = threadRay.resultsSync.iterator();
				}
				int finished = pertick;
				while (cachedIteratorRay.hasNext()) {
					if (finished-- < 0) {
						break;
					}
					BlockPos p = new BlockPos(cachedIteratorRay.next());

					switch (griefPreventionMethod) {
					case GRIEF_DEFENDER:
						if (!GriefDefenderHandler.shouldHarmBlock(p)) {
							continue;
						}
						break;
					default:
						break;
					}

					BlockState state = Blocks.AIR.defaultBlockState();
					double dis = new Location(p.getX(), 0, p.getZ())
							.distance(new Location(position.getX(), 0, position.getZ()));
					if (world.random.nextFloat() < 1 / 5.0 && dis < 15) {
						BlockPos offset = p.relative(Direction.DOWN);
						if (!threadRay.results.contains(offset) && world.random.nextFloat() < (15.0f - dis) / 15.0f) {
							state = Blocks.FIRE.defaultBlockState();
						}
					}
					world.getBlockState(p).getBlock().wasExploded(world, p, ex);
					world.setBlock(p, state, 3);
					if (world.random.nextFloat() < 1 / 20.0 && world instanceof ServerLevel serverlevel) {
						if (ticksSinceBlastStart == 1) {
							serverlevel.getChunkSource().chunkMap.getPlayers(new ChunkPos(p), false).forEach(player -> {
								serverlevel.playSound(null, player.getX(), player.getY(), player.getZ(),
										BallistixSounds.SOUND_NUCLEAREXPLOSION.get(), // Change to your sound event
										SoundSource.PLAYERS, 25, 1.0F);
							});
						}
						serverlevel.getChunkSource().chunkMap.getPlayers(new ChunkPos(p), false)
								.forEach(pl -> PacketDistributor.sendToPlayer(pl,
										new PacketSpawnBlastParticle(p, BlastParticleSpawnType.EXPLOSION)));
//			Minecraft.getInstance().particleEngine.createParticle(ParticleTypes.EXPLOSION, p.getX() + 0.5,
//				p.getY() + 0.5, p.getZ() + 0.5, 0, 0, 0);
					}
				}
				if (!cachedIteratorRay.hasNext()) {
					rayDone = true;
				}
				if (ModList.get().isLoaded(References.NUCLEAR_SCIENCE_ID)) {
					addRadiation = true;
				}
			}
		}
		if (threadSimple.isComplete && threadRay.isComplete && callCount % 2 == 0) {
			if (ModList.get().isLoaded(References.NUCLEAR_SCIENCE_ID)) {
				if (ticksSinceBlastStart == 1)
					attackEntities((float) Constants.EXPLOSIVE_NUCLEAR_SIZE * 2, ex);

				boolean add = switch (griefPreventionMethod) {
				case GRIEF_DEFENDER -> GriefDefenderHandler.shouldAddParticle(position);
				default -> true;
				};

				if (add && addRadiation) {
					RadiationHandler.addNuclearExplosionRadiation(world, position);
				}
			}
			if (perticksimple == -1) {
				cachedIterator = threadSimple.results.iterator();
				perticksimple = (int) (threadSimple.results.size() / Constants.EXPLOSIVE_NUCLEAR_DURATION * 2 + 1);
			}
			int finished = perticksimple;
			while (cachedIterator.hasNext()) {
				if (finished-- < 0) {
					break;
				}

				BlockPos pos = new BlockPos(cachedIterator.next()).offset(position);

				switch (griefPreventionMethod) {
				case GRIEF_DEFENDER:
					if (!GriefDefenderHandler.shouldHarmBlock(pos)) {
						continue;
					}
					break;
				default:
					break;
				}
				if (ModList.get().isLoaded(References.NUCLEAR_SCIENCE_ID) && pos.distSqr(position)
						/ (Constants.EXPLOSIVE_NUCLEAR_SIZE * Constants.EXPLOSIVE_NUCLEAR_SIZE * 4) < 0.6
								+ 0.2 * world.random.nextDouble()) {
					RadiationHandler.addNuclearExplosiveIrradidatedBlock(pos, world);
				}
			}
			if (!cachedIterator.hasNext()) {
				if (ticksSinceBlastStart == 1)
					attackEntities((float) Constants.EXPLOSIVE_NUCLEAR_SIZE * 2, ex);
				return ticksSinceBlastStart > 1000;
			}
		}
		return false;

	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void produceParticles() {

		RandomSource random = world.random;

		double x = position.getX() + 0.5;
		double y = position.getY() + 0.5;
		double z = position.getZ() + 0.5;

		double centerX = x;
		double centerY = y;
		double centerZ = z;

		double initialSpeed = 1.2;

		if (ticksSinceBlastStart < 5) {
			// Fireball
			ParticleOptions particle = new ParticleOptionBlastSmoke().setParameters(1.0f, 1.0f, 1.0f, 3f, -0.045f, 1500,
					true, true, 200, 0.97);
			BlastThermobaric.spawnSurroundingParticles(particle, random, centerX, centerY, centerZ, 150, 10, 90,
					initialSpeed, true);

			// Centersmokes
			initialSpeed = 2; // Increase/decrease to taste
			particle = new ParticleOptionBlastSmoke().setParameters(1.0f, 1.0f, 1.0f, 2.5f, 0.045f, 1500, true, 0.99);
			BlastThermobaric.spawnSurroundingParticles(particle, random, centerX, centerY, centerZ, 75, 0, 20,
					initialSpeed, true);
			// Centersmokes
			initialSpeed = 2; // Increase/decrease to taste
			particle = new ParticleOptionBlastSmoke().setParameters(1.0f, 1.0f, 1.0f, 2.5f, 0.01f, 1500, true, 0.995);
			BlastThermobaric.spawnSurroundingParticles(particle, random, centerX, centerY, centerZ, 125, 0, 20,
					initialSpeed, true);
			// Centersmokes
			initialSpeed = 2; // Increase/decrease to taste
			particle = new ParticleOptionBlastSmoke().setParameters(1.0f, 1.0f, 1.0f, 2.5f, -0.015f, 1500, true, 0.97);
			BlastThermobaric.spawnSurroundingParticles(particle, random, centerX, centerY, centerZ, 100, 0, 20,
					initialSpeed, true);

			// Shockwave
			if (ticksSinceBlastStart == 2) {
				initialSpeed = 1.5; // Increase/decrease to taste
				particle = new ParticleOptionBlastSmoke().setParameters(1.5f, 1.5f, 1.5f, 3f, 0, 150, false, 1);
				BlastThermobaric.spawnSurroundingParticles(particle, random, centerX, centerY + 25, centerZ, 200, 0, 0,
						initialSpeed, false);
			}
		} else {

			// Centersmokes rising
			initialSpeed = 0.7; // Increase/decrease to taste
			ParticleOptions particle = new ParticleOptionBlastSmoke().setParameters(1.0f, 1.0f, 1.0f, 3f, -0.045f,
					Mth.clamp(1500 - ticksSinceBlastStart, 1, 1500), true, 0.95);
			BlastThermobaric.spawnSurroundingParticles(particle, random, centerX,
					centerY + 0.024f * ticksSinceBlastStart, centerZ, 1, -20, 20, initialSpeed, true);
			if (ticksSinceBlastStart < 1250) { // Centerfire rising
				initialSpeed = 0.5; // Increase/decrease to taste
				particle = new ParticleOptionBlastSmoke().setParameters(1.0f, 1.0f, 1.0f, 3f, -0.045f,
						Mth.clamp(1250 - ticksSinceBlastStart, 1, 1250), true, true, 500, 0.97);
				BlastThermobaric.spawnSurroundingParticles(particle, random, centerX,
						centerY + 0.033f * ticksSinceBlastStart, centerZ, 1, -20, 20, initialSpeed, true);
			}
		}
	}

	@Override
	public boolean isDoneCalculating() {
		if (world.isClientSide) {
			return shouldRenderCustomClient;
		}
		return (threadRay == null || threadRay.isComplete);
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
