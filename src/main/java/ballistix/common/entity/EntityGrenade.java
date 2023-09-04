package ballistix.common.entity;

import ballistix.api.entity.IDefusable;
import ballistix.common.blast.Blast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.item.ItemGrenade.SubtypeGrenade;
import ballistix.registers.BallistixBlocks;
import ballistix.registers.BallistixEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

public class EntityGrenade extends ThrowableProjectile implements IDefusable {
	private static final EntityDataAccessor<Integer> FUSE = SynchedEntityData.defineId(EntityGrenade.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> TYPE = SynchedEntityData.defineId(EntityGrenade.class, EntityDataSerializers.INT);
	private int grenadeOrdinal = -1;
	private int fuse = 80;

	public EntityGrenade(EntityType<? extends EntityGrenade> type, Level worldIn) {
		super(type, worldIn);
	}

	public EntityGrenade(Level worldIn) {
		this(BallistixEntities.ENTITY_GRENADE.get(), worldIn);
	}

	public void setExplosiveType(SubtypeGrenade explosive) {
		grenadeOrdinal = explosive.ordinal();
		fuse = explosive.explosiveType.fuse;
	}

	public SubtypeGrenade getExplosiveType() {
		return grenadeOrdinal == -1 ? null : SubtypeGrenade.values()[grenadeOrdinal];
	}

	@Override
	protected void defineSynchedData() {
		entityData.define(FUSE, 80);
		entityData.define(TYPE, -1);
	}

	@Override
	public void defuse() {
		remove(RemovalReason.DISCARDED);
		if (grenadeOrdinal != -1) {
			SubtypeBlast explosive = SubtypeGrenade.values()[grenadeOrdinal].explosiveType;
			ItemEntity item = new ItemEntity(level(), getBlockX() + 0.5, getBlockY() + 0.5, getBlockZ() + 0.5, new ItemStack(BallistixBlocks.SUBTYPEBLOCKREGISTER_MAPPINGS.get(explosive).get()));
			level().addFreshEntity(item);
		}
	}

	@Override
	public boolean isPickable() {
		return !isRemoved();
	}

	@Override
	public void tick() {
		if (!level().isClientSide) {
			entityData.set(TYPE, grenadeOrdinal);
			entityData.set(FUSE, fuse);
		} else {
			grenadeOrdinal = entityData.get(TYPE);
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
		--fuse;
		if (fuse <= 0) {
			remove(RemovalReason.DISCARDED);
			if (grenadeOrdinal != -1) {
				SubtypeBlast explosive = SubtypeGrenade.values()[grenadeOrdinal].explosiveType;
				Blast b = Blast.createFromSubtype(explosive, level(), blockPosition());
				if (b != null) {
					b.performExplosion();
				}
			}
		} else {
			updateInWaterStateAndDoFluidPushing();
			if (level().isClientSide) {
				level().addParticle(ParticleTypes.SMOKE, getX(), getY() + 0.5D, getZ(), 0.0D, 0.0D, 0.0D);
			}
		}
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {
		compound.putInt("Fuse", fuse);
		compound.putInt("type", grenadeOrdinal);
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
		fuse = compound.getInt("Fuse");
		grenadeOrdinal = compound.getInt("type");
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
