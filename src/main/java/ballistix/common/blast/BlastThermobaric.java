package ballistix.common.blast;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.joml.Vector3f;

import ballistix.api.blast.IHasCustomRender;
import ballistix.client.particle.ParticleOptionBlastSmoke;
import ballistix.common.blast.thread.raycast.ThreadRaycastBlast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.packet.type.client.particle.BlastParticleSpawnType;
import ballistix.common.packet.type.client.particle.PacketSpawnBlastParticle;
import ballistix.common.settings.Constants;
import ballistix.compatibility.griefdefender.GriefDefenderHandler;
import electrodynamics.Electrodynamics;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;

public class BlastThermobaric extends BlastCalculating implements IHasCustomRender {

	public BlastThermobaric(Level world, BlockPos position) {
		super(world, position);
	}

	@Override
	public void doPreExplode() {
		if (!world.isClientSide) {
			thread = new ThreadRaycastBlast(world, position, (int) Constants.EXPLOSIVE_THERMOBARIC_SIZE,
					(float) Constants.EXPLOSIVE_THERMOBARIC_ENERGY, null);
			thread.start();
		}

	}

	private ThreadRaycastBlast thread;
	private int pertick = -1;
	private Iterator<BlockPos> cachedIterator;

	@Override
	public boolean doExplode(int callCount) {
		super.doExplode(callCount);
		if (thread == null) {
			return !world.isClientSide;
		}
		if (thread.isComplete && callCount % 2 == 0) {
			Explosion ex = new Explosion(world, null, null, null, position.getX(), position.getY(), position.getZ(),
					(float) Constants.EXPLOSIVE_THERMOBARIC_SIZE, false, BlockInteraction.DESTROY,
					ParticleTypes.EXPLOSION, ParticleTypes.EXPLOSION_EMITTER, SoundEvents.GENERIC_EXPLODE);
			synchronized (thread.resultsSync) {
				if (pertick == -1) {
					hasStarted = true;
					world.playSound(null, position, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 25, 1);
					pertick = (int) (thread.resultsSync.size() / Constants.EXPLOSIVE_THERMOBARIC_DURATION * 2 + 1);
					cachedIterator = thread.resultsSync.iterator();
				}
				int finished = pertick;
				while (cachedIterator.hasNext()) {
					if (finished-- < 0) {
						break;
					}
					BlockPos p = new BlockPos(cachedIterator.next());
					Block block = world.getBlockState(p).getBlock();
					switch (griefPreventionMethod) {
					case NONE:
						block.wasExploded(world, p, ex);
						world.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
						break;
					case GRIEF_DEFENDER:
						GriefDefenderHandler.destroyBlock(block, ex, p, world);
						break;
					case SABER_FACTIONS:
						break;
					}
					if (world.random.nextFloat() < 0 / 10.0 && world instanceof ServerLevel serverlevel) {
						serverlevel.getChunkSource().chunkMap.getPlayers(new ChunkPos(p), false)
								.forEach(pl -> PacketDistributor.sendToPlayer(pl,
										new PacketSpawnBlastParticle(p, BlastParticleSpawnType.EXPLOSION)));
//			Minecraft.getInstance().particleEngine.createParticle(ParticleTypes.EXPLOSION, p.getX() + 0.5,
//				p.getY() + 0.5, p.getZ() + 0.5, 0, 0, 0);
					}
				}
				if (!cachedIterator.hasNext()) {
					attackEntities((float) Constants.EXPLOSIVE_THERMOBARIC_SIZE * 2, ex);
					return true;
				}
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

		double initialSpeed = 0.8;
		
		if (ticksSinceBlastStart == 3) {
			initialSpeed = 1; // Increase/decrease to taste
			ParticleOptions particle = new ParticleOptionBlastSmoke().setParameters(1.5f, 1.5f, 1.5f, 2f, 0, 150, false,
					1);
			spawnSurroundingParticles(particle, random, x, y + 12, z, 100, 0, 0, initialSpeed, false);
		}
		if (ticksSinceBlastStart <= 5) {
			// Fireball
			ParticleOptions particle = new ParticleOptionBlastSmoke().setParameters(1.0f, 1.0f, 1.0f, 2f, -0.045f, 750,
					true, true, 40, 0.95);
			spawnSurroundingParticles(particle, random, x, y, z, 100, 50, 90, initialSpeed, true);

			// Centersmokes
			initialSpeed = 1; // Increase/decrease to taste
			particle = new ParticleOptionBlastSmoke().setParameters(0.8f, 0.8f, 0.8f, 2.5f, 0.033f, 750, true, 0.95);
			spawnSurroundingParticles(particle, random, x, y, z, 100, 0, 20, initialSpeed, true);

			// Centersmokes
			initialSpeed = 1; // Increase/decrease to taste
			particle = new ParticleOptionBlastSmoke().setParameters(0.8f, 0.8f, 0.8f, 2.5f, -0.033f, 750, true, 0.95);
			spawnSurroundingParticles(particle, random, x, y, z, 100, 0, 20, initialSpeed, true);
		}
		if (ticksSinceBlastStart > 15)
			return;
		Set<BlockPos> edgeBlocks = new HashSet<>();

		int r = (int) (ticksSinceBlastStart * 3 + Constants.EXPLOSIVE_THERMOBARIC_SIZE / 3.0);
		for (double i = 0; i < 360.0; i += 360.0 / (Math.PI * 2 * r)) {
			if (Electrodynamics.RANDOM.nextDouble() < 0.1) {
				for (int j = 0; j < 3; j++) {
					double angle = Math.toRadians(i);
					double dirX = Math.cos(angle);
					double dirZ = Math.sin(angle);
					int rx = (int) (x + (r + j) * dirX);
					int rz = (int) (z + (r + j) * dirZ);
					int ry = world.getHeight(Types.WORLD_SURFACE, rx, rz);

					Minecraft.getInstance().particleEngine.createParticle(
							new ParticleOptionBlastSmoke().setParameters(1.3f, 1.3f, 1.3f, 2, 0, 10, false, 1), rx + 0.5,
							ry, rz + 0.5, dirX * 0.6, 0, dirZ * 0.6);
				}
			}
		}

	}

	static void spawnSurroundingParticles(ParticleOptions particle, RandomSource random, double centerX, double centerY,
			double centerZ, int count, double thetamin, double thetamax, double initialSpeed, boolean shouldRandomize) {
		if (thetamin == thetamax) {
			for (float rad = 0; rad < 2.0 * Mth.PI; rad += 2 * Mth.PI / count) {
				double x = Mth.cos(rad) * (shouldRandomize ? random.nextDouble() * 0.05 + 1 : 1) * initialSpeed;
				double z = Mth.sin(rad) * (shouldRandomize ? random.nextDouble() * 0.05 + 1 : 1) * initialSpeed;
				Minecraft.getInstance().particleEngine.createParticle(particle, centerX, centerY, centerZ, x, 0, z);
			}
		} else {
			// Convert latitude bounds to radians
			double latMinRad = Math.toRadians(thetamin);
			double latMaxRad = Math.toRadians(thetamax);

			// Precompute sin of latitudes for the uniform cos(theta) range
			double sinLatMin = Math.sin(latMinRad);
			double sinLatMax = Math.sin(latMaxRad);

			for (int i = 0; i < count; i++) {
				// Uniform random in [0,1]
				double u = Math.random();
				double v = Math.random();

				// Compute cos(theta) in the correct range
				double cosTheta = sinLatMin + u * (sinLatMax - sinLatMin);
				double theta = Math.acos(cosTheta);

				// Azimuth angle in [0, 2*pi)
				double phi = 2.0 * Math.PI * v;
				double theta_random = theta * (1 + random.nextDouble() * 0.15);
				double phi_random = phi * (1 + random.nextDouble() * 0.15);
				// Convert spherical (theta, phi) -> Cartesian (x, y, z)
				double sinTheta = Math.sin(theta_random);
				double x = sinTheta * Math.cos(phi_random) * (shouldRandomize ? random.nextDouble() : 1) * initialSpeed;
				double z = sinTheta * Math.sin(phi_random) * (shouldRandomize ? random.nextDouble() : 1) * initialSpeed;
				double y = Math.cos(theta_random) * (shouldRandomize ? random.nextDouble() : 1) * initialSpeed;

				// Store the point
				Minecraft.getInstance().particleEngine.createParticle(particle, centerX, centerY, centerZ, x, y, z);
			}
		}
	}

	@Override
	public boolean isInstantaneous() {
		return false;
	}

	@Override
	public SubtypeBlast getBlastType() {
		return SubtypeBlast.thermobaric;
	}
	// TODO: Finish block model

	@Override
	public boolean isDoneCalculating() {
		if (world.isClientSide) {
			return shouldRenderCustomClient;
		}
		return thread == null || thread.isComplete;
	}

	@Override
	public boolean shouldRender() {
		return pertick > 0;
	}

}
