package ballistix.api.damage;

import net.minecraft.util.DamageSource;

public class DamageSourceChemicalGas {
	public static DamageSource INSTANCE = new DamageSource("chemicalgas").bypassArmor().bypassMagic();
}
