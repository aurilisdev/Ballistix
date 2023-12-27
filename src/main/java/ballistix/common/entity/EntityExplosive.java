package ballistix.common.entity;

import ballistix.api.entity.IDefusable;
import ballistix.common.blast.Blast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.registers.BallistixBlocks;
import ballistix.registers.BallistixEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntityExplosive extends Entity implements IDefusable {

	private static final DataParameter<Integer> FUSE = EntityDataManager.defineId(EntityExplosive.class, DataSerializers.INT);
	private static final DataParameter<Integer> TYPE = EntityDataManager.defineId(EntityExplosive.class, DataSerializers.INT);
	public int blastOrdinal = -1;
	public int fuse = 80;

	public EntityExplosive(EntityType<? extends EntityExplosive> type, World worldIn) {
		super(type, worldIn);
		blocksBuilding = true;
	}

	public EntityExplosive(World worldIn, double x, double y, double z) {
		this(BallistixEntities.ENTITY_EXPLOSIVE.get(), worldIn);
		setPos(x, y, z);
		double d0 = worldIn.random.nextDouble() * ((float) Math.PI * 2F);
		this.setDeltaMovement(-Math.sin(d0) * 0.02D, 0.2F, -Math.cos(d0) * 0.02D);
		xo = x;
		yo = y;
		zo = z;
	}

	@Override
	public boolean isPickable() {
		return !isAlive();
	}

	public void setBlastType(SubtypeBlast explosive) {
		blastOrdinal = explosive.ordinal();
		fuse = explosive.fuse;
	}

	public SubtypeBlast getBlastType() {
		return blastOrdinal == -1 ? null : SubtypeBlast.values()[blastOrdinal];
	}

	@Override
	protected void defineSynchedData() {
		entityData.define(FUSE, 80);
		entityData.define(TYPE, -1);
	}

	@Override
	public void defuse() {
		remove(false);
		if (blastOrdinal != -1) {
			SubtypeBlast explosive = SubtypeBlast.values()[blastOrdinal];
			ItemEntity item = new ItemEntity(level, blockPosition().getX() + 0.5, blockPosition().getY() + 0.5, blockPosition().getZ() + 0.5, new ItemStack(BallistixBlocks.SUBTYPEBLOCKREGISTER_MAPPINGS.get(explosive).get()));
			level.addFreshEntity(item);
		}
	}

	@Override
	public void tick() {
		if (!level.isClientSide) {
			entityData.set(TYPE, blastOrdinal);
			entityData.set(FUSE, fuse);
		} else {
			blastOrdinal = entityData.get(TYPE);
			fuse = entityData.get(FUSE);
		}
		if (!isNoGravity()) {
			this.setDeltaMovement(getDeltaMovement().add(0.0D, -0.04D, 0.0D));
		}

		move(MoverType.SELF, getDeltaMovement());
		this.setDeltaMovement(getDeltaMovement().scale(0.98D));
		if (onGround) {
			this.setDeltaMovement(getDeltaMovement().multiply(0.7D, -0.5D, 0.7D));
		}

		--fuse;
		if (fuse <= 0) {
			remove(false);
			if (blastOrdinal != -1) {
				SubtypeBlast explosive = SubtypeBlast.values()[blastOrdinal];
				Blast b = Blast.createFromSubtype(explosive, level, blockPosition());
				if (b != null) {
					b.performExplosion();
				}
			}
		} else {
			updateInWaterStateAndDoFluidPushing();
			if (level.isClientSide) {
				level.addParticle(ParticleTypes.LAVA, getX(), getY() + 0.5D, getZ(), 0.0D, 0.0D, 0.0D);
			}
		}

	}

	@Override
	protected void addAdditionalSaveData(CompoundNBT compound) {
		compound.putInt("Fuse", fuse);
		compound.putInt("type", blastOrdinal);
	}

	@Override
	protected void readAdditionalSaveData(CompoundNBT compound) {
		fuse = compound.getInt("Fuse");
		blastOrdinal = compound.getInt("type");
	}

	@Override
	public IPacket<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
