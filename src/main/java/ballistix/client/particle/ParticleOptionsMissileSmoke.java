package ballistix.client.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import ballistix.registers.BallistixParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class ParticleOptionsMissileSmoke extends ParticleType<ParticleOptionsMissileSmoke> implements ParticleOptions {

    public static final MapCodec<ParticleOptionsMissileSmoke> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
	    .group(Codec.FLOAT.fieldOf("r").forGetter(instance0 -> instance0.r),
		    Codec.FLOAT.fieldOf("g").forGetter(instance0 -> instance0.g),
		    Codec.FLOAT.fieldOf("b").forGetter(instance0 -> instance0.b),
		    Codec.FLOAT.fieldOf("scale").forGetter(instance0 -> instance0.scale),
		    Codec.INT.fieldOf("lifetime").forGetter(instance0 -> instance0.lifetime),
		    Codec.BOOL.fieldOf("physics").forGetter(instance0 -> instance0.hasPhysics))
	    .apply(instance, (r, g, b, scale, lifetime, physics) -> new ParticleOptionsMissileSmoke().setParameters(r,
		    g, b, scale, lifetime, physics)));
    public static final StreamCodec<RegistryFriendlyByteBuf, ParticleOptionsMissileSmoke> STREAM_CODEC = StreamCodec
	    .composite(ByteBufCodecs.FLOAT, instance0 -> instance0.r, ByteBufCodecs.FLOAT, instance0 -> instance0.g,
		    ByteBufCodecs.FLOAT, instance0 -> instance0.b, ByteBufCodecs.FLOAT, instance0 -> instance0.scale,
		    ByteBufCodecs.INT, instance0 -> instance0.lifetime, ByteBufCodecs.BOOL,
		    instance0 -> instance0.hasPhysics,
		    (r, g, b, scale, lifetime, physics) -> new ParticleOptionsMissileSmoke().setParameters(r, g, b,
			    scale, lifetime, physics));

    public float r;
    public float g;
    public float b;
    public float scale;
    public int lifetime = 1;
    public boolean hasPhysics;

    public ParticleOptionsMissileSmoke() {
	super(false);
    }

    public ParticleOptionsMissileSmoke setParameters(float r, float g, float b, float scale, int lifetime,
	    boolean physics) {
	this.r = r;
	this.g = g;
	this.b = b;
	this.scale = scale;
	this.lifetime = lifetime;
	this.hasPhysics = physics;
	return this;
    }

    @Override
    public ParticleType<?> getType() {
	return BallistixParticles.PARTICLE_MISSILE_SMOKE.get();
    }

    @Override
    public MapCodec<ParticleOptionsMissileSmoke> codec() {
	return CODEC;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, ParticleOptionsMissileSmoke> streamCodec() {
	return STREAM_CODEC;
    }
}
