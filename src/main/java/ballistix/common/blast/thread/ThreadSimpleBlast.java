package ballistix.common.blast.thread;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ThreadSimpleBlast extends ThreadBlast {

	public ThreadSimpleBlast(World world, BlockPos position, int banJing, Entity source, boolean destroyBedrock) {
		super(world, position, banJing, 0, source);
		explosionRadius = 40;
	}

	@Override
	public void run() {
		if (!worldObj.isRemote) {

//			for (int x = -this.explosionRadius; x < this.explosionRadius; ++x)
//			{
//				for (int y = -this.explosionRadius; y < this.explosionRadius; ++y)
//				{
//					for (int z = -this.explosionRadius; z < this.explosionRadius; ++z)
//					{
//						{
//							Pos3D targetPosition = position.clone().translate(new Pos3D(x, y, z));
//
//							double dist = position.distance(targetPosition);
//
//							if (dist < this.explosionRadius - 1 || worldObj.rand.nextFloat() > 0.7)
//							{
//								Block block = worldObj.getBlock((int) targetPosition.xPos, (int) targetPosition.yPos, (int) targetPosition.zPos);
//								if (block != null && !block.isAir(worldObj, (int) targetPosition.xPos, (int) targetPosition.yPos, (int) targetPosition.zPos))
//								{
//									if (!destroyBedrock && block.getBlockHardness(worldObj, (int) targetPosition.xPos, (int) targetPosition.yPos, (int) targetPosition.zPos) < 0)
//									{
//										continue;
//									}
//									if (DefenseUtils.canBreak(worldObj, block, targetPosition.xPos, targetPosition.yPos, targetPosition.zPos))
//									{
//										this.results.add(new HashedPos3D(targetPosition.clone(), (int) (dist * 20 + 20 * worldObj.rand.nextFloat())));
//									}
//								}
//							}
//						}
//					}
//				}
//			}
		}

		super.run();
	}
}