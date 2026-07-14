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

public class ParticleShockwave extends TextureSheetParticle {
	private final SpriteSet sprites;
	private double friction = 0.95;
	private float startQuadSize;
	private float startAlpha;

	public ParticleShockwave(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, ParticleOptionsShockwave type, SpriteSet set) {
		super(level, x, y, z, 0.0, 0.0, 0.0);
		this.friction = 0.96F;
		this.speedUpWhenYMotionIsBlocked = true;
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
		rCol *= 0.6 + brightnessRandom;
		gCol *= 0.6 + brightnessRandom;
		bCol *= 0.6 + brightnessRandom;
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public void tick() {
		this.xd *= friction;
		this.yd *= friction;
		this.zd *= friction;
		super.tick();
	}

	@Override
	public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
		super.render(buffer, renderInfo, partialTicks);

		float lifeProgress = (this.age + partialTicks) / this.lifetime;
		if (lifeProgress <= 1 && lifeProgress >= 0) {
			// Gradually shrink and expand the particle
			this.quadSize = startQuadSize * Mth.cos((float) (Mth.PI * 2 * Math.pow(lifeProgress - 0.5, 2)));

			this.alpha = startAlpha * Mth.cos((float) (Mth.PI * 2 * Math.pow(lifeProgress - 0.5, 2)));
		}
	}

	public static class Factory implements ParticleProvider<ParticleOptionsShockwave>, ParticleEngine.SpriteParticleRegistration<ParticleOptionsShockwave> {

		private final SpriteSet sprites;

		public Factory(SpriteSet sprites) {
			this.sprites = sprites;
		}

		@Override
		public Particle createParticle(ParticleOptionsShockwave type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			return new ParticleShockwave(level, x, y, z, xSpeed, ySpeed, zSpeed, type, sprites);
		}

		@Override
		public ParticleProvider<ParticleOptionsShockwave> create(SpriteSet sprites) {
			return new ParticleShockwave.Factory(sprites);
		}

	}
}
