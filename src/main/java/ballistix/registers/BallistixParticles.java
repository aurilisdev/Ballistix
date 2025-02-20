package ballistix.registers;

import ballistix.References;
import ballistix.client.particle.ParticleOptionBlastSmoke;
import ballistix.client.particle.ParticleOptionMissileSmoke;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BallistixParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister
	    .create(Registries.PARTICLE_TYPE, References.ID);

    public static final DeferredHolder<ParticleType<?>, ParticleOptionBlastSmoke> PARTICLE_BLAST_SMOKE = PARTICLES
	    .register("custom_blast_smoke", ParticleOptionBlastSmoke::new); // 'true' = alwaysShow
    public static final DeferredHolder<ParticleType<?>, ParticleOptionMissileSmoke> PARTICLE_MISSILE_SMOKE = PARTICLES
	    .register("custom_missile_smoke", ParticleOptionMissileSmoke::new); // 'true' = alwaysShow
}
