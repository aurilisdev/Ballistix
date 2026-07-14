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
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntityBallistixFallingBlock extends ThrowableEntity implements IEntityAdditionalSpawnData {

	private BlockState blockState = Blocks.SAND.defaultBlockState();
	public int time;
	public boolean dropItem = true;
	private boolean hurtEntities;
	private int fallDamageMax = 40;
	private float fallDamageAmount = 2.0F;
	public CompoundNBT blockData;
	protected static final DataParameter<BlockPos> DATA_START_POS = EntityDataManager.defineId(FallingBlockEntity.class, DataSerializers.BLOCK_POS);

	public EntityBallistixFallingBlock(EntityType<? extends ThrowableEntity> entityType, World level) {
		super(entityType, level);
	}

	public EntityBallistixFallingBlock(World world, double x, double y, double z, BlockState blockState) {
		this(BallistixEntities.ENTITY_BALLISTIXFALLINGBLOCK.get(), world);
		this.blockState = blockState;
		this.blocksBuilding = true;
		this.hurtEntities = true;
		this.setPos(x, y + (1.0F - this.getBbHeight()) / 2.0F, z);
		this.setDeltaMovement(Vector3d.ZERO);
		this.xo = x;
		this.yo = y;
		this.zo = z;
		this.setStartPos(this.blockPosition());
	}

	@Override
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

	@Override
	protected boolean isMovementNoisy() {
		return false;
	}

	@Override
	protected void defineSynchedData() {
		this.entityData.define(DATA_START_POS, BlockPos.ZERO);
	}

	@Override
	public boolean isPickable() {
		return !this.removed;
	}

	@Override
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

	@Override
	public boolean causeFallDamage(float fallDistance, float multiplier) {
		if (this.hurtEntities) {
			int i = MathHelper.ceil(fallDistance - 1.0F);
			if (i > 0) {
				List<Entity> list = Lists.newArrayList(this.level.getEntities(this, this.getBoundingBox()));
				boolean flag = this.blockState.is(BlockTags.ANVIL);
				DamageSource damagesource = flag ? DamageSource.ANVIL : DamageSource.FALLING_BLOCK;

				for (Entity entity : list) {
					entity.hurt(damagesource, Math.min(MathHelper.floor(i * this.fallDamageAmount), this.fallDamageMax));
				}
			}
		}

		return false;
	}

	@Override
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

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean displayFireAnimation() {
		return false;
	}

	@Override
	public void fillCrashReportCategory(CrashReportCategory report) {
		super.fillCrashReportCategory(report);
		report.setDetail("Immitating BlockState", this.blockState.toString());
	}

	public BlockState getBlockState() {
		return this.blockState;
	}

	@Override
	public boolean onlyOpCanSetNbt() {
		return true;
	}

	@Override
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
