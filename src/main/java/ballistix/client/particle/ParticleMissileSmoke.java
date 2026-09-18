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
import voltaic.Voltaic;

public class ParticleMissileSmoke extends TextureSheetParticle {
    private final SpriteSet sprites;
    private boolean burning;
    private double friction = 0.95;
    private float startRed = 1.0F, startGreen = 0.7F, startBlue = 0.2F;
    private float endRed = 0.7F, endGreen = 0.1f, endBlue = 0.1f;
    private float endGray = 0.5F;
    private final float startQuadSize;

    public ParticleMissileSmoke(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed,
	    double zSpeed, ParticleOptionsMissileSmoke options, SpriteSet set) {
	super(level, x, y, z, 0.0, 0.0, 0.0);
	friction = 0.96F;
	gravity = 0;
	speedUpWhenYMotionIsBlocked = true;
	sprites = set;
	xd = xSpeed;
	yd = ySpeed;
	zd = zSpeed;
	rCol = options.r;
	gCol = options.g;
	bCol = options.b;
	quadSize = options.scale;
	startQuadSize = options.scale;
	lifetime = (int) (options.lifetime * (0.8 + Voltaic.RANDOM.nextDouble() * 0.2));
	setSpriteFromAge(sprites);
	hasPhysics = options.hasPhysics;

	// Randomize values so particles don't look the same. Could be done in
	// explosives, but this needs to be done for every explosive. Thus this saves
	// space...
	double brightnessRandom = 0.2 * Voltaic.RANDOM.nextDouble();
	startRed *= 0.8 + brightnessRandom;
	startGreen *= 0.8 + brightnessRandom;
	startBlue *= 0.8 + brightnessRandom;
	endRed *= 0.8 + brightnessRandom;
	endGreen *= 0.8 + brightnessRandom;
	endBlue *= 0.8 + brightnessRandom;
	rCol *= 0.4 + brightnessRandom;
	gCol *= 0.4 + brightnessRandom;
	bCol *= 0.4 + brightnessRandom;
	endGray *= 0.8 + brightnessRandom;
	setColor(startRed, startGreen, startBlue);
	gravity *= burning ? 0.75 + Voltaic.RANDOM.nextDouble() * 0.5 : 1.5 * Voltaic.RANDOM.nextDouble();
    }

    @Override
    public ParticleRenderType getRenderType() {
	return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void tick() {
	xd *= friction;
	yd *= friction;
	zd *= friction;

	// Proceed with default ticking (position update, age increment, etc.)
	super.tick();

	// Update sprite by age, if animated
	setSpriteFromAge(sprites);
    }

    @Override
    public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
	super.render(buffer, renderInfo, partialTicks);
	float lifeProgress = (age + partialTicks) / lifetime;
	if (lifeProgress <= 1 && lifeProgress >= 0) {
	    int burningTime = lifetime / 3;
	    int orangeTime = (int) (burningTime / 1.3);
	    rCol = ParticleBlastSmoke.smoothTransition(age + partialTicks, startRed, endRed, endGray, orangeTime,
		    burningTime - orangeTime);
	    gCol = ParticleBlastSmoke.smoothTransition(age + partialTicks, startGreen, endGreen, endGray, orangeTime,
		    burningTime - orangeTime);
	    bCol = ParticleBlastSmoke.smoothTransition(age + partialTicks, startBlue, endBlue, endGray, orangeTime,
		    burningTime - orangeTime);

	    // Gradually shrink the particle
	    quadSize = startQuadSize * Mth.cos((float) (Mth.PI / 2f * Math.pow(1 - lifeProgress - 1, 5)));
	}
    }

    public static class Factory implements ParticleProvider<ParticleOptionsMissileSmoke>,
	    ParticleEngine.SpriteParticleRegistration<ParticleOptionsMissileSmoke> {

	private final SpriteSet sprites;

	public Factory(SpriteSet sprites) {
	    this.sprites = sprites;
	}

	@Override
	public Particle createParticle(ParticleOptionsMissileSmoke type, ClientLevel level, double x, double y,
		double z, double xSpeed, double ySpeed, double zSpeed) {
	    return new ParticleMissileSmoke(level, x, y, z, xSpeed, ySpeed, zSpeed, type, sprites);
	}

	@Override
	public ParticleProvider<ParticleOptionsMissileSmoke> create(SpriteSet sprites) {
	    return new ParticleMissileSmoke.Factory(sprites);
	}

    }
}
