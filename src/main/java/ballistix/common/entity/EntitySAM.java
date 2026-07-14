package ballistix.common.entity;

import java.util.UUID;

import javax.annotation.Nullable;

import ballistix.api.missile.MissileManager;
import ballistix.api.missile.virtual.VirtualProjectile;
import ballistix.client.particle.ParticleOptionsMissileSmoke;
import ballistix.common.settings.BallistixConstants;
import ballistix.registers.BallistixEntities;
import net.minecraft.client.Minecraft;
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
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;
import voltaic.Voltaic;
import voltaic.prefab.utilities.CodecUtils;

//want to keep this separate from the missiles since this thing has one job and one job only :D
public class EntitySAM extends Entity {

    private static final float RAD2DEG = (float) (180.0F / Math.PI);

    private static final DataParameter<Float> SPEED = EntityDataManager.defineId(EntitySAM.class, DataSerializers.FLOAT);
    private static final DataParameter<Integer> VARIANT = EntityDataManager.defineId(EntitySAM.class, DataSerializers.INT);

    @Nullable
    public UUID id;
    public float speed = 0.0F;
    public int variant = 0;

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

            if (sam == null || sam.hasExploded()) {
                removeAfterChangingDimensions();
                return;
            }

            if (!blockPosition().equals(sam.blockPosition()) || !getDeltaMovement().equals(sam.deltaMovement)) {
                setPos(sam.position.x, sam.position.y, sam.position.z);
                speed = sam.speed;
                setDeltaMovement(sam.deltaMovement);
            }



        }

        if (isServer) {
            entityData.set(SPEED, speed);
            entityData.set(VARIANT, variant);
        } else {
            speed = entityData.get(SPEED);
            variant = entityData.get(VARIANT);
        }

        setPos(getX() + getDeltaMovement().x * speed, getY() + getDeltaMovement().y * speed, getZ() + getDeltaMovement().z * speed);

        xRot = (float) (Math.atan(getDeltaMovement().y() / Math.sqrt(getDeltaMovement().x() * getDeltaMovement().x() + getDeltaMovement().z() * getDeltaMovement().z())) * RAD2DEG);
        yRot = (float) (Math.atan2(getDeltaMovement().x(), getDeltaMovement().z()) * RAD2DEG);

        float topSpeed = variant == 0 ? BallistixConstants.SAM_TOP_SPEED : BallistixConstants.ANTIBALLISTICMISSILE_TOP_SPEED;

        if(speed < topSpeed) {
            speed += variant == 0 ? BallistixConstants.SAM_ACCELERATION : BallistixConstants.ANTIBALLISTICMISSILE_ACCELERATION;
        }
        
        if (isServer || speed >= 3.0F) {
            return;
        }

        float x = (float) getX();
        float y = (float) getY();
        float z = (float) getZ();
        float motionX = (float) (speed * getDeltaMovement().x);
        float motionY = (float) (speed * getDeltaMovement().y);
        float motionZ = (float) (speed * getDeltaMovement().z);
        x -= motionX;
        y -= motionY;
        z -= motionZ;
        for (int i = 0; i < (variant == 0 ? 2 : 4); i++) {
            Minecraft.getInstance().particleEngine.createParticle(new ParticleOptionsMissileSmoke().setParameters(1, 1, 1, variant == 0 ? 0.2F : 0.5f, 50, true), x, y, z, -motionX * (0.4 + 0.2 * Voltaic.RANDOM.nextDouble()), -motionY * (0.4 + 0.2 * Voltaic.RANDOM.nextDouble()), -motionZ * (0.4 + 0.2 * Voltaic.RANDOM.nextDouble()));
        }

    }

    @Override
    protected void defineSynchedData() {
        entityData.define(SPEED, 0.0F);
        entityData.define(VARIANT, 0);
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT compound) {
    	if (level instanceof ServerWorld && (!((ServerWorld) level).getChunkSource().isEntityTickingChunk(new ChunkPos(blockPosition())) || !level.hasChunkAt(blockPosition()))) {
            remove(false);
        }
        if (id != null) {
            CodecUtils.UUID_CODEC.encode(id, NBTDynamicOps.INSTANCE, new CompoundNBT()).result().ifPresent(tag -> compound.put("id", tag));
        }
        speed = compound.getFloat("speed");
        variant = compound.getInt("variant");
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT compound) {
        CodecUtils.UUID_CODEC.decode(NBTDynamicOps.INSTANCE, compound.getCompound("id")).result().ifPresent(pair -> id = pair.getFirst());
        compound.putFloat("speed", speed);
        compound.putInt("variant", variant);
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
    public void remove(boolean reason) {
        if (!level.isClientSide) {
            if (id != null) {
                VirtualProjectile.VirtualSAM missile = MissileManager.getSAM(level.dimension(), id);
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
