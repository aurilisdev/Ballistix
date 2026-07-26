package ballistix.common.entity;

import java.util.UUID;

import javax.annotation.Nullable;

import ballistix.Ballistix;
import ballistix.api.blast.IBlast;
import ballistix.api.blast.IHasCustomRender;
import ballistix.api.entity.ITraceableEntity;
import ballistix.common.blast.util.Blast;
import ballistix.common.blast.util.BlastLasting;
import ballistix.registers.BallistixEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.minecraftforge.network.NetworkHooks;

public class EntityBlast extends Entity implements ITraceableEntity {
    private static final EntityDataAccessor<Integer> CALLCOUNT = SynchedEntityData.defineId(EntityBlast.class,
	    EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> TYPE = SynchedEntityData.defineId(EntityBlast.class,
	    EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Boolean> SHOULDSTARTCUSTOMRENDER = SynchedEntityData
	    .defineId(EntityBlast.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> TICKCOUNT = SynchedEntityData.defineId(EntityBlast.class,
	    EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SHOULD_PERSIST = SynchedEntityData.defineId(EntityBlast.class,
	    EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> PERSISTANCE_TICKS = SynchedEntityData.defineId(EntityBlast.class,
	    EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TICKS_PERSISTED = SynchedEntityData.defineId(EntityBlast.class,
	    EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> HAS_MATURED = SynchedEntityData.defineId(EntityBlast.class,
	    EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> TICKS_AT_MATURITY = SynchedEntityData.defineId(EntityBlast.class,
	    EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> MOVEMENT_TICKS = SynchedEntityData.defineId(EntityBlast.class,
	    EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TICKS_MOVING = SynchedEntityData.defineId(EntityBlast.class,
	    EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> MOVING = SynchedEntityData.defineId(EntityBlast.class,
	    EntityDataSerializers.BOOLEAN);

    private Blast blast;
    public ResourceLocation blastId;
    public int callcount = 0;
    public boolean shouldRenderCustom = false;
    public int ticksWhenCustomRender;
    private boolean detonated = false;
    private boolean shouldPersist = false;
    private int persistanceTicks = 0;
    private int ticksPersisted = 0;
    private int movementTicks = 0;
    private int ticksMoving = 0;
    public boolean hasMatured = false; // has the blast completed its initial explosion
    public int ticksAtMaturity = 0; // keeps track of ticks at maturity for rendering purposes if needed
    private boolean moving = false;
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

    @Override
    @Nullable
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

    @Override
    public void restoreFrom(Entity entity) {
	super.restoreFrom(entity);
	if (entity instanceof EntityBlast blastEntity) {
	    this.cachedOwner = blastEntity.cachedOwner;
	}
    }

    public EntityBlast(EntityType<? extends EntityBlast> type, Level worldIn) {
	super(type, worldIn);
	blocksBuilding = true;
    }

    public EntityBlast(Level worldIn, @Nullable Entity owner) {
	this(BallistixEntities.ENTITY_BLAST.get(), worldIn);
	setOwner(owner);
    }

    @Override
    public boolean shouldRender(double x, double y, double z) {
	return true;
    }

    public void setPersistant(int tickCount, int movementTicks) { // set to -1 for infinite
	shouldPersist = true;
	persistanceTicks = tickCount;
	this.movementTicks = movementTicks;
    }

    public void setBlastType(IBlast explosive) {
	blastId = explosive.id();
	blast = getBlastType().createBlast(level, blockPosition(), getOwner(), this);
    }

    @Nullable
    public IBlast getBlastType() {
	return blastId == null ? null : Blast.BLAST_MAP.get(blastId);
    }

    @Override
    protected void defineSynchedData() {
	entityData.define(CALLCOUNT, 0);
	entityData.define(TYPE, "");
	entityData.define(SHOULDSTARTCUSTOMRENDER, false);
	entityData.define(TICKCOUNT, 0);
	entityData.define(SHOULD_PERSIST, false);
	entityData.define(PERSISTANCE_TICKS, 0);
	entityData.define(HAS_MATURED, false);
	entityData.define(TICKS_AT_MATURITY, 0);
	entityData.define(TICKS_PERSISTED, 0);
	entityData.define(MOVEMENT_TICKS, 0);
	entityData.define(MOVING, false);
	entityData.define(TICKS_MOVING, 0);
    }

    @Override
    public void tick() {
	tickCount++;
	if (detonated /* || tickCount > 1000 */) {
	    if (!level.isClientSide && tickCount > 20) {
		remove(RemovalReason.DISCARDED);
	    }
	    return;
	}

	if (!level.isClientSide) {
	    if (blastId != null) {
		entityData.set(TYPE, blastId.toString());
	    }
	    entityData.set(CALLCOUNT, callcount);
	    entityData.set(SHOULDSTARTCUSTOMRENDER, blast instanceof IHasCustomRender has && has.shouldRender());
	    entityData.set(TICKCOUNT, tickCount);
	    entityData.set(SHOULD_PERSIST, shouldPersist);
	    entityData.set(PERSISTANCE_TICKS, persistanceTicks);
	    entityData.set(TICKS_PERSISTED, ticksPersisted);
	    entityData.set(HAS_MATURED, hasMatured);
	    entityData.set(TICKS_AT_MATURITY, ticksAtMaturity);
	    entityData.set(MOVEMENT_TICKS, movementTicks);
	    entityData.set(MOVING, moving);
	    entityData.set(TICKS_MOVING, ticksMoving);
	} else {
	    String str = entityData.get(TYPE);
	    if (!str.isEmpty()) {
		blastId = new ResourceLocation(str);
	    }
	    callcount = entityData.get(CALLCOUNT);
	    if (!shouldRenderCustom && entityData.get(SHOULDSTARTCUSTOMRENDER)) {
		ticksWhenCustomRender = tickCount;
	    }
	    shouldRenderCustom = entityData.get(SHOULDSTARTCUSTOMRENDER);
	    if (blast != null) {
		blast.shouldRenderCustomClient = shouldRenderCustom;
	    }
	    tickCount = entityData.get(TICKCOUNT);
	    shouldPersist = entityData.get(SHOULD_PERSIST);
	    persistanceTicks = entityData.get(PERSISTANCE_TICKS);
	    ticksPersisted = entityData.get(TICKS_PERSISTED);
	    hasMatured = entityData.get(HAS_MATURED);
	    ticksAtMaturity = entityData.get(TICKS_AT_MATURITY);
	    movementTicks = entityData.get(MOVEMENT_TICKS);
	    moving = entityData.get(MOVING);
	    ticksMoving = entityData.get(TICKS_MOVING);
	    if (blast instanceof BlastLasting lasting) {
		lasting.ticksSinceBlastStart = tickCount - ticksWhenCustomRender;
	    }
	}

	if (blastId == null) {
	    return;
	}

	if (blast == null) {
	    blast = getBlastType().createBlast(level, blockPosition(), getOwner(), this);
	    if (shouldPersist && hasMatured) {
		blast.isRepeating = true;
	    }
	}

	if (blast != null) {

	    if (shouldPersist) {

		if (persistanceTicks == -1 || ticksPersisted > persistanceTicks) {
		    detonated = true;
		    return;
		}

		if (hasMatured) {

		    if (moving) {

			setPos(getX() + getDeltaMovement().x, getY() + getDeltaMovement().y,
				getZ() + getDeltaMovement().z);

			ticksMoving++;

			if (ticksMoving >= movementTicks) {
			    moving = false;
			    ticksMoving = 0;
			    callcount = 0;
			    blast = getBlastType().createBlast(level, blockPosition(), getOwner(), this);
			    blast.isRepeating = true;
			}

		    } else {

			if (callcount == 0) {
			    blast.preExplode();
			} else {
			    if (blast.explode(callcount)) {
				blast.postExplode();
				if (!level.isClientSide) {
				    double dX = level.random.nextDouble() * (level.random.nextBoolean() ? 1 : -1);
				    double dY = level.random.nextDouble() * (level.random.nextBoolean() ? 1 : -1);
				    double dZ = level.random.nextDouble() * (level.random.nextBoolean() ? 1 : -1);

				    // Weights to keep it between min and max build heights

				    int deltaHeight = level.getMaxBuildHeight() - level.getMinBuildHeight();

				    float fifths = deltaHeight / 5.0F;

				    // min weight

				    if (dY < 0 && getY() <= level.getMinBuildHeight() + fifths) {

					float relativeHeight = (float) (getY() - level.getMinBuildHeight());
					float perc = 1.0F - relativeHeight / fifths;

					if (level.random.nextFloat() <= perc) {
					    dY = Math.abs(dY);
					}

				    }

				    // max weight

				    if (dY > 0 && getY() >= level.getMinBuildHeight() + fifths * 3) {

					float relativeHeight = (float) (getY() - level.getMinBuildHeight());
					float perc = relativeHeight / (fifths * 5);

					if (level.random.nextFloat() <= perc) {
					    dY = -dY;
					}

				    }

				    setDeltaMovement(dX, dY, dZ);

				}
				moving = true;
				persistanceTicks++;
			    }
			}
			callcount++;

		    }

		} else {

		    if (callcount == 0) {
			blast.preExplode();
		    } else {
			if (blast.explode(callcount)) {
			    blast.postExplode();
			    hasMatured = true;
			    ticksAtMaturity = tickCount;
			    ticksPersisted = 0;
			    ticksMoving = 0;
			    callcount = 0;
			    // unload the chunk at this point
			    ChunkPos pos = level.getChunk(blockPosition()).getPos();
			    ForgeChunkManager.forceChunk((ServerLevel) level, Ballistix.ID, blockPosition(), pos.x,
				    pos.z, false, true);
			}
		    }

		    callcount++;
		}

	    } else {
		if (callcount == 0) {
		    blast.preExplode();
		} else {
		    if (blast.explode(callcount)) {
			detonated = true;
			blast.postExplode();
		    }
		}

		callcount++;

	    }

	}
    }

    @Override
    public void onAddedToWorld() {
	super.onAddedToWorld();
	if (!level.isClientSide() && !hasMatured) {
	    ChunkPos pos = level.getChunk(blockPosition()).getPos();
	    ForgeChunkManager.forceChunk((ServerLevel) level, Ballistix.ID, blockPosition(), pos.x, pos.z, true, true);
	}
    }

    @Override
    public void remove(RemovalReason reason) {
	if (!level.isClientSide && reason == RemovalReason.DISCARDED && !hasMatured) {
	    ChunkPos pos = level.getChunk(blockPosition()).getPos();
	    ForgeChunkManager.forceChunk((ServerLevel) level, Ballistix.ID, blockPosition(), pos.x, pos.z, false, true);
	}
	super.remove(reason);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
	ResourceLocation.CODEC.encodeStart(NbtOps.INSTANCE, blastId).result()
		.ifPresent(tag -> compound.put("type", tag));
	compound.putInt("callcount", callcount);
	compound.putInt("persistanceticks", persistanceTicks);
	compound.putInt("tickspersisted", ticksPersisted);
	compound.putInt("ticksatmaturity", ticksAtMaturity);
	compound.putInt("movementticks", movementTicks);
	compound.putInt("ticksmoving", ticksMoving);
	compound.putBoolean("moivng", moving);
	compound.putBoolean("shouldpersist", shouldPersist);
	compound.putBoolean("hasmatured", hasMatured);
	if (this.ownerUUID != null) {
	    compound.putUUID("Owner", this.ownerUUID);
	}
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
	ResourceLocation.CODEC.decode(NbtOps.INSTANCE, compound.get("type")).result()
		.ifPresent(pair -> blastId = pair.getFirst());
	callcount = compound.getInt("callcount");
	if (blastId != null) {
	    setBlastType(getBlastType());
	}
	persistanceTicks = compound.getInt("persistanceticks");
	ticksPersisted = compound.getInt("tickspersisted");
	ticksAtMaturity = compound.getInt("ticksatmaturity");
	shouldPersist = compound.getBoolean("shouldpersist");
	hasMatured = compound.getBoolean("hasmatured");
	movementTicks = compound.getInt("movementticks");
	moving = compound.getBoolean("moving");
	ticksMoving = compound.getInt("ticksmoving");
	if (compound.hasUUID("Owner")) {
	    this.ownerUUID = compound.getUUID("Owner");
	    this.cachedOwner = null;
	}
    }

    @Override
    public Packet<?> getAddEntityPacket() {
	return NetworkHooks.getEntitySpawningPacket(this);
    }

    public Blast getBlast() {
	return blast;
    }

}
