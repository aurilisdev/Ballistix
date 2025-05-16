package ballistix.common.entity;

import java.util.List;

import javax.annotation.Nullable;

import ballistix.common.blast.Blast;
import ballistix.compatibility.griefdefender.GriefDefenderHandler;
import ballistix.registers.BallistixDamageTypes;
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
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraft.world.Explosion;

public class EntityShrapnel extends ThrowableEntity {
	
    private static final DataParameter<Boolean> ISEXPLOSIVE = EntityDataManager.defineId(EntityShrapnel.class, DataSerializers.BOOLEAN);
    public boolean isExplosive = false;

    private final Blast.GriefPreventionMethod griefPreventionMethod = Blast.getGriefPreventionMethod();

    public EntityShrapnel(EntityType<? extends EntityShrapnel> type, World worldIn) {
        super(type, worldIn);
    }

    public EntityShrapnel(World worldIn) {
        this(BallistixEntities.ENTITY_SHRAPNEL.get(), worldIn);
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
        EntitySize size = getDimensions(Pose.STANDING);
        setBoundingBox(new AxisAlignedBB(getX() - size.width * 2, getY() - size.height * 2, getZ() - size.width * 2, getX() + size.width * 2, getY() + size.height * 2, getZ() + size.width * 2));
        if (isOnGround() || tickCount > (isExplosive ? 400 : 100) || level.getBlockState(blockPosition()).getMaterial().blocksMotion()) {
            remove(false);
        }

        switch(griefPreventionMethod) {
            case GRIEF_DEFENDER:
                if(!GriefDefenderHandler.shouldHarmBlock(blockPosition())) {
                    if(!level.isClientSide) {
                        remove(false);
                    }
                    return;

                }
                break;
            default:
                break;
        }

        if (!level.isClientSide) {
            List<LivingEntity> livings = level.getEntitiesOfClass(LivingEntity.class, getBoundingBox());
            for (LivingEntity living : livings) {
                living.hurt(BallistixDamageTypes.SHRAPNEL, 10);
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
    protected void defineSynchedData() {
        entityData.define(ISEXPLOSIVE, false);
    }

    @Override
    public void remove(boolean reason) {
        if (isExplosive) {
            level.explode(this, BallistixDamageTypes.SHRAPNEL, null, getX(), getY(), getZ(), 3, true, Explosion.Mode.BREAK);
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
