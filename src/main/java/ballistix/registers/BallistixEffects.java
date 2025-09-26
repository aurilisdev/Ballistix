package ballistix.registers;

import ballistix.Ballistix;
import ballistix.common.effect.EffectFrostbite;
import ballistix.common.effect.EffectToxin;
import ballistix.common.effect.EffectVirus;
import net.minecraft.potion.Effect;

public class BallistixEffects {
	
	public static final Effect VIRUS = new EffectVirus().setRegistryName(Ballistix.ID, "virus");
	public static final Effect TOXIN = new EffectToxin().setRegistryName(Ballistix.ID, "toxin");
    public static final Effect FROSTBITE = new EffectFrostbite().setRegistryName(Ballistix.ID, "frostbite");

}
