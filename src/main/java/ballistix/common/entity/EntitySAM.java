package ballistix.common.entity;

import java.util.UUID;

import ballistix.api.missile.MissileManager;
import ballistix.api.missile.virtual.VirtualProjectile;
import net.minecraft.core.Rotations;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;

import ballistix.registers.BallistixEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

import com.mojang.math.Vector3f;

//want to keep this separate from the missiles since this thing has one job and one job only :D
public class EntitySAM extends Entity {

    private static final float RAD2DEG = (float) (180.0F / Math.PI);

    private static final EntityDataAccessor<Rotations> ROTATION = SynchedEntityData.defineId(EntityBullet.class, EntityDataSerializers.ROTATIONS);
	private static final EntityDataAccessor<Float> SPEED = SynchedEntityData.defineId(EntityBullet.class, EntityDataSerializers.FLOAT);

	public Vector3f rotation = new Vector3f(0, 0, 0);
	@Nullable
	public UUID id;
	public float speed = 0.0F;
	
    public EntitySAM(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public EntitySAM(Level level) {
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
                setPos(sam.position);
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

			setPos(new Vec3(getX() + getDeltaMovement().x, getY() + getDeltaMovement().y, getZ() + getDeltaMovement().z));

		}

		setYRot((float) Math.atan2(rotation.z(), rotation.x()) * RAD2DEG);
		setXRot((float) (Math.asin(rotation.y()) * RAD2DEG));


    }
    
    @Override
	protected void defineSynchedData() {
		entityData.define(SPEED, 0.0F);
		entityData.define(ROTATION, new Rotations(0, 0, 0));
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
		UUIDUtil.CODEC.decode(NbtOps.INSTANCE, compound.getCompound("id")).result().ifPresent(pair -> id = pair.getFirst());
		rotation = new Vector3f(compound.getFloat("xrot"), compound.getFloat("yrot"), compound.getFloat("zrot"));
		compound.putFloat("speed", speed);
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {
		if (level instanceof ServerLevel server && (!server.isPositionEntityTicking(blockPosition()) || !server.hasChunkAt(blockPosition()))) {
			setRemoved(RemovalReason.DISCARDED);
		}
		if (id != null) {
			UUIDUtil.CODEC.encode(id, NbtOps.INSTANCE, new CompoundTag()).result().ifPresent(tag -> compound.put("id", tag));
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
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (player.isSecondaryUseActive()) {
            return InteractionResult.PASS;
        }
        if (!this.level.isClientSide) {
            return player.startRiding(this, true) ? InteractionResult.CONSUME : InteractionResult.PASS;
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void remove(RemovalReason reason) {
        if (!level.isClientSide) {
            if (id != null) {
                VirtualProjectile.VirtualSAM missile = MissileManager.getSAM(level.dimension(), id);
                if (missile != null) missile.setSpawned(false, -1);
            }
        }
        super.remove(reason);
    }

    @Override
    public boolean isAlwaysTicking() {
        return true;
    }
    
    @Override
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

}
