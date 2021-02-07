package ballistix.common.blast;

import java.util.List;

import ballistix.common.block.SubtypeBlast;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Explosion.Mode;
import net.minecraft.world.World;

public class BlastAttractive extends Blast {

	public BlastAttractive(World world, BlockPos position) {
		super(world, position);
	}

	@Override
	public void doPreExplode() {
	}

	@Override
	public boolean doExplode(int callCount) {
		if (!world.isRemote) {
			world.createExplosion(null, position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5, 1f, Mode.BREAK);
		}
		float x = position.getX();
		float y = position.getY();
		float z = position.getZ();
		float size = 5f;
		float f2 = size * 2.0F;
		int k1 = MathHelper.floor(x - (double) f2 - 1.0D);
		int l1 = MathHelper.floor(x + (double) f2 + 1.0D);
		int i2 = MathHelper.floor(y - (double) f2 - 1.0D);
		int i1 = MathHelper.floor(y + (double) f2 + 1.0D);
		int j2 = MathHelper.floor(z - (double) f2 - 1.0D);
		int j1 = MathHelper.floor(z + (double) f2 + 1.0D);
		List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(k1, i2, j2, l1, i1, j1));

		for (int k2 = 0; k2 < list.size(); ++k2) {
			Entity entity = list.get(k2);
			double d5 = entity.getPosX() - x;
			double d7 = (entity instanceof TNTEntity ? entity.getPosY() : entity.getPosYEye()) - y;
			double d9 = entity.getPosZ() - z;
			double d13 = MathHelper.sqrt(d5 * d5 + d7 * d7 + d9 * d9);
			if (d13 != 0.0D) {
				d5 = d5 / d13;
				d7 = d7 / d13;
				d9 = d9 / d13;
				double d11 = -2;
				entity.setMotion(entity.getMotion().add(d5 * d11, d7 * d11, d9 * d11));
			}
		}
		return true;
	}

	@Override
	public void doPostExplode() {
	}

	@Override
	public SubtypeBlast getBlastType() {
		return SubtypeBlast.attractive;
	}

}
