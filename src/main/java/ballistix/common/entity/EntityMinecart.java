package ballistix.common.entity;

import ballistix.api.entity.IDefusable;
import ballistix.common.blast.Blast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.item.ItemMinecart.SubtypeMinecart;
import ballistix.registers.BallistixEntities;
import ballistix.registers.BallistixItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.network.NetworkHooks;

public class EntityMinecart extends AbstractMinecart implements IDefusable {
	private static final EntityDataAccessor<Integer> FUSE = SynchedEntityData.defineId(EntityMinecart.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> TYPE = SynchedEntityData.defineId(EntityMinecart.class, EntityDataSerializers.INT);
	public int blastOrdinal = -1;
	public int fuse = -1;
	private boolean exploded;

	public EntityMinecart(EntityType<? extends EntityMinecart> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	public Type getMinecartType() {
		return Type.TNT;
	}

	public EntityMinecart(Level worldIn) {
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
			explode(getDeltaMovement().horizontalDistanceSqr());
		}
		if (horizontalCollision) {
			double d0 = getDeltaMovement().horizontalDistanceSqr();
			if (d0 >= 0.01F) {
				explode(d0);
			}
		}
	}

	@Override
	public boolean hurt(DamageSource source, float damage) {
		Entity entity = source.getDirectEntity();
		if (entity instanceof AbstractArrow abstractarrow) {
			if (abstractarrow.isOnFire()) {
				explode(abstractarrow.getDeltaMovement().lengthSqr());
			}
		}
		return super.hurt(source, damage);
	}

	@Override
	public void destroy(DamageSource source) {
		double d0 = getDeltaMovement().horizontalDistanceSqr();
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
			remove(RemovalReason.DISCARDED);
			if (blastOrdinal != -1) {
				SubtypeBlast explosive = SubtypeBlast.values()[blastOrdinal];
				Blast b = Blast.createFromSubtype(explosive, level, blockPosition());
				if (b != null) {
					b.performExplosion();
				}
			}
		}
	}

	@Override
	public void remove(RemovalReason reason) {
		super.remove(reason);
		if (!exploded) {
			if (blastOrdinal != -1) {
				ItemEntity item = new ItemEntity(level, getBlockX() + 0.5, getBlockY() + 0.5, getBlockZ() + 0.5, new ItemStack(BallistixItems.getItem(getExplosiveType())));
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
	public boolean causeFallDamage(float par1, float par2, DamageSource source) {
		if (par1 >= 3.0F) {
			float f = par1 / 10.0F;
			explode(f * f);
		}

		return super.causeFallDamage(par1, par2, source);
	}

	@Override
	protected Item getDropItem() {
		if (blastOrdinal != -1) {
			return BallistixItems.getItem(getExplosiveType());
		}
		return Items.MINECART;
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
				level.playSound((Player) null, this.getX(), this.getY(), this.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
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
	public float getBlockExplosionResistance(Explosion ex, BlockGetter getter, BlockPos pos, BlockState state, FluidState fluidState, float val) {
		return !isPrimed() || !state.is(BlockTags.RAILS) && !getter.getBlockState(pos.above()).is(BlockTags.RAILS) ? super.getBlockExplosionResistance(ex, getter, pos, state, fluidState, val) : 0.0F;
	}

	@Override
	public boolean shouldBlockExplode(Explosion ex, BlockGetter getter, BlockPos pos, BlockState state, float val) {
		return !isPrimed() || !state.is(BlockTags.RAILS) && !getter.getBlockState(pos.above()).is(BlockTags.RAILS) && super.shouldBlockExplode(ex, getter, pos, state, val);
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
