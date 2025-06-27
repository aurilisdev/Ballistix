package ballistix.common.effect;

import ballistix.common.settings.BallistixConstants;
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

import java.util.List;

public class EffectVirus extends MobEffect {

    public static final Color COLOR = new Color(78, 200, 49, 255);

    public EffectVirus(MobEffectCategory category, int color) {
        super(category, color);
    }

    public EffectVirus() {
        this(MobEffectCategory.HARMFUL, COLOR.color());
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {

        Level level = livingEntity.level;

        if(level.random.nextFloat() < 0.5F) {

            List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, new AABB(livingEntity.blockPosition()).inflate(BallistixConstants.VIRUS_EFFECT_RADIUS));

            for(LivingEntity entity : list) {
                entity.addEffect(new MobEffectInstance(BallistixEffects.VIRUS.get(), -1));
            }

        }

        if(level.random.nextFloat() < 0.05F) {
            livingEntity.hurt(BallistixDamageTypes.VIRUS, (float) (Math.pow(amplifier, 1.3) + 1));
            if (livingEntity instanceof Player pl) {
                pl.causeFoodExhaustion(0.05F * (amplifier + 1));
            }
        }


    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}
