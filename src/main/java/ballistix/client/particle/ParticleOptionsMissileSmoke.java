package ballistix.client.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import ballistix.registers.BallistixParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.registries.ForgeRegistries;

public class ParticleOptionsMissileSmoke extends ParticleType<ParticleOptionsMissileSmoke> implements ParticleOptions {

	public static final Codec<ParticleOptionsMissileSmoke> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			//
			Codec.FLOAT.fieldOf("r").forGetter(instance0 -> instance0.r),
			//
			Codec.FLOAT.fieldOf("g").forGetter(instance0 -> instance0.g),
			//
			Codec.FLOAT.fieldOf("b").forGetter(instance0 -> instance0.b),
			//
			Codec.FLOAT.fieldOf("scale").forGetter(instance0 -> instance0.scale),
			//
			Codec.INT.fieldOf("lifetime").forGetter(instance0 -> instance0.lifetime),
			//
			Codec.BOOL.fieldOf("physics").forGetter(instance0 -> instance0.hasPhysics))
			//
			.apply(instance, (r, g, b, scale, lifetime, physics) -> new ParticleOptionsMissileSmoke().setParameters(r, g, b, scale, lifetime, physics)));

	public static final ParticleOptions.Deserializer<ParticleOptionsMissileSmoke> DESERIALIZER = new ParticleOptions.Deserializer<>() {

		@Override
		public ParticleOptionsMissileSmoke fromCommand(ParticleType<ParticleOptionsMissileSmoke> type, StringReader reader) throws CommandSyntaxException {
			ParticleOptionsMissileSmoke particle = new ParticleOptionsMissileSmoke();

			reader.expect(' ');
			float r = reader.readFloat();

			reader.expect(' ');
			float g = reader.readFloat();

			reader.expect(' ');
			float b = reader.readFloat();

			reader.expect(' ');
			float scale = reader.readFloat();

			reader.expect(' ');
			int lifetime = reader.readInt();

			reader.expect(' ');
			boolean physics = reader.readBoolean();

			return particle.setParameters(r, g, b, scale, lifetime, physics);

		}

		@Override
		public ParticleOptionsMissileSmoke fromNetwork(ParticleType<ParticleOptionsMissileSmoke> type, FriendlyByteBuf buffer) {
			return new ParticleOptionsMissileSmoke().setParameters(buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readInt(), buffer.readBoolean());
		}
	};

	public float r;
	public float g;
	public float b;
	public float scale;
	public int lifetime = 1;
	public boolean hasPhysics;

	public ParticleOptionsMissileSmoke() {
		super(false, DESERIALIZER);
	}

	public ParticleOptionsMissileSmoke setParameters(float r, float g, float b, float scale, int lifetime, boolean physics) {
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
	public Codec<ParticleOptionsMissileSmoke> codec() {
		return CODEC;
	}

	@Override
	public void writeToNetwork(FriendlyByteBuf buffer) {
		buffer.writeFloat(r);
		buffer.writeFloat(g);
		buffer.writeFloat(b);
		buffer.writeFloat(scale);
		buffer.writeInt(lifetime);
		buffer.writeBoolean(hasPhysics);
	}

	@Override
	public String writeToString() {
		return ForgeRegistries.PARTICLE_TYPES.getKey(getType()).toString() + ", r: " + r + ", g: " + g + ", b: " + b + ", scale: " + scale + ", lifetime: " + lifetime + ", physics: " + hasPhysics;
	}
}
