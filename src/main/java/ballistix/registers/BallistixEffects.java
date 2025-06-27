package ballistix.registers;

import ballistix.Ballistix;
import ballistix.common.effect.EffectVirus;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class BallistixEffects {

    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, Ballistix.ID);

    public static final RegistryObject<EffectVirus> VIRUS = EFFECTS.register("virus", EffectVirus::new);
}
