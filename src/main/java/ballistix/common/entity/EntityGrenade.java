package ballistix.common.entity;

import javax.annotation.Nullable;

import ballistix.api.blast.IBlast;
import ballistix.api.entity.IDefusable;
import ballistix.common.blast.util.Blast;
import ballistix.registers.BallistixEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntityGrenade extends ThrowableEntity implements IDefusable {

	private static final DataParameter<Integer> FUSE = EntityDataManager.defineId(EntityGrenade.class, DataSerializers.INT);
	private static final DataParameter<String> TYPE = EntityDataManager.defineId(EntityMinecart.class, DataSerializers.STRING);
	private ResourceLocation blastId = null;
	private int fuse = 80;

	public EntityGrenade(EntityType<? extends EntityGrenade> type, World worldIn) {
		super(type, worldIn);
	}

	public EntityGrenade(World worldIn) {
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
		remove(false);
		if (blastId != null) {
			IBlast explosive = Blast.BLAST_MAP.get(blastId);
			ItemEntity item = new ItemEntity(level, blockPosition().getX() + 0.5, blockPosition().getY() + 0.5, blockPosition().getZ() + 0.5, new ItemStack(explosive.getExplosiveItem().get()));
			level.addFreshEntity(item);
		}
	}

	@Override
	public boolean isPickable() {
		return !isAlive();
	}

	@Override
	protected void defineSynchedData() {
		entityData.define(FUSE, 80);
		entityData.define(TYPE, "");
	}

	@Override
	public void tick() {
		if (!level.isClientSide) {
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
		if (isOnGround()) {
			this.setDeltaMovement(getDeltaMovement().multiply(0.7D, -0.5D, 0.7D));
		}
		--fuse;
		if (fuse <= 0) {
			remove(false);
			if (blastId != null) {
				IBlast explosive = Blast.BLAST_MAP.get(blastId);
				Blast b = explosive.createBlast(level, blockPosition());
				if (b != null) {
					b.performExplosion();
				}
			}
		} else {
			updateInWaterStateAndDoFluidPushing();
			if (level.isClientSide) {
				level.addParticle(ParticleTypes.SMOKE, getX(), getY() + 0.5D, getZ(), 0.0D, 0.0D, 0.0D);
			}
		}
	}

	@Override
	protected void addAdditionalSaveData(CompoundNBT compound) {
		compound.putInt("Fuse", fuse);
		ResourceLocation.CODEC.encodeStart(NBTDynamicOps.INSTANCE, blastId).result().ifPresent(tag -> compound.put("type", tag));
	}

	@Override
	protected void readAdditionalSaveData(CompoundNBT compound) {
		fuse = compound.getInt("Fuse");
		ResourceLocation.CODEC.decode(NBTDynamicOps.INSTANCE, compound.get("type")).result().ifPresent(pair -> blastId = pair.getFirst());
	}
	
	@Override
	public IPacket<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

}
