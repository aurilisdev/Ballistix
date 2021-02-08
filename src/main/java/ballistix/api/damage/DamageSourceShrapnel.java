package ballistix.api.damage;

import net.minecraft.util.DamageSource;

public class DamageSourceShrapnel {
	public static DamageSource INSTANCE = new DamageSource("shrapnel").setDamageIsAbsolute();
}
