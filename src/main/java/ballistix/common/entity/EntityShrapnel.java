package ballistix.common.entity;

import java.util.List;

import javax.annotation.Nullable;

import ballistix.DeferredRegisters;
import ballistix.api.damage.DamageSourceShrapnel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

public class EntityShrapnel extends ThrowableProjectile {
    private static final EntityDataAccessor<Boolean> ISEXPLOSIVE = SynchedEntityData.defineId(EntityShrapnel.class, EntityDataSerializers.BOOLEAN);
    public boolean isExplosive = false;

    public EntityShrapnel(EntityType<? extends EntityShrapnel> type, Level worldIn) {
	super(type, worldIn);
    }

    public EntityShrapnel(Level worldIn) {
	this(DeferredRegisters.ENTITY_SHRAPNEL.get(), worldIn);
    }

    @Override
    protected void defineSynchedData() {
	entityData.define(ISEXPLOSIVE, false);
    }

    @Override
    public void tick() {
	if (!level.isClientSide) {
	    entityData.set(ISEXPLOSIVE, isExplosive);
	} else {
	    isExplosive = entityData.get(ISEXPLOSIVE);
	}
	if (!isNoGravity()) {
	    this.setDeltaMovement(getDeltaMovement().add(0.0D, -0.04D, 0.0D));
	}
	setPos(getX() + getDeltaMovement().x, getY() + getDeltaMovement().y, getZ() + getDeltaMovement().z);
	EntityDimensions size = getDimensions(Pose.STANDING);
	setBoundingBox(new AABB(getX() - size.width * 2, getY() - size.height * 2, getZ() - size.width * 2, getX() + size.width * 2,
		getY() + size.height * 2, getZ() + size.width * 2));
	if (onGround || tickCount > (isExplosive ? 400 : 100) || level.getBlockState(blockPosition()).getMaterial().blocksMotion()) {
	    remove(RemovalReason.DISCARDED);
	}
	if (!level.isClientSide) {
	    List<LivingEntity> livings = level.getEntitiesOfClass(LivingEntity.class, getBoundingBox());
	    for (LivingEntity living : livings) {
		living.hurt(DamageSourceShrapnel.INSTANCE, 10);
		remove(RemovalReason.DISCARDED);
	    }
	}
    }

    @Override
    public void shootFromRotation(@Nullable Entity entity, float pitch, float yaw, float par4, float force, float par6) {
	float f = -Mth.sin(yaw * ((float) Math.PI / 180F)) * Mth.cos(pitch * ((float) Math.PI / 180F));
	float f1 = -Mth.sin((pitch + par4) * ((float) Math.PI / 180F));
	float f2 = Mth.cos(yaw * ((float) Math.PI / 180F)) * Mth.cos(pitch * ((float) Math.PI / 180F));
	shoot(f, f1, f2, force, par6);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
	compound.putBoolean("type", isExplosive);
    }

    @Override
    public void remove(RemovalReason reason) {
	if (isExplosive) {
	    Explosion ex = new Explosion(level, this, DamageSourceShrapnel.INSTANCE, null, getX(), getY(), getZ(), 2, true, BlockInteraction.DESTROY);
	    if (!level.isClientSide) {
		int explosionRadius = 2;
		for (int i = -explosionRadius; i <= explosionRadius; i++) {
		    for (int j = -explosionRadius; j <= explosionRadius; j++) {
			for (int k = -explosionRadius; k <= explosionRadius; k++) {
			    int idistance = i * i + j * j + k * k;
			    if (idistance <= explosionRadius * explosionRadius && level.random.nextFloat()
				    * (explosionRadius * explosionRadius) < explosionRadius * explosionRadius * 1.85 - idistance) {
				BlockPos pos = new BlockPos(getX() + i, getY() + j, getZ() + k);
				BlockState block = level.getBlockState(pos);
				if (block != Blocks.AIR.defaultBlockState() && block != Blocks.VOID_AIR.defaultBlockState()
					&& block.getExplosionResistance(level, pos, ex) < explosionRadius * 2 * level.random.nextFloat()) {
				    block.onBlockExploded(level, pos, ex);
				}
			    }
			}
		    }
		}
	    }
	    ex.finalizeExplosion(true);
	}
	super.remove(reason);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
	isExplosive = compound.getBoolean("type");
    }

    @Override
    public Packet<?> getAddEntityPacket() {
	return NetworkHooks.getEntitySpawningPacket(this);
    }
}
