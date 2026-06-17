package ballistix.registers;

import ballistix.Ballistix;
import ballistix.client.particle.ParticleOptionsBlastSmoke;
import ballistix.client.particle.ParticleOptionsMissileSmoke;
import ballistix.client.particle.ParticleOptionsShockwave;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BallistixParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister
	    .create(ForgeRegistries.PARTICLE_TYPES, Ballistix.ID);

    public static final RegistryObject<ParticleOptionsBlastSmoke> PARTICLE_BLAST_SMOKE = PARTICLES
	    .register("blastsmoke", ParticleOptionsBlastSmoke::new);
    public static final RegistryObject<ParticleOptionsMissileSmoke> PARTICLE_MISSILE_SMOKE = PARTICLES
	    .register("missilesmoke", ParticleOptionsMissileSmoke::new);
    public static final RegistryObject<ParticleOptionsShockwave> PARTICLE_SHOCKWAVE = PARTICLES.register("shockwave",
	    ParticleOptionsShockwave::new);
}
