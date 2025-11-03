package ballistix.common.effect;

import java.util.List;

import ballistix.common.settings.BallistixConfig;
import ballistix.registers.BallistixDamageTypes;
import ballistix.registers.BallistixEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import voltaic.prefab.utilities.math.Color;

public class EffectVirus extends MobEffect {

    public static final Color COLOR = new Color(78, 200, 49, 255);

    public EffectVirus(MobEffectCategory category, int color) {
        super(category, color);
    }

    public EffectVirus() {
        this(MobEffectCategory.HARMFUL, COLOR.color());
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {

        Level level = livingEntity.level();

        if(level.random.nextFloat() < 0.5F) {

            List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, new AABB(livingEntity.blockPosition()).inflate(BallistixConfig.INSTANCE.VIRUS_EFFECT_RADIUS.get()));

            for(LivingEntity entity : list) {
                entity.addEffect(new MobEffectInstance(BallistixEffects.VIRUS, -1));
            }

        }

        if(level.random.nextFloat() < 0.05F) {
            livingEntity.hurt(livingEntity.damageSources().source(BallistixDamageTypes.VIRUS, livingEntity), (float) (Math.pow(amplifier, 1.3) + 1));
            if (livingEntity instanceof Player pl) {
                pl.causeFoodExhaustion(0.05F * (amplifier + 1));
            }
        }




        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
