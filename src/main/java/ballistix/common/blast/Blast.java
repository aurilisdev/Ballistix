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
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;

public abstract class Blast {
    public BlockPos position;
    public Level world;
    public boolean hasStarted;

    protected Blast(Level world, BlockPos position) {
	this.world = world;
	this.position = position;
    }

    public boolean isInstantaneous() {
	return true;
    }

    public abstract SubtypeBlast getBlastType();

    @Deprecated(since = "Should not be called externally!", forRemoval = false)
    public void doPreExplode() {
    }

    @Deprecated(since = "Should not be called externally!", forRemoval = false)
    public abstract boolean doExplode(int callCount);

    @Deprecated(since = "Should not be called externally!", forRemoval = false)
    public void doPostExplode() {
    }

    @Deprecated(since = "Should not be called externally!", forRemoval = false)
    public final void preExplode() {
	PreBlastEvent evt = new PreBlastEvent(world, this);
	MinecraftForge.EVENT_BUS.post(evt);

	if (!evt.isCanceled()) {
	    doPreExplode();
	}
    }

    @Deprecated(since = "Should not be called externally!", forRemoval = false)
    public final boolean explode(int callcount) {
	BlastEvent evt = new BlastEvent(world, this);
	MinecraftForge.EVENT_BUS.post(evt);
	if (!evt.isCanceled()) {
	    return doExplode(callcount);
	}
	return true;
    }

    @Deprecated(since = "Should not be called externally!", forRemoval = false)
    public final void postExplode() {
	PostBlastEvent evt = new PostBlastEvent(world, this);
	MinecraftForge.EVENT_BUS.post(evt);

	if (!evt.isCanceled()) {
	    doPostExplode();
	}
    }

    @SuppressWarnings("java:S1874")
    public EntityBlast performExplosion() {
	ConstructBlastEvent evt = new ConstructBlastEvent(world, this);
	MinecraftForge.EVENT_BUS.post(evt);
	Explosion explosion = new Explosion(world, null, null, null, position.getX(), position.getY(), position.getZ(), 3, true,
		BlockInteraction.BREAK);
	if (!ForgeEventFactory.onExplosionStart(world, explosion) && !evt.isCanceled()) {
	    if (isInstantaneous()) {
		doPreExplode();
		doExplode(0);
		doPostExplode();
	    } else if (!world.isClientSide) {
		EntityBlast entity = new EntityBlast(world);
		entity.setPos(position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5);
		entity.setBlastType(getBlastType());
		world.addFreshEntity(entity);
		return entity;
	    }
	}
	return null;
    }

    protected void attackEntities(float size) {
	Map<Player, Vec3> playerKnockbackMap = Maps.newHashMap();
	float f2 = size * 2.0F;
	int k1 = Mth.floor(position.getX() - (double) f2 - 1.0D);
	int l1 = Mth.floor(position.getX() + (double) f2 + 1.0D);
	int i2 = Mth.floor(position.getY() - (double) f2 - 1.0D);
	int i1 = Mth.floor(position.getY() + (double) f2 + 1.0D);
	int j2 = Mth.floor(position.getZ() - (double) f2 - 1.0D);
	int j1 = Mth.floor(position.getZ() + (double) f2 + 1.0D);
	List<Entity> list = world.getEntities(null, new AABB(k1, i2, j2, l1, i1, j1));
	Vec3 vector3d = new Vec3(position.getX(), position.getY(), position.getZ());

	for (Entity entity : list) {
	    if (!entity.ignoreExplosion()) {
		double d12 = Mth.sqrt((float) entity.distanceToSqr(vector3d)) / f2;
		if (d12 <= 1.0D) {
		    double d5 = entity.getX() - position.getX();
		    double d7 = (entity instanceof PrimedTnt ? entity.getY() : entity.getEyeY()) - position.getY();
		    double d9 = entity.getZ() - position.getZ();
		    double d13 = Mth.sqrt((float) (d5 * d5 + d7 * d7 + d9 * d9));
		    if (d13 != 0.0D) {
			d5 = d5 / d13;
			d7 = d7 / d13;
			d9 = d9 / d13;
			double d14 = Explosion.getSeenPercent(vector3d, entity);
			double d10 = (1.0D - d12) * d14;
			entity.hurt(DamageSource.explosion((LivingEntity) null), (int) ((d10 * d10 + d10) / 2.0D * 7.0D * f2 + 1.0D));
			double d11 = d10;
			if (entity instanceof LivingEntity le) {
			    d11 = ProtectionEnchantment.getExplosionKnockbackAfterDampener(le, d10);
			}

			entity.setDeltaMovement(entity.getDeltaMovement().add(d5 * d11, d7 * d11, d9 * d11));
			if (entity instanceof Player playerentity) {
			    if (!playerentity.isSpectator() && (!playerentity.isCreative() || !playerentity.getAbilities().flying)) {
				playerKnockbackMap.put(playerentity, new Vec3(d5 * d10, d7 * d10, d9 * d10));
			    }
			}
		    }
		}
	    }
	}
	for (Entry<Player, Vec3> entry : playerKnockbackMap.entrySet()) {
	    if (entry.getKey()instanceof ServerPlayer serverplayerentity) {
		serverplayerentity.connection.send(
			new ClientboundExplodePacket(position.getX(), position.getY(), position.getZ(), size, new ArrayList<>(), entry.getValue()));
	    }
	}
    }

    public static Blast createFromSubtype(SubtypeBlast explosive, Level world, BlockPos pos) {
	try {
	    return (Blast) explosive.blastClass.getConstructor(Level.class, BlockPos.class).newInstance(world, pos);
	} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException
		| InvocationTargetException e) {
	    e.printStackTrace();
	}
	return null;
    }
}
