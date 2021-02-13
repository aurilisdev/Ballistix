package ballistix.common.blast;

import java.util.List;

import ballistix.common.block.SubtypeBlast;
import ballistix.common.settings.Constants;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class BlastDebilitation extends Blast {

	public BlastDebilitation(World world, BlockPos position) {
		super(world, position);
	}

	@Override
	public boolean isInstantaneous() {
		return false;
	}

	@Override
	public void doPreExplode() {
	}

	@Override
	public boolean doExplode(int callCount) {
		int radius = (int) Constants.EXPLOSIVE_DEBILITATION_SIZE;
		if (world.isRemote && world instanceof ClientWorld && callCount % 3 == 0) {
			for (int x = -radius; x <= radius; x++) {
				for (int y = -radius; y <= radius; y++) {
					for (int z = -radius; z <= radius; z++) {
						if (x * x + y * y + z * z < radius * radius && world.rand.nextDouble() < 1 / 20.0) {
							world.addParticle(new RedstoneParticleData(1, 1, 1, 5), position.getX() + x + 0.5 + world.rand.nextDouble() - 1.0, position.getY() + y + 0.5 + world.rand.nextDouble() - 1.0,
									position.getZ() + z + 0.5 + world.rand.nextDouble() - 1.0, 0.0D, 0.0D, 0.0D);
						}
					}
				}
			}
		}
		if (!world.isRemote) {
			float x = position.getX();
			float y = position.getY();
			float z = position.getZ();
			int k1 = MathHelper.floor(x - (double) radius - 1.0D);
			int l1 = MathHelper.floor(x + (double) radius + 1.0D);
			int i2 = MathHelper.floor(y - (double) radius - 1.0D);
			int i1 = MathHelper.floor(y + (double) radius + 1.0D);
			int j2 = MathHelper.floor(z - (double) radius - 1.0D);
			int j1 = MathHelper.floor(z + (double) radius + 1.0D);
			List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(k1, i2, j2, l1, i1, j1));
			for (int k2 = 0; k2 < list.size(); ++k2) {
				Entity entity = list.get(k2);
				if (entity instanceof LivingEntity) {
					LivingEntity living = (LivingEntity) entity;
					living.addPotionEffect(new EffectInstance(Effects.POISON, 360));
					living.addPotionEffect(new EffectInstance(Effects.MINING_FATIGUE, 360));
					living.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 360, 2));
				}
			}
		}
		return callCount > Constants.EXPLOSIVE_DEBILITATION_DURATION;
	}

	@Override
	public void doPostExplode() {
	}

	@Override
	public SubtypeBlast getBlastType() {
		return SubtypeBlast.debilitation;
	}

}
