package ballistix.prefab.utils;

import ballistix.client.particle.ParticleOptionsShockwave;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import voltaic.Voltaic;

/**
 * 
 */
/**
 * 
 */
public class ParticleUtilities {

	public static void spawnParticleRing(ParticleOptions particle, double centerX, double centerY, double centerZ, int count, double initialSpeed, boolean shouldRandomize) {
		for (float rad = 0; rad < 2.0 * Mth.PI; rad += 2 * Mth.PI / count) {
			double x = Mth.cos(rad) * (shouldRandomize ? Voltaic.RANDOM.nextDouble() * 0.05 + 1 : 1) * initialSpeed;
			double z = Mth.sin(rad) * (shouldRandomize ? Voltaic.RANDOM.nextDouble() * 0.05 + 1 : 1) * initialSpeed;
			Minecraft.getInstance().particleEngine.createParticle(particle, centerX, centerY, centerZ, x, 0, z);
		}
	}

	public static void spawnParticleSphere(ParticleOptions particle, double centerX, double centerY, double centerZ, int count, double thetamin, double thetamax, double initialSpeed, boolean shouldRandomize) {
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
			double theta_random = theta * (1 + Voltaic.RANDOM.nextDouble() * 0.15);
			double phi_random = phi * (1 + Voltaic.RANDOM.nextDouble() * 0.15);
			// Convert spherical (theta, phi) -> Cartesian (x, y, z)
			double sinTheta = Math.sin(theta_random);
			double x = sinTheta * Math.cos(phi_random) * (shouldRandomize ? Voltaic.RANDOM.nextDouble() : 1) * initialSpeed;
			double z = sinTheta * Math.sin(phi_random) * (shouldRandomize ? Voltaic.RANDOM.nextDouble() : 1) * initialSpeed;
			double y = Math.cos(theta_random) * (shouldRandomize ? Voltaic.RANDOM.nextDouble() : 1) * initialSpeed;

			// Store the point
			Minecraft.getInstance().particleEngine.createParticle(particle, centerX, centerY, centerZ, x, y, z);
		}
	}

	/**
	 * @param world
	 * @param x
	 * @param z
	 * @param progress
	 * @param spawnSize
	 * @param endSize
	 * @param chance
	 * @return currentShockwaveRadius
	 */
	public static double progressGroundShockwave(Level world, double x, double z, double progress, double spawnSize, double endSize, double chance) {
		if (progress > 1)
			return endSize;
		int r = (int) (spawnSize + (endSize - spawnSize) * progress);
		for (double i = 0; i < 360.0; i += 360.0 / (Math.PI * 2 * r) * (0.5 + Voltaic.RANDOM.nextDouble())) {
			if (Voltaic.RANDOM.nextDouble() < chance) {
				double angle = Math.toRadians(i);
				double dirX = Math.cos(angle);
				double dirZ = Math.sin(angle);
				int rx = (int) (x + r * dirX);
				int rz = (int) (z + r * dirZ);
				int ry = world.getHeight(Types.WORLD_SURFACE, rx, rz);

				int life = 15;
				if (life > 0) {
					Minecraft.getInstance().particleEngine.createParticle(new ParticleOptionsShockwave().setParameters(1f, 1f, 1f, (float) (1.0f - progress), 1.3f, life, false, 1), rx + 1.5, ry, rz + 0.5, 0, 0, 0);
				}
			}
		}
		return r;
	}
}
