package ballistix.client.particle;

import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager.IParticleMetaFactory;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;


public class ParticleShockwave extends SpriteTexturedParticle {
	
	private final IAnimatedSprite sprites;
	private double friction = 0.95;
	private float startQuadSize;
	private float startAlpha;

	public ParticleShockwave(ClientWorld level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, ParticleOptionsShockwave type, IAnimatedSprite set) {
		super(level, x, y, z, 0.0, 0.0, 0.0);
		this.friction = 0.96F;
		this.sprites = set;
		this.xd = xSpeed;
		this.yd = ySpeed;
		this.zd = zSpeed;
		this.rCol = type.r;
		this.gCol = type.g;
		this.bCol = type.b;
		this.alpha = 0;
		this.startAlpha = type.a;
		this.quadSize = type.scale;
		this.startQuadSize = type.scale;
		this.lifetime = type.lifetime;
		this.setSpriteFromAge(sprites);
		this.hasPhysics = type.hasPhysics;
		this.friction = type.friction;

		// Randomize values so particles don't look the same. Could be done in
		// explosives, but this needs to be done for every explosive. Thus this saves
		// space...
		double brightnessRandom = 0.4 * level.random.nextDouble();
		rCol *= (0.6 + brightnessRandom);
		gCol *= (0.6 + brightnessRandom);
		bCol *= (0.6 + brightnessRandom);
	}

	@Override
	public IParticleRenderType getRenderType() {
		return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public void tick() {
		this.xd *= friction;
		this.yd *= friction;
		this.zd *= friction;
		super.tick();
	}

	@Override
	public void render(IVertexBuilder buffer, ActiveRenderInfo renderInfo, float partialTicks) {
		super.render(buffer, renderInfo, partialTicks);

		float lifeProgress = (float) (this.age + partialTicks) / (float) this.lifetime;
		if (lifeProgress <= 1 && lifeProgress >= 0) {
			// Gradually shrink and expand the particle
			this.quadSize = startQuadSize * MathHelper.cos((float) (Math.PI * 2 * Math.pow((lifeProgress - 0.5), 2)));

			this.alpha = startAlpha * MathHelper.cos((float) (Math.PI * 2 * Math.pow((lifeProgress - 0.5), 2)));
		}
	}

	public static class Factory implements IParticleFactory<ParticleOptionsShockwave>, IParticleMetaFactory<ParticleOptionsShockwave> {

		private final IAnimatedSprite sprites;

		public Factory(IAnimatedSprite sprites) {
			this.sprites = sprites;
		}

		@Override
		public Particle createParticle(ParticleOptionsShockwave type, ClientWorld level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			return new ParticleShockwave(level, x, y, z, xSpeed, ySpeed, zSpeed, type, sprites);
		}

		@Override
		public IParticleFactory<ParticleOptionsShockwave> create(IAnimatedSprite sprites) {
			return new ParticleShockwave.Factory(sprites);
		}

	}
}
