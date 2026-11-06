package ballistix.client.effect;

import ballistix.client.effect.particle.BlastParticle;
import ballistix.client.effect.particle.BlastParticleRenderer;
import ballistix.client.effect.particle.ParticleKind;
import ballistix.common.settings.BallistixConfig;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

/** What every phase of one blast shares: its place, size and wind, its random streams and particle spawning. */
final class BlastScene {

    private static final float MIN_SCALE = 0.55f;
    private static final float MAX_SCALE = 2.25f;
    private static final float FINE_DETAIL_THRESHOLD = 0.25f;

    final ClientLevel level;
    final Vec3 origin;
    final BlastStyle style;
    final double radius;
    final Timeline timeline;
    /** Size of the whole effect relative to the style's reference radius. */
    final float scale;
    /** Radius in blocks of the fireball core. */
    final float core;
    final Vec3 wind;
    final RandomSource random;
    /** Separate stream for the ground haze so its pattern does not depend on how many other puffs were spawned. */
    final RandomSource hazeRandom;
    /** Fraction of the full particle count to spawn, from the player's particle setting. */
    final float detail;

    BlastScene(ClientLevel level, Vec3 origin, BlastStyle style, double radius, ParticleStatus particles) {
	this.level = level;
	this.origin = origin;
	this.style = style;
	this.radius = radius;
	long seed = level.random.nextLong();
	random = RandomSource.create(seed);
	hazeRandom = RandomSource.create(~seed);
	wind = direction(random.nextDouble() * Mth.TWO_PI, 0);
	scale = Mth.clamp((float) Math.sqrt(radius / style.extent.referenceRadius()), MIN_SCALE, MAX_SCALE);
	core = style.extent.core() * scale;
	timeline = Timeline.of(style, scale);
	detail = particleDetail(particles) * style.particleDensity
		* BallistixConfig.INSTANCE.BLAST_PARTICLE_DENSITY.get().floatValue();
    }

    static Vec3 direction(double azimuth, double elevation) {
	double horizontal = Math.cos(elevation);
	return new Vec3(Math.cos(azimuth) * horizontal, Math.sin(elevation), Math.sin(azimuth) * horizontal);
    }

    private static float particleDetail(ParticleStatus particles) {
	return switch (particles) {
	case ALL -> 1f;
	case DECREASED -> 0.57f;
	case MINIMAL -> 0.23f;
	};
    }

    int count(int fullCount) {
	return Math.max(1, Math.round(fullCount * detail));
    }

    /** Scales a base lifetime by the style, so the big blasts' clouds linger. */
    int lifetime(int ticks) {
	return Math.round(ticks * style.timing.lifetimeScale());
    }

    /** Sparks, chips and embers are dropped at the lowest particle settings. */
    boolean hasFineDetail() {
	return detail > FINE_DETAIL_THRESHOLD;
    }

    BlastParticle puff(ParticleKind kind, Vec3 pos, Vec3 velocity, float size, int life, int hotColor,
	    int coolColor) {
	BlastParticle particle = new BlastParticle(level, kind, pos, velocity, size, life, hotColor, coolColor);
	BlastParticleRenderer.add(particle);
	return particle;
    }

    /** The tick lengths of a blast's phases once scaled to the configured blast radius. */
    record Timeline(int fireballTicks, int pressureFrontTicks, int aftermathTicks, int craterLingerTicks) {

	private static final float MIN_DURATION_SCALE = 0.75f;
	private static final float MAX_DURATION_SCALE = 1.6f;

	private static Timeline of(BlastStyle style, float scale) {
	    float durationScale = Mth.clamp(scale, MIN_DURATION_SCALE, MAX_DURATION_SCALE);
	    BlastStyle.Timing timing = style.timing;
	    return new Timeline(timing.fireballTicks(), (int) (24 + style.extent.core() * 0.95f),
		    Math.round(timing.aftermathTicks() * durationScale),
		    Math.round(timing.craterLingerTicks() * durationScale));
	}
    }
}
