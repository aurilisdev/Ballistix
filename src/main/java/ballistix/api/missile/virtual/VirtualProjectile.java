package ballistix.api.missile.virtual;

import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import ballistix.api.missile.MissileManager;
import ballistix.api.silo.ILauncherPlatform;
import ballistix.api.silo.ILauncherSupportFrame;
import ballistix.client.particle.ParticleOptionsMissileSmoke;
import ballistix.common.entity.EntityBullet;
import ballistix.common.entity.EntityRailgunRound;
import ballistix.common.entity.EntitySAM;
import ballistix.common.settings.BallistixConfig;
import ballistix.common.tile.radar.TileFireControlRadar;
import ballistix.common.tile.turret.GenericTileTurret;
import ballistix.compatibility.TessellateCompat;
import ballistix.registers.BallistixDamageTypes;
import ballistix.registers.BallistixSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import voltaic.Voltaic;
import voltaic.api.multiblock.subnodebased.TileMultiSubnode;
import voltaic.prefab.utilities.BlockEntityUtils;

public abstract class VirtualProjectile {

	public volatile float speed;
	public volatile Vec3 position;
	public volatile Vec3 deltaMovement;
    public final float range;
    public final boolean canHitPlayers;
    public final UUID id;
	protected volatile boolean hasExploded = false;
	protected volatile boolean isSpawned = false;
	public volatile float distanceTraveled = 0.0F;
    protected int tickCount = 0;
	protected volatile int entityId = -1;
    private volatile boolean spawnPending;

    protected VirtualProjectile(float speed, Vec3 position, Vec3 deltaMovement, float range, boolean canHitPlayers,
	    float distanceTraveled, UUID id, boolean hasExploded, boolean isSpawned, int entityId) {
	this(speed, position, deltaMovement, range, canHitPlayers, id);
	this.distanceTraveled = distanceTraveled;
	this.hasExploded = hasExploded;
	this.isSpawned = isSpawned;
	this.entityId = entityId;
    }

    public VirtualProjectile(float speed, Vec3 position, Vec3 deltaMovement, float range, boolean canHitPlayers,
	    UUID id) {
	this.speed = speed;
	this.position = position;
	this.deltaMovement = deltaMovement;
	this.range = range;
	this.canHitPlayers = canHitPlayers;
	this.id = id;
    }

    // only ticks on server
    public void tick(ServerLevel level) {

	tickCount++;

	if (deltaMovement.length() <= 0) {
	    hasExploded = true;
	    return;
	}

	if (distanceTraveled >= range + 5) {
	    onReachMaxDistance(level);
	    hasExploded = true;
	    return;
	}

	if (hasExploded) {
	    return;
	}

	BlockPos projected = projectMovementForCollision(level);

	if (projected != null && !isInValidBlockstate(projected, level)) {
	    BlockState state = level.getBlockState(projected);

	    if (!state.getCollisionShape(level, projected).isEmpty()) {
		onHitBlock(level, projected);
		hasExploded = true;
		return;
	    }
	}

	updatePosition(level);

	AABB box = getBoundingBox().inflate(speed);

	for (VirtualMissile missile : MissileManager.getMissilesForLevel(level.dimension())) {

	    if (!missile.hasExploded() && missile.getBoundingBox().intersects(box)) {
		onHitMissile(level, missile);
		hasExploded = true;
		return;
	    }

	}

	if (canHitPlayers) {
	    LivingEntity selected = null;
	    double lastMag = 0;

	    for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, box)) {

		double deltaX = entity.getX() - position.x;
		double deltaY = entity.getY() - position.y;
		double deltaZ = entity.getZ() - position.z;

		double mag = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

		if (selected == null) {
		    selected = entity;
		    lastMag = mag;
		} else if (mag < lastMag) {
		    selected = entity;
		}

	    }

