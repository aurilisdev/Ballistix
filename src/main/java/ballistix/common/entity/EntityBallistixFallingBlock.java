package ballistix.common.entity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;

import ballistix.registers.BallistixEntities;
import net.minecraft.CrashReportCategory;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;

public class EntityBallistixFallingBlock extends ThrowableProjectile implements IEntityAdditionalSpawnData {

    private BlockState blockState = Blocks.SAND.defaultBlockState();
    public int time;
    public boolean dropItem = true;
    private boolean hurtEntities;
    private int fallDamageMax = 40;
    private float fallDamageAmount = 2.0F;
    public CompoundTag blockData;
    protected static final EntityDataAccessor<BlockPos> DATA_START_POS = SynchedEntityData.defineId(EntityBallistixFallingBlock.class, EntityDataSerializers.BLOCK_POS);
    private Set<BlockPos> whitelist = new HashSet<BlockPos>();

    public EntityBallistixFallingBlock(EntityType<? extends EntityBallistixFallingBlock> entityType, Level level) {
        super(entityType, level);
    }

    public EntityBallistixFallingBlock(Level world, double x, double y, double z, BlockState blockState, Set<BlockPos> whitelist) {
        this(BallistixEntities.ENTITY_BALLISTIXFALLINGBLOCK.get(), world);
        this.blockState = blockState;
        this.blocksBuilding = true;
        this.hurtEntities = true;
        this.setPos(x, y + (double) ((1.0F - this.getBbHeight()) / 2.0F), z);
        this.setDeltaMovement(Vec3.ZERO);
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.setStartPos(this.blockPosition());
	this.whitelist = whitelist;

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
    protected void defineSynchedData() {
        entityData.define(DATA_START_POS, BlockPos.ZERO);
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.put("BlockState", NbtUtils.writeBlockState(this.blockState));
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
    protected void onHitBlock(BlockHitResult result) {
	if (whitelist.contains(result.getBlockPos()) ? tickCount > 5 : tickCount > 3) {
	    BlockState state = level().getBlockState(result.getBlockPos());

	    if (!state.isAir() && !state.liquid()) {
		if (!level().isClientSide && !state.is(Blocks.ANVIL)) {
		    level().setBlockAndUpdate(blockPosition(), blockState);
		}
		removeAfterChangingDimensions();
	    }
	}
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        if (this.hurtEntities) {
            int i = Mth.ceil(fallDistance - 1.0F);
            if (i > 0) {
                List<Entity> list = Lists.newArrayList(level().getEntities(this, this.getBoundingBox()));
                boolean flag = this.blockState.is(BlockTags.ANVIL);

                for (Entity entity : list) {
                    entity.hurt(flag ? entity.damageSources().anvil(entity) : entity.damageSources().fallingBlock(entity), (float) Math.min(Mth.floor((float) i * this.fallDamageAmount), this.fallDamageMax));
                }
            }
        }

        return false;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        BlockState.CODEC.decode(NbtOps.INSTANCE, tag.get("BlockState")).result().ifPresent(pair -> blockState = pair.getFirst());
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
    
    @Override
    protected float getGravity() {
    	return 0.04F;
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
    public void writeSpawnData(FriendlyByteBuf buffer) {
        CompoundTag tag = new CompoundTag();
        addAdditionalSaveData(tag);
        buffer.writeNbt(tag);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf buffer) {
        readAdditionalSaveData(buffer.readNbt());
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return super.shouldRenderAtSqrDistance(distance);
    }

    @Override
    public boolean shouldRender(double x, double y, double z) {
        return super.shouldRender(x, y, z);
    }
    
    @Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
    
}
