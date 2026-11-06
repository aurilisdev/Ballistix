package ballistix.prefab.utils;

import ballistix.client.particle.ParticleOptionsShockwave;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
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

    private static final int MAX_SHOCKWAVE_SAMPLES = 192;

    public static void spawnDetonationBurst(double x, double y, double z, float sparkSpread, ParticleOptions spark) {
	Minecraft minecraft = Minecraft.getInstance();
	ParticleStatus detail = minecraft.options.particles().get();
	if (detail == ParticleStatus.MINIMAL)
	    return;

	ParticleEngine engine = minecraft.particleEngine;
	engine.createParticle(ParticleTypes.FLASH, x, y, z, 0, 0, 0);
	int count = Math.min(48, 12 + (int) (sparkSpread * 4));
	if (detail == ParticleStatus.DECREASED)
	    count /= 2;

	double speedScale = Math.sqrt(sparkSpread);
	for (int i = 0; i < count; i++) {
	    double angle = 2 * Math.PI * (i + Voltaic.RANDOM.nextDouble()) / count;
	    double dirX = Math.cos(angle);
	    double dirZ = Math.sin(angle);
	    double radius = sparkSpread * (0.65 + Voltaic.RANDOM.nextDouble() * 0.55);
	    double speed = speedScale * (0.22 + Voltaic.RANDOM.nextDouble() * 0.38);
	    double rise = 0.12 + Voltaic.RANDOM.nextDouble() * speed;
	    engine.createParticle(spark, x + radius * dirX, y + rise * sparkSpread, z + radius * dirZ,
		    speed * dirX, rise, speed * dirZ);
	}
    }

    public static void spawnParticleRing(ParticleOptions particle, double centerX, double centerY, double centerZ,
	    int count, double initialSpeed, boolean shouldRandomize) {
	for (float rad = 0; rad < 2.0 * Mth.PI; rad += 2 * Mth.PI / count) {
	    double x = Mth.cos(rad) * (shouldRandomize ? Voltaic.RANDOM.nextDouble() * 0.05 + 1 : 1) * initialSpeed;
	    double z = Mth.sin(rad) * (shouldRandomize ? Voltaic.RANDOM.nextDouble() * 0.05 + 1 : 1) * initialSpeed;
	    Minecraft.getInstance().particleEngine.createParticle(particle, centerX, centerY, centerZ, x, 0, z);
	}
    }

    public static void spawnParticleSphere(ParticleOptions particle, double centerX, double centerY, double centerZ,
	    int count, double thetamin, double thetamax, double initialSpeed, boolean shouldRandomize) {
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
	    double x = sinTheta * Math.cos(phi_random) * (shouldRandomize ? Voltaic.RANDOM.nextDouble() : 1)
		    * initialSpeed;
	    double z = sinTheta * Math.sin(phi_random) * (shouldRandomize ? Voltaic.RANDOM.nextDouble() : 1)
		    * initialSpeed;
	    double y = Math.cos(theta_random) * (shouldRandomize ? Voltaic.RANDOM.nextDouble() : 1) * initialSpeed;

	    // Store the point
	    Minecraft.getInstance().particleEngine.createParticle(particle, centerX, centerY, centerZ, x, y, z);
	}
    }

    public static double progressGroundShockwave(Level world, double x, double z, double progress, double spawnSize,
	    double endSize, double chance) {
	return progressGroundShockwave(world, x, z, progress, spawnSize, endSize, chance, 0xFFFFFF);
    }

    public static double progressGroundShockwave(Level world, double x, double z, double progress, double spawnSize,
	    double endSize, double chance, int color) {
	if (!Double.isFinite(progress) || progress > 1 || endSize <= spawnSize)
	    return endSize;

	progress = Math.max(0, progress);
	int radius = (int) (spawnSize + (endSize - spawnSize) * progress);
	if (radius <= 0)
	    return radius;

	Minecraft minecraft = Minecraft.getInstance();
	ParticleStatus detail = minecraft.options.particles().get();
	int budget = switch (detail) {
	    case ALL -> MAX_SHOCKWAVE_SAMPLES;
	    case DECREASED -> MAX_SHOCKWAVE_SAMPLES / 2;
	    case MINIMAL -> MAX_SHOCKWAVE_SAMPLES / 6;
	};
	double circumference = 2 * Math.PI * radius;
	int samples = (int) Math.min(circumference, budget);
	double spawnChance = Math.min(1, chance * Math.max(1, circumference / MAX_SHOCKWAVE_SAMPLES));

	float red = (color >> 16 & 0xFF) / 255f;
	float green = (color >> 8 & 0xFF) / 255f;
	float blue = (color & 0xFF) / 255f;
	float opacity = 1f - 0.85f * (float) progress;
	ParticleOptionsShockwave front = new ParticleOptionsShockwave().setParameters(red, green, blue, opacity,
		1.8f, 12, false, 0.98);
	ParticleOptionsShockwave wake = new ParticleOptionsShockwave().setParameters(red * 0.65f, green * 0.65f,
		blue * 0.65f, opacity * 0.4f, 2.4f, 22, false, 0.95);

	ParticleEngine engine = minecraft.particleEngine;
	for (int i = 0; i < samples; i++) {
	    if (Voltaic.RANDOM.nextDouble() >= spawnChance)
		continue;
	    double angle = 2 * Math.PI * (i + Voltaic.RANDOM.nextDouble()) / samples;
	    double dirX = Math.cos(angle);
	    double dirZ = Math.sin(angle);
	    int rx = Mth.floor(x + radius * dirX);
	    int rz = Mth.floor(z + radius * dirZ);
	    if (!world.hasChunkAt(new BlockPos(rx, 0, rz)))
		continue;
	    int ry = world.getHeight(Types.WORLD_SURFACE, rx, rz);
	    engine.createParticle(front, rx + 0.5, ry + 0.25, rz + 0.5, dirX * 0.18, 0, dirZ * 0.18);
	    if (detail == ParticleStatus.ALL) {
		engine.createParticle(wake, rx + 0.5 - dirX * 0.7, ry + 0.25, rz + 0.5 - dirZ * 0.7,
			-dirX * 0.1, 0.07, -dirZ * 0.1);
	    }
	}
	return radius;
    }
}
