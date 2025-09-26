package ballistix.common.entity;

import ballistix.api.blast.IBlast;
import ballistix.api.entity.IDefusable;
import ballistix.common.blast.tier3.BlastDarkmatter;
import ballistix.common.blast.util.Blast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.registers.BallistixEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.ItemEntity;
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

public class EntityExplosive extends Entity implements IDefusable {
	
	private static final DataParameter<Integer> FUSE = EntityDataManager.defineId(EntityExplosive.class, DataSerializers.INT);
	private static final DataParameter<String> TYPE = EntityDataManager.defineId(EntityExplosive.class, DataSerializers.STRING);
	public ResourceLocation blastId = null;
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

	public void setBlastType(IBlast explosive) {
		blastId = explosive.id();
		fuse = explosive.fuse();
	}

	public IBlast getBlastType() {
		return blastId == null ? null : Blast.BLAST_MAP.get(blastId);
	}

	@Override
	public void defuse() {
		remove(false);
		if (blastId != null) {
			IBlast blast = Blast.BLAST_MAP.get(blastId);
			ItemEntity item = new ItemEntity(level, blockPosition().getX() + 0.5, blockPosition().getY() + 0.5, blockPosition().getZ() + 0.5, new ItemStack(blast.getExplosiveItem().get()));
			level.addFreshEntity(item);
		}
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

		if(!level.isClientSide && blastId != null && Blast.BLAST_MAP.get(blastId) == SubtypeBlast.largeantimatter) {

			for(EntityBlast entity : level.getEntitiesOfClass(EntityBlast.class, getBoundingBox().inflate(getDeltaMovement().length()))) {
				if(entity.blastId == SubtypeBlast.darkmatter.id() && entity.getBlast() != null) {
					BlastDarkmatter blast = (BlastDarkmatter) entity.getBlast();
					blast.canceled = true;
					entity.remove(false);
					IBlast explosive = Blast.BLAST_MAP.get(blastId);
					Blast b = explosive.createBlast(level, blockPosition());
					if (b != null) {
						b.performExplosion();
					}
					removeAfterChangingDimensions();
					return;
				}
			}

		}

		--fuse;
		if (fuse <= 0) {
			if(!level.isClientSide()) {
				remove(false);
			}
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
				level.addParticle(ParticleTypes.LAVA, getX(), getY() + 0.5D, getZ(), 0.0D, 0.0D, 0.0D);
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
