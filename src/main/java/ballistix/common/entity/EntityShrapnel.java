package ballistix.common.entity;

import java.util.List;

import javax.annotation.Nullable;

import ballistix.api.damage.DamageSourceShrapnel;
import ballistix.registers.BallistixEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Explosion.Mode;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntityShrapnel extends ThrowableEntity {

	private static final DataParameter<Boolean> ISEXPLOSIVE = EntityDataManager.defineId(EntityShrapnel.class, DataSerializers.BOOLEAN);
	public boolean isExplosive = false;

	public EntityShrapnel(EntityType<? extends EntityShrapnel> type, World worldIn) {
		super(type, worldIn);
	}

	public EntityShrapnel(World worldIn) {
		this(BallistixEntities.ENTITY_SHRAPNEL.get(), worldIn);
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
		EntitySize size = getDimensionsForge(Pose.STANDING);
		setBoundingBox(new AxisAlignedBB(getX() - size.width * 2, getY() - size.height * 2, getZ() - size.width * 2, getX() + size.width * 2, getY() + size.height * 2, getZ() + size.width * 2));
		if (onGround || tickCount > (isExplosive ? 400 : 100) || level.getBlockState(blockPosition()).getMaterial().blocksMotion()) {
			remove(false);
		}
		if (!level.isClientSide) {
			List<LivingEntity> livings = level.getEntitiesOfClass(LivingEntity.class, getBoundingBox());
			for (LivingEntity living : livings) {
				living.hurt(DamageSourceShrapnel.INSTANCE, 10);
				remove(false);
			}
		}
	}

	@Override
	public void shootFromRotation(@Nullable Entity entity, float pitch, float yaw, float par4, float force, float par6) {
		float f = -MathHelper.sin(yaw * ((float) Math.PI / 180F)) * MathHelper.cos(pitch * ((float) Math.PI / 180F));
		float f1 = -MathHelper.sin((pitch + par4) * ((float) Math.PI / 180F));
		float f2 = MathHelper.cos(yaw * ((float) Math.PI / 180F)) * MathHelper.cos(pitch * ((float) Math.PI / 180F));
		shoot(f, f1, f2, force, par6);
	}

	@Override
	protected void addAdditionalSaveData(CompoundNBT compound) {
		compound.putBoolean("type", isExplosive);
	}

	@Override
	public void remove(boolean reason) {
		if (isExplosive) {
			level.explode(this, DamageSourceShrapnel.INSTANCE, null, getX(), getY(), getZ(), 3, true, Mode.DESTROY);
		}
		super.remove(reason);
	}

	@Override
	protected void readAdditionalSaveData(CompoundNBT compound) {
		isExplosive = compound.getBoolean("type");
	}

	@Override
	public IPacket<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
