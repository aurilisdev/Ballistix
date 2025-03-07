package ballistix.common.entity;

import java.util.UUID;

import javax.annotation.Nullable;

import ballistix.api.missile.MissileManager;
import ballistix.api.missile.virtual.VirtualProjectile;
import ballistix.registers.BallistixEntities;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class EntityRailgunRound extends Entity {

	private static final float RAD2DEG = (float) (180.0F / Math.PI);

	private static final EntityDataAccessor<Float> SPEED = SynchedEntityData.defineId(EntityRailgunRound.class, EntityDataSerializers.FLOAT);

	@Nullable
	public UUID id;
	public float speed = 0.0F;

	public EntityRailgunRound(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	public EntityRailgunRound(Level level) {
		this(BallistixEntities.ENTITY_RAILGUNROUND.get(), level);
	}

	@Override
	public void tick() {
		Level level = level();

		boolean isClient = level.isClientSide();

		boolean isServer = !isClient;

		if (tickCount > 30 && getDeltaMovement().length() <= 0) {
			if (isServer) {
				removeAfterChangingDimensions();
			}
			return;
		}

		if (isServer) {
			if (id == null) {
				removeAfterChangingDimensions();
				return;
			}

			VirtualProjectile.VirtualRailgunRound railgunround = MissileManager.getRailgunRound(level.dimension(), id);

			if (railgunround == null) {
				removeAfterChangingDimensions();
				return;
			}

			if (railgunround.hasExploded()) {
				removeAfterChangingDimensions();
				return;
			}

			if (!blockPosition().equals(railgunround.blockPosition())) {
				setPos(railgunround.position);
				setDeltaMovement(railgunround.deltaMovement);
				speed = railgunround.speed;
			}

		}

		if (isServer) {
			entityData.set(SPEED, speed);
		} else {
			speed = entityData.get(SPEED);
		}

		setPos(new Vec3(getX() + getDeltaMovement().x * speed, getY() + getDeltaMovement().y * speed, getZ() + getDeltaMovement().z * speed));

		setXRot((float) (Math.atan(getDeltaMovement().y() / Math.sqrt(getDeltaMovement().x() * getDeltaMovement().x() + getDeltaMovement().z() * getDeltaMovement().z())) * RAD2DEG));
		setYRot((float) (Math.atan2(getDeltaMovement().x(), getDeltaMovement().z()) * RAD2DEG));

	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		builder.define(SPEED, 0.0F);
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
		UUIDUtil.CODEC.decode(NbtOps.INSTANCE, compound.getCompound("id")).ifSuccess(pair -> id = pair.getFirst());
		compound.putFloat("speed", speed);
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {
		if (level() instanceof ServerLevel server && (!server.isPositionEntityTicking(blockPosition()) || !server.hasChunkAt(blockPosition()))) {
			setRemoved(RemovalReason.DISCARDED);
		}
		if (id != null) {
			UUIDUtil.CODEC.encode(id, NbtOps.INSTANCE, new CompoundTag()).ifSuccess(tag -> compound.put("id", tag));
		}
		speed = compound.getFloat("speed");
	}

	@Override
	public void remove(RemovalReason reason) {
		if (!level().isClientSide) {
			if (id != null) {
				VirtualProjectile.VirtualRailgunRound missile = MissileManager.getRailgunRound(level().dimension(), id);
				if (missile != null)
					missile.setSpawned(false, -1);
			}
		}
		super.remove(reason);
	}

	@Override
	public boolean isAlwaysTicking() {
		return true;
	}

}
