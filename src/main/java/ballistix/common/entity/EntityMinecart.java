package ballistix.common.entity;

import ballistix.api.entity.IDefusable;
import ballistix.common.blast.Blast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.item.ItemMinecart.SubtypeMinecart;
import ballistix.registers.BallistixEntities;
import ballistix.registers.BallistixItems;
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
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntityMinecart extends AbstractMinecartEntity implements IDefusable {

	private static final DataParameter<Integer> FUSE = EntityDataManager.defineId(EntityMinecart.class, DataSerializers.INT);
	private static final DataParameter<Integer> TYPE = EntityDataManager.defineId(EntityMinecart.class, DataSerializers.INT);
	private int blastOrdinal = -1;
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

	public void setExplosiveType(SubtypeMinecart explosive) {
		blastOrdinal = explosive.ordinal();
	}

	public SubtypeMinecart getExplosiveType() {
		return blastOrdinal == -1 ? null : SubtypeMinecart.values()[blastOrdinal];
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(FUSE, -1);
		entityData.define(TYPE, -1);
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
			if (blastOrdinal != -1) {
				SubtypeBlast explosive = SubtypeMinecart.values()[blastOrdinal].explosiveType;
				Blast b = Blast.createFromSubtype(explosive, level, blockPosition());
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
			if (blastOrdinal != -1) {
				ItemEntity item = new ItemEntity(level, blockPosition().getX() + 0.5, blockPosition().getY() + 0.5, blockPosition().getZ() + 0.5, new ItemStack(BallistixItems.getItem(getExplosiveType())));
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
	public ItemStack getPickedResult(RayTraceResult target) {
		if (blastOrdinal != -1) {
			return new ItemStack(BallistixItems.getItem(getExplosiveType()));
		}
		return super.getPickedResult(target);
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