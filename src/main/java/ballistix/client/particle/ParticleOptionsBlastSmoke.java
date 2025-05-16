package ballistix.client.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import ballistix.registers.BallistixParticles;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.registries.ForgeRegistries;

public class ParticleOptionsBlastSmoke extends ParticleType<ParticleOptionsBlastSmoke> implements IParticleData {

	public static final Codec<ParticleOptionsBlastSmoke> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			//
			Codec.FLOAT.fieldOf("r").forGetter(instance0 -> instance0.r),
			//
			Codec.FLOAT.fieldOf("g").forGetter(instance0 -> instance0.g),
			//
			Codec.FLOAT.fieldOf("b").forGetter(instance0 -> instance0.b),
			//
			Codec.FLOAT.fieldOf("scale").forGetter(instance0 -> instance0.scale),
			//
			Codec.FLOAT.fieldOf("gravity").forGetter(instance0 -> instance0.gravity),
			//
			Codec.INT.fieldOf("lifetime").forGetter(instance0 -> instance0.lifetime),
			//
			Codec.BOOL.fieldOf("physics").forGetter(instance0 -> instance0.hasPhysics),
			//
			Codec.BOOL.fieldOf("burning").forGetter(instance0 -> instance0.burning),
			//
			Codec.INT.fieldOf("burningTime").forGetter(instance0 -> instance0.burningTime),
			//
			Codec.DOUBLE.fieldOf("friction").forGetter(instance0 -> instance0.friction))
			//
			.apply(instance, (r, g, b, scale, gravity, lifetime, physics, burning, burningTime, friction) -> new ParticleOptionsBlastSmoke().setParameters(r, g, b, scale, gravity, lifetime, physics, burning, burningTime, friction)));

	public static final IParticleData.IDeserializer<ParticleOptionsBlastSmoke> DESERIALIZER = new IParticleData.IDeserializer<ParticleOptionsBlastSmoke>() {

		@Override
		public ParticleOptionsBlastSmoke fromCommand(ParticleType<ParticleOptionsBlastSmoke> type, StringReader reader) throws CommandSyntaxException {
			ParticleOptionsBlastSmoke particle = new ParticleOptionsBlastSmoke();
			
			reader.expect(' ');
			float r = reader.readFloat();

			reader.expect(' ');
			float g = reader.readFloat();

			reader.expect(' ');
			float b = reader.readFloat();

			reader.expect(' ');
			float scale = reader.readFloat();

			reader.expect(' ');
			float gravity = reader.readFloat();

			reader.expect(' ');
			int lifetime = reader.readInt();
			
			reader.expect(' ');
			boolean physics = reader.readBoolean();
			
			reader.expect(' ');
			boolean burning = reader.readBoolean();

			reader.expect(' ');
			int burningTime = reader.readInt();
			
			reader.expect(' ');
			double friction = reader.readDouble();

			return particle.setParameters(r, g, b, scale, gravity, lifetime, physics, burning, burningTime, friction);

		}

		@Override
		public ParticleOptionsBlastSmoke fromNetwork(ParticleType<ParticleOptionsBlastSmoke> type, PacketBuffer buffer) {
			return new ParticleOptionsBlastSmoke().setParameters(buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readInt(), buffer.readBoolean(), buffer.readBoolean(), buffer.readInt(), buffer.readDouble());
		}
	};
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
		super(false, DESERIALIZER);
	}

	public ParticleOptionsBlastSmoke setParameters(float r, float g, float b, float scale, float gravity, int lifetime, boolean physics, boolean burning, int burningTime, double friction) {
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

	public ParticleOptionsBlastSmoke setParameters(float r, float g, float b, float scale, float gravity, int lifetime, boolean physics, double friction) {
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
	public Codec<ParticleOptionsBlastSmoke> codec() {
		return CODEC;
	}

	@Override
	public void writeToNetwork(PacketBuffer buffer) {
		buffer.writeFloat(r);
		buffer.writeFloat(g);
		buffer.writeFloat(b);
		buffer.writeFloat(scale);
		buffer.writeFloat(gravity);
		buffer.writeInt(lifetime);
		buffer.writeBoolean(hasPhysics);
		buffer.writeBoolean(burning);
		buffer.writeInt(burningTime);
		buffer.writeDouble(friction);
	}

	@Override
	public String writeToString() {
		return ForgeRegistries.PARTICLE_TYPES.getKey(getType()).toString() + ", r: " + r + ", g: " + g + ", b: " + b + ", scale: " + scale + ", gravity: " + gravity + ", lifetime: " + lifetime +  ", physics: " + hasPhysics + ", burning: " + burning + ", burningTime: " + burningTime + ", friction: " + friction;
	}
}
