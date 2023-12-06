package ballistix.common.entity;

import ballistix.api.entity.IDefusable;
import ballistix.common.blast.Blast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.registers.BallistixBlocks;
import ballistix.registers.BallistixEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

public class EntityExplosive extends Entity implements IDefusable {
	private static final EntityDataAccessor<Integer> FUSE = SynchedEntityData.defineId(EntityExplosive.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> TYPE = SynchedEntityData.defineId(EntityExplosive.class, EntityDataSerializers.INT);
	public int blastOrdinal = -1;
	public int fuse = 80;

	public EntityExplosive(EntityType<? extends EntityExplosive> type, Level worldIn) {
		super(type, worldIn);
		blocksBuilding = true;
	}

	public EntityExplosive(Level worldIn, double x, double y, double z) {
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
		return !isRemoved();
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
		remove(RemovalReason.DISCARDED);
		if (blastOrdinal != -1) {
			SubtypeBlast explosive = SubtypeBlast.values()[blastOrdinal];
			ItemEntity item = new ItemEntity(level, getBlockX() + 0.5, getBlockY() + 0.5, getBlockZ() + 0.5, new ItemStack(BallistixBlocks.SUBTYPEBLOCKREGISTER_MAPPINGS.get(explosive).get()));
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
			remove(RemovalReason.DISCARDED);
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
	protected void addAdditionalSaveData(CompoundTag compound) {
		compound.putInt("Fuse", fuse);
		compound.putInt("type", blastOrdinal);
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
		fuse = compound.getInt("Fuse");
		blastOrdinal = compound.getInt("type");
	}

	@Override
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}