package ballistix.common.effect;

import ballistix.registers.BallistixDamageTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import voltaic.prefab.utilities.math.Color;

public class EffectToxin extends Effect {

    public static final Color COLOR = new Color(100, 200, 49, 255);

    public EffectToxin(EffectType category, int color) {
        super(category, color);
    }

    public EffectToxin() {
        super(EffectType.HARMFUL, COLOR.color());
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        int i = 25 >> amplifier;
        return i > 0 ? amplifier % i == 0 : true;
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
        livingEntity.hurt(BallistixDamageTypes.TOXIN, (float) (Math.pow(amplifier, 1.3) + 1));
    }

}
