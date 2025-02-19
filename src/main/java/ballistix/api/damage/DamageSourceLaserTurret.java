package ballistix.api.damage;

import net.minecraft.world.damagesource.DamageSource;

public class DamageSourceLaserTurret {

	public static DamageSource INSTANCE = new DamageSource("laserturret").bypassMagic();
	
}
