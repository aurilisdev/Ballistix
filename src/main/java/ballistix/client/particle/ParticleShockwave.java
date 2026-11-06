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
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.util.Mth;

public class ParticleShockwave extends TextureSheetParticle {
    private final SpriteSet sprites;
    private double friction = 0.95;
    private final float startQuadSize;
    private final float startAlpha;

    public ParticleShockwave(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed,
	    double zSpeed, ParticleOptionsShockwave type, SpriteSet set) {
	super(level, x, y, z, 0.0, 0.0, 0.0);
	friction = 0.96F;
	speedUpWhenYMotionIsBlocked = true;
	sprites = set;
	xd = xSpeed;
	yd = ySpeed;
	zd = zSpeed;
	rCol = type.r;
	gCol = type.g;
	bCol = type.b;
	alpha = 0;
	startAlpha = type.a;
	quadSize = type.scale;
	startQuadSize = type.scale;
	lifetime = type.lifetime;
	setSpriteFromAge(sprites);
	hasPhysics = type.hasPhysics;
	friction = type.friction;

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
    protected int getLightColor(float partialTicks) {
	return LightTexture.FULL_BRIGHT;
    }

    @Override
    public void tick() {
	xd *= friction;
	yd *= friction;
	zd *= friction;
	super.tick();
    }

    @Override
    public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
	float lifeProgress = Mth.clamp((age + partialTicks) / lifetime, 0f, 1f);
	quadSize = startQuadSize * (0.65f + lifeProgress * 0.65f);
	alpha = startAlpha * (1f - lifeProgress) * (1f - lifeProgress);
	super.render(buffer, renderInfo, partialTicks);
    }

    public static class Factory implements ParticleProvider<ParticleOptionsShockwave>,
	    ParticleEngine.SpriteParticleRegistration<ParticleOptionsShockwave> {

	private final SpriteSet sprites;

	public Factory(SpriteSet sprites) {
	    this.sprites = sprites;
	}

	@Override
	public Particle createParticle(ParticleOptionsShockwave type, ClientLevel level, double x, double y, double z,
		double xSpeed, double ySpeed, double zSpeed) {
	    return new ParticleShockwave(level, x, y, z, xSpeed, ySpeed, zSpeed, type, sprites);
	}

	@Override
	public ParticleProvider<ParticleOptionsShockwave> create(SpriteSet sprites) {
	    return new ParticleShockwave.Factory(sprites);
	}

    }
}
