package ballistix.client.shake;

public class CameraShakeEffect {
	private final double duration; // in ticks or seconds
	private final double fadeInTime; // portion or absolute
	private final double fadeOutTime;
	private final double amplitude; // max angle offset
	private final double frequency; // how quickly the noise evolves
	private final long startTime; // system time or in-game tick

	private final PerlinNoise noise;
	private final double xOffsetSeed;
	private final double yOffsetSeed;

	public CameraShakeEffect(double duration, double fadeInTime, double fadeOutTime, double amplitude, double frequency,
			long worldTime, long seed) {
		this.duration = duration;
		this.fadeInTime = fadeInTime;
		this.fadeOutTime = fadeOutTime;
		this.amplitude = amplitude;
		this.frequency = frequency;
		this.startTime = worldTime;

		this.noise = new PerlinNoise(seed);
		// Random offsets so that x/y samples don't use the same chunk of noise
		// This prevents them from being locked in the same pattern.
		this.xOffsetSeed = seed * 1.37;
		this.yOffsetSeed = seed * 2.17;
	}

	/**
	 * Computes the current yaw/pitch offset for this shake effect.
	 * 
	 * @param currentTime The current in-game tick or system time
	 * @return a float[] of size 2: {yawOffset, pitchOffset}
	 */
	public float[] getShakeOffsets(long currentTime) {
		double elapsed = currentTime - startTime;
		if (elapsed < 0 || elapsed > duration) {
			// outside of effect window
			return new float[] { 0f, 0f };
		}

		// fade factor: 0->1->0 over the life of the effect
		double fadeMultiplier = computeFadeMultiplier(elapsed);

		// sample noise
		double t = elapsed * frequency * 0.01; // scale time for noise
		double noiseX = noise.noise(xOffsetSeed + t, 0.0, 0.0);
		double noiseY = noise.noise(yOffsetSeed + t, 0.0, 0.0);

		// Map noise from [-1,1] to offset
		float yawOffset = (float) (noiseX * amplitude * fadeMultiplier);
		float pitchOffset = (float) (noiseY * amplitude * fadeMultiplier);

		return new float[] { yawOffset, pitchOffset };
	}

	private double computeFadeMultiplier(double elapsed) {
		// Let’s treat fadeInTime / fadeOutTime as absolute times in ticks for clarity.
		if (elapsed < fadeInTime) {
			// Fade in from 0 to 1
			return elapsed / fadeInTime;
		} else if (elapsed > duration - fadeOutTime) {
			// Fade out from 1 to 0
			double fadeOutStart = duration - fadeOutTime;
			return 1.0 - (elapsed - fadeOutStart) / fadeOutTime;
		} else {
			// Full intensity
			return 1.0;
		}
	}

	public boolean isComplete(long currentTime) {
		return currentTime - startTime > duration;
	}
}
