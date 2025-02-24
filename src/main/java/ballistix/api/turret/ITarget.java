package ballistix.api.turret;

import ballistix.api.missile.virtual.VirtualMissile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public interface ITarget<T> {

    Vec3 getTargetLocation();

    public default BlockPos getTargetBlockPos() {
        return new BlockPos((int) Math.floor(getTargetLocation().x), (int) Math.floor(getTargetLocation().y), (int) Math.floor(getTargetLocation().z));
    }

    float getTargetSpeed();

    Vec3 getTargetMovement();

    T getTarget();

    public static record TargetMissile(VirtualMissile missile) implements ITarget<VirtualMissile> {

        @Override
        public Vec3 getTargetLocation() {
            return missile.position;
        }

        @Override
        public float getTargetSpeed() {
            return missile.speed;
        }

        @Override
        public Vec3 getTargetMovement() {
            return missile.deltaMovement;
        }

        @Override
        public VirtualMissile getTarget() {
            return missile;
        }
    }

    public static record TargetLivingEntity(LivingEntity entity) implements ITarget<LivingEntity> {

        @Override
        public Vec3 getTargetLocation() {
            return new Vec3(entity.getX(), entity.getY() + entity.getBbHeight() * 0.75, entity.getZ());
        }

        @Override
        public float getTargetSpeed() {
            return 1.0F;
        }

        @Override
        public Vec3 getTargetMovement() {
            return entity.getDeltaMovement();
        }

        @Override
        public LivingEntity getTarget() {
            return entity;
        }
    }
}
