package ballistix.common.entity;

import java.util.UUID;

import ballistix.api.missile.MissileManager;
import ballistix.api.missile.virtual.VirtualProjectile;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import org.joml.Vector3f;

import ballistix.registers.BallistixEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

public class EntityBullet extends Entity {

	private static final float RAD2DEG = (float) (180.0F / Math.PI);

	private static final EntityDataAccessor<Vector3f> ROTATION = SynchedEntityData.defineId(EntityBullet.class, EntityDataSerializers.VECTOR3);
	private static final EntityDataAccessor<Float> SPEED = SynchedEntityData.defineId(EntityBullet.class, EntityDataSerializers.FLOAT);

	public Vector3f rotation = new Vector3f(0, 0, 0);
	@Nullable
	public UUID id;
	public float speed = 0.0F;

	public EntityBullet(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	public EntityBullet(Level level) {
		this(BallistixEntities.ENTITY_BULLET.get(), level);
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

			VirtualProjectile.VirtualBullet bullet = MissileManager.getBullet(level.dimension(), id);

			if (bullet == null) {
				removeAfterChangingDimensions();
				return;
			}

			if (bullet.hasExploded()) {
				removeAfterChangingDimensions();
				return;
			}

			if (blockPosition().equals(bullet.blockPosition())) {
				setPos(bullet.position);
				setDeltaMovement(bullet.deltaMovement);
				speed = bullet.speed;
			}

		}

		if (isServer) {
			entityData.set(SPEED, speed);
			entityData.set(ROTATION, rotation);
		} else {
			speed = entityData.get(SPEED);
			rotation = entityData.get(ROTATION);
		}

		for (int i = 0; i < speed; i++) {

			setPos(new Vec3(getX() + getDeltaMovement().x, getY() + getDeltaMovement().y, getZ() + getDeltaMovement().z));

		}

		setYRot((float) Math.atan2(rotation.z, rotation.x) * RAD2DEG);
		setXRot((float) (Math.asin(rotation.y) * RAD2DEG));

	}

	@Override
	protected void defineSynchedData() {
		entityData.define(SPEED, 0.0F);
		entityData.define(ROTATION, new Vector3f(0, 0, 0));
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
		UUIDUtil.CODEC.decode(NbtOps.INSTANCE, compound.getCompound("id")).result().ifPresent(pair -> id = pair.getFirst());
		rotation = new Vector3f(compound.getFloat("xrot"), compound.getFloat("yrot"), compound.getFloat("zrot"));
		compound.putFloat("speed", speed);
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {
		if (level() instanceof ServerLevel server && (!server.isPositionEntityTicking(blockPosition()) || !server.hasChunkAt(blockPosition()))) {
			setRemoved(RemovalReason.DISCARDED);
		}
		if (id != null) {
			UUIDUtil.CODEC.encode(id, NbtOps.INSTANCE, new CompoundTag()).result().ifPresent(tag -> compound.put("id", tag));
		}
		compound.putFloat("xrot", rotation.x);
		compound.putFloat("yrot", rotation.y);
		compound.putFloat("zrot", rotation.z);
		speed = compound.getFloat("speed");
	}

	@Override
	public void remove(RemovalReason reason) {
		if (!level().isClientSide) {
			if (id != null) {
				VirtualProjectile.VirtualBullet missile = MissileManager.getBullet(level().dimension(), id);
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

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

}
