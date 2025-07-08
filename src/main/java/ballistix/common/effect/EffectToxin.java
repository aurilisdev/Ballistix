package ballistix.common.effect;

import ballistix.registers.BallistixDamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import voltaic.prefab.utilities.math.Color;

public class EffectToxin extends MobEffect {

    public static final Color COLOR = new Color(100, 200, 49, 255);

    public EffectToxin(MobEffectCategory category, int color) {
        super(category, color);
    }

    public EffectToxin() {
        super(MobEffectCategory.HARMFUL, COLOR.color());
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        int i = 25 >> amplifier;
        return i > 0 ? amplifier % i == 0 : true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        livingEntity.hurt(livingEntity.damageSources().source(BallistixDamageTypes.TOXIN, livingEntity), (float) (Math.pow(amplifier, 1.3) + 1));
        return true;
    }

}
