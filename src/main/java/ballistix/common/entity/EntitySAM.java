package ballistix.common.entity;

import java.util.UUID;

import javax.annotation.Nullable;

import ballistix.api.missile.MissileManager;
import ballistix.api.missile.virtual.VirtualProjectile;
import ballistix.client.particle.ParticleOptionsMissileSmoke;
import ballistix.common.settings.BallistixConstants;
import ballistix.registers.BallistixEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
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
import net.minecraftforge.network.NetworkHooks;
import voltaic.Voltaic;

//want to keep this separate from the missiles since this thing has one job and one job only :D
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

	    VirtualProjectile.VirtualSAM sam = MissileManager.getSAM(level.dimension(), id);

	    if ((sam == null) || sam.hasExploded()) {
		removeAfterChangingDimensions();
		return;
	    }

	    if (!blockPosition().equals(sam.blockPosition()) || !getDeltaMovement().equals(sam.deltaMovement)) {
		setPos(sam.position);
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

	setPos(new Vec3(getX() + getDeltaMovement().x * speed, getY() + getDeltaMovement().y * speed,
		getZ() + getDeltaMovement().z * speed));

	setXRot((float) (Math.atan(getDeltaMovement().y() / Math.sqrt(
		getDeltaMovement().x() * getDeltaMovement().x() + getDeltaMovement().z() * getDeltaMovement().z()))
		* RAD2DEG));
	setYRot((float) (Math.atan2(getDeltaMovement().x(), getDeltaMovement().z()) * RAD2DEG));

	float topSpeed = variant == 0 ? BallistixConstants.SAM_TOP_SPEED
		: BallistixConstants.ANTIBALLISTICMISSILE_TOP_SPEED;

	if (speed < topSpeed) {
	    speed += variant == 0 ? BallistixConstants.SAM_ACCELERATION
		    : BallistixConstants.ANTIBALLISTICMISSILE_ACCELERATION;
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
	    Minecraft.getInstance().particleEngine.createParticle(
		    new ParticleOptionsMissileSmoke().setParameters(1, 1, 1, variant == 0 ? 0.2F : 0.5f, 50, true), x,
		    y, z, -motionX * (0.4 + 0.2 * Voltaic.RANDOM.nextDouble()),
		    -motionY * (0.4 + 0.2 * Voltaic.RANDOM.nextDouble()),
		    -motionZ * (0.4 + 0.2 * Voltaic.RANDOM.nextDouble()));
	}

    }

    @Override
    protected void defineSynchedData() {
	entityData.define(SPEED, 0.0F);
	entityData.define(VARIANT, 0);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
	if (level() instanceof ServerLevel server
		&& (!server.isPositionEntityTicking(blockPosition()) || !server.hasChunkAt(blockPosition()))) {
	    setRemoved(RemovalReason.DISCARDED);
	}
	if (id != null) {
	    UUIDUtil.CODEC.encode(id, NbtOps.INSTANCE, new CompoundTag()).result()
		    .ifPresent(tag -> compound.put("id", tag));
	}
	speed = compound.getFloat("speed");
	variant = compound.getInt("variant");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
	UUIDUtil.CODEC.decode(NbtOps.INSTANCE, compound.getCompound("id")).result()
		.ifPresent(pair -> id = pair.getFirst());
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

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
	return NetworkHooks.getEntitySpawningPacket(this);
    }

}
