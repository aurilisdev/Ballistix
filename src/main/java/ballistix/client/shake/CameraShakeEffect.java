package ballistix.client.shake;

public class CameraShakeEffect {

    private static final Offsets NONE = new Offsets(0, 0);

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

	// A fast impact gives way to a softer, irregular rumble.
	double remaining = 1 - elapsed / duration;
	double strength = amplitude * remaining * remaining;
	double impact = Math.sin(elapsed * 0.95) * Math.exp(-elapsed / 4.5);
	double rumble = Math.min(elapsed / 2, 1);
	double time = elapsed * 0.34;
	float pitch = (float) (strength * (-0.52 * impact + 0.23 * rumble * noise.noise(time, 11.7, 0)));
	float roll = (float) (strength * (0.85 * rollDirection * impact + 0.65 * rumble * noise.noise(time, 37.3, 0)));
	return new Offsets(pitch, roll);
    }

    public boolean isComplete(long gameTime) {
	return gameTime - startTime >= duration;
    }

    public record Offsets(float pitch, float roll) {
    }
}
