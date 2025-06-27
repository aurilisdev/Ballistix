package ballistix.common.entity;

import javax.annotation.Nullable;

import ballistix.api.blast.IBlast;
import ballistix.api.entity.IDefusable;
import ballistix.common.blast.util.Blast;
import ballistix.registers.BallistixEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntityMinecart extends AbstractMinecartEntity implements IDefusable {

	private static final DataParameter<Integer> FUSE = EntityDataManager.defineId(EntityMinecart.class, DataSerializers.INT);
	private static final DataParameter<String> TYPE = EntityDataManager.defineId(EntityMinecart.class, DataSerializers.STRING);
	private ResourceLocation blastId = null;
	private int fuse = -1;
	private boolean exploded;

	public EntityMinecart(EntityType<? extends EntityMinecart> type, World worldIn) {
		super(type, worldIn);
	}

	@Override
	public Type getMinecartType() {
		return Type.TNT;
	}

	public EntityMinecart(World worldIn) {
		this(BallistixEntities.ENTITY_MINECART.get(), worldIn);
	}

	public void setExplosiveType(IBlast explosive) {
		blastId = explosive.id();
	}

	@Nullable
	public IBlast getExplosiveType() {
		return blastId == null ? null : Blast.BLAST_MAP.get(blastId);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(FUSE, -1);
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
		super.tick();
		if (fuse > 0) {
			--fuse;
			level.addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5D, this.getZ(), 0.0D, 0.0D, 0.0D);
		} else if (fuse == 0) {
			explode(getHorizontalDistanceSqr(getDeltaMovement()));
		}
		if (horizontalCollision) {
			double d0 = getHorizontalDistanceSqr(getDeltaMovement());
			if (d0 >= 0.01F) {
				explode(d0);
			}
		}
	}

	@Override
	public boolean hurt(DamageSource source, float damage) {
		Entity entity = source.getDirectEntity();
		if (entity instanceof AbstractArrowEntity) {
			AbstractArrowEntity abstractarrow = (AbstractArrowEntity) entity;
			if (abstractarrow.isOnFire()) {
				explode(abstractarrow.getDeltaMovement().lengthSqr());
			}
		}
		return super.hurt(source, damage);
	}

	@Override
	public void destroy(DamageSource source) {
		double d0 = getHorizontalDistanceSqr(getDeltaMovement());
		if (!source.isFire() && !source.isExplosion() && d0 < 0.01F) {
			super.destroy(source);
			if (!source.isExplosion() && level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
				this.spawnAtLocation(Blocks.TNT);
			}

		} else if (fuse < 0) {
			primeFuse();
			fuse = random.nextInt(20) + random.nextInt(20);
		}
	}

	protected void explode(double val) {
		if (!level.isClientSide) {
			exploded = true;
			remove(false);
			if (blastId != null) {
				IBlast explosive = Blast.BLAST_MAP.get(blastId);
				Blast b = explosive.createBlast(level, blockPosition());
				if (b != null) {
					b.performExplosion();
				}
			}
		}
	}

	@Override
	public void remove(boolean reason) {
		super.remove(reason);
		if (!exploded) {
			if (blastId != null) {
				ItemEntity item = new ItemEntity(level, blockPosition().getX() + 0.5, blockPosition().getY() + 0.5, blockPosition().getZ() + 0.5, new ItemStack(Blast.BLAST_TO_MINECART_MAP.get(Blast.BLAST_MAP.get(blastId))));
				level.addFreshEntity(item);
			}
		}
	}

	@Override
	public void defuse() {
		if (!exploded) {
			fuse = -1;
			entityData.set(FUSE, fuse);
		}
	}

	@Override
	public boolean causeFallDamage(float par1, float par2) {
		if (par1 >= 3.0F) {
			float f = par1 / 10.0F;
			explode(f * f);
		}

		return super.causeFallDamage(par1, par2);
	}

	@Override
	public ItemStack getCartItem() {
		if (blastId != null) {
			return new ItemStack(Blast.BLAST_TO_MINECART_MAP.get(Blast.BLAST_MAP.get(blastId)));
		}
		return ItemStack.EMPTY;
	}

	@Override
	public void activateMinecart(int par1, int par2, int par3, boolean toggle) {
		if (toggle && fuse < 0) {
			primeFuse();
		}
	}

	@Override
	public void handleEntityEvent(byte b) {
		if (b == 10) {
			primeFuse();
		} else {
			super.handleEntityEvent(b);
		}
	}

	public void primeFuse() {
		fuse = 80;
		if (!level.isClientSide) {
			level.broadcastEntityEvent(this, (byte) 10);
			if (!isSilent()) {
				level.playSound((PlayerEntity) null, this.getX(), this.getY(), this.getZ(), SoundEvents.TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
			}
		}
	}

	public int getFuse() {
		return fuse;
	}

	public boolean isPrimed() {
		return fuse > -1;
	}

	@Override
	public float getBlockExplosionResistance(Explosion ex, IBlockReader getter, BlockPos pos, BlockState state, FluidState fluidState, float val) {
		return !isPrimed() || !state.is(BlockTags.RAILS) && !getter.getBlockState(pos.above()).is(BlockTags.RAILS) ? super.getBlockExplosionResistance(ex, getter, pos, state, fluidState, val) : 0.0F;
	}

	@Override
	public boolean shouldBlockExplode(Explosion ex, IBlockReader getter, BlockPos pos, BlockState state, float val) {
		return !isPrimed() || !state.is(BlockTags.RAILS) && !getter.getBlockState(pos.above()).is(BlockTags.RAILS) && super.shouldBlockExplode(ex, getter, pos, state, val);
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
