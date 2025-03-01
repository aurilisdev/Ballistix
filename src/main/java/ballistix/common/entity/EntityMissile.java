package ballistix.common.entity;

import java.util.Random;
import java.util.UUID;

import javax.annotation.Nullable;

import ballistix.Ballistix;
import ballistix.api.missile.MissileManager;
import ballistix.api.missile.virtual.VirtualMissile;
import ballistix.registers.BallistixEntities;
import electrodynamics.Electrodynamics;
import electrodynamics.prefab.utilities.CodecUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntityMissile extends Entity {

	private static final DataParameter<Integer> MISSILE_TYPE = EntityDataManager.defineId(EntityMissile.class, DataSerializers.INT);
	private static final DataParameter<BlockPos> TARGET = EntityDataManager.defineId(EntityMissile.class, DataSerializers.BLOCK_POS);
	private static final DataParameter<Float> SPEED = EntityDataManager.defineId(EntityMissile.class, DataSerializers.FLOAT);
	private static final DataParameter<Float> START_X = EntityDataManager.defineId(EntityMissile.class, DataSerializers.FLOAT);
	private static final DataParameter<Float> START_Z = EntityDataManager.defineId(EntityMissile.class, DataSerializers.FLOAT);
	private static final DataParameter<Boolean> IS_ITEM = EntityDataManager.defineId(EntityMissile.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> CURRENTLYEXPLODING = EntityDataManager.defineId(EntityMissile.class, DataSerializers.BOOLEAN);

	public int missileType = -1;
	public float speed = 0.0F;
	@Nullable
	public UUID id;
	public boolean isItem = false;
	public boolean isExploding = false;
	public BlockPos target = Ballistix.OUT_OF_REACH;
	public float startX;
	public float startZ;

	public EntityMissile(EntityType<? extends EntityMissile> type, World worldIn) {
		super(type, worldIn);
		blocksBuilding = true;
	}

	public EntityMissile(World worldIn) {
		this(BallistixEntities.ENTITY_MISSILE.get(), worldIn);
	}

	@Override
	protected void defineSynchedData() {
		entityData.define(TARGET, Ballistix.OUT_OF_REACH);
		entityData.define(MISSILE_TYPE, -1);
		entityData.define(START_X, 0.0F);
		entityData.define(START_Z, 0.0F);
		entityData.define(SPEED, 0.0F);
		entityData.define(IS_ITEM, true);
		entityData.define(CURRENTLYEXPLODING, false);
	}

	@Override
	public AxisAlignedBB getBoundingBoxForCulling() {
		return super.getBoundingBoxForCulling().expandTowards(20, 20, 20);
	}

	@Override
	public void tick() {
		boolean isClientSide = level.isClientSide;
		boolean isServerSide = !isClientSide;

		if (isServerSide) {
			if (id == null) {
				removeAfterChangingDimensions();
				return;
			}

			VirtualMissile missile = MissileManager.getMissile(level.dimension(), id);

			if (missile == null) {
				removeAfterChangingDimensions();
				return;
			}

			if (missile.hasExploded()) {
				removeAfterChangingDimensions();
				return;
			}
			if (missile.blastEntity != null) {
				isExploding = true;
			}

			if (!blockPosition().equals(missile.blockPosition())) {
				setPos(missile.position.x, missile.position.y, missile.position.z);
				setDeltaMovement(missile.deltaMovement);
				speed = missile.speed;
			}

		}

		if (isServerSide) {

			entityData.set(TARGET, target);
			entityData.set(MISSILE_TYPE, missileType);
			entityData.set(START_X, startX);
			entityData.set(START_Z, startZ);
			entityData.set(SPEED, speed);
			entityData.set(IS_ITEM, isItem);
			entityData.set(CURRENTLYEXPLODING, isExploding);

		} else {

			target = entityData.get(TARGET);
			missileType = entityData.get(MISSILE_TYPE);
			startX = entityData.get(START_X);
			startZ = entityData.get(START_Z);
			speed = entityData.get(SPEED);
			isItem = entityData.get(IS_ITEM);
			isExploding = entityData.get(CURRENTLYEXPLODING);
		}
		if (isExploding) {
			return;
		}

		if (getDeltaMovement().length() > 0) {

			xRot = (float) (Math.atan(getDeltaMovement().y() / Math.sqrt(getDeltaMovement().x() * getDeltaMovement().x() + getDeltaMovement().z() * getDeltaMovement().z())) * 180.0D / Math.PI);
			yRot = (float) (Math.atan2(getDeltaMovement().x(), getDeltaMovement().z()) * 180.0D / Math.PI);

		}

		if (!isItem) {

			float iDeltaX = target.getX() - startX;
			float iDeltaZ = target.getZ() - startZ;

			float initialDistance = (float) Math.sqrt(iDeltaX * iDeltaX + iDeltaZ * iDeltaZ);
			float halfwayDistance = initialDistance / 2.0F;

			float deltaX = (float) (getX() - startX);
			float deltaZ = (float) (getZ() - startZ);

			float distanceTraveled = (float) Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

			double maxRadii = VirtualMissile.MAX_CRUISING_ALTITUDE - VirtualMissile.ARC_TURN_HEIGHT_MIN;

			float turnRadius = (float) MathHelper.clamp(halfwayDistance, 0.001F, maxRadii);

			float deltaY = (float) (getY() - VirtualMissile.ARC_TURN_HEIGHT_MIN);

			float phi = 0;
			float signY = 1;

			if (halfwayDistance <= maxRadii) {

				if (getY() >= VirtualMissile.ARC_TURN_HEIGHT_MIN && distanceTraveled < halfwayDistance) {

					phi = (float) Math.asin(MathHelper.clamp(deltaY / turnRadius, 0, 1));

				} else if (distanceTraveled >= halfwayDistance) {

					phi = (float) Math.asin(MathHelper.clamp((initialDistance - distanceTraveled) / turnRadius, 0, 1));
					signY = -1;

				} else if (distanceTraveled >= initialDistance) {

					signY = -1;

				}

				float x = (float) ((iDeltaX / initialDistance) * Math.sin(phi));
				float z = (float) ((iDeltaZ / initialDistance) * Math.sin(phi));

				setDeltaMovement(x, Math.cos(phi) * signY, z);

			} else {

				if (getY() >= VirtualMissile.ARC_TURN_HEIGHT_MIN && distanceTraveled < halfwayDistance) {

					if (distanceTraveled <= turnRadius) {

						phi = (float) Math.asin(MathHelper.clamp(deltaY / turnRadius, 0, 1));

					} else {

						phi = (float) (Math.PI / 2.0);

					}

				} else if (distanceTraveled >= halfwayDistance) {

					if (distanceTraveled >= initialDistance - turnRadius) {

						phi = (float) Math.asin(MathHelper.clamp((initialDistance - distanceTraveled) / turnRadius, 0, 1));
						signY = -1;

					} else {

						phi = (float) (Math.PI / 2.0);

					}

				} else if (distanceTraveled >= initialDistance) {

					signY = -1;

				}

				float x = (float) (iDeltaX / initialDistance * Math.sin(phi));
				float z = (float) (iDeltaZ / initialDistance * Math.sin(phi));

				setDeltaMovement(x, Math.cos(phi) * signY, z);

			}

		}

		Vector3d vec = new Vector3d(getX() + speed * getDeltaMovement().x, getY() + speed * getDeltaMovement().y, getZ() + speed * getDeltaMovement().z);

		setPos(vec.x, vec.y, vec.z);

		if (!isItem && !target.equals(Ballistix.OUT_OF_REACH) && speed < 3.0F) {
			speed += 0.02F;
		}

		if (isServerSide || speed >= 3.0F) {
			return;
		}

		// exhaust only when missile is accelerating

		float widthOver2 = getDimensions(getPose()).width / 2.0F;

		for (int i = 0; i < 5; i++) {

			float x = (float) (getX() - widthOver2 + nextFloat(Electrodynamics.RANDOM, widthOver2));
			float y = (float) (getY() - nextFloat(Electrodynamics.RANDOM, 0.5F));
			float z = (float) (getZ() - widthOver2 + nextFloat(Electrodynamics.RANDOM, widthOver2));

			level.addParticle(ParticleTypes.LARGE_SMOKE, x, y, z, -speed * (getDeltaMovement().x + Electrodynamics.RANDOM.nextFloat()), -speed * (getDeltaMovement().y - 0.075f + Electrodynamics.RANDOM.nextFloat()), -speed * (getDeltaMovement().z + Electrodynamics.RANDOM.nextFloat()));

		}

		float motionX = (float) (-speed * getDeltaMovement().x);
		float motionY = (float) (-speed * getDeltaMovement().y);
		float motionZ = (float) (-speed * getDeltaMovement().z);
		for (int i = 0; i < 4; i++) {
			level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, false, this.getX(), this.getY(), this.getZ(), random.nextDouble() / 1.5 - 0.3333 + motionX, random.nextDouble() / 1.5 - 0.3333 + motionY, random.nextDouble() / 1.5 - 0.3333 + motionZ);
		}

		for (int i = 0; i < 4; i++) {
			level.addParticle(ParticleTypes.CLOUD, false, this.getX(), this.getY(), this.getZ(), random.nextDouble() / 1.5 - 0.3333 + motionX, random.nextDouble() / 1.5 - 0.3333 + motionY, random.nextDouble() / 1.5 - 0.3333 + motionZ);
		}

	}

	@Override
	protected boolean canRide(Entity entityIn) {
		return true;
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
	protected void addAdditionalSaveData(CompoundNBT compound) {
		if (level instanceof ServerWorld && (!((ServerWorld) level).getChunkSource().isEntityTickingChunk(new ChunkPos(blockPosition())) || !((ServerWorld) level).hasChunkAt(blockPosition()))) {
			remove(false);
		}
		compound.putInt("range", missileType);
		if (id != null) {
			CodecUtils.UUID_CODEC.encode(id, NBTDynamicOps.INSTANCE, new CompoundNBT()).result().ifPresent(tag -> compound.put("id", tag));
		}
		BlockPos.CODEC.encode(target, NBTDynamicOps.INSTANCE, new CompoundNBT()).result().ifPresent(tag -> compound.put("target", tag));
		compound.putFloat("startx", startX);
		compound.putFloat("startz", startZ);
		compound.putBoolean("isitem", isItem);
	}

	@Override
	protected void readAdditionalSaveData(CompoundNBT compound) {
		missileType = compound.getInt("range");
		CodecUtils.UUID_CODEC.decode(NBTDynamicOps.INSTANCE, compound.getCompound("id")).result().ifPresent(pair -> id = pair.getFirst());
		BlockPos.CODEC.decode(NBTDynamicOps.INSTANCE, compound.getCompound("target")).result().ifPresent(pair -> target = pair.getFirst());
		startX = compound.getFloat("startx");
		startZ = compound.getFloat("startz");
		isItem = compound.getBoolean("isitem");
	}

	@Override
	public void remove(boolean keepData) {
		if (!level.isClientSide) {
			if (id != null) {
				VirtualMissile missile = MissileManager.getMissile(level.dimension(), id);
				if (missile != null)
					missile.setSpawned(false, -1);
			}
		}
		super.remove(keepData);
	}

	@Override
	public IPacket<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	private float nextFloat(Random rand, float bound) {
		float r = rand.nextFloat();
		r = r * bound;
		if (r >= bound) { // may need to correct a rounding problem
			r = Float.intBitsToFloat(Float.floatToIntBits(bound) - 1);
		}
		return r;
	}

}
