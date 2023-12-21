package ballistix.api.damage;

import net.minecraft.world.damagesource.DamageSource;

public class DamageSourceChemicalGas {
	public static DamageSource INSTANCE = new DamageSource("chemicalgas").bypassArmor().bypassMagic();
}