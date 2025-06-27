package ballistix.registers;

import ballistix.Ballistix;
import ballistix.common.effect.EffectVirus;
import net.minecraft.potion.Effect;

public class BallistixEffects {
	
	public static final Effect VIRUS = new EffectVirus().setRegistryName(Ballistix.ID, "virus");

}
