package ballistix.common.blast;

import java.util.List;

import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.settings.BallistixConstants;
import ballistix.compatibility.griefdefender.GriefDefenderHandler;
import ballistix.registers.BallistixDamageTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class BlastContagious extends Blast {

	public BlastContagious(World world, BlockPos position) {
		super(world, position);
	}

	@Override
	public boolean isInstantaneous() {
		return false;
	}

	@Override
	public void doPreExplode() {
		if(!world.isClientSide) {
			world.playSound(null, position, SoundEvents.GENERIC_EXPLODE, SoundCategory.BLOCKS, 25, 1);
		}
	}

	@Override
	public boolean doExplode(int callCount) {
		hasStarted = true;
		int radius = (int) BallistixConstants.EXPLOSIVE_CONTAGIOUS_SIZE;
		if (world.isClientSide && callCount % 3 == 0) {
			for (int x = -radius; x <= radius; x++) {
				for (int y = -radius; y <= radius; y++) {
					for (int z = -radius; z <= radius; z++) {

						double xPos = position.getX() + x + 0.5 + world.random.nextDouble() - 1.0;
						double yPos = position.getY() + y + 0.5 + world.random.nextDouble() - 1.0;
						double zPos = position.getZ() + z + 0.5 + world.random.nextDouble() - 1.0;


						boolean add = false;
						switch (griefPreventionMethod) {
						case GRIEF_DEFENDER:
							add = GriefDefenderHandler.shouldHarmBlock(new BlockPos(xPos, yPos, zPos));
							break;
						default:
							add = true;
							break;
						}

						if (add && x * x + y * y + z * z < radius * radius && world.random.nextDouble() < 1 / 10.0) {
							Minecraft.getInstance().particleEngine.createParticle(new RedstoneParticleData(0.5f, 0.4f, 0, 5), xPos, yPos , zPos, 0.0D, 0.0D, 0.0D);
						}
					}
				}
			}
		}
		if (!world.isClientSide) {

			float x = position.getX();
			float y = position.getY();
			float z = position.getZ();

			int x0 = MathHelper.floor(x - (double) radius - 1.0D);
			int x1 = MathHelper.floor(x + (double) radius + 1.0D);
			int y0 = MathHelper.floor(y - (double) radius - 1.0D);
			int y1 = MathHelper.floor(y + (double) radius + 1.0D);
			int z0 = MathHelper.floor(z - (double) radius - 1.0D);
			int z1 = MathHelper.floor(z + (double) radius + 1.0D);

			List<Entity> entities = world.getEntities(null, new AxisAlignedBB(x0, y0, z0, x1, y1, z1));

			for (Entity entity : entities) {

				switch (griefPreventionMethod) {
					case GRIEF_DEFENDER :
						if(!GriefDefenderHandler.shouldEntityBeHarmed(entity)) {
							continue;
						}
						break;
					default:
						break;
				}

				if (entity instanceof LivingEntity) {
					LivingEntity living = (LivingEntity) entity;
					living.addEffect(new EffectInstance(Effects.BLINDNESS, 360, 2));
					living.addEffect(new EffectInstance(Effects.WEAKNESS, 360, 2));
					living.addEffect(new EffectInstance(Effects.HUNGER, 360, 3));
					if (callCount % 10 == 0) {
						living.hurt(BallistixDamageTypes.CHEMICAL_GAS, 4);
					}
				}
			}
		}
		return callCount > BallistixConstants.EXPLOSIVE_CONTAGIOUS_DURATION;
	}

	@Override
	public SubtypeBlast getBlastType() {
		return SubtypeBlast.contagious;
	}

}