	    if (selected != null) {
		onHitLiving(level, selected);
		hasExploded = true;
		return;
	    }
	}

	distanceTraveled += speed;

	if (!spawnPending && !isSpawned && level.hasChunkAt(blockPosition())
		&& level.isPositionEntityTicking(blockPosition())) {
	    Entity entity = makeNewEntity(level);
	    spawnPending = true;
	    try {
		TessellateCompat.runOnMain(() -> {
		    try {
			if (!hasExploded && level.addFreshEntity(entity)) {
			    setSpawned(true, entity.getId());
			}
		    } finally {
			spawnPending = false;
		    }
		});
	    } catch (RuntimeException e) {
		spawnPending = false;
		throw e;
	    }
	}

	if (isSpawned && (!level.hasChunkAt(blockPosition()) || level.getEntity(entityId) == null)) {
	    setSpawned(false, -1);
	}
    }

    protected boolean isInValidBlockstate(BlockPos pos, ServerLevel world) {

	if (pos == null) {
	    return true;
	}

	BlockEntity blockentity = world.getBlockEntity(pos);

	return blockentity instanceof GenericTileTurret;

    }

    public void updatePosition(ServerLevel level) {
	position = new Vec3(position.x + deltaMovement.x * speed, position.y + deltaMovement.y * speed,
		position.z + deltaMovement.z * speed);
    }

    public abstract void onHitMissile(Level world, VirtualMissile missile);

    public abstract void onHitLiving(Level world, LivingEntity entity);

    public abstract void onHitBlock(Level world, BlockPos block);

    public void onReachMaxDistance(Level world) {

    }

    public abstract AABB getBoundingBox();

    public BlockPos blockPosition() {
	return new BlockPos((int) Math.floor(position.x), (int) Math.floor(position.y), (int) Math.floor(position.z));
    }

    public void setSpawned(boolean spawned, int id) {
	entityId = id;
	isSpawned = spawned;
    }

    public boolean hasExploded() {
	return hasExploded;
    }

    public abstract Entity makeNewEntity(Level world);

    @Nullable
    public BlockPos projectMovementForCollision(ServerLevel world) {

	Vec3 currPos = position.scale(1.0);

	int iterations = Math.abs((int) Math.ceil(speed));

	BlockPos pos;
	BlockState state;

	for (int i = 0; i < iterations; i++) {

	    pos = new BlockPos((int) Math.floor(currPos.x), (int) Math.floor(currPos.y), (int) Math.floor(currPos.z));
	    state = world.getBlockState(pos);

	    if (state.getCollisionShape(world, blockPosition()).isEmpty() || isInValidBlockstate(pos, world)) {
		currPos = currPos.add(deltaMovement);
		continue;
	    }

	    return pos;

	}

	return null;

    }

    public static class VirtualBullet extends VirtualProjectile {

	public static final Codec<VirtualBullet> CODEC = RecordCodecBuilder
		.create(instance -> instance
			.group(Codec.FLOAT.fieldOf("speed").forGetter(instance0 -> instance0.speed),
				Vec3.CODEC.fieldOf("position").forGetter(instance0 -> instance0.position),
				Vec3.CODEC.fieldOf("movement").forGetter(instance0 -> instance0.deltaMovement),
				Codec.FLOAT.fieldOf("range").forGetter(instance0 -> instance0.range),
				Codec.FLOAT.fieldOf("distancetraveled")
					.forGetter(instance0 -> instance0.distanceTraveled),
				UUIDUtil.CODEC.fieldOf("id").forGetter(instance0 -> instance0.id),
				Codec.BOOL.fieldOf("hasexploded").forGetter(instance0 -> instance0.hasExploded),
				Codec.BOOL.fieldOf("hasspawned").forGetter(instance0 -> instance0.isSpawned),
				Codec.INT.fieldOf("entityid").forGetter(instance0 -> instance0.entityId))
			.apply(instance, VirtualBullet::new));

	protected VirtualBullet(float speed, Vec3 position, Vec3 deltaMovement, float range, float distanceTraveled,
		UUID id, boolean hasExploded, boolean isSpawned, int entityId) {
	    super(speed, position, deltaMovement, range, true, distanceTraveled, id, hasExploded, isSpawned, entityId);
	}

	public VirtualBullet(float speed, Vec3 position, Vec3 deltaMovement, float range) {
	    super(speed, position, deltaMovement, range, true, UUID.randomUUID());
	}

	@Override
	public void onHitMissile(Level world, VirtualMissile missile) {
	    missile.health = missile.health - 1.0F;
	    if (missile.health <= 0) {
		world.playSound(null, blockPosition(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 2.0F,
			1.0F);
	    }
	}

	@Override
	public void onHitLiving(Level world, LivingEntity entity) {
	    entity.hurt(entity.damageSources().source(BallistixDamageTypes.CIWS_BULLET), 10);
	}

	@Override
	public void onHitBlock(Level world, BlockPos block) {

	}

	@Override
	public AABB getBoundingBox() {
	    return new AABB(position.x - 0.05F, position.y, position.z - 0.05F, position.x + 0.05F, position.y + 0.1F,
		    position.z + 0.05F);
	}

	@Override
	public Entity makeNewEntity(Level world) {
	    EntityBullet bullet = new EntityBullet(world);
	    bullet.setPos(position);
	    bullet.setDeltaMovement(deltaMovement);
	    bullet.id = id;
	    bullet.speed = speed;
	    return bullet;
	}
    }

    public static class VirtualRailgunRound extends VirtualProjectile {

	public static final Codec<VirtualRailgunRound> CODEC = RecordCodecBuilder
		.create(instance -> instance
			.group(Codec.FLOAT.fieldOf("speed").forGetter(instance0 -> instance0.speed),
				Vec3.CODEC.fieldOf("position").forGetter(instance0 -> instance0.position),
				Vec3.CODEC.fieldOf("movement").forGetter(instance0 -> instance0.deltaMovement),
				Codec.FLOAT.fieldOf("range").forGetter(instance0 -> instance0.range),
				Codec.FLOAT.fieldOf("distancetraveled")
					.forGetter(instance0 -> instance0.distanceTraveled),
				UUIDUtil.CODEC.fieldOf("id").forGetter(instance0 -> instance0.id),
				Codec.BOOL.fieldOf("hasexploded").forGetter(instance0 -> instance0.hasExploded),
				Codec.BOOL.fieldOf("hasspawned").forGetter(instance0 -> instance0.isSpawned),
				Codec.INT.fieldOf("entityid").forGetter(instance0 -> instance0.entityId))
			.apply(instance, VirtualRailgunRound::new));

	protected VirtualRailgunRound(float speed, Vec3 position, Vec3 deltaMovement, float range,
		float distanceTraveled, UUID id, boolean hasExploded, boolean isSpawned, int entityId) {
	    super(speed, position, deltaMovement, range, true, distanceTraveled, id, hasExploded, isSpawned, entityId);
	}

	public VirtualRailgunRound(float speed, Vec3 position, Vec3 deltaMovement, float range) {
	    super(speed, position, deltaMovement, range, true, UUID.randomUUID());
	}

	@Override
	public void onHitMissile(Level world, VirtualMissile missile) {
	    MissileManager.removeMissile(world.dimension(), missile.getId());
	    world.playSound(null, blockPosition(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 2.0F,
		    1.0F);
	}

	@Override
	public void onHitLiving(Level world, LivingEntity entity) {
	    entity.hurt(entity.damageSources().source(BallistixDamageTypes.RAILGUN_ROUND), 20);
	}

	@Override
	public void onHitBlock(Level world, BlockPos block) {
	    BlockState state = world.getBlockState(block);
	    if (state.getDestroySpeed(world, block) < 50.0F && !state.is(Blocks.BEDROCK)) {
		world.destroyBlock(block, false);
	    }
	    world.playSound(null, block, BallistixSounds.SOUND_RODHITTINGGROUND.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
	}

	@Override
	public AABB getBoundingBox() {
	    return new AABB(position.x - 0.05F, position.y, position.z - 0.05F, position.x + 0.05F, position.y + 0.1F,
		    position.z + 0.05F);
	}

	@Override
	public Entity makeNewEntity(Level world) {
	    EntityRailgunRound railgunround = new EntityRailgunRound(world);
	    railgunround.setPos(position);
	    railgunround.setDeltaMovement(deltaMovement);
	    railgunround.id = id;
	    railgunround.speed = speed;
	    return railgunround;
	}
    }

    public static class VirtualSAM extends VirtualProjectile {

	public static final Codec<VirtualSAM> CODEC = RecordCodecBuilder.create(instance -> instance
		.group(Codec.FLOAT.fieldOf("speed").forGetter(instance0 -> instance0.speed),
			Vec3.CODEC.fieldOf("position").forGetter(instance0 -> instance0.position),
			Vec3.CODEC.fieldOf("movement").forGetter(instance0 -> instance0.deltaMovement),
			Codec.FLOAT.fieldOf("range").forGetter(instance0 -> instance0.range),
			Codec.FLOAT.fieldOf("distancetraveled").forGetter(instance0 -> instance0.distanceTraveled),
			UUIDUtil.CODEC.fieldOf("id").forGetter(instance0 -> instance0.id),
			Codec.BOOL.fieldOf("hasexploded").forGetter(instance0 -> instance0.hasExploded),
			Codec.BOOL.fieldOf("hasspawned").forGetter(instance0 -> instance0.isSpawned),
			Codec.INT.fieldOf("entityid").forGetter(instance0 -> instance0.entityId),
			BlockPos.CODEC.fieldOf("radarpos").forGetter(instance0 -> instance0.radarPos),
			Codec.INT.fieldOf("variant").forGetter(instance0 -> instance0.variant),
			UUIDUtil.CODEC.optionalFieldOf("targetmissileid")
				.forGetter(instance0 -> Optional.ofNullable(instance0.targetMissileId)))
		.apply(instance, VirtualSAM::new));

	private BlockPos radarPos = BlockEntityUtils.OUT_OF_REACH;
	private final int variant;
	private TileFireControlRadar radar = null;
	@Nullable
	private UUID targetMissileId;

	protected VirtualSAM(float speed, Vec3 position, Vec3 deltaMovement, float range, float distanceTraveled,
		UUID id, boolean hasExploded, boolean isSpawned, int entityId, BlockPos radarPos, int variant,
		Optional<UUID> targetMissileId) {
	    super(speed, position, deltaMovement, range, true, distanceTraveled, id, hasExploded, isSpawned, entityId);
	    this.radarPos = radarPos;
	    this.variant = variant;
	    this.targetMissileId = targetMissileId.orElse(null);
	}

	public VirtualSAM(float speed, Vec3 position, Vec3 deltaMovement, float range, BlockPos radarPos, int variant,
		@Nullable UUID targetMissileId) {
	    super(speed, position, deltaMovement, range, false, UUID.randomUUID());
	    this.radarPos = radarPos;
	    this.variant = variant;
	    this.targetMissileId = targetMissileId;
	}

	@Override
	protected boolean isInValidBlockstate(BlockPos pos, ServerLevel world) {
	    if (variant == 0) {
		return super.isInValidBlockstate(pos, world);
	    }

	    if (pos == null) {
		return true;
	    }

	    BlockEntity blockentity = world.getBlockEntity(pos);

	    if (blockentity instanceof ILauncherPlatform || blockentity instanceof ILauncherSupportFrame) {
		return true;
	    }

	    if (blockentity instanceof TileMultiSubnode subnode) {
		BlockEntity owner = world.getBlockEntity(subnode.parentPos.getValue());

		return owner instanceof ILauncherPlatform || owner instanceof ILauncherSupportFrame;
	    }

	    return false;
	}

	@Override
	public void onHitMissile(Level world, VirtualMissile missile) {
	    if (Voltaic.RANDOM
		    .nextDouble() < (variant == 0 ? BallistixConfig.INSTANCE.SAM_CHANCE_TO_DESTROY.getAsDouble()
			    : BallistixConfig.INSTANCE.ANTIBALLISTICMISSILE_CHANCE_TO_DESTROY.getAsDouble())) {
		MissileManager.removeMissile(world.dimension(), missile.getId());
	    }
	    world.explode(null, null, null, position.x, position.y, position.z, 2.0F, false,
		    Level.ExplosionInteraction.BLOCK);
	}

	@Override
	public void onReachMaxDistance(Level world) {
	    world.explode(null, null, null, position.x, position.y, position.z, 2.0F, false,
		    Level.ExplosionInteraction.BLOCK, ParticleTypes.EXPLOSION, ParticleTypes.EXPLOSION_EMITTER,
		    SoundEvents.GENERIC_EXPLODE);
	}

	@Override
	public void onHitBlock(Level world, BlockPos block) {
	    world.explode(null, null, null, position.x, position.y, position.z, 2.0F, false,
		    Level.ExplosionInteraction.BLOCK, ParticleTypes.EXPLOSION, ParticleTypes.EXPLOSION_EMITTER,
		    SoundEvents.GENERIC_EXPLODE);
	}

	@Override
	public void onHitLiving(Level world, LivingEntity entity) {
	    world.explode(null, null, null, position.x, position.y, position.z, 2.0F, false,
		    Level.ExplosionInteraction.BLOCK, ParticleTypes.EXPLOSION, ParticleTypes.EXPLOSION_EMITTER,
		    SoundEvents.GENERIC_EXPLODE);
	}

	@Override
	public AABB getBoundingBox() {
	    return variant == 0 ?
	    //
		    new AABB(position.x - 0.25 * speed, position.y, position.z - 0.25F * speed,
			    position.x + 0.25F * speed, position.y + 1F * speed, position.z + 0.25F * speed)
		    :
		    //
		    new AABB(position.x - 0.25F * speed, position.y, position.z - 0.25F * speed,
			    position.x + 0.25F * speed, position.y + 2.0F * speed, position.z + 0.25F * speed);
	}

	@Override
	public Entity makeNewEntity(Level world) {
	    EntitySAM sam = new EntitySAM(world);
	    sam.setPos(position);
	    sam.setDeltaMovement(deltaMovement);
	    sam.id = id;
	    sam.speed = speed;
	    sam.variant = variant;
	    return sam;
	}

	@Override
	public void updatePosition(ServerLevel level) {

	    float topSpeed = (float) (variant == 0 ? BallistixConfig.INSTANCE.SAM_TOP_SPEED.getAsDouble()
		    : BallistixConfig.INSTANCE.ANTIBALLISTICMISSILE_TOP_SPEED.getAsDouble());

	    float minSpeed = (float) (variant == 0
		    ? topSpeed * BallistixConfig.INSTANCE.SAM_MINTURNSPEED_PERC.getAsDouble()
		    : topSpeed * BallistixConfig.INSTANCE.ANTIBALLISTICMISSILE_MINTURNSPEED_PERC.getAsDouble());

	    if (radarPos == null || radarPos.equals(BlockEntityUtils.OUT_OF_REACH) || speed < minSpeed) {
		super.updatePosition(level);
		return;
	    }

	    if (radar == null && level.getBlockEntity(radarPos) instanceof TileFireControlRadar radar) {
		this.radar = radar;
	    }

	    if (radar != null && radar.isRemoved()) {
		radar = null;
	    }

	    if (radar == null || radar.isRemoved() || radar.tracking == null || radar.tracking.hasExploded()) {
		super.updatePosition(level);
		return;
	    }

	    VirtualMissile tracking = null;

	    if (targetMissileId != null) {
		tracking = MissileManager.getMissile(level.dimension(), targetMissileId);
	    }

	    if ((tracking == null || tracking.hasExploded()) && radar != null) {
		tracking = radar.getTargetFor(blockPosition(), position, topSpeed);
	    }

	    if (tracking == null || tracking.hasExploded()) {
		super.updatePosition(level);
		return;
	    }

	    float trackingSpeed = tracking.speed;
	    Vec3 trackingVector = tracking.deltaMovement;
	    double timeToIntercept = TileFireControlRadar.getTimeToIntercept(tracking.position, trackingVector,
		    trackingSpeed, topSpeed, position);

	    if (timeToIntercept <= 0) {
		super.updatePosition(level);
		return;
	    }

	    Vec3 missileVelocity = trackingVector.scale(trackingSpeed);

	    Vec3 interceptionPos = tracking.position.add(missileVelocity.scale(timeToIntercept));

	    double deltaX = interceptionPos.x - position.x;
	    double deltaY = interceptionPos.y - position.y;
	    double deltaZ = interceptionPos.z - position.z;

	    Vec3 desiredVector = new Vec3(deltaX, deltaY, deltaZ).normalize();
	    Vec3 currVector = deltaMovement.normalize();

	    double dotProduct = desiredVector.dot(currVector);
	    double maxTurnRadians = variant == 0 ? BallistixConfig.INSTANCE.SAM_ENTITY_TURNINGSPEEDRADIANS.getAsDouble()
		    : BallistixConfig.INSTANCE.ANTIBALLISTICMISSILE_ENTITY_TURNINGSPEEDRADIANS.getAsDouble();

	    if (dotProduct != 0) {

		if (Math.acos(dotProduct) <= maxTurnRadians) {
		    deltaMovement = desiredVector;
		} else {

		    Vec3 perpVector = currVector.cross(desiredVector).cross(currVector).normalize();

		    Vec3 result = currVector.scale(Math.cos(maxTurnRadians))
			    .add(perpVector.scale(Math.sin(maxTurnRadians)));

		    deltaMovement = result.normalize();
		}

	    }

	    super.updatePosition(level);

	}

	@Override
	public void tick(ServerLevel level) {
	    super.tick(level);

	    float topSpeed = (float) (variant == 0 ? BallistixConfig.INSTANCE.SAM_TOP_SPEED.getAsDouble()
		    : BallistixConfig.INSTANCE.ANTIBALLISTICMISSILE_TOP_SPEED.getAsDouble());

	    if (speed < topSpeed) {
		speed += variant == 0 ? BallistixConfig.INSTANCE.SAM_ACCELERATION.get()
			: BallistixConfig.INSTANCE.ANTIBALLISTICMISSILE_ACCELERATION.get();
	    }

	    if (speed >= topSpeed) {
		return;
	    }

	    float x = (float) position.x;
	    float y = (float) position.y;
	    float z = (float) position.z;
	    float motionX = (float) (speed * deltaMovement.x);
	    float motionY = (float) (speed * deltaMovement.y);
	    float motionZ = (float) (speed * deltaMovement.z);
	    x -= motionX;
	    y -= motionY;
	    z -= motionZ;
	    for (int i = 0; i < 3; i++) {
		level.addParticle(new ParticleOptionsMissileSmoke().setParameters(1, 1, 1, 0.3f * 1, 50, true), x, y, z,
			-motionX * (0.4 + 0.2 * Voltaic.RANDOM.nextDouble()),
			-motionY * (0.4 + 0.2 * Voltaic.RANDOM.nextDouble()),
			-motionZ * (0.4 + 0.2 * Voltaic.RANDOM.nextDouble()));

	    }

	}
    }

}
