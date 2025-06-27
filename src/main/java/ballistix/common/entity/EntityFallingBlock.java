package ballistix.common.entity;

import java.util.List;

import com.google.common.collect.Lists;

import ballistix.registers.BallistixEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.EndGatewayTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntityFallingBlock extends ThrowableEntity implements IEntityAdditionalSpawnData {

	private BlockState blockState = Blocks.SAND.defaultBlockState();
	public int time;
	public boolean dropItem = true;
	private boolean hurtEntities;
	private int fallDamageMax = 40;
	private float fallDamageAmount = 2.0F;
	public CompoundNBT blockData;
	protected static final DataParameter<BlockPos> DATA_START_POS = EntityDataManager.defineId(FallingBlockEntity.class, DataSerializers.BLOCK_POS);

	public EntityFallingBlock(EntityType<? extends EntityFallingBlock> entityType, World level) {
		super(entityType, level);
	}

	public EntityFallingBlock(World world, double x, double y, double z, BlockState blockState) {
		this(BallistixEntities.ENTITY_FALLINGBLOCK.get(), world);
		this.blockState = blockState;
		this.blocksBuilding = true;
		this.hurtEntities = true;
		this.setPos(x, y + (double) ((1.0F - this.getBbHeight()) / 2.0F), z);
		this.setDeltaMovement(Vector3d.ZERO);
		this.xo = x;
		this.yo = y;
		this.zo = z;
		this.setStartPos(this.blockPosition());
	}

	public boolean isAttackable() {
		return false;
	}

	public void setStartPos(BlockPos pos) {
		this.entityData.set(DATA_START_POS, pos);
	}

	@OnlyIn(Dist.CLIENT)
	public BlockPos getStartPos() {
		return this.entityData.get(DATA_START_POS);
	}

	protected boolean isMovementNoisy() {
		return false;
	}

	protected void defineSynchedData() {
		this.entityData.define(DATA_START_POS, BlockPos.ZERO);
	}

	public boolean isPickable() {
		return !this.removed;
	}

	public void tick() {
		RayTraceResult raytraceresult = ProjectileHelper.getHitResult(this, this::canHitEntity);
		boolean flag = false;
		if (raytraceresult.getType() == RayTraceResult.Type.BLOCK) {
			BlockPos blockpos = ((BlockRayTraceResult) raytraceresult).getBlockPos();
			BlockState blockstate = this.level.getBlockState(blockpos);
			if (blockstate.is(Blocks.NETHER_PORTAL)) {
				this.handleInsidePortal(blockpos);
				flag = true;
			} else if (blockstate.is(Blocks.END_GATEWAY)) {
				TileEntity tileentity = this.level.getBlockEntity(blockpos);
				if (tileentity instanceof EndGatewayTileEntity && EndGatewayTileEntity.canEntityTeleport(this)) {
					((EndGatewayTileEntity) tileentity).teleportEntity(this);
				}

				flag = true;
			}
		}

		if (raytraceresult.getType() != RayTraceResult.Type.MISS && !flag && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
			this.onHit(raytraceresult);
		}

		this.checkInsideBlocks();
		Vector3d vector3d = this.getDeltaMovement();
		double d2 = this.getX() + vector3d.x;
		double d0 = this.getY() + vector3d.y;
		double d1 = this.getZ() + vector3d.z;
		this.updateRotation();

		this.setDeltaMovement(vector3d.add(0, -0.04D, 0).multiply(0.9, 1, 0.9));
		if (!this.isNoGravity()) {
			Vector3d vector3d1 = this.getDeltaMovement();
			this.setDeltaMovement(vector3d1.x, vector3d1.y - (double) this.getGravity(), vector3d1.z);
		}

		this.setPos(d2, d0, d1);
	}

	protected void addAdditionalSaveData(CompoundNBT tag) {
		tag.put("BlockState", NBTUtil.writeBlockState(this.blockState));
		tag.putInt("Time", this.time);
		tag.putBoolean("DropItem", this.dropItem);
		tag.putBoolean("HurtEntities", this.hurtEntities);
		tag.putFloat("FallHurtAmount", this.fallDamageAmount);
		tag.putInt("FallHurtMax", this.fallDamageMax);
		if (this.blockData != null) {
			tag.put("TileEntityData", this.blockData);
		}

	}

	@Override
	protected void onHitBlock(BlockRayTraceResult hit) {
		BlockState state = level.getBlockState(hit.getBlockPos());

		if (!state.isAir(level, hit.getBlockPos())) {
			if (!level.isClientSide) {
				level.setBlockAndUpdate(blockPosition(), blockState);
			}
			remove(false);
		}
	}

	public boolean causeFallDamage(float fallDistance, float multiplier) {
		if (this.hurtEntities) {
			int i = MathHelper.ceil(fallDistance - 1.0F);
			if (i > 0) {
				List<Entity> list = Lists.newArrayList(this.level.getEntities(this, this.getBoundingBox()));
				boolean flag = this.blockState.is(BlockTags.ANVIL);
				DamageSource damagesource = flag ? DamageSource.ANVIL : DamageSource.FALLING_BLOCK;

				for (Entity entity : list) {
					entity.hurt(damagesource, (float) Math.min(MathHelper.floor((float) i * this.fallDamageAmount), this.fallDamageMax));
				}
			}
		}

		return false;
	}

	protected void readAdditionalSaveData(CompoundNBT tag) {
		this.blockState = NBTUtil.readBlockState(tag.getCompound("BlockState"));
		this.time = tag.getInt("Time");
		if (tag.contains("HurtEntities", 99)) {
			this.hurtEntities = tag.getBoolean("HurtEntities");
			this.fallDamageAmount = tag.getFloat("FallHurtAmount");
			this.fallDamageMax = tag.getInt("FallHurtMax");
		} else if (this.blockState.is(BlockTags.ANVIL)) {
			this.hurtEntities = true;
		}

		if (tag.contains("DropItem", 99)) {
			this.dropItem = tag.getBoolean("DropItem");
		}

		if (tag.contains("TileEntityData", 10)) {
			this.blockData = tag.getCompound("TileEntityData");
		}

		if (this.blockState.isAir()) {
			this.blockState = Blocks.SAND.defaultBlockState();
		}

	}

	@OnlyIn(Dist.CLIENT)
	public World getLevel() {
		return this.level;
	}

	public void setHurtsEntities(boolean shouldHurt) {
		this.hurtEntities = shouldHurt;
	}

	@OnlyIn(Dist.CLIENT)
	public boolean displayFireAnimation() {
		return false;
	}

	public void fillCrashReportCategory(CrashReportCategory report) {
		super.fillCrashReportCategory(report);
		report.setDetail("Immitating BlockState", this.blockState.toString());
	}

	public BlockState getBlockState() {
		return this.blockState;
	}

	public boolean onlyOpCanSetNbt() {
		return true;
	}

	public IPacket<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public void writeSpawnData(PacketBuffer buffer) {
		CompoundNBT data = new CompoundNBT();
		addAdditionalSaveData(data);
		buffer.writeNbt(data);

	}

	@Override
	public void readSpawnData(PacketBuffer additionalData) {
		readAdditionalSaveData(additionalData.readNbt());
	}
}
