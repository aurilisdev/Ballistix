package ballistix.common.entity;

import java.util.UUID;

import javax.annotation.Nullable;

import ballistix.api.blast.IBlast;
import ballistix.api.entity.IDefusable;
import ballistix.api.entity.ITraceableEntity;
import ballistix.common.blast.util.Blast;
import ballistix.registers.BallistixEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.network.NetworkHooks;

public class EntityMinecart extends AbstractMinecart implements IDefusable, ITraceableEntity {

    private static final EntityDataAccessor<Integer> FUSE = SynchedEntityData.defineId(EntityMinecart.class,
	    EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> TYPE = SynchedEntityData.defineId(EntityMinecart.class,
	    EntityDataSerializers.STRING);
    private ResourceLocation blastId = null;
    private int fuse = -1;
    private boolean exploded;
    @Nullable
    private UUID ownerUUID;
    @Nullable
    private Entity cachedOwner;

    public void setOwner(@Nullable Entity cachedOwner) {
	if (cachedOwner != null) {
	    this.ownerUUID = cachedOwner.getUUID();
	    this.cachedOwner = cachedOwner;
	}
    }

    @Nullable
    @Override
    public Entity getOwner() {
	if (this.cachedOwner != null && !this.cachedOwner.isRemoved()) {
	    return this.cachedOwner;
	} else if (this.ownerUUID != null && this.level instanceof ServerLevel serverlevel) {
	    this.cachedOwner = serverlevel.getEntity(this.ownerUUID);
	    return this.cachedOwner;
	} else {
	    return null;
	}
    }

    protected boolean ownedBy(Entity entity) {
	return entity.getUUID().equals(this.ownerUUID);
    }

    public EntityMinecart(EntityType<? extends EntityMinecart> type, Level worldIn) {
	super(type, worldIn);
    }

    @Override
    public Type getMinecartType() {
	return Type.TNT;
    }

    public EntityMinecart(Level worldIn, @Nullable Entity owner) {
	this(BallistixEntities.ENTITY_MINECART.get(), worldIn);
	setOwner(owner);
    }

    public void setExplosiveType(IBlast explosive) {
	blastId = explosive.id();
    }

    @Nullable
    public IBlast getExplosiveType() {
	return blastId == null ? null : Blast.BLAST_MAP.get(blastId);
    }

    @Override
    protected void defineSynchedData() {
	super.defineSynchedData();
	entityData.define(FUSE, -1);
	entityData.define(TYPE, "");
    }

    @Override
    public void tick() {
	if (!level.isClientSide) {
	    if (blastId != null) {
		entityData.set(TYPE, blastId.toString());
	    }
	    entityData.set(FUSE, fuse);
	} else {
	    String str = entityData.get(TYPE);
	    if (!str.isEmpty()) {
		blastId = new ResourceLocation(str);
	    }
	    fuse = entityData.get(FUSE);
	}
	super.tick();
	if (fuse > 0) {
	    --fuse;
	    level.addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5D, this.getZ(), 0.0D, 0.0D, 0.0D);
	} else if (fuse == 0) {
	    explode(getDeltaMovement().horizontalDistanceSqr());
	}
	if (horizontalCollision) {
	    double d0 = getDeltaMovement().horizontalDistanceSqr();
	    if (d0 >= 0.01F) {
		explode(d0);
	    }
	}
    }

    @Override
    public boolean hurt(DamageSource source, float damage) {
	Entity entity = source.getDirectEntity();
	if (entity instanceof AbstractArrow abstractarrow) {
	    if (abstractarrow.isOnFire()) {
		explode(abstractarrow.getDeltaMovement().lengthSqr());
	    }
	}
	return super.hurt(source, damage);
    }

    @Override
    public void destroy(DamageSource source) {
	double d0 = getDeltaMovement().horizontalDistanceSqr();
	if (!source.isFire() && !source.isExplosion() && d0 < 0.01F) {
	    super.destroy(source);
	} else if (fuse < 0) {
	    primeFuse();
	    fuse = random.nextInt(20) + random.nextInt(20);
	}
    }

    protected void explode(double val) {
	if (!level.isClientSide) {
	    exploded = true;
	    remove(RemovalReason.DISCARDED);
	    if (blastId != null) {
		IBlast explosive = Blast.BLAST_MAP.get(blastId);
		Blast b = explosive.createBlast(level, blockPosition(), getOwner(), this);
		if (b != null) {
		    b.performExplosion();
		}
	    }
	}
    }

    @Override
    public void defuse() {
	if (!exploded) {
	    fuse = -1;
	    entityData.set(FUSE, fuse);
	}
    }

    @Override
    public boolean causeFallDamage(float par1, float par2, DamageSource source) {
	if (par1 >= 3.0F) {
	    float f = par1 / 10.0F;
	    explode(f * f);
	}

	return super.causeFallDamage(par1, par2, source);
    }

    @Override
    protected Item getDropItem() {
	if (blastId != null) {
	    return Blast.BLAST_TO_MINECART_MAP.get(Blast.BLAST_MAP.get(blastId));
	}
	return Items.MINECART;
    }

    @Override
    public void activateMinecart(int par1, int par2, int par3, boolean toggle) {
	if (toggle && fuse < 0) {
	    primeFuse();
	}
    }

    @Override
    public void handleEntityEvent(byte b) {
	if (b == 10) {
	    primeFuse();
	} else {
	    super.handleEntityEvent(b);
	}
    }

    public void primeFuse() {
	fuse = 80;
	if (!level.isClientSide) {
	    level.broadcastEntityEvent(this, (byte) 10);
	    if (!isSilent()) {
		level.playSound((Player) null, this.getX(), this.getY(), this.getZ(), SoundEvents.TNT_PRIMED,
			SoundSource.BLOCKS, 1.0F, 1.0F);
	    }
	}
    }

    public int getFuse() {
	return fuse;
    }

    public boolean isPrimed() {
	return fuse > -1;
    }

    @Override
    public float getBlockExplosionResistance(Explosion ex, BlockGetter getter, BlockPos pos, BlockState state,
	    FluidState fluidState, float val) {
	return !isPrimed() || !state.is(BlockTags.RAILS) && !getter.getBlockState(pos.above()).is(BlockTags.RAILS)
		? super.getBlockExplosionResistance(ex, getter, pos, state, fluidState, val)
		: 0.0F;
    }

    @Override
    public boolean shouldBlockExplode(Explosion ex, BlockGetter getter, BlockPos pos, BlockState state, float val) {
	return !isPrimed() || !state.is(BlockTags.RAILS) && !getter.getBlockState(pos.above()).is(BlockTags.RAILS)
		&& super.shouldBlockExplode(ex, getter, pos, state, val);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
	super.addAdditionalSaveData(compound);
	compound.putInt("Fuse", fuse);
	if (this.ownerUUID != null) {
	    compound.putUUID("Owner", this.ownerUUID);
	}
	ResourceLocation.CODEC.encodeStart(NbtOps.INSTANCE, blastId).result()
		.ifPresent(tag -> compound.put("type", tag));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
	super.readAdditionalSaveData(compound);
	fuse = compound.getInt("Fuse");
	if (compound.hasUUID("Owner")) {
	    this.ownerUUID = compound.getUUID("Owner");
	    this.cachedOwner = null;
	}
	ResourceLocation.CODEC.decode(NbtOps.INSTANCE, compound.get("type")).result()
		.ifPresent(pair -> blastId = pair.getFirst());
    }

    @Override
    public Packet<?> getAddEntityPacket() {
	return NetworkHooks.getEntitySpawningPacket(this);
    }

}
