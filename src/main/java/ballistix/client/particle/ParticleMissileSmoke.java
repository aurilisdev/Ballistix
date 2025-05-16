package ballistix.client.particle;

import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager.IParticleMetaFactory;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.IAnimatedSprite;
import voltaic.Voltaic;

public class ParticleMissileSmoke extends SpriteTexturedParticle {
	private final IAnimatedSprite sprites;
	private boolean burning;
	private double friction = 0.95;
	private float startRed = 1.0F, startGreen = 0.7F, startBlue = 0.2F;
	private float endRed = 0.7F, endGreen = 0.1f, endBlue = 0.1f;
	private float endGray = 0.5F;
	private float startQuadSize;

	public ParticleMissileSmoke(ClientWorld level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, ParticleOptionsMissileSmoke options, IAnimatedSprite set) {
		super(level, x, y, z, 0.0, 0.0, 0.0);
		this.friction = 0.96F;
		this.gravity = 0;
		this.sprites = set;
		this.xd = xSpeed;
		this.yd = ySpeed;
		this.zd = zSpeed;
		this.rCol = options.r;
		this.gCol = options.g;
		this.bCol = options.b;
		this.quadSize = options.scale;
		this.startQuadSize = options.scale;
		this.lifetime = (int) (options.lifetime * (0.8 + Voltaic.RANDOM.nextDouble() * 0.2));
		this.setSpriteFromAge(sprites);
		this.hasPhysics = options.hasPhysics;

		// Randomize values so particles don't look the same. Could be done in
		// explosives, but this needs to be done for every explosive. Thus this saves
		// space...
		double brightnessRandom = 0.2 * Voltaic.RANDOM.nextDouble();
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
		setColor(startRed, startGreen, startBlue);
		gravity *= burning ? (0.75 + Voltaic.RANDOM.nextDouble() * 0.5) : 1.5 * Voltaic.RANDOM.nextDouble();
	}

	@Override
	public IParticleRenderType getRenderType() {
		return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	@Override
	public void tick() {
		this.xd *= friction;
		this.yd *= friction;
		this.zd *= friction;

		// Proceed with default ticking (position update, age increment, etc.)
		super.tick();

		// Update sprite by age, if animated
		this.setSpriteFromAge(sprites);
	}

	@Override
	public void render(IVertexBuilder buffer, ActiveRenderInfo renderInfo, float partialTicks) {
		super.render(buffer, renderInfo, partialTicks);
		float lifeProgress = (float) (this.age + partialTicks) / (float) this.lifetime;
		if (lifeProgress <= 1 && lifeProgress >= 0) {
			int burningTime = lifetime / 3;
			int orangeTime = (int) (burningTime / 1.3);
			this.rCol = ParticleBlastSmoke.smoothTransition(age + partialTicks, startRed, endRed, endGray, orangeTime, burningTime - orangeTime);
			this.gCol = ParticleBlastSmoke.smoothTransition(age + partialTicks, startGreen, endGreen, endGray, orangeTime, burningTime - orangeTime);
			this.bCol = ParticleBlastSmoke.smoothTransition(age + partialTicks, startBlue, endBlue, endGray, orangeTime, burningTime - orangeTime);

			// Gradually shrink the particle
			this.quadSize = startQuadSize * MathHelper.cos((float) (Math.PI / 2f * Math.pow((1 - lifeProgress) - 1, 5)));
		}
	}

	public static class Factory implements IParticleFactory<ParticleOptionsMissileSmoke>, IParticleMetaFactory<ParticleOptionsMissileSmoke> {

		private final IAnimatedSprite sprites;

		public Factory(IAnimatedSprite sprites) {
			this.sprites = sprites;
		}

		@Override
		public Particle createParticle(ParticleOptionsMissileSmoke type, ClientWorld level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			return new ParticleMissileSmoke(level, x, y, z, xSpeed, ySpeed, zSpeed, type, sprites);
		}

		@Override
		public IParticleFactory<ParticleOptionsMissileSmoke> create(IAnimatedSprite sprites) {
			return new ParticleMissileSmoke.Factory(sprites);
		}

	}
}
