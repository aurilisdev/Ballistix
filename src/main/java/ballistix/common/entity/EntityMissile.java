package ballistix.common.entity;

import java.util.HashSet;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import ballistix.common.blast.Blast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.settings.Constants;
import ballistix.registers.BallistixEntities;
import electrodynamics.Electrodynamics;
import electrodynamics.prefab.utilities.BlockEntityUtils;
import electrodynamics.prefab.utilities.Scheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class EntityMissile extends Entity {

    public static final ConcurrentHashMap<ResourceKey<Level>, HashSet<EntityMissile>> MISSILES = new ConcurrentHashMap<>();

    private static final EntityDataAccessor<Integer> EXPLOSIVE_TYPE = SynchedEntityData.defineId(EntityMissile.class,
	    EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> MISSILE_TYPE = SynchedEntityData.defineId(EntityMissile.class,
	    EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_ITEM = SynchedEntityData.defineId(EntityMissile.class,
	    EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> ISSTUCK = SynchedEntityData.defineId(EntityMissile.class,
	    EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> HEALTH = SynchedEntityData.defineId(EntityMissile.class,
	    EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> START_X = SynchedEntityData.defineId(EntityMissile.class,
	    EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> START_Z = SynchedEntityData.defineId(EntityMissile.class,
	    EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> SPEED = SynchedEntityData.defineId(EntityMissile.class,
	    EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<BlockPos> TARGET = SynchedEntityData.defineId(EntityMissile.class,
	    EntityDataSerializers.BLOCK_POS);

    public static final int MAX_CRUISING_ALTITUDE = 300;
    public static final int WORLD_BUILD_HEIGHT = 320;
    public static final int ARC_TURN_HEIGHT_MIN = 150;
    private ChunkPos lastChunk;

    public BlockPos target = BlockEntityUtils.OUT_OF_REACH;
    public int blastOrdinal = -1;
    public int missileType = -1;
    public boolean isItem = false;
    private EntityBlast blastEntity = null;
    public boolean isStuck = false;
    public float speed;
    public float startX = 0;
    public float startZ = 0;
    public int frequency = 0;
    public float health = Constants.MISSILE_HEALTH;

    public EntityMissile(EntityType<? extends EntityMissile> type, Level worldIn) {
	super(type, worldIn);
	blocksBuilding = true;
    }

    public EntityMissile(Level worldIn) {
	this(BallistixEntities.ENTITY_MISSILE.get(), worldIn);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
	builder.define(EXPLOSIVE_TYPE, -1);
	builder.define(MISSILE_TYPE, -1);
	builder.define(ISSTUCK, -1);
	builder.define(START_X, -1.0F);
	builder.define(START_Z, -1.0F);
	builder.define(SPEED, -1.0F);
	builder.define(TARGET, BlockEntityUtils.OUT_OF_REACH);
	builder.define(IS_ITEM, false);
	builder.define(HEALTH, health);
    }

    private void releaseChunk() {
	ServerLevel serverWorld = (ServerLevel) this.level();
	ChunkPos chunkPos = new ChunkPos(this.blockPosition());
	serverWorld.setChunkForced(chunkPos.x, chunkPos.z, false);

    }

    private void forceLoadChunk() {
	ServerLevel serverWorld = (ServerLevel) this.level();
	ChunkPos chunkPos = new ChunkPos(this.blockPosition());
	for (int x = -1; x <= 1; x++) {
	    for (int z = -1; z <= 1; z++) {
		serverWorld.setChunkForced(chunkPos.x + x, chunkPos.z + z, true);
	    }
	}
    }

    @Override
    public void tick() {
	if (!this.level().isClientSide) {
	    ChunkPos newChunk = new ChunkPos(this.blockPosition());
	    if (!newChunk.equals(this.lastChunk)) {
		releaseChunk(); // Unload previous chunk
		forceLoadChunk(); // Load new chunk
		this.lastChunk = newChunk;
	    }
	}
	Level level = level();
	boolean isClientSide = level.isClientSide;
	boolean isServerSide = !isClientSide;

	if (isServerSide) {

	    entityData.set(EXPLOSIVE_TYPE, blastOrdinal);
	    entityData.set(MISSILE_TYPE, missileType);
	    entityData.set(ISSTUCK, isStuck ? 1 : -1);
	    entityData.set(START_X, startX);
	    entityData.set(START_Z, startZ);
	    entityData.set(SPEED, speed);
	    entityData.set(TARGET, target);
	    entityData.set(IS_ITEM, isItem);

	} else {

	    blastOrdinal = entityData.get(EXPLOSIVE_TYPE);
	    missileType = entityData.get(MISSILE_TYPE);
	    boolean old = isStuck;
	    isStuck = entityData.get(ISSTUCK) > 0;
	    if (isStuck != old) {
		setPos(getX() - speed * getDeltaMovement().x * 1, getY() - speed * getDeltaMovement().y * 1,
			getZ() - speed * getDeltaMovement().z * 1);
	    }
	    startX = entityData.get(START_X);
	    startZ = entityData.get(START_Z);
	    speed = entityData.get(SPEED);
	    target = entityData.get(TARGET);
	    isItem = entityData.get(IS_ITEM);
	}

	if (isServerSide && health <= 0) {
	    level().playSound(null, blockPosition(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 2.0F,
		    1.0F);
	    removeAfterChangingDimensions();
	    return;
	}

	if ((!isItem && target.equals(BlockEntityUtils.OUT_OF_REACH)) || blastOrdinal == -1) {
	    if (isServerSide) {
		removeAfterChangingDimensions();
	    }
	    return;
	}

	if (isStuck) {
	    if (isServerSide && blastEntity.getBlast().hasStarted) {
		removeAfterChangingDimensions();
	    }
	    return;
	}

	BlockState state = level.getBlockState(blockPosition());

	if (blastEntity != null) {
	    return;
	}

	if (getDeltaMovement().length() > 0 && !isStuck) {

	    setXRot((float) (Math.atan(getDeltaMovement().y() / Math.sqrt(
		    getDeltaMovement().x() * getDeltaMovement().x() + getDeltaMovement().z() * getDeltaMovement().z()))
		    * 180.0D / Math.PI));
	    setYRot((float) (Math.atan2(getDeltaMovement().x(), getDeltaMovement().z()) * 180.0D / Math.PI));

	}

	if (isServerSide) {

	    if (!state.getCollisionShape(level, blockPosition()).isEmpty()
		    && (isItem || getY() < target.getY() && getDeltaMovement().y() < 0 && tickCount > 20)) {

		SubtypeBlast explosive = SubtypeBlast.values()[blastOrdinal];

		setPos(getX() - speed * getDeltaMovement().x * 2, getY() - speed * getDeltaMovement().y * 2,
			getZ() - speed * getDeltaMovement().z * 2);

		Blast b = explosive.createBlast(level, blockPosition());

		if (b != null) {

		    blastEntity = b.performExplosion();

		    if (blastEntity == null) {

			removeAfterChangingDimensions();

		    } else {

			isStuck = true;

		    }
		}

	    }

	    if (!isItem && getY() >= ARC_TURN_HEIGHT_MIN) {
		float iDeltaX = target.getX() - startX;
		float iDeltaZ = target.getZ() - startZ;

		float initialDistance = (float) Math.sqrt(iDeltaX * iDeltaX + iDeltaZ * iDeltaZ);
		float halfwayDistance = initialDistance / 2.0F;

		float deltaX = (float) (getPosition().x - startX);
		float deltaZ = (float) (getPosition().z - startZ);

		float distanceTraveled = (float) Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

		double maxRadii = MAX_CRUISING_ALTITUDE - ARC_TURN_HEIGHT_MIN;

		float turnRadius = (float) Mth.clamp(halfwayDistance, 0.001F, maxRadii);

		float deltaY = (float) (getPosition().y - ARC_TURN_HEIGHT_MIN);

		float phi = 0;
		float signY = 1;

		if (halfwayDistance <= maxRadii) {

		    if (getPosition().y >= ARC_TURN_HEIGHT_MIN && distanceTraveled < halfwayDistance) {

			phi = (float) Math.asin(Mth.clamp(deltaY / turnRadius, 0, 1));

		    } else if (distanceTraveled >= halfwayDistance) {

			phi = (float) Math.asin(Mth.clamp((initialDistance - distanceTraveled) / turnRadius, 0, 1));
			signY = -1;

		    } else if (distanceTraveled >= initialDistance) {

			signY = -1;

		    }

		    float x = (float) ((iDeltaX / initialDistance) * Math.sin(phi));
		    float z = (float) ((iDeltaZ / initialDistance) * Math.sin(phi));

		    setDeltaMovement(new Vec3(x, Math.cos(phi) * signY, z));

		} else {

		    if (getPosition().y >= ARC_TURN_HEIGHT_MIN && distanceTraveled < halfwayDistance) {

			if (distanceTraveled <= turnRadius) {

			    phi = (float) Math.asin(Mth.clamp(deltaY / turnRadius, 0, 1));

			} else {

			    phi = (float) (Math.PI / 2.0);

			}

		    } else if (distanceTraveled >= halfwayDistance) {

			if (distanceTraveled >= initialDistance - turnRadius) {

			    phi = (float) Math.asin(Mth.clamp((initialDistance - distanceTraveled) / turnRadius, 0, 1));
			    signY = -1;

			} else {

			    phi = (float) (Math.PI / 2.0);

			}

		    } else if (distanceTraveled >= initialDistance) {

			signY = -1;

		    }

		    float x = (float) (iDeltaX / initialDistance * Math.sin(phi));
		    float z = (float) (iDeltaZ / initialDistance * Math.sin(phi));

		    setDeltaMovement(new Vec3(x, Math.cos(phi) * signY, z));

		}
	    }
	}

	if (isServerSide || state.getCollisionShape(level, blockPosition()).isEmpty()) {

	    setPos(getX() + speed * getDeltaMovement().x, getY() + speed * getDeltaMovement().y,
		    getZ() + speed * getDeltaMovement().z);

	}

	if (!isItem && !target.equals(BlockEntityUtils.OUT_OF_REACH) && speed < 3.0F) {
	    speed += 0.02F;
	}

	if (isServerSide || speed >= 3.0F) {
	    return;
	}

	// exhaust only when missile is accelerating

	float widthOver2 = getDimensions(getPose()).width() / 2.0F;

	for (int i = 0; i < 5; i++) {

	    float x = (float) (getX() - widthOver2 + Electrodynamics.RANDOM.nextFloat(widthOver2));
	    float y = (float) (getY() - Electrodynamics.RANDOM.nextFloat(0.5F));
	    float z = (float) (getZ() - widthOver2 + Electrodynamics.RANDOM.nextFloat(widthOver2));

	    level.addParticle(ParticleTypes.LARGE_SMOKE, x, y, z,
		    -speed * (getDeltaMovement().x + Electrodynamics.RANDOM.nextFloat()),
		    -speed * (getDeltaMovement().y - 0.075f + Electrodynamics.RANDOM.nextFloat()),
		    -speed * (getDeltaMovement().z + Electrodynamics.RANDOM.nextFloat()));

	}

	float motionX = (float) (-speed * getDeltaMovement().x);
	float motionY = (float) (-speed * getDeltaMovement().y);
	float motionZ = (float) (-speed * getDeltaMovement().z);
	for (int i = 0; i < 4; i++) {
	    level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, false, this.getX(), this.getY(), this.getZ(),
		    random.nextDouble() / 1.5 - 0.3333 + motionX, random.nextDouble() / 1.5 - 0.3333 + motionY,
		    random.nextDouble() / 1.5 - 0.3333 + motionZ);
	}

	for (int i = 0; i < 4; i++) {
	    level.addParticle(ParticleTypes.CLOUD, false, this.getX(), this.getY(), this.getZ(),
		    random.nextDouble() / 1.5 - 0.3333 + motionX, random.nextDouble() / 1.5 - 0.3333 + motionY,
		    random.nextDouble() / 1.5 - 0.3333 + motionZ);
	}

    }

    @Override
    protected boolean canRide(Entity entityIn) {
	return true;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
	compound.putInt("type", blastOrdinal);
	compound.putInt("range", missileType);
	compound.putBoolean("isItem", isItem);
	compound.put("target", NbtUtils.writeBlockPos(target));
	compound.putFloat("startx", startX);
	compound.putFloat("startz", startZ);
	compound.putFloat("speed", speed);
	compound.putInt("freq", frequency);
	compound.putFloat("health", health);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
	blastOrdinal = compound.getInt("type");
	missileType = compound.getInt("range");
	isItem = compound.getBoolean("isItem");
	blastOrdinal = compound.getInt("type");
	Optional<BlockPos> pos = NbtUtils.readBlockPos(compound, "target");
	target = pos.orElse(BlockEntityUtils.OUT_OF_REACH);
	startX = compound.getFloat("startx");
	startZ = compound.getFloat("startz");
	speed = compound.getFloat("speed");
	frequency = compound.getInt("freq");
	health = compound.getFloat("health");
    }

    @Override
    public void checkDespawn() {

    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
	if (player.isSecondaryUseActive()) {
	    return InteractionResult.PASS;
	} else {
	    if (!this.level().isClientSide) {
		return player.startRiding(this, true) ? InteractionResult.CONSUME : InteractionResult.PASS;
	    } else {
		return InteractionResult.SUCCESS;
	    }
	}
    }

    @Override
    public boolean isPickable() {
	return true;
    }

    @Override
    public void onAddedToLevel() {
	super.onAddedToLevel();
	HashSet<EntityMissile> set = MISSILES.getOrDefault(level().dimension(), new HashSet<>());
	set.add(this);
	MISSILES.put(level().dimension(), set);
    }

    @Override
    public void onRemovedFromLevel() {
	super.onRemovedFromLevel();
	HashSet<EntityMissile> set = MISSILES.getOrDefault(level().dimension(), new HashSet<>());
	set.remove(this);
	if (!this.level().isClientSide) {
	    releaseChunk();
	    Scheduler.schedule(1, this::forceLoadChunk);
	}
	// MISSILES.put(level().dimension(), set);
    }

    @Override
    public void remove(RemovalReason reason) {
	super.remove(reason);
	HashSet<EntityMissile> set = MISSILES.getOrDefault(level().dimension(), new HashSet<>());
	set.remove(this);

    }

    public Vec3 getPosition() {
	return new Vec3(getX(), getY(), getZ());
    }

}
