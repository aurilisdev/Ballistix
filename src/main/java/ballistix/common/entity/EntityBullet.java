package ballistix.common.entity;

import java.util.UUID;

import javax.annotation.Nullable;

import ballistix.api.missile.MissileManager;
import ballistix.api.missile.virtual.VirtualProjectile;
import ballistix.registers.BallistixEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import voltaic.prefab.utilities.CodecUtils;

public class EntityBullet extends Entity {

    private static final float RAD2DEG = (float) (180.0F / Math.PI);

    private static final EntityDataAccessor<Float> SPEED = SynchedEntityData.defineId(EntityBullet.class, EntityDataSerializers.FLOAT);


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

            if ((bullet == null) || bullet.hasExploded()) {
                removeAfterChangingDimensions();
                return;
            }

            if (!blockPosition().equals(bullet.blockPosition())) {
                setPos(bullet.position);
                setDeltaMovement(bullet.deltaMovement);
                speed = bullet.speed;
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
    protected void defineSynchedData() {
        entityData.define(SPEED, 0.0F);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        CodecUtils.UUID_CODEC.decode(NbtOps.INSTANCE, compound.getCompound("id")).result().ifPresent(pair -> id = pair.getFirst());
        compound.putFloat("speed", speed);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        if (level instanceof ServerLevel server && (!server.isPositionEntityTicking(blockPosition()) || !server.hasChunkAt(blockPosition()))) {
            setRemoved(RemovalReason.DISCARDED);
        }
        if (id != null) {
            CodecUtils.UUID_CODEC.encode(id, NbtOps.INSTANCE, new CompoundTag()).result().ifPresent(tag -> compound.put("id", tag));
        }
        speed = compound.getFloat("speed");
    }

    @Override
    public void remove(RemovalReason reason) {
        if (!level.isClientSide) {
            if (id != null) {
                VirtualProjectile.VirtualBullet missile = MissileManager.getBullet(level.dimension(), id);
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
