package ballistix.common.entity;

import java.util.UUID;

import javax.annotation.Nullable;

import ballistix.api.missile.MissileManager;
import ballistix.api.missile.virtual.VirtualMissile;
import ballistix.client.particle.ParticleOptionsMissileSmoke;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;
import voltaic.Voltaic;
import voltaic.prefab.utilities.BlockEntityUtils;
import voltaic.prefab.utilities.CodecUtils;

public class EntityMissile extends Entity {

	private static final DataParameter<Integer> MISSILE_TYPE = EntityDataManager.defineId(EntityMissile.class, DataSerializers.INT);
	private static final DataParameter<BlockPos> TARGET = EntityDataManager.defineId(EntityMissile.class, DataSerializers.BLOCK_POS);
	private static final DataParameter<Float> SPEED = EntityDataManager.defineId(EntityMissile.class, DataSerializers.FLOAT);
	private static final DataParameter<Float> START_X = EntityDataManager.defineId(EntityMissile.class, DataSerializers.FLOAT);
	private static final DataParameter<Float> START_Z = EntityDataManager.defineId(EntityMissile.class, DataSerializers.FLOAT);
	private static final DataParameter<Integer> FLIGHT_PATH = EntityDataManager.defineId(EntityMissile.class, DataSerializers.INT);
	private static final DataParameter<Boolean> CURRENTLYEXPLODING = EntityDataManager.defineId(EntityMissile.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> HASIGNIGHTED = EntityDataManager.defineId(EntityMissile.class, DataSerializers.BOOLEAN);

	public int missileType = -1;
	public float speed = 0.0F;
	@Nullable
	public UUID id;
	public int flightPath = 2;
	public boolean isExploding = false;
	public BlockPos target = BlockEntityUtils.OUT_OF_REACH;
	public float startX;
	public float startZ;
	private boolean hasIgnighted = false;

	public EntityMissile(EntityType<? extends EntityMissile> type, World worldIn) {
		super(type, worldIn);
		blocksBuilding = true;
	}

	public EntityMissile(World worldIn) {
		this(BallistixEntities.ENTITY_MISSILE.get(), worldIn);
	}

	@Override
	protected void defineSynchedData() {
		entityData.define(TARGET, BlockEntityUtils.OUT_OF_REACH);
		entityData.define(MISSILE_TYPE, -1);
		entityData.define(START_X, 0.0F);
		entityData.define(START_Z, 0.0F);
		entityData.define(SPEED, 0.0F);
		entityData.define(FLIGHT_PATH, 2);
		entityData.define(CURRENTLYEXPLODING, false);
		entityData.define(HASIGNIGHTED, false);
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
			entityData.set(FLIGHT_PATH, flightPath);
			entityData.set(CURRENTLYEXPLODING, isExploding);
			entityData.set(HASIGNIGHTED, hasIgnighted);

		} else {

			target = entityData.get(TARGET);
			missileType = entityData.get(MISSILE_TYPE);
			startX = entityData.get(START_X);
			startZ = entityData.get(START_Z);
			speed = entityData.get(SPEED);
			flightPath = entityData.get(FLIGHT_PATH);
			isExploding = entityData.get(CURRENTLYEXPLODING);
			hasIgnighted = entityData.get(HASIGNIGHTED);
		}
		if (isExploding) {
			return;
		}

		if (getDeltaMovement().length() > 0) {

			xRot = (float) (Math.atan(getDeltaMovement().y() / Math.sqrt(getDeltaMovement().x() * getDeltaMovement().x() + getDeltaMovement().z() * getDeltaMovement().z())) * 180.0D / Math.PI);
			yRot = (float) (Math.atan2(getDeltaMovement().x(), getDeltaMovement().z()) * 180.0D / Math.PI);

		}

		VirtualMissile.FlightPath path = VirtualMissile.FlightPath.values()[flightPath];

		if (path == VirtualMissile.FlightPath.SILO && missileType != -1) {

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

		} else if (path == VirtualMissile.FlightPath.VLS && missileType != -1) {
			if(!hasIgnighted && speed > -0.15) {
				speed -= 0.03F;
			} else if (!hasIgnighted) {
				hasIgnighted = true;
			} else if (speed > 0.5) {

				Vector3d desiredVector = new Vector3d(target.getX() - getX(), target.getY() - getY(), target.getZ() - getZ()).normalize();
				Vector3d currVector = getDeltaMovement().normalize();

				double dotProduct = desiredVector.dot(getDeltaMovement().normalize());
				double maxTurnRadians = 0.05;

				if(dotProduct != 0) {

					if(Math.acos(dotProduct) <= maxTurnRadians) {
						setDeltaMovement(desiredVector);
					} else {

						Vector3d perpVector = currVector.cross(desiredVector).cross(currVector).normalize();

						Vector3d result = currVector.scale(Math.cos(maxTurnRadians)).add(perpVector.scale(Math.sin(maxTurnRadians)));

						setDeltaMovement(result.normalize());


					}
				}
			}
		}

		if(tickCount != 0) {
			setPos(getX() + speed * getDeltaMovement().x, getY() + speed * getDeltaMovement().y, getZ() + speed * getDeltaMovement().z);
		}

		if ((path == VirtualMissile.FlightPath.SILO || (path == VirtualMissile.FlightPath.VLS && hasIgnighted)) && !target.equals(BlockEntityUtils.OUT_OF_REACH) && speed < 3.0F) {
			speed += 0.02F;
		}

		if (missileType == -1 || isServerSide || speed >= 3.0F || (path == VirtualMissile.FlightPath.VLS && !hasIgnighted)) {
			return;
		}

		float x = (float) (getX());
		float y = (float) (getY());
		float z = (float) (getZ());
		float motionX = (float) (speed * getDeltaMovement().x);
		float motionY = (float) (speed * getDeltaMovement().y);
		float motionZ = (float) (speed * getDeltaMovement().z);
		x -= motionX;
		y -= motionY;
		z -= motionZ;
		for (int i = 0; i < 4; i++) {
			Minecraft.getInstance().particleEngine.createParticle(new ParticleOptionsMissileSmoke().setParameters(1, 1, 1, missileType == 1 ? 0.3f : missileType == 2 ? 0.5f : 0.2f, 50, true), x, y, z, -motionX * (0.4 + 0.2 * Voltaic.RANDOM.nextDouble()), -motionY * (0.4 + 0.2 * Voltaic.RANDOM.nextDouble()), -motionZ * (0.4 + 0.2 * Voltaic.RANDOM.nextDouble()));
		}

	}

	@Override
	protected boolean canRide(Entity entityIn) {
		return true;
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
		compound.putInt("flightpath", flightPath);
		compound.putBoolean("hasignited", hasIgnighted);
	}

	@Override
	protected void readAdditionalSaveData(CompoundNBT compound) {
		missileType = compound.getInt("range");
		CodecUtils.UUID_CODEC.decode(NBTDynamicOps.INSTANCE, compound.getCompound("id")).result().ifPresent(pair -> id = pair.getFirst());
		BlockPos.CODEC.decode(NBTDynamicOps.INSTANCE, compound.getCompound("target")).result().ifPresent(pair -> target = pair.getFirst());
		startX = compound.getFloat("startx");
		startZ = compound.getFloat("startz");
		flightPath = compound.getInt("flightpath");
		hasIgnighted = compound.getBoolean("hasignited");
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
	public boolean isPickable() {
		return true;
	}

	@Override
	public void remove(boolean reason) {
		if (!level.isClientSide) {
			if (id != null) {
				VirtualMissile missile = MissileManager.getMissile(level.dimension(), id);
				if (missile != null)
					missile.setSpawned(false, -1);
			}
		}
		super.remove(reason);
	}
	
	@Override
	public IPacket<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

}
