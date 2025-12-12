package ballistix.common.blast.tier1;

import java.util.List;

import org.joml.Vector3f;

import ballistix.common.blast.util.Blast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.settings.BallistixConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class BlastDebilitation extends Blast {

    public BlastDebilitation(Level world, BlockPos position, Entity owner) {
	super(world, position, owner);
    }

    @Override
    public boolean isInstantaneous() {
	return false;
    }

    @Override
    public void doPreExplode() {
	if (!world.isClientSide) {
	    world.playSound(null, position, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 25, 1);
	}
    }

    @Override
    public boolean doExplode(int callCount) {
	hasStarted = true;
	int radius = (int) BallistixConfig.INSTANCE.EXPLOSIVE_DEBILITATION_SIZE.getAsDouble();
	if (world.isClientSide && callCount % 3 == 0) {
	    for (int x = -radius; x <= radius; x++) {
		for (int y = -radius; y <= radius; y++) {
		    for (int z = -radius; z <= radius; z++) {

			double xPos = position.getX() + x + 0.5 + world.random.nextDouble() - 1.0;
			double yPos = position.getY() + y + 0.5 + world.random.nextDouble() - 1.0;
			double zPos = position.getZ() + z + 0.5 + world.random.nextDouble() - 1.0;

			boolean add = canSpawnParticle(new BlockPos((int) xPos, (int) yPos, (int) zPos));

			if (add && x * x + y * y + z * z < radius * radius && world.random.nextDouble() < 1 / 10.0) {
			    Minecraft.getInstance().particleEngine.createParticle(
				    new DustParticleOptions(new Vector3f(1, 1, 1), 20), xPos, yPos, zPos, 0.0D, 0.0D,
				    0.0D);
			}
		    }
		}
	    }
	}
	if (!world.isClientSide) {

	    float x = position.getX();
	    float y = position.getY();
	    float z = position.getZ();

	    int x0 = Mth.floor(x - (double) radius - 1.0D);
	    int x1 = Mth.floor(x + (double) radius + 1.0D);
	    int y0 = Mth.floor(y - (double) radius - 1.0D);
	    int y1 = Mth.floor(y + (double) radius + 1.0D);
	    int z0 = Mth.floor(z - (double) radius - 1.0D);
	    int z1 = Mth.floor(z + (double) radius + 1.0D);
	    List<Entity> list = world.getEntities(null, new AABB(x0, y0, z0, x1, y1, z1));
	    for (Entity entity : list) {

		if (!canHarmEntity(entity)) {
		    continue;
		}

		if (entity instanceof LivingEntity living) {
		    living.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 360), owner);
		    living.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 1200), owner);
		    living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 1200, 2), owner);
		}
	    }
	}
	return callCount > BallistixConfig.INSTANCE.EXPLOSIVE_DEBILITATION_DURATION.getAsDouble();
    }

    @Override
    public SubtypeBlast getBlastType() {
	return SubtypeBlast.debilitation;
    }

}
