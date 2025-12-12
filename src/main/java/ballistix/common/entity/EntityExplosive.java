package ballistix.common.entity;

import java.util.UUID;

import javax.annotation.Nullable;

import ballistix.api.blast.IBlast;
import ballistix.api.entity.IDefusable;
import ballistix.common.blast.tier3.BlastDarkmatter;
import ballistix.common.blast.util.Blast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.registers.BallistixEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EntityExplosive extends Entity implements IDefusable, TraceableEntity {
    private static final EntityDataAccessor<Integer> FUSE = SynchedEntityData.defineId(EntityExplosive.class,
	    EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> TYPE = SynchedEntityData.defineId(EntityExplosive.class,
	    EntityDataSerializers.STRING);
    public ResourceLocation blastId = null;
    public int fuse = 80;
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
	} else if (this.ownerUUID != null && this.level() instanceof ServerLevel serverlevel) {
	    this.cachedOwner = serverlevel.getEntity(this.ownerUUID);
	    return this.cachedOwner;
	} else {
	    return null;
	}
    }

    protected boolean ownedBy(Entity entity) {
	return entity.getUUID().equals(this.ownerUUID);
    }

    public EntityExplosive(EntityType<? extends EntityExplosive> type, Level worldIn) {
	super(type, worldIn);
	blocksBuilding = true;
    }

    public EntityExplosive(Level worldIn, double x, double y, double z, @Nullable Entity owner) {
	this(BallistixEntities.ENTITY_EXPLOSIVE.get(), worldIn);
	setPos(x, y, z);
	double d0 = worldIn.random.nextDouble() * ((float) Math.PI * 2F);
	this.setDeltaMovement(-Math.sin(d0) * 0.02D, 0.2F, -Math.cos(d0) * 0.02D);
	xo = x;
	yo = y;
	zo = z;
	this.setOwner(owner);
    }

    @Override
    public boolean isPickable() {
	return !isRemoved();
    }

    public void setBlastType(IBlast explosive) {
	blastId = explosive.id();
	fuse = explosive.fuse();
    }

    public IBlast getBlastType() {
	return blastId == null ? null : Blast.BLAST_MAP.get(blastId);
    }

    @Override
    public void defuse() {
	remove(RemovalReason.DISCARDED);
	if (blastId != null) {
	    IBlast blast = Blast.BLAST_MAP.get(blastId);
	    ItemEntity item = new ItemEntity(level(), getBlockX() + 0.5, getBlockY() + 0.5, getBlockZ() + 0.5,
		    new ItemStack(blast.getExplosiveItem().get()));
	    level().addFreshEntity(item);
	}
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
	builder.define(FUSE, 80);
	builder.define(TYPE, "");
    }

    @Override
    public void tick() {
	if (!level().isClientSide) {
	    if (blastId != null) {
		entityData.set(TYPE, blastId.toString());
	    }
	    entityData.set(FUSE, fuse);
	} else {
	    String str = entityData.get(TYPE);
	    if (!str.isEmpty()) {
		blastId = ResourceLocation.parse(str);
	    }
	    fuse = entityData.get(FUSE);
	}
	if (!isNoGravity()) {
	    this.setDeltaMovement(getDeltaMovement().add(0.0D, -0.04D, 0.0D));
	}

	move(MoverType.SELF, getDeltaMovement());
	this.setDeltaMovement(getDeltaMovement().scale(0.98D));
	if (onGround()) {
	    this.setDeltaMovement(getDeltaMovement().multiply(0.7D, -0.5D, 0.7D));
	}

	if (!level().isClientSide && blastId != null && Blast.BLAST_MAP.get(blastId) == SubtypeBlast.largeantimatter) {

	    for (EntityBlast entity : level().getEntitiesOfClass(EntityBlast.class,
		    getBoundingBox().inflate(getDeltaMovement().length()))) {
		if (entity.blastId == SubtypeBlast.darkmatter.id() && entity.getBlast() != null) {
		    BlastDarkmatter blast = (BlastDarkmatter) entity.getBlast();
		    blast.canceled = true;
		    entity.remove(RemovalReason.DISCARDED);
		    IBlast explosive = Blast.BLAST_MAP.get(blastId);
		    Blast b = explosive.createBlast(level(), blockPosition(), getOwner());
		    if (b != null) {
			b.performExplosion();
		    }
		    removeAfterChangingDimensions();
		    return;
		}
	    }

	}

	--fuse;
	if (fuse <= 0) {
	    if (!level().isClientSide()) {
		remove(RemovalReason.DISCARDED);
	    }
	    if (blastId != null) {
		IBlast explosive = Blast.BLAST_MAP.get(blastId);
		Blast b = explosive.createBlast(level(), blockPosition(), getOwner());
		if (b != null) {
		    b.performExplosion();
		}
	    }
	} else {
	    updateInWaterStateAndDoFluidPushing();
	    if (level().isClientSide) {
		level().addParticle(ParticleTypes.LAVA, getX(), getY() + 0.5D, getZ(), 0.0D, 0.0D, 0.0D);
	    }
	}

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
	compound.putInt("Fuse", fuse);
	if (this.ownerUUID != null) {
	    compound.putUUID("Owner", this.ownerUUID);
	}
	ResourceLocation.CODEC.encodeStart(NbtOps.INSTANCE, blastId).ifSuccess(tag -> compound.put("type", tag));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
	fuse = compound.getInt("Fuse");
	if (compound.hasUUID("Owner")) {
	    this.ownerUUID = compound.getUUID("Owner");
	    this.cachedOwner = null;
	}
	ResourceLocation.CODEC.decode(NbtOps.INSTANCE, compound.get("type"))
		.ifSuccess(pair -> blastId = pair.getFirst());
    }

}
