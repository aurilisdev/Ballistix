package ballistix.api.turret;

import ballistix.api.missile.virtual.VirtualMissile;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

public interface ITarget<T> {

    Vector3d getTargetLocation();

    public default BlockPos getTargetBlockPos() {
        return new BlockPos((int) Math.floor(getTargetLocation().x), (int) Math.floor(getTargetLocation().y), (int) Math.floor(getTargetLocation().z));
    }

    float getTargetSpeed();

    Vector3d getTargetMovement();

    T getTarget();

    public static class TargetMissile implements ITarget<VirtualMissile> {
    	
    	private final VirtualMissile missile;
    	
    	public TargetMissile(VirtualMissile missile) {
    		this.missile = missile;
    	}

        @Override
        public Vector3d getTargetLocation() {
            return missile.position;
        }

        @Override
        public float getTargetSpeed() {
            return missile.speed;
        }

        @Override
        public Vector3d getTargetMovement() {
            return missile.deltaMovement;
        }

        @Override
        public VirtualMissile getTarget() {
            return missile;
        }
    }

    public static class TargetLivingEntity implements ITarget<LivingEntity> {
    	
    	private final LivingEntity entity;
    	
    	public TargetLivingEntity(LivingEntity entity) {
    		this.entity = entity;
    	}

        @Override
        public Vector3d getTargetLocation() {
            return new Vector3d(entity.getX(), entity.getY() + entity.getBbHeight() * 0.75, entity.getZ());
        }

        @Override
        public float getTargetSpeed() {
            return 1.0F;
        }

        @Override
        public Vector3d getTargetMovement() {
            return entity.getDeltaMovement();
        }

        @Override
        public LivingEntity getTarget() {
            return entity;
        }
    }
}
