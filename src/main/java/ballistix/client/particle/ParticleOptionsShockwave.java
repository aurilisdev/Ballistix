package ballistix.client.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import ballistix.registers.BallistixParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.registries.ForgeRegistries;

public class ParticleOptionsShockwave extends ParticleType<ParticleOptionsShockwave> implements ParticleOptions {

    public static final Codec<ParticleOptionsShockwave> CODEC = RecordCodecBuilder.create(instance -> instance.group(
	    //
	    Codec.FLOAT.fieldOf("r").forGetter(instance0 -> instance0.r),
	    //
	    Codec.FLOAT.fieldOf("g").forGetter(instance0 -> instance0.g),
	    //
	    Codec.FLOAT.fieldOf("b").forGetter(instance0 -> instance0.b),
	    //
	    Codec.FLOAT.fieldOf("a").forGetter(instance0 -> instance0.b),
	    //
	    Codec.FLOAT.fieldOf("scale").forGetter(instance0 -> instance0.scale),
	    //
	    Codec.INT.fieldOf("lifetime").forGetter(instance0 -> instance0.lifetime),
	    //
	    Codec.BOOL.fieldOf("physics").forGetter(instance0 -> instance0.hasPhysics),
	    //
	    Codec.DOUBLE.fieldOf("friction").forGetter(instance0 -> instance0.friction))
	    //
	    .apply(instance, (r, g, b, a, scale, lifetime, physics, friction) -> new ParticleOptionsShockwave()
		    .setParameters(r, g, b, a, scale, lifetime, physics, friction)));

    public static final ParticleOptions.Deserializer<ParticleOptionsShockwave> DESERIALIZER = new ParticleOptions.Deserializer<>() {

	@Override
	public ParticleOptionsShockwave fromCommand(ParticleType<ParticleOptionsShockwave> type, StringReader reader)
		throws CommandSyntaxException {
	    ParticleOptionsShockwave particle = new ParticleOptionsShockwave();

	    reader.expect(' ');
	    float r = reader.readFloat();

	    reader.expect(' ');
	    float g = reader.readFloat();

	    reader.expect(' ');
	    float b = reader.readFloat();

	    reader.expect(' ');
	    float a = reader.readFloat();

	    reader.expect(' ');
	    float scale = reader.readFloat();

	    reader.expect(' ');
	    int lifetime = reader.readInt();

	    reader.expect(' ');
	    boolean physics = reader.readBoolean();

	    reader.expect(' ');
	    double friction = reader.readDouble();

	    return particle.setParameters(r, g, b, a, scale, lifetime, physics, friction);

	}

	@Override
	public ParticleOptionsShockwave fromNetwork(ParticleType<ParticleOptionsShockwave> type,
		FriendlyByteBuf buffer) {
	    return new ParticleOptionsShockwave().setParameters(buffer.readFloat(), buffer.readFloat(),
		    buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readInt(), buffer.readBoolean(),
		    buffer.readDouble());
	}
    };
    public float r;
    public float g;
    public float b;
    public float a;
    public float scale;
    public int lifetime;
    public boolean hasPhysics;
    public double friction;

    public ParticleOptionsShockwave() {
	super(false, DESERIALIZER);
    }

    public ParticleOptionsShockwave setParameters(float r, float g, float b, float a, float scale, int lifetime,
	    boolean physics, double friction) {
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
    public Codec<ParticleOptionsShockwave> codec() {
	return CODEC;
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buffer) {
	buffer.writeFloat(r);
	buffer.writeFloat(g);
	buffer.writeFloat(b);
	buffer.writeFloat(a);
	buffer.writeFloat(scale);
	buffer.writeInt(lifetime);
	buffer.writeBoolean(hasPhysics);
	buffer.writeDouble(friction);
    }

    @Override
    public String writeToString() {
	return ForgeRegistries.PARTICLE_TYPES.getKey(getType()).toString() + ", r: " + r + ", g: " + g + ", b: " + b
		+ ", a: " + a + ", scale: " + scale + ", lifetime: " + lifetime + ", physics: " + hasPhysics
		+ ", friction: " + friction;
    }
}
