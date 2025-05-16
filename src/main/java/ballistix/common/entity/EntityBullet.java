package ballistix.common.entity;

import java.util.UUID;

import javax.annotation.Nullable;

import ballistix.api.missile.MissileManager;
import ballistix.api.missile.virtual.VirtualProjectile;
import ballistix.registers.BallistixEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;
import voltaic.prefab.utilities.CodecUtils;

public class EntityBullet extends Entity {

    private static final float RAD2DEG = (float) (180.0F / Math.PI);

    private static final DataParameter<Float> SPEED = EntityDataManager.defineId(EntityBullet.class, DataSerializers.FLOAT);


    @Nullable
    public UUID id;
    public float speed = 0.0F;

    public EntityBullet(EntityType<?> entityType, World level) {
        super(entityType, level);
    }

    public EntityBullet(World level) {
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

            if (bullet == null) {
                removeAfterChangingDimensions();
                return;
            }

            if (bullet.hasExploded()) {
                removeAfterChangingDimensions();
                return;
            }

            if (!blockPosition().equals(bullet.blockPosition())) {
                setPos(bullet.position.x, bullet.position.y, bullet.position.z);
                setDeltaMovement(bullet.deltaMovement);
                speed = bullet.speed;
            }

        }

        if (isServer) {
            entityData.set(SPEED, speed);
        } else {
            speed = entityData.get(SPEED);
        }

        setPos(getX() + getDeltaMovement().x * speed, getY() + getDeltaMovement().y * speed, getZ() + getDeltaMovement().z * speed);

        xRot = (float) (Math.atan(getDeltaMovement().y() / Math.sqrt(getDeltaMovement().x() * getDeltaMovement().x() + getDeltaMovement().z() * getDeltaMovement().z())) * RAD2DEG);
        yRot = (float) (Math.atan2(getDeltaMovement().x(), getDeltaMovement().z()) * RAD2DEG);


    }

    @Override
    protected void defineSynchedData() {
        entityData.define(SPEED, 0.0F);
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT compound) {
        CodecUtils.UUID_CODEC.decode(NBTDynamicOps.INSTANCE, compound.getCompound("id")).result().ifPresent(pair -> id = pair.getFirst());
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
        speed = compound.getFloat("speed");
    }

    @Override
    public void remove(boolean reason) {
        if (!level.isClientSide) {
            if (id != null) {
                VirtualProjectile.VirtualBullet missile = MissileManager.getBullet(level.dimension(), id);
                if (missile != null) missile.setSpawned(false, -1);
            }
        }
        super.remove(reason);
    }
    
    @Override
	public IPacket<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

}
