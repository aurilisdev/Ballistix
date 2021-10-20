package ballistix.common.entity;

import ballistix.DeferredRegisters;
import ballistix.common.blast.Blast;
import ballistix.common.blast.IHasCustomRenderer;
import ballistix.common.block.SubtypeBlast;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntityBlast extends Entity {
    private static final DataParameter<Integer> CALLCOUNT = EntityDataManager.createKey(EntityBlast.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> TYPE = EntityDataManager.createKey(EntityBlast.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> SHOULDSTARTCUSTOMRENDER = EntityDataManager.createKey(EntityBlast.class, DataSerializers.BOOLEAN);

    private Blast blast;
    public int blastOrdinal = -1;
    public int callcount = 0;
    public boolean shouldRenderCustom = false;
    public int ticksWhenCustomRender;

    public EntityBlast(EntityType<? extends EntityBlast> type, World worldIn) {
	super(type, worldIn);
	preventEntitySpawning = true;
    }

    public EntityBlast(World worldIn) {
	this(DeferredRegisters.ENTITY_BLAST.get(), worldIn);
    }

    public void setBlastType(SubtypeBlast explosive) {
	blastOrdinal = explosive.ordinal();
	blast = Blast.createFromSubtype(getBlastType(), world, getPosition());
    }

    public SubtypeBlast getBlastType() {
	return blastOrdinal == -1 ? null : SubtypeBlast.values()[blastOrdinal];
    }

    @Override
    protected void registerData() {
	dataManager.register(CALLCOUNT, 80);
	dataManager.register(TYPE, -1);
	dataManager.register(SHOULDSTARTCUSTOMRENDER, false);
    }

    @Override
    public void tick() {
	if (!world.isRemote) {
	    dataManager.set(TYPE, blastOrdinal);
	    dataManager.set(CALLCOUNT, callcount);
	    dataManager.set(SHOULDSTARTCUSTOMRENDER, blast instanceof IHasCustomRenderer && ((IHasCustomRenderer) blast).shouldRender());
	} else {
	    blastOrdinal = dataManager.get(TYPE);
	    callcount = dataManager.get(CALLCOUNT);
	    if (!shouldRenderCustom && dataManager.get(SHOULDSTARTCUSTOMRENDER) == Boolean.TRUE) {
		ticksWhenCustomRender = ticksExisted;
	    }
	    shouldRenderCustom = dataManager.get(SHOULDSTARTCUSTOMRENDER);
	}
	if (blast != null) {
	    if (callcount == 0) {
		blast.preExplode();
	    } else if (blast.explode(callcount)) {
		blast.postExplode();
		remove();
	    }
	    callcount++;
	} else {
	    if (blastOrdinal == -1) {
		if (ticksExisted > 60) {
		    remove();
		}
	    } else {
		blast = Blast.createFromSubtype(getBlastType(), world, getPosition());
	    }
	}
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
	compound.putInt("type", blastOrdinal);
	compound.putInt("callcount", callcount);
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
	blastOrdinal = compound.getInt("type");
	callcount = compound.getInt("callcount");
	if (blastOrdinal != -1) {
	    setBlastType(getBlastType());
	}
    }

    @Override
    public IPacket<?> createSpawnPacket() {
	return NetworkHooks.getEntitySpawningPacket(this);
    }

    public Blast getBlast() {
	return blast;
    }
}
