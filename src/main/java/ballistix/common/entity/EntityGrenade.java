package ballistix.common.entity;

import javax.annotation.Nullable;

import ballistix.api.blast.IBlast;
import ballistix.api.entity.IDefusable;
import ballistix.common.blast.Blast;
import ballistix.registers.BallistixEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

public class EntityGrenade extends ThrowableProjectile implements IDefusable {

	private static final EntityDataAccessor<Integer> FUSE = SynchedEntityData.defineId(EntityGrenade.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<String> TYPE = SynchedEntityData.defineId(EntityGrenade.class, EntityDataSerializers.STRING);
	private ResourceLocation blastId = null;
	private int fuse = 80;

	public EntityGrenade(EntityType<? extends EntityGrenade> type, Level worldIn) {
		super(type, worldIn);
	}

	public EntityGrenade(Level worldIn) {
		this(BallistixEntities.ENTITY_GRENADE.get(), worldIn);
	}

	public void setExplosiveType(IBlast explosive) {
		blastId = explosive.id();
		fuse = explosive.fuse();
	}

	@Nullable
	public IBlast getExplosiveType() {
		return blastId == null ? null : Blast.BLAST_MAP.get(blastId);
	}


	@Override
	public void defuse() {
		remove(RemovalReason.DISCARDED);
		if (blastId != null) {
			IBlast explosive = Blast.BLAST_MAP.get(blastId);
			ItemEntity item = new ItemEntity(level(), getBlockX() + 0.5, getBlockY() + 0.5, getBlockZ() + 0.5, new ItemStack(explosive.getExplosiveItem().get()));
			level().addFreshEntity(item);
		}
	}

	@Override
	public boolean isPickable() {
		return !isRemoved();
	}

	@Override
	protected void defineSynchedData() {
		entityData.define(FUSE, 80);
		entityData.define(TYPE, "");
	}

	@Override
	public void tick() {
		if (!level().isClientSide) {
			if(blastId != null) {
				entityData.set(TYPE, blastId.toString());
			}
			entityData.set(FUSE, fuse);
		} else {
			String str = entityData.get(TYPE);
			if(!str.isEmpty()) {
				blastId = new ResourceLocation(str);
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
		--fuse;
		if (fuse <= 0) {
			remove(RemovalReason.DISCARDED);
			if (blastId != null) {
				IBlast explosive = Blast.BLAST_MAP.get(blastId);
				Blast b = explosive.createBlast(level(), blockPosition());
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
		ResourceLocation.CODEC.encodeStart(NbtOps.INSTANCE, blastId).result().ifPresent(tag -> compound.put("type", tag));
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
		fuse = compound.getInt("Fuse");
		ResourceLocation.CODEC.decode(NbtOps.INSTANCE, compound.get("type")).result().ifPresent(pair -> blastId = pair.getFirst());
	}
	
	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

}
