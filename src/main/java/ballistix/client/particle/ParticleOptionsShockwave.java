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

public class ParticleOptionsShockwave extends ParticleType<ParticleOptionsShockwave> implements ParticleOptions {

	public static final MapCodec<ParticleOptionsShockwave> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
			.group(Codec.FLOAT.fieldOf("r").forGetter(instance0 -> instance0.r),
					Codec.FLOAT.fieldOf("g").forGetter(instance0 -> instance0.g),
					Codec.FLOAT.fieldOf("b").forGetter(instance0 -> instance0.b),
					Codec.FLOAT.fieldOf("a").forGetter(instance0 -> instance0.b),
					Codec.FLOAT.fieldOf("scale").forGetter(instance0 -> instance0.scale),
					Codec.INT.fieldOf("lifetime").forGetter(instance0 -> instance0.lifetime),
					Codec.BOOL.fieldOf("physics").forGetter(instance0 -> instance0.hasPhysics),
					Codec.DOUBLE.fieldOf("friction").forGetter(instance0 -> instance0.friction))
			.apply(instance, (r, g, b, a, scale, lifetime, physics, friction) -> new ParticleOptionsShockwave()
					.setParameters(r, g, b, a, scale, lifetime, physics, friction)));

	public static final StreamCodec<RegistryFriendlyByteBuf, ParticleOptionsShockwave> STREAM_CODEC = CodecUtils
			.composite(ByteBufCodecs.FLOAT, instance0 -> instance0.r, ByteBufCodecs.FLOAT, instance0 -> instance0.g,
					ByteBufCodecs.FLOAT, instance0 -> instance0.b, ByteBufCodecs.FLOAT, instance0 -> instance0.a, ByteBufCodecs.FLOAT, instance0 -> instance0.scale,
					ByteBufCodecs.INT, instance0 -> instance0.lifetime, ByteBufCodecs.BOOL,
					instance0 -> instance0.hasPhysics, ByteBufCodecs.DOUBLE, instance0 -> instance0.friction,
					(r, g, b, a, scale, lifetime, physics, friction) -> new ParticleOptionsShockwave().setParameters(r, g,
							b, a, scale, lifetime, physics, friction));

	public float r;
	public float g;
	public float b;
	public float a;
	public float scale;
	public int lifetime;
	public boolean hasPhysics;
	public double friction;

	public ParticleOptionsShockwave() {
		super(false);
	}

	public ParticleOptionsShockwave setParameters(float r, float g, float b, float a, float scale, int lifetime, boolean physics,
			double friction) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
		this.scale = scale;
		this.lifetime = lifetime;
		this.hasPhysics = physics;
		this.friction = friction;
		return this;
	}

	@Override
	public ParticleType<?> getType() {
		return BallistixParticles.PARTICLE_SHOCKWAVE.get();
	}

	@Override
	public MapCodec<ParticleOptionsShockwave> codec() {
		return CODEC;
	}

	@Override
	public StreamCodec<? super RegistryFriendlyByteBuf, ParticleOptionsShockwave> streamCodec() {
		return STREAM_CODEC;
	}
}
