package ballistix.common.entity;

import java.util.UUID;

import javax.annotation.Nullable;

import ballistix.api.missile.MissileManager;
import ballistix.api.missile.virtual.VirtualProjectile;
import ballistix.client.particle.ParticleOptionsMissileSmoke;
import ballistix.registers.BallistixEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import voltaic.Voltaic;

public class EntitySAM extends Entity {
    private static final float RAD2DEG = (float) (180.0F / Math.PI);
    private static final EntityDataAccessor<Float> SPEED = SynchedEntityData.defineId(EntitySAM.class,
	    EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(EntitySAM.class,
	    EntityDataSerializers.INT);

    @Nullable
    public UUID id;
    public float speed = 0.0F;
    public int variant = 0;

    public EntitySAM(EntityType<?> entityType, Level level) {
	super(entityType, level);
    }

    public EntitySAM(Level level) {
	this(BallistixEntities.ENTITY_SAM.get(), level);
    }

    private static final double MIN_YAW_HORIZONTAL_RATIO_SQR = 0.0025D;

    private void updateRotationFromMovement(Vec3 movement) {
	double horizontalSqr = movement.x * movement.x + movement.z * movement.z;
	double lengthSqr = horizontalSqr + movement.y * movement.y;

	if (lengthSqr <= 1.0E-7D) {
	    return;
	}

	double horizontal = Math.sqrt(horizontalSqr);

	setXRot((float) (Math.atan2(movement.y, horizontal) * RAD2DEG));

	// Near vertical, yaw is unstable and visually meaningless.
	// Keep the previous yaw instead of letting atan2 tiny x/z noise flicker it.
	if (horizontalSqr > lengthSqr * MIN_YAW_HORIZONTAL_RATIO_SQR) {
	    float targetYaw = (float) (Math.atan2(movement.x, movement.z) * RAD2DEG);
	    setYRot(unwrapYaw(targetYaw, getYRot()));
	}
    }

    private static float unwrapYaw(float yaw, float referenceYaw) {
	while (yaw - referenceYaw < -180.0F) {
	    yaw += 360.0F;
	}

	while (yaw - referenceYaw >= 180.0F) {
	    yaw -= 360.0F;
	}

	return yaw;
    }

    @Override
    public void tick() {
	    super.tick();

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

	    VirtualProjectile.VirtualSAM sam = MissileManager.getSAM(level.dimension(), id);

	    if (sam == null || sam.hasExploded()) {
		removeAfterChangingDimensions();
		return;
	    }

	    Vec3 movement = sam.deltaMovement;

	    setPos(sam.position);
	    setDeltaMovement(movement);
	    updateRotationFromMovement(movement);

	    speed = sam.speed;

	    entityData.set(SPEED, speed);
	    entityData.set(VARIANT, variant);

	    return;
	}

	speed = entityData.get(SPEED);
	variant = entityData.get(VARIANT);

	Vec3 movement = getDeltaMovement();

	setPos(new Vec3(getX() + movement.x * speed, getY() + movement.y * speed, getZ() + movement.z * speed));

	updateRotationFromMovement(movement);

	if (speed >= 3.0F) {
	    return;
	}

	float x = (float) getX();
	float y = (float) getY();
	float z = (float) getZ();

	float motionX = (float) (speed * movement.x);
	float motionY = (float) (speed * movement.y);
	float motionZ = (float) (speed * movement.z);

	x -= motionX;
	y -= motionY;
	z -= motionZ;

	for (int i = 0; i < (variant == 0 ? 2 : 4); i++) {
	    Minecraft.getInstance().particleEngine.createParticle(
		    new ParticleOptionsMissileSmoke().setParameters(1, 1, 1, variant == 0 ? 0.2F : 0.5F, 50, true), x,
		    y, z, -motionX * (0.4 + 0.2 * Voltaic.RANDOM.nextDouble()),
		    -motionY * (0.4 + 0.2 * Voltaic.RANDOM.nextDouble()),
		    -motionZ * (0.4 + 0.2 * Voltaic.RANDOM.nextDouble()));
	}
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
	builder.define(SPEED, 0.0F);
	builder.define(VARIANT, 0);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
	if (level() instanceof ServerLevel server
		&& (!server.isPositionEntityTicking(blockPosition()) || !server.hasChunkAt(blockPosition()))) {
	    setRemoved(RemovalReason.DISCARDED);
	}

	UUIDUtil.CODEC.decode(NbtOps.INSTANCE, compound.getCompound("id")).ifSuccess(pair -> id = pair.getFirst());

	speed = compound.getFloat("speed");
	variant = compound.getInt("variant");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
	if (id != null) {
	    UUIDUtil.CODEC.encode(id, NbtOps.INSTANCE, new CompoundTag()).ifSuccess(tag -> compound.put("id", tag));
	}

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
    public InteractionResult interact(Player player, InteractionHand hand) {
	if (player.isSecondaryUseActive()) {
	    return InteractionResult.PASS;
	}
	if (!this.level().isClientSide) {
	    return player.startRiding(this, true) ? InteractionResult.CONSUME : InteractionResult.PASS;
	}
	return InteractionResult.SUCCESS;
    }

    @Override
    public void remove(RemovalReason reason) {
	if (!level().isClientSide) {
	    if (id != null) {
		VirtualProjectile.VirtualSAM missile = MissileManager.getSAM(level().dimension(), id);
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
