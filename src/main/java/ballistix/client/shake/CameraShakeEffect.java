package ballistix.client.shake;

/** One camera shake: an impact jolt followed by a rumble, both dying away over the shake's duration. */
public class CameraShakeEffect {

    private static final Offsets NONE = new Offsets(0, 0);

    private static final double HIGH_OCTAVE_WEIGHT = 0.55;
    private static final double MID_OCTAVE_WEIGHT = 0.30;
    private static final double LOW_OCTAVE_WEIGHT = 0.15;
    private static final double PITCH_NOISE_LANE = 11.7;
    private static final double ROLL_NOISE_LANE = 37.3;
    private static final double CROSS_COUPLING = 0.22;
    private static final double SOFT_LIMIT = 1.0;
    private static final double SOFT_HEADROOM = 0.6;
    private static final double FADE_OUT_START = 0.75;
    private static final double ENVELOPE_EXPONENT = 1.4;
    private static final double RUMBLE_RAMP_TICKS = 2;

    private final double duration;
    private final double amplitude;
    private final long startTime;
    private final PerlinNoise noise;
    private final int rollDirection;

    public CameraShakeEffect(double duration, double amplitude, long startTime, long seed) {
	this.duration = duration;
	this.amplitude = amplitude;
	this.startTime = startTime;
	noise = new PerlinNoise(seed);
	rollDirection = (seed & 1) == 0 ? 1 : -1;
    }

    public Offsets getShakeOffsets(double gameTime) {
	double elapsed = gameTime - startTime;
	if (elapsed <= 0 || elapsed >= duration)
	    return NONE;

	double remaining = 1 - elapsed / duration;
	double strength = amplitude * Math.pow(remaining, ENVELOPE_EXPONENT);
	double impact = impactWave(elapsed);
	double rumble = Math.min(elapsed / RUMBLE_RAMP_TICKS, 1) * (0.35 + 0.65 * remaining);

	double pitch = -0.52 * impact + 0.6 * rumble * rumbleNoise(elapsed, PITCH_NOISE_LANE);
	double roll = 0.85 * rollDirection * impact + rumble * rumbleNoise(elapsed, ROLL_NOISE_LANE);

	// Each axis leaks into the other, which rocks the view diagonally instead of along two independent axes.
	double coupledPitch = softClamp(pitch + CROSS_COUPLING * roll);
	double coupledRoll = softClamp(roll + CROSS_COUPLING * pitch);

	double fade = fadeOut(elapsed);
	return new Offsets((float) (strength * coupledPitch * fade), (float) (strength * coupledRoll * fade));
    }

    public boolean isComplete(long gameTime) {
	return gameTime - startTime >= duration;
    }

    /** The initial hit, a higher ring and a low tick, all damped away within a few ticks. */
    private static double impactWave(double elapsed) {
	double hit = 0.85 * Math.sin(elapsed * 0.95);
	double ring = 0.30 * Math.sin(elapsed * 1.37 + 1.7);
	double tick = 0.15 * Math.sin(elapsed * 2.23 + 3.1);
	return (hit + ring + tick) * Math.exp(-elapsed / 4.5);
    }

    private double rumbleNoise(double elapsed, double lane) {
	return HIGH_OCTAVE_WEIGHT * noise.noise(elapsed * 2.0 + 1.0, lane, 0)
		+ MID_OCTAVE_WEIGHT * noise.noise(elapsed * 0.7 + 2.0, lane + 17.3, 0)
		+ LOW_OCTAVE_WEIGHT * noise.noise(elapsed * 0.15, lane + 41.9, 0);
    }

    /** Smoothly eases the whole shake to zero over its last quarter. */
    private double fadeOut(double elapsed) {
	double fadeStart = duration * FADE_OUT_START;
	if (elapsed <= fadeStart)
	    return 1;
	double t = (elapsed - fadeStart) / (duration - fadeStart);
	double t3 = t * t * t;
	double t4 = t3 * t;
	double t5 = t4 * t;
	return Math.max(0, 1 - 6 * t5 + 15 * t4 - 10 * t3);
    }

    /** Passes values below the limit unchanged and rolls larger ones off smoothly towards limit + headroom. */
    private static double softClamp(double value) {
	double magnitude = Math.abs(value);
	if (magnitude <= SOFT_LIMIT)
	    return value;
	return Math.signum(value) * (SOFT_LIMIT + SOFT_HEADROOM * Math.tanh((magnitude - SOFT_LIMIT) / SOFT_HEADROOM));
    }

    public record Offsets(float pitch, float roll) {
    }
}
