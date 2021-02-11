package ballistix.common.entity;

import ballistix.DeferredRegisters;
import ballistix.common.blast.Blast;
import ballistix.common.block.SubtypeBlast;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntityExplosive extends Entity {
	private static final DataParameter<Integer> FUSE = EntityDataManager.createKey(EntityExplosive.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> TYPE = EntityDataManager.createKey(EntityExplosive.class, DataSerializers.VARINT);
	public int blastOrdinal = -1;
	public int fuse = 80;

	public EntityExplosive(EntityType<? extends EntityExplosive> type, World worldIn) {
		super(type, worldIn);
		preventEntitySpawning = true;
	}

	public EntityExplosive(World worldIn, double x, double y, double z) {
		this(DeferredRegisters.ENTITY_EXPLOSIVE.get(), worldIn);
		setPosition(x, y, z);
		double d0 = worldIn.rand.nextDouble() * ((float) Math.PI * 2F);
		this.setMotion(-Math.sin(d0) * 0.02D, 0.2F, -Math.cos(d0) * 0.02D);
		prevPosX = x;
		prevPosY = y;
		prevPosZ = z;
	}

	public void setBlastType(SubtypeBlast explosive) {
		blastOrdinal = explosive.ordinal();
		fuse = explosive.fuse;
	}

	public SubtypeBlast getBlastType() {
		return blastOrdinal == -1 ? null : SubtypeBlast.values()[blastOrdinal];
	}

	@Override
	protected void registerData() {
		dataManager.register(FUSE, 80);
		dataManager.register(TYPE, -1);
	}

	@Override
	public void tick() {
		if (!world.isRemote) {
			dataManager.set(TYPE, blastOrdinal);
			dataManager.set(FUSE, fuse);
		} else {
			blastOrdinal = dataManager.get(TYPE);
			fuse = dataManager.get(FUSE);
		}
		if (!hasNoGravity()) {
			this.setMotion(getMotion().add(0.0D, -0.04D, 0.0D));
		}

		move(MoverType.SELF, getMotion());
		this.setMotion(getMotion().scale(0.98D));
		if (onGround) {
			this.setMotion(getMotion().mul(0.7D, -0.5D, 0.7D));
		}

		--fuse;
		if (fuse <= 0) {
			this.remove();
			if (blastOrdinal != -1) {
				SubtypeBlast explosive = SubtypeBlast.values()[blastOrdinal];
				Blast b = Blast.createFromSubtype(explosive, world, getPosition());
				if (b != null) {
					b.performExplosion();
				}
			}
		} else {
			func_233566_aG_();
			if (world.isRemote) {
				world.addParticle(ParticleTypes.LAVA, getPosX(), getPosY() + 0.5D, getPosZ(), 0.0D, 0.0D, 0.0D);
			}
		}

	}

	@Override
	protected void writeAdditional(CompoundNBT compound) {
		compound.putInt("Fuse", fuse);
		compound.putInt("type", blastOrdinal);
	}

	@Override
	protected void readAdditional(CompoundNBT compound) {
		fuse = compound.getInt("Fuse");
		blastOrdinal = compound.getInt("type");
	}

	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
