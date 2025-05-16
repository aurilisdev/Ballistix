package ballistix.registers;

import net.minecraft.util.DamageSource;

public class BallistixDamageTypes {
	public static final DamageSource CHEMICAL_GAS = new DamageSource("chemicalgas").bypassArmor().bypassMagic();
	public static final DamageSource CIWS_BULLET = new DamageSource("ciwsbullet").bypassMagic();
	public static final DamageSource LASER_TURRET = new DamageSource("laserturret").bypassMagic();
	public static final DamageSource RAILGUN_ROUND = new DamageSource("railgunround").bypassMagic();
	public static final DamageSource SHRAPNEL = new DamageSource("shrapnel").bypassMagic();
}
