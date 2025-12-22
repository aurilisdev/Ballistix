package ballistix.common.blast.tier1;

import java.util.List;

import javax.annotation.Nullable;

import org.joml.Vector3f;

import ballistix.common.blast.util.Blast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.settings.BallistixConfig;
import ballistix.registers.BallistixDamageTypes;
import ballistix.registers.BallistixEffects;
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

public class BlastChemical extends Blast {

    public BlastChemical(Level world, BlockPos position, @Nullable  Entity owner, @Nullable Entity blastEntity) {
	super(world, position, owner, blastEntity);
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
	int radius = (int) BallistixConfig.INSTANCE.EXPLOSIVE_CHEMICAL_SIZE.getAsDouble();
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
				    new DustParticleOptions(new Vector3f(0.7f, 0.8f, 0), 5), xPos, yPos, zPos, 0.0D,
				    0.0D, 0.0D);
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

	    List<Entity> entities = world.getEntities(null, new AABB(x0, y0, z0, x1, y1, z1));
	    for (Entity entity : entities) {

		if (!canHarmEntity(entity)) {
		    continue;
		}

		if (entity instanceof LivingEntity living) {

		    living.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 360, 2), owner);

		    living.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 360), owner);

		    living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 360, 2), owner);

		    living.addEffect(new MobEffectInstance(BallistixEffects.TOXIN, 360), owner);

		    if (callCount % 10 == 0) {

			living.hurt(living.damageSources().source(BallistixDamageTypes.CHEMICAL_GAS, owner), 2);

		    }
		}
	    }
	}
	return callCount > BallistixConfig.INSTANCE.EXPLOSIVE_CHEMICAL_DURATION.getAsDouble();
    }

    @Override
    public SubtypeBlast getBlastType() {
	return SubtypeBlast.chemical;
    }

}
