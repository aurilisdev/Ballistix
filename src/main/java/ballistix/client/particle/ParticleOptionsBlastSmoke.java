package ballistix.client.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import ballistix.registers.BallistixParticles;
import electrodynamics.prefab.utilities.CodecUtils;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class ParticleOptionsBlastSmoke extends ParticleType<ParticleOptionsBlastSmoke> implements ParticleOptions {

    public static final MapCodec<ParticleOptionsBlastSmoke> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
	    .group(Codec.FLOAT.fieldOf("r").forGetter(instance0 -> instance0.r),
		    Codec.FLOAT.fieldOf("g").forGetter(instance0 -> instance0.g),
		    Codec.FLOAT.fieldOf("b").forGetter(instance0 -> instance0.b),
		    Codec.FLOAT.fieldOf("scale").forGetter(instance0 -> instance0.scale),
		    Codec.FLOAT.fieldOf("gravity").forGetter(instance0 -> instance0.gravity),
		    Codec.INT.fieldOf("lifetime").forGetter(instance0 -> instance0.lifetime),
		    Codec.BOOL.fieldOf("physics").forGetter(instance0 -> instance0.hasPhysics),
		    Codec.BOOL.fieldOf("burning").forGetter(instance0 -> instance0.burning),
		    Codec.INT.fieldOf("burningTime").forGetter(instance0 -> instance0.burningTime),
		    Codec.DOUBLE.fieldOf("friction").forGetter(instance0 -> instance0.friction))
	    .apply(instance,
		    (r, g, b, scale, gravity, lifetime, physics, burning, burningTime,
			    friction) -> new ParticleOptionsBlastSmoke().setParameters(r, g, b, scale, gravity, lifetime,
				    physics, burning, burningTime, friction)));

    public static final StreamCodec<RegistryFriendlyByteBuf, ParticleOptionsBlastSmoke> STREAM_CODEC = CodecUtils
	    .composite(ByteBufCodecs.FLOAT, instance0 -> instance0.r, ByteBufCodecs.FLOAT, instance0 -> instance0.g,
		    ByteBufCodecs.FLOAT, instance0 -> instance0.b, ByteBufCodecs.FLOAT, instance0 -> instance0.scale,
		    ByteBufCodecs.FLOAT, instance0 -> instance0.gravity, ByteBufCodecs.INT,
		    instance0 -> instance0.lifetime, ByteBufCodecs.BOOL, instance0 -> instance0.hasPhysics,
		    ByteBufCodecs.BOOL, instance0 -> instance0.burning, ByteBufCodecs.INT,
		    instance0 -> instance0.burningTime, ByteBufCodecs.DOUBLE, instance0 -> instance0.friction,
		    (r, g, b, scale, gravity, lifetime, physics, burning, burningTime,
			    friction) -> new ParticleOptionsBlastSmoke().setParameters(r, g, b, scale, gravity, lifetime,
				    physics, burning, burningTime, friction));

    public float r;
    public float g;
    public float b;
    public float scale;
    public float gravity;
    public int lifetime;
    public int burningTime;
    public boolean hasPhysics;
    public boolean burning;
    public double friction;

    public ParticleOptionsBlastSmoke() {
	super(false);
    }

    public ParticleOptionsBlastSmoke setParameters(float r, float g, float b, float scale, float gravity, int lifetime,
	    boolean physics, boolean burning, int burningTime, double friction) {
	this.r = r;
	this.g = g;
	this.b = b;
	this.scale = scale;
	this.gravity = gravity;
	this.lifetime = lifetime;
	this.hasPhysics = physics;
	this.burning = burning;
	this.friction = friction;
	this.burningTime = burningTime;
	return this;
    }

    public ParticleOptionsBlastSmoke setParameters(float r, float g, float b, float scale, float gravity, int lifetime,
	    boolean physics, double friction) {
	this.r = r;
	this.g = g;
	this.b = b;
	this.scale = scale;
	this.gravity = gravity;
	this.lifetime = lifetime;
	this.hasPhysics = physics;
	this.friction = friction;
	return this;
    }

    @Override
    public ParticleType<?> getType() {
	return BallistixParticles.PARTICLE_BLAST_SMOKE.get();
    }

    @Override
    public MapCodec<ParticleOptionsBlastSmoke> codec() {
	return CODEC;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, ParticleOptionsBlastSmoke> streamCodec() {
	return STREAM_CODEC;
    }
}
