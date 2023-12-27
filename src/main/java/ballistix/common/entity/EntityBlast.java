package ballistix.common.entity;

import ballistix.common.blast.Blast;
import ballistix.common.blast.IHasCustomRenderer;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.registers.BallistixEntities;
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
	private static final DataParameter<Integer> CALLCOUNT = EntityDataManager.defineId(EntityBlast.class, DataSerializers.INT);
	private static final DataParameter<Integer> TYPE = EntityDataManager.defineId(EntityBlast.class, DataSerializers.INT);
	private static final DataParameter<Boolean> SHOULDSTARTCUSTOMRENDER = EntityDataManager.defineId(EntityBlast.class, DataSerializers.BOOLEAN);

	private Blast blast;
	public int blastOrdinal = -1;
	public int callcount = 0;
	public boolean shouldRenderCustom = false;
	public int ticksWhenCustomRender;

	public EntityBlast(EntityType<? extends EntityBlast> type, World worldIn) {
		super(type, worldIn);
		blocksBuilding = true;
	}

	public EntityBlast(World worldIn) {
		this(BallistixEntities.ENTITY_BLAST.get(), worldIn);
	}
	
	@Override
	public boolean shouldRender(double x, double y, double z) {
		return true;
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
	public void tick() {
		if (!level.isClientSide) {
			entityData.set(TYPE, blastOrdinal);
			entityData.set(CALLCOUNT, callcount);
			entityData.set(SHOULDSTARTCUSTOMRENDER, blast instanceof IHasCustomRenderer && ((IHasCustomRenderer) blast).shouldRender());
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
				remove(false);
			}
			callcount++;
		} else if (blastOrdinal == -1) {
			if (tickCount > 60) {
				remove(false);
			}
		} else {
			blast = Blast.createFromSubtype(getBlastType(), level, blockPosition());
		}
	}

	@Override
	protected void addAdditionalSaveData(CompoundNBT compound) {
		compound.putInt("type", blastOrdinal);
		compound.putInt("callcount", callcount);
	}

	@Override
	protected void readAdditionalSaveData(CompoundNBT compound) {
		blastOrdinal = compound.getInt("type");
		callcount = compound.getInt("callcount");
		if (blastOrdinal != -1) {
			setBlastType(getBlastType());
		}
	}

	@Override
	public IPacket<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	public Blast getBlast() {
		return blast;
	}
}
