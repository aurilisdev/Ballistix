package ballistix.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.Mth;

public class ParticleBlastSmoke extends TextureSheetParticle {
	private final SpriteSet sprites;
	private boolean burning;
	private int burningTime;
	private double friction = 0.95;
	private float startRed = 1.0F, startGreen = 0.7F, startBlue = 0.2F;
	private float endRed = 0.6F, endGreen = 0.2f, endBlue = 0.2f;
	private float endGray = 0.325F;
	private float startQuadSize;

	public static double smoothStep(double u) {
		// clamp u to [0, 1] to be safe, though ideally caller ensures that
		if (u < 0)
			u = 0;
		if (u > 1)
			u = 1;
		return Math.pow((1 - Mth.cos((float) (Mth.PI * u))) / 2.0, 5.0);
	}

	public static float smoothTransition(float time, float x, float y, float z, int t1, int t2) {
		// Handle out-of-range times if needed
		if (time <= 0) {
			return x;
		} else if (time >= t1 + t2) {
			return z;
		}

		// Phase 1: time in [0, t1], transition x -> y
		if (time <= t1) {
			double u = time / (double) t1; // goes from 0 to 1
			double s = smoothStep(u); // smooth interpolation factor
			return (float) (x + (y - x) * s); // smoothly interpolate from x to y
		}
		// Phase 2: time in [t1, t1 + t2], transition y -> z
		double u = (time - t1) / (double) t2; // goes from 0 to 1
		double s = smoothStep(u);
		return (float) (y + (z - y) * s); // smoothly interpolate from y to z
	}

	public ParticleBlastSmoke(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, ParticleOptionsBlastSmoke options, SpriteSet set) {
		super(level, x, y, z, 0.0, 0.0, 0.0);
		this.friction = 0.96F;
		this.gravity = options.gravity;
		this.speedUpWhenYMotionIsBlocked = true;
		this.burningTime = options.burningTime;
		this.sprites = set;
		this.xd = xSpeed;
		this.yd = ySpeed;
		this.zd = zSpeed;
		this.rCol = options.r;
		this.gCol = options.g;
		this.bCol = options.b;
		this.quadSize = options.scale;
		this.startQuadSize = options.scale;
		this.lifetime = options.lifetime;
		this.setSprite(sprites.get(level.random));
		this.hasPhysics = options.hasPhysics;
		this.burning = options.burning;
		this.friction = options.friction;

		// Randomize values so particles don't look the same. Could be done in
		// explosives, but this needs to be done for every explosive. Thus this saves
		// space...
		double brightnessRandom = 0.2 * level.random.nextDouble();
		burningTime *= (0.9 + brightnessRandom);
		startRed *= (0.8 + brightnessRandom);
		startGreen *= (0.8 + brightnessRandom);
		startBlue *= (0.8 + brightnessRandom);
		endRed *= (0.8 + brightnessRandom);
		endGreen *= (0.8 + brightnessRandom);
		endBlue *= (0.8 + brightnessRandom);
		rCol *= (0.4 + brightnessRandom);
		gCol *= (0.4 + brightnessRandom);
		bCol *= (0.4 + brightnessRandom);
		endGray *= (0.8 + brightnessRandom);
		gravity *= burning ? (0.75 + level.random.nextDouble() * 0.5) : 1.5 * level.random.nextDouble();
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	@Override
	public void tick() {
		this.xd *= friction;
		this.yd *= friction;
		this.zd *= friction;

		// Proceed with default ticking (position update, age increment, etc.)
		super.tick();

	}

	@Override
	public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
		float lifeProgress = (float) (this.age + partialTicks) / (float) this.lifetime;
		if (lifeProgress <= 1 && lifeProgress >= 0) {
			if (burning) {

				int orangeLast = (int) (burningTime / 1.4);

				this.rCol = smoothTransition(age + partialTicks, startRed, endRed, endGray, orangeLast, burningTime - orangeLast);
				this.gCol = smoothTransition(age + partialTicks, startGreen, endGreen, endGray, orangeLast, burningTime - orangeLast);
				this.bCol = smoothTransition(age + partialTicks, startBlue, endBlue, endGray, orangeLast, burningTime - orangeLast);
			}

			// Gradually shrink the particle
			this.quadSize = startQuadSize * Mth.cos((float) (Mth.PI / 2f * Math.pow((1 - lifeProgress) - 1, 5)));

		}
		super.render(buffer, renderInfo, partialTicks);
	}

	public static class Factory implements ParticleProvider<ParticleOptionsBlastSmoke>, ParticleEngine.SpriteParticleRegistration<ParticleOptionsBlastSmoke> {

		private final SpriteSet sprites;

		public Factory(SpriteSet sprites) {
			this.sprites = sprites;
		}

		@Override
		public Particle createParticle(ParticleOptionsBlastSmoke type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			return new ParticleBlastSmoke(level, x, y, z, xSpeed, ySpeed, zSpeed, type, sprites);
		}

		@Override
		public ParticleProvider<ParticleOptionsBlastSmoke> create(SpriteSet sprites) {
			return new ParticleBlastSmoke.Factory(sprites);
		}

	}
}
