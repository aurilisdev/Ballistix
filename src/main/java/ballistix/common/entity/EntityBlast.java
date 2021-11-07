package ballistix.common.entity;

import ballistix.DeferredRegisters;
import ballistix.common.blast.Blast;
import ballistix.common.blast.IHasCustomRenderer;
import ballistix.common.block.SubtypeBlast;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

public class EntityBlast extends Entity {
    private static final EntityDataAccessor<Integer> CALLCOUNT = SynchedEntityData.defineId(EntityBlast.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TYPE = SynchedEntityData.defineId(EntityBlast.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SHOULDSTARTCUSTOMRENDER = SynchedEntityData.defineId(EntityBlast.class,
	    EntityDataSerializers.BOOLEAN);

    private Blast blast;
    public int blastOrdinal = -1;
    public int callcount = 0;
    public boolean shouldRenderCustom = false;
    public int ticksWhenCustomRender;

    public EntityBlast(EntityType<? extends EntityBlast> type, Level worldIn) {
	super(type, worldIn);
	blocksBuilding = true;
    }

    public EntityBlast(Level worldIn) {
	this(DeferredRegisters.ENTITY_BLAST.get(), worldIn);
    }

    public void setBlastType(SubtypeBlast explosive) {
	blastOrdinal = explosive.ordinal();
	blast = Blast.createFromSubtype(getBlastType(), level, blockPosition());
    }

    public SubtypeBlast getBlastType() {
	return blastOrdinal == -1 ? null : SubtypeBlast.values()[blastOrdinal];
    }

    @Override
    protected void defineSynchedData() {
	entityData.define(CALLCOUNT, 80);
	entityData.define(TYPE, -1);
	entityData.define(SHOULDSTARTCUSTOMRENDER, false);
    }

    @Override
    @Deprecated(since = "Uses deprecated methods", forRemoval = false)
    public void tick() {
	if (!level.isClientSide) {
	    entityData.set(TYPE, blastOrdinal);
	    entityData.set(CALLCOUNT, callcount);
	    entityData.set(SHOULDSTARTCUSTOMRENDER, blast instanceof IHasCustomRenderer has && has.shouldRender());
	} else {
	    blastOrdinal = entityData.get(TYPE);
	    callcount = entityData.get(CALLCOUNT);
	    if (!shouldRenderCustom && entityData.get(SHOULDSTARTCUSTOMRENDER) == Boolean.TRUE) {
		ticksWhenCustomRender = tickCount;
	    }
	    shouldRenderCustom = entityData.get(SHOULDSTARTCUSTOMRENDER);
	}
	if (blast != null) {
	    if (callcount == 0) {
		blast.preExplode();
	    } else if (blast.explode(callcount)) {
		blast.postExplode();
		remove(RemovalReason.DISCARDED);
	    }
	    callcount++;
	} else {
	    if (blastOrdinal == -1) {
		if (tickCount > 60) {
		    remove(RemovalReason.DISCARDED);
		}
	    } else {
		blast = Blast.createFromSubtype(getBlastType(), level, blockPosition());
	    }
	}
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
	compound.putInt("type", blastOrdinal);
	compound.putInt("callcount", callcount);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
	blastOrdinal = compound.getInt("type");
	callcount = compound.getInt("callcount");
	if (blastOrdinal != -1) {
	    setBlastType(getBlastType());
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
