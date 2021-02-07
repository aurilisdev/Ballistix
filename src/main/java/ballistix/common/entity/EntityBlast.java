package ballistix.common.entity;

import ballistix.DeferredRegisters;
import ballistix.common.blast.Blast;
import ballistix.common.block.SubtypeExplosive;
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
	public int blastOrdinal = -1;
	public int callcount = 0;

	public EntityBlast(EntityType<? extends EntityBlast> type, World worldIn) {
		super(type, worldIn);
		preventEntitySpawning = true;
	}

	public EntityBlast(World worldIn) {
		this(DeferredRegisters.ENTITY_BLAST.get(), worldIn);
	}

	public void setExplosiveType(SubtypeExplosive explosive) {
		blastOrdinal = explosive.ordinal();
	}

	public SubtypeExplosive getExplosiveType() {
		return blastOrdinal == -1 ? null : SubtypeExplosive.values()[blastOrdinal];
	}

	public Blast blast;

	@Override
	protected void registerData() {
		dataManager.register(CALLCOUNT, 80);
		dataManager.register(TYPE, -1);
	}

	@Override
	public void tick() {
		if (!world.isRemote) {
			dataManager.set(TYPE, blastOrdinal);
			dataManager.set(CALLCOUNT, callcount);
		} else {
			blastOrdinal = dataManager.get(TYPE);
			callcount = dataManager.get(CALLCOUNT);
		}
		if (blast != null) {
			if (callcount == 0) {
				blast.doPreExplode();
			} else if (blast.doExplode(callcount)) {
				blast.doPostExplode();
				remove();
			}
			callcount++;
		} else {
			remove();
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
		if (blast == null && blastOrdinal != -1) {
			blast = Blast.createFromSubtype(getExplosiveType(), world, getPosition());
		}
	}

	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
