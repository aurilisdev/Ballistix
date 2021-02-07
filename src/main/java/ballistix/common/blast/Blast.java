package ballistix.common.blast;

import java.lang.reflect.InvocationTargetException;

import ballistix.api.event.BlastEvent;
import ballistix.api.event.BlastEvent.ConstructBlastEvent;
import ballistix.api.event.BlastEvent.PostBlastEvent;
import ballistix.api.event.BlastEvent.PreBlastEvent;
import ballistix.common.block.SubtypeExplosive;
import ballistix.common.entity.EntityBlast;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public abstract class Blast {
	public BlockPos position;
	public World world;

// public EntityExploding controller = null;
	public Blast(World world, BlockPos position) {
		this.world = world;
		this.position = position;
	}

	public boolean isInstantaneous() {
		return true;
	}

	public abstract void doPreExplode();

	public abstract boolean doExplode(int callCount);

	public abstract void doPostExplode();

	@Deprecated
	public final void preExplode() {
		PreBlastEvent evt = new PreBlastEvent(world, this);
		MinecraftForge.EVENT_BUS.post(evt);

		if (!evt.isCanceled()) {
			doPreExplode();
		}
	}

	@Deprecated
	public final void explode(int callcount) {
		BlastEvent evt = new BlastEvent(world, this);
		MinecraftForge.EVENT_BUS.post(evt);

		if (!evt.isCanceled()) {
			doExplode(callcount);
		}
	}

	@Deprecated
	public final void postExplode() {
		PostBlastEvent evt = new PostBlastEvent(world, this);
		MinecraftForge.EVENT_BUS.post(evt);

		if (!evt.isCanceled()) {
			doPostExplode();
		}
	}

	public void performExplosion() {
		ConstructBlastEvent evt = new ConstructBlastEvent(world, this);
		MinecraftForge.EVENT_BUS.post(evt);

		if (!evt.isCanceled()) {
			if (isInstantaneous()) {
				doPreExplode();
				doExplode(0);
				doPostExplode();
			} else if (!world.isRemote) {
				EntityBlast entity = new EntityBlast(world);
				entity.setPosition(position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5);
				entity.blast = this;
				world.addEntity(entity);
			}
		}
	}

	public static Blast createFromSubtype(SubtypeExplosive explosive, World world, BlockPos pos) {
		try {
			return (Blast) explosive.blastClass.getConstructor(World.class, BlockPos.class).newInstance(world, pos);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
}
