package ballistix.registers;

import ballistix.Ballistix;
import ballistix.common.effect.EffectVirus;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BallistixEffects {

    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Ballistix.ID);

    public static final RegistryObject<EffectVirus> VIRUS = EFFECTS.register("virus", EffectVirus::new);
}
