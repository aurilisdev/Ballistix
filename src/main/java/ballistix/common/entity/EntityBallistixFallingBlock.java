package ballistix.common.entity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import ballistix.common.blast.util.Blast;
import ballistix.registers.BallistixEntities;
import net.minecraft.CrashReportCategory;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;

public class EntityBallistixFallingBlock extends ThrowableProjectile implements IEntityWithComplexSpawn {

    private BlockState blockState = Blocks.SAND.defaultBlockState();
    public int time;
    public boolean dropItem = true;
    private boolean hurtEntities;
    private int fallDamageMax = 40;
    private float fallDamageAmount = 2.0F;
    public CompoundTag blockData;
    protected static final EntityDataAccessor<BlockPos> DATA_START_POS = SynchedEntityData
	    .defineId(EntityBallistixFallingBlock.class, EntityDataSerializers.BLOCK_POS);
    private Set<BlockPos> whitelist = new HashSet<>();

    public EntityBallistixFallingBlock(EntityType<? extends EntityBallistixFallingBlock> entityType, Level level) {
	super(entityType, level);
    }

    public EntityBallistixFallingBlock(Level world, double x, double y, double z, BlockState blockState,
	    Set<BlockPos> whitelist, @Nullable Entity owner) {
	this(BallistixEntities.ENTITY_BALLISTIXFALLINGBLOCK.get(), world);
	this.blockState = blockState;
	blocksBuilding = true;
	hurtEntities = true;
	this.setPos(x, y + (1.0F - getBbHeight()) / 2.0F, z);
	this.setDeltaMovement(Vec3.ZERO);
	xo = x;
	yo = y;
	zo = z;
	setStartPos(blockPosition());
	this.whitelist = whitelist;
	setOwner(owner);
    }

    @Override
    public boolean isAttackable() {
	return false;
    }

    public void setStartPos(BlockPos pos) {
	entityData.set(DATA_START_POS, pos);
    }

    @OnlyIn(Dist.CLIENT)
    public BlockPos getStartPos() {
	return entityData.get(DATA_START_POS);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
	builder.define(DATA_START_POS, BlockPos.ZERO);
    }

    @Override
    public boolean isPickable() {
	return !isRemoved();
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
	super.addAdditionalSaveData(tag);
	tag.put("BlockState", NbtUtils.writeBlockState(blockState));
	tag.putInt("Time", time);
	tag.putBoolean("DropItem", dropItem);
	tag.putBoolean("HurtEntities", hurtEntities);
	tag.putFloat("FallHurtAmount", fallDamageAmount);
	tag.putInt("FallHurtMax", fallDamageMax);
	if (blockData != null) {
	    tag.put("TileEntityData", blockData);
	}
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
	if (whitelist.contains(result.getBlockPos()) ? tickCount > 5 : tickCount > 3) {
	    BlockState state = level().getBlockState(result.getBlockPos());

	    if (!state.isAir() && !state.liquid()) {
		if (!level().isClientSide && !state.is(Blocks.ANVIL)) {
		    if (Blast.canPlaceBlockState(level(), blockState, blockPosition(), state, getOwner())) {
			level().setBlockAndUpdate(blockPosition(), blockState);
		    }
		}
	    }
	    removeAfterChangingDimensions();
	}
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
	if (hurtEntities) {
	    int i = Mth.ceil(fallDistance - 1.0F);
	    if (i > 0) {
		List<Entity> list = Lists.newArrayList(level().getEntities(this, getBoundingBox()));
		boolean flag = blockState.is(BlockTags.ANVIL);

		for (Entity entity : list) {
		    entity.hurt(
			    flag ? entity.damageSources().source(DamageTypes.FALLING_ANVIL, getOwner())
				    : entity.damageSources().source(DamageTypes.FALLING_BLOCK, getOwner()),
			    Math.min(Mth.floor(i * fallDamageAmount), fallDamageMax));
		}
	    }
	}

	return false;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
	super.readAdditionalSaveData(tag);
	BlockState.CODEC.decode(NbtOps.INSTANCE, tag.get("BlockState")).ifSuccess(pair -> blockState = pair.getFirst());
	time = tag.getInt("Time");
	if (tag.contains("HurtEntities", 99)) {
	    hurtEntities = tag.getBoolean("HurtEntities");
	    fallDamageAmount = tag.getFloat("FallHurtAmount");
	    fallDamageMax = tag.getInt("FallHurtMax");
	} else if (blockState.is(BlockTags.ANVIL)) {
	    hurtEntities = true;
	}

	if (tag.contains("DropItem", 99)) {
	    dropItem = tag.getBoolean("DropItem");
	}

	if (tag.contains("TileEntityData", 10)) {
	    blockData = tag.getCompound("TileEntityData");
	}

	if (blockState.isAir()) {
	    blockState = Blocks.SAND.defaultBlockState();
	}

    }

    @Override
    protected double getDefaultGravity() {
	return 0.04;
    }

    public void setHurtsEntities(boolean shouldHurt) {
	hurtEntities = shouldHurt;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean displayFireAnimation() {
	return false;
    }

    @Override
    public void fillCrashReportCategory(CrashReportCategory report) {
	super.fillCrashReportCategory(report);
	report.setDetail("Immitating BlockState", blockState.toString());
    }

    public BlockState getBlockState() {
	return blockState;
    }

    @Override
    public boolean onlyOpCanSetNbt() {
	return true;
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
	CompoundTag tag = new CompoundTag();
	addAdditionalSaveData(tag);
	buffer.writeNbt(tag);
    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf buffer) {
	CompoundTag tag = buffer.readNbt();
	if (tag != null)
	    readAdditionalSaveData(tag);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
	return super.shouldRenderAtSqrDistance(distance);
    }

    @Override
    public boolean shouldRender(double x, double y, double z) {
	return super.shouldRender(x, y, z);
    }
}
