package ballistix.api.damage;

import net.minecraft.world.damagesource.DamageSource;

public class DamageSourceShrapnel {
	public static DamageSource INSTANCE = new DamageSource("shrapnel").bypassMagic();
}