package ballistix.registers;

import ballistix.References;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;

public class BallistixDamageTypes {

	public static final ResourceKey<DamageType> CHEMICAL_GAS = create("chemicalgas");
	public static final ResourceKey<DamageType> SHRAPNEL = create("shrapnel");

	public static ResourceKey<DamageType> create(String name) {
		return ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(References.ID, name));
	}

	public static void registerTypes(BootstapContext<DamageType> context) {
		context.register(CHEMICAL_GAS, new DamageType("electricity", DamageScaling.NEVER, 0.1F, DamageEffects.HURT));
		context.register(SHRAPNEL, new DamageType("radiation", DamageScaling.NEVER, 0, DamageEffects.HURT));
	}

}
