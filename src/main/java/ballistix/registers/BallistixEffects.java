package ballistix.registers;

import ballistix.Ballistix;
import ballistix.common.effect.EffectVirus;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BallistixEffects {

    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, Ballistix.ID);

    public static final DeferredHolder<MobEffect, EffectVirus> VIRUS = EFFECTS.register("virus", EffectVirus::new);
}
