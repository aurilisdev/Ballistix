package ballistix.common.effect;

import ballistix.registers.BallistixDamageTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import voltaic.prefab.utilities.math.Color;

public class EffectFrostbite extends Effect {

    public static final Color COLOR = new Color(0, 100, 256, 255);

    public EffectFrostbite(EffectType category, int color) {
        super(category, color);
    }

    public EffectFrostbite() {
        this(EffectType.HARMFUL, COLOR.color());
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        int i = 25 >> amplifier;
        return i > 0 ? amplifier % i == 0 : true;
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
        livingEntity.hurt(BallistixDamageTypes.FROSTBITE, (float) (Math.pow(amplifier, 1.3) + 1));
    }


}
