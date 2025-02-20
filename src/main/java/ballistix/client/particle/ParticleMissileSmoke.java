package ballistix.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.Mth;

public class ParticleMissileSmoke extends TextureSheetParticle {
    private final SpriteSet sprites;
    private boolean burning;
    private double friction = 0.95;
    private float startRed = 1.0F, startGreen = 0.7F, startBlue = 0.2F;
    private float endRed = 0.7F, endGreen = 0.1f, endBlue = 0.1f;
    private float endGray = 0.5F;
    private float startQuadSize;

    public ParticleMissileSmoke(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed,
	    double zSpeed, ParticleOptionMissileSmoke options, SpriteSet set) {
	super(level, x, y, z, 0.0, 0.0, 0.0);
	this.friction = 0.96F;
	this.gravity = 0;
	this.speedUpWhenYMotionIsBlocked = true;
	this.sprites = set;
	this.xd = xSpeed;
	this.yd = ySpeed;
	this.zd = zSpeed;
	this.rCol = options.r;
	this.gCol = options.g;
	this.bCol = options.b;
	this.quadSize = options.scale;
	this.startQuadSize = options.scale;
	this.lifetime = (int) (options.lifetime * (0.8 + level.random.nextDouble() * 0.2));
	this.setSpriteFromAge(sprites);
	this.hasPhysics = options.hasPhysics;

	double brightnessRandom = 0.2 * level.random.nextDouble();
	startRed *= (0.8 + brightnessRandom);
	startGreen *= (0.8 + brightnessRandom);
	startBlue *= (0.8 + brightnessRandom);
	endRed *= (0.8 + brightnessRandom);
	endGreen *= (0.8 + brightnessRandom);
	endBlue *= (0.8 + brightnessRandom);
	rCol *= (0.4 + brightnessRandom);
	gCol *= (0.4 + brightnessRandom);
	bCol *= (0.4 + brightnessRandom);
	setColor(startRed, startGreen, startBlue);
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
	float lifeProgress = (float) this.age / (float) this.lifetime;
	
	int burningTime = lifetime / 3;
	int orangeTime = (int) (burningTime / 1.3);
	this.rCol = ParticleBlastSmoke.smoothTransition(age, startRed, endRed, endGray, orangeTime, burningTime - orangeTime);
	this.gCol = ParticleBlastSmoke.smoothTransition(age, startGreen, endGreen, endGray, orangeTime, burningTime - orangeTime);
	this.bCol = ParticleBlastSmoke.smoothTransition(age, startBlue, endBlue, endGray, orangeTime, burningTime - orangeTime);

	// Gradually shrink the particle
	this.quadSize = startQuadSize * Mth.cos((float) (Mth.PI / 2f * Math.pow((1 - lifeProgress) - 1, 5)));

	// Proceed with default ticking (position update, age increment, etc.)
	super.tick();

	// Update sprite by age, if animated
	this.setSpriteFromAge(sprites);
    }

    public static class Factory implements ParticleProvider<ParticleOptionMissileSmoke>,
	    ParticleEngine.SpriteParticleRegistration<ParticleOptionMissileSmoke> {

	private final SpriteSet sprites;

	public Factory(SpriteSet sprites) {
	    this.sprites = sprites;
	}

	@Override
	public Particle createParticle(ParticleOptionMissileSmoke type, ClientLevel level, double x, double y, double z,
		double xSpeed, double ySpeed, double zSpeed) {
	    return new ParticleMissileSmoke(level, x, y, z, xSpeed, ySpeed, zSpeed, type, sprites);
	}

	@Override
	public ParticleProvider<ParticleOptionMissileSmoke> create(SpriteSet sprites) {
	    return new ParticleMissileSmoke.Factory(sprites);
	}

    }
}
