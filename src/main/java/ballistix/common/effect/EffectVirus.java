package ballistix.common.effect;

import ballistix.common.settings.BallistixConstants;
import ballistix.registers.BallistixDamageTypes;
import ballistix.registers.BallistixEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import voltaic.prefab.utilities.math.Color;

import java.util.List;

public class EffectVirus extends Effect {

    public static final Color COLOR = new Color(78, 200, 49, 255);

    public EffectVirus(EffectType category, int color) {
        super(category, color);
    }

    public EffectVirus() {
        this(EffectType.HARMFUL, COLOR.color());
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {

        World level = livingEntity.level;

        if(level.random.nextFloat() < 0.5F) {

            List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(livingEntity.blockPosition()).inflate(BallistixConstants.VIRUS_EFFECT_RADIUS));

            for(LivingEntity entity : list) {
                entity.addEffect(new EffectInstance(BallistixEffects.VIRUS, -1));
            }

        }

        if(level.random.nextFloat() < 0.05F) {
            livingEntity.hurt(BallistixDamageTypes.VIRUS, (float) (Math.pow(amplifier, 1.3) + 1));
            if (livingEntity instanceof PlayerEntity) {
                ((PlayerEntity) livingEntity).causeFoodExhaustion(0.05F * (amplifier + 1));
            }
        }


    }

    @Override
	public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
		return true;
	}
}
