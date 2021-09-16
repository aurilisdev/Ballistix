package ballistix.common.blast;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Maps;

import ballistix.api.event.BlastEvent;
import ballistix.api.event.BlastEvent.ConstructBlastEvent;
import ballistix.api.event.BlastEvent.PostBlastEvent;
import ballistix.api.event.BlastEvent.PreBlastEvent;
import ballistix.common.block.SubtypeBlast;
import ballistix.common.entity.EntityBlast;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SExplosionPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.Explosion.Mode;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;

public abstract class Blast {
    public BlockPos position;
    public World world;

    protected Blast(World world, BlockPos position) {
	this.world = world;
	this.position = position;
    }

    public boolean isInstantaneous() {
	return true;
    }

    public abstract SubtypeBlast getBlastType();

    @Deprecated
    public void doPreExplode() {
    }

    @Deprecated
    public abstract boolean doExplode(int callCount);

    @Deprecated
    public void doPostExplode() {
    }

    @Deprecated
    public final void preExplode() {
	PreBlastEvent evt = new PreBlastEvent(world, this);
	MinecraftForge.EVENT_BUS.post(evt);

	if (!evt.isCanceled()) {
	    doPreExplode();
	}
    }

    @Deprecated
    public final boolean explode(int callcount) {
	BlastEvent evt = new BlastEvent(world, this);
	MinecraftForge.EVENT_BUS.post(evt);
	if (!evt.isCanceled()) {
	    return doExplode(callcount);
	}
	return true;
    }

    @Deprecated
    public final void postExplode() {
	PostBlastEvent evt = new PostBlastEvent(world, this);
	MinecraftForge.EVENT_BUS.post(evt);

	if (!evt.isCanceled()) {
	    doPostExplode();
	}
    }

    @SuppressWarnings("java:S1874")
    public void performExplosion() {
	ConstructBlastEvent evt = new ConstructBlastEvent(world, this);
	MinecraftForge.EVENT_BUS.post(evt);
	Explosion explosion = new Explosion(world, null, null, null, position.getX(), position.getY(), position.getZ(), 3, true, Mode.BREAK);
	if (ForgeEventFactory.onExplosionStart(world, explosion)) {
	    return;
	}
	if (!evt.isCanceled()) {
	    if (isInstantaneous()) {
		doPreExplode();
		doExplode(0);
		doPostExplode();
	    } else if (!world.isRemote) {
		EntityBlast entity = new EntityBlast(world);
		entity.setPosition(position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5);
		entity.setBlastType(getBlastType());
		world.addEntity(entity);
	    }
	}
    }

    protected void attackEntities(float size) {
	Map<PlayerEntity, Vector3d> playerKnockbackMap = Maps.newHashMap();
	float f2 = size * 2.0F;
	int k1 = MathHelper.floor(position.getX() - (double) f2 - 1.0D);
	int l1 = MathHelper.floor(position.getX() + (double) f2 + 1.0D);
	int i2 = MathHelper.floor(position.getY() - (double) f2 - 1.0D);
	int i1 = MathHelper.floor(position.getY() + (double) f2 + 1.0D);
	int j2 = MathHelper.floor(position.getZ() - (double) f2 - 1.0D);
	int j1 = MathHelper.floor(position.getZ() + (double) f2 + 1.0D);
	List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(k1, i2, j2, l1, i1, j1));
	Vector3d vector3d = new Vector3d(position.getX(), position.getY(), position.getZ());

	for (Entity entity : list) {
	    if (!entity.isImmuneToExplosions()) {
		double d12 = MathHelper.sqrt(entity.getDistanceSq(vector3d)) / f2;
		if (d12 <= 1.0D) {
		    double d5 = entity.getPosX() - position.getX();
		    double d7 = (entity instanceof TNTEntity ? entity.getPosY() : entity.getPosYEye()) - position.getY();
		    double d9 = entity.getPosZ() - position.getZ();
		    double d13 = MathHelper.sqrt(d5 * d5 + d7 * d7 + d9 * d9);
		    if (d13 != 0.0D) {
			d5 = d5 / d13;
			d7 = d7 / d13;
			d9 = d9 / d13;
			double d14 = Explosion.getBlockDensity(vector3d, entity);
			double d10 = (1.0D - d12) * d14;
			entity.attackEntityFrom(DamageSource.causeExplosionDamage((LivingEntity) null),
				(int) ((d10 * d10 + d10) / 2.0D * 7.0D * f2 + 1.0D));
			double d11 = d10;
			if (entity instanceof LivingEntity) {
			    d11 = ProtectionEnchantment.getBlastDamageReduction((LivingEntity) entity, d10);
			}

			entity.setMotion(entity.getMotion().add(d5 * d11, d7 * d11, d9 * d11));
			if (entity instanceof PlayerEntity) {
			    PlayerEntity playerentity = (PlayerEntity) entity;
			    if (!playerentity.isSpectator() && (!playerentity.isCreative() || !playerentity.abilities.isFlying)) {
				playerKnockbackMap.put(playerentity, new Vector3d(d5 * d10, d7 * d10, d9 * d10));
			    }
			}
		    }
		}
	    }
	}
	for (Entry<PlayerEntity, Vector3d> entry : playerKnockbackMap.entrySet()) {
	    if (entry.getKey() instanceof ServerPlayerEntity) {
		ServerPlayerEntity serverplayerentity = (ServerPlayerEntity) entry.getKey();
		serverplayerentity.connection.sendPacket(
			new SExplosionPacket(position.getX(), position.getY(), position.getZ(), size, new ArrayList<>(), entry.getValue()));
	    }
	}
    }

    public static Blast createFromSubtype(SubtypeBlast explosive, World world, BlockPos pos) {
	try {
	    return (Blast) explosive.blastClass.getConstructor(World.class, BlockPos.class).newInstance(world, pos);
	} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException
		| InvocationTargetException e) {
	    e.printStackTrace();
	}
	return null;
    }
}
