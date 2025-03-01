package ballistix.common.entity;

import java.util.UUID;

import ballistix.api.missile.MissileManager;
import ballistix.api.missile.virtual.VirtualProjectile;

import ballistix.registers.BallistixEntities;
import electrodynamics.prefab.utilities.CodecUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Rotations;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

//want to keep this separate from the missiles since this thing has one job and one job only :D
public class EntitySAM extends Entity {

	private static final float RAD2DEG = (float) (180.0F / Math.PI);

	private static final DataParameter<Rotations> ROTATION = EntityDataManager.defineId(EntitySAM.class, DataSerializers.ROTATIONS);
	private static final DataParameter<Float> SPEED = EntityDataManager.defineId(EntitySAM.class, DataSerializers.FLOAT);

	public Vector3f rotation = new Vector3f(0, 0, 0);
	@Nullable
	public UUID id;
	public float speed = 0.0F;

	public EntitySAM(EntityType<?> entityType, World level) {
		super(entityType, level);
	}

	public EntitySAM(World level) {
		this(BallistixEntities.ENTITY_SAM.get(), level);
	}

	@Override
	public void tick() {

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

			VirtualProjectile.VirtualSAM sam = MissileManager.getSAM(level.dimension(), id);

			if (sam == null) {
				removeAfterChangingDimensions();
				return;
			}

			if (sam.hasExploded()) {
				removeAfterChangingDimensions();
				return;
			}

			if (blockPosition().equals(sam.blockPosition())) {
				setPos(sam.position.x, sam.position.y, sam.position.z);
				setDeltaMovement(sam.deltaMovement);
				speed = sam.speed;
			}

		}

		if (isServer) {
			entityData.set(SPEED, speed);
			entityData.set(ROTATION, new Rotations(rotation.x(), rotation.y(), rotation.z()));
		} else {
			speed = entityData.get(SPEED);
			Rotations rots = entityData.get(ROTATION);
			rotation = new Vector3f(rots.getX(), rots.getY(), rots.getZ());
		}

		for (int i = 0; i < speed; i++) {

			setPos(getX() + getDeltaMovement().x, getY() + getDeltaMovement().y, getZ() + getDeltaMovement().z);

		}

		yRot = (float) Math.atan2(rotation.z(), rotation.x()) * RAD2DEG;
		xRot = (float) (Math.asin(rotation.y()) * RAD2DEG);

	}

	@Override
	protected void defineSynchedData() {
		entityData.define(SPEED, 0.0F);
		entityData.define(ROTATION, new Rotations(0, 0, 0));
	}

	@Override
	protected void readAdditionalSaveData(CompoundNBT compound) {
		CodecUtils.UUID_CODEC.decode(NBTDynamicOps.INSTANCE, compound.getCompound("id")).result().ifPresent(pair -> id = pair.getFirst());
		rotation = new Vector3f(compound.getFloat("xrot"), compound.getFloat("yrot"), compound.getFloat("zrot"));
		compound.putFloat("speed", speed);
	}

	@Override
	protected void addAdditionalSaveData(CompoundNBT compound) {
		if (level instanceof ServerWorld && (!((ServerWorld) level).getChunkSource().isEntityTickingChunk(new ChunkPos(blockPosition())) || !((ServerWorld) level).hasChunkAt(blockPosition()))) {
			remove(false);
		}
		if (id != null) {
			CodecUtils.UUID_CODEC.encode(id, NBTDynamicOps.INSTANCE, new CompoundNBT()).result().ifPresent(tag -> compound.put("id", tag));
		}
		compound.putFloat("xrot", rotation.x());
		compound.putFloat("yrot", rotation.y());
		compound.putFloat("zrot", rotation.z());
		speed = compound.getFloat("speed");
	}

	@Override
	public boolean isPickable() {
		return true;
	}

	@Override
	protected boolean canRide(Entity vehicle) {
		return true;
	}

	@Override
	public void checkDespawn() {

	}

	@Override
	public ActionResultType interact(PlayerEntity player, Hand hand) {
		if (player.isSecondaryUseActive()) {
			return ActionResultType.PASS;
		}
		if (!this.level.isClientSide) {
			return player.startRiding(this, true) ? ActionResultType.CONSUME : ActionResultType.PASS;
		}
		return ActionResultType.SUCCESS;
	}

	@Override
	public void remove(boolean keepData) {
		if (!level.isClientSide) {
			if (id != null) {
				VirtualProjectile.VirtualSAM missile = MissileManager.getSAM(level.dimension(), id);
				if (missile != null)
					missile.setSpawned(false, -1);
			}
		}
		super.remove(keepData);
	}

	@Override
	public IPacket<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

}
