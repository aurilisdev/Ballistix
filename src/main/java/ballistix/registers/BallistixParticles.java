package ballistix.registers;

import ballistix.Ballistix;
import ballistix.client.particle.ParticleOptionsBlastSmoke;
import ballistix.client.particle.ParticleOptionsMissileSmoke;
import ballistix.client.particle.ParticleOptionsShockwave;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BallistixParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, Ballistix.ID);

    public static final DeferredHolder<ParticleType<?>, ParticleOptionsBlastSmoke> PARTICLE_BLAST_SMOKE = PARTICLES.register("blastsmoke", ParticleOptionsBlastSmoke::new);
    public static final DeferredHolder<ParticleType<?>, ParticleOptionsMissileSmoke> PARTICLE_MISSILE_SMOKE = PARTICLES.register("missilesmoke", ParticleOptionsMissileSmoke::new);
    public static final DeferredHolder<ParticleType<?>, ParticleOptionsShockwave> PARTICLE_SHOCKWAVE = PARTICLES.register("shockwave", ParticleOptionsShockwave::new);
}
