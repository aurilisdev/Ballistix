package ballistix.common.blast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ballistix.common.blast.thread.ThreadSimpleBlast;
import ballistix.common.block.SubtypeBlast;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SExplosionPacket;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.Explosion.Mode;
import net.minecraft.world.World;

public class BlastDarkmatter extends Blast {

	public BlastDarkmatter(World world, BlockPos position) {
		super(world, position);
	}

	@Override
	public void doPreExplode() {
		if (!world.isRemote) {
			thread = new ThreadSimpleBlast(world, position, 50, Integer.MAX_VALUE, null);
			thread.start();
		}
	}

	private ThreadSimpleBlast thread;
	private int pertick = -1;

	@Override
	public boolean doExplode(int callCount) {
		if (!world.isRemote) {
			if (thread == null) {
				return true;
			}
			Explosion ex = new Explosion(world, null, position.getX(), position.getY(), position.getZ(), 25, true, Mode.BREAK);
			if (thread.isComplete) {
				if (pertick == -1) {
					pertick = (int) (thread.results.size() / 6000.0);
				}
				int finished = pertick;
				Iterator<BlockPos> iterator = thread.results.iterator();
				while (iterator.hasNext()) {
					if (finished-- < 0) {
						break;
					}
					BlockPos p = new BlockPos(iterator.next());
					world.getBlockState(p).getBlock().onExplosionDestroy(world, p, ex);
					world.setBlockState(p, Blocks.AIR.getDefaultState(), 2);
					iterator.remove();
				}
				if (thread.results.isEmpty()) {
					return true;
				}
			}
			float x = position.getX();
			float y = position.getY();
			float z = position.getZ();
			float size = 50f;
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
					double d11 = (-0.2 - callCount / 600.0) / d13;
					entity.setMotion(entity.getMotion().add(d5 * d11, d7 * d11, d9 * d11));
					if (entity instanceof ServerPlayerEntity) {
						ServerPlayerEntity serverplayerentity = (ServerPlayerEntity) entity;
						if (!serverplayerentity.isCreative()) {
							serverplayerentity.connection.sendPacket(new SExplosionPacket(x, y, z, size, new ArrayList<>(), new Vector3d(d5 * d11, d7 * d11, d9 * d11)));
						}
					}
				}
			}
			attackEntities((float) (callCount / 300.0));
		}
		return false;
	}

	@Override
	public void doPostExplode() {
	}

	@Override
	public boolean isInstantaneous() {
		return false;
	}

	@Override
	public SubtypeBlast getBlastType() {
		return SubtypeBlast.darkmatter;
	}

}
