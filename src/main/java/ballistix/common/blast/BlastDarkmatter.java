package ballistix.common.blast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ballistix.common.blast.thread.ThreadSimpleBlast;
import ballistix.common.block.SubtypeBlast;
import ballistix.common.settings.Constants;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.FallingBlockEntity;
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
	    thread = new ThreadSimpleBlast(world, position, (int) Constants.EXPLOSIVE_DARKMATTER_RADIUS, Integer.MAX_VALUE, null);
	    thread.start();
	}
    }

    private ThreadSimpleBlast thread;
    private int callAtStart = -1;
    private int pertick = -1;

    @Override
    public boolean doExplode(int callCount) {
	if (!world.isRemote) {
	    if (thread == null) {
		return true;
	    }
	    Explosion ex = new Explosion(world, null, null, null, position.getX(), position.getY(), position.getZ(),
		    (float) Constants.EXPLOSIVE_DARKMATTER_RADIUS, false, Mode.BREAK);
	    if (thread.isComplete) {
		if (callAtStart == -1) {
		    callAtStart = callCount;
		}
		if (pertick == -1) {
		    pertick = (int) (thread.results.size() / Constants.EXPLOSIVE_DARKMATTER_DURATION);
		}
		int finished = pertick;
		Iterator<BlockPos> iterator = thread.results.iterator();
		while (iterator.hasNext()) {
		    if (finished-- < 0) {
			break;
		    }
		    BlockPos p = new BlockPos(iterator.next());
		    world.getBlockState(p).getBlock().onExplosionDestroy(world, p, ex);
		    world.setBlockState(p, Blocks.AIR.getDefaultState(), 2 | 16 | 32);
		    iterator.remove();
		}
		if (thread.results.isEmpty()) {
		    return true;
		}
	    }
	    float x = position.getX();
	    float y = position.getY();
	    float z = position.getZ();
	    float size = (float) Constants.EXPLOSIVE_DARKMATTER_RADIUS;
	    float f2 = size * 2.0F;
	    int k1 = MathHelper.floor(x - (double) f2 - 1.0D);
	    int l1 = MathHelper.floor(x + (double) f2 + 1.0D);
	    int i2 = MathHelper.floor(y - (double) f2 - 1.0D);
	    int i1 = MathHelper.floor(y + (double) f2 + 1.0D);
	    int j2 = MathHelper.floor(z - (double) f2 - 1.0D);
	    int j1 = MathHelper.floor(z + (double) f2 + 1.0D);
	    List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(k1, i2, j2, l1, i1, j1));

	    for (Entity entity : list) {
		double d5 = entity.getPosX() - x;
		double d7 = (entity instanceof TNTEntity ? entity.getPosY() : entity.getPosYEye()) - y;
		double d9 = entity.getPosZ() - z;
		double d13 = MathHelper.sqrt(d5 * d5 + d7 * d7 + d9 * d9);
		if (d13 != 0.0D) {
		    d5 = d5 / d13;
		    d7 = d7 / d13;
		    d9 = d9 / d13;
		    double d11 = (-0.2 - (callCount - callAtStart) / 150.0) / d13;
		    entity.setMotion(entity.getMotion().add(d5 * d11, d7 * d11, d9 * d11));
		    if (entity instanceof ServerPlayerEntity) {
			ServerPlayerEntity serverplayerentity = (ServerPlayerEntity) entity;
			if (!serverplayerentity.isCreative()) {
			    serverplayerentity.connection
				    .sendPacket(new SExplosionPacket(x, y, z, size, new ArrayList<>(), new Vector3d(d5 * d11, d7 * d11, d9 * d11)));
			}
		    } else if (entity instanceof FallingBlockEntity) {
			entity.remove();
		    }
		}
	    }
	    attackEntities((float) ((callCount - callAtStart) / 75.0));
	}
	return false;
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
