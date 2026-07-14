package ballistix.client.shake;

import java.util.Random;

public class PerlinNoise {
	private static final int PERM_SIZE = 512;
	private final int[] permutation = new int[PERM_SIZE];

	public PerlinNoise(long seed) {
		Random rand = new Random(seed);

		int[] p = new int[256];
		for (int i = 0; i < 256; i++) {
			p[i] = i;
		}

		// Shuffle
		for (int i = 255; i > 0; i--) {
			int idx = rand.nextInt(i + 1);
			int temp = p[i];
			p[i] = p[idx];
			p[idx] = temp;
		}

		// Duplicate
		for (int i = 0; i < 256; i++) {
			permutation[256 + i] = permutation[i] = p[i];
		}
	}

	private static double fade(double t) {
		// 6t^5 – 15t^4 + 10t^3
		return t * t * t * (t * (t * 6 - 15) + 10);
	}

	private static double lerp(double a, double b, double t) {
		return a + t * (b - a);
	}

	private static double grad(int hash, double x, double y, double z) {
		int h = hash & 15;
		double u = h < 8 ? x : y;
		double v = h < 4 ? y : h == 12 || h == 14 ? x : z;
		return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
	}

	public double noise(double x, double y, double z) {
		int X = (int) Math.floor(x) & 255;
		int Y = (int) Math.floor(y) & 255;
		int Z = (int) Math.floor(z) & 255;

		x -= Math.floor(x);
		y -= Math.floor(y);
		z -= Math.floor(z);

		double u = fade(x);
		double v = fade(y);
		double w = fade(z);

		int A = permutation[X] + Y;
		int AA = permutation[A] + Z;
		int AB = permutation[A + 1] + Z;
		int B = permutation[X + 1] + Y;
		int BA = permutation[B] + Z;
		int BB = permutation[B + 1] + Z;

		return lerp(
				lerp(lerp(grad(permutation[AA], x, y, z), grad(permutation[BA], x - 1, y, z), u),
						lerp(grad(permutation[AB], x, y - 1, z), grad(permutation[BB], x - 1, y - 1, z), u), v),
				lerp(lerp(grad(permutation[AA + 1], x, y, z - 1), grad(permutation[BA + 1], x - 1, y, z - 1), u),
						lerp(grad(permutation[AB + 1], x, y - 1, z - 1), grad(permutation[BB + 1], x - 1, y - 1, z - 1),
								u),
						v),
				w);
	}
}
