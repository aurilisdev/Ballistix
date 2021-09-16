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
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntityMissile extends Entity {
    private static final DataParameter<Integer> TYPE = EntityDataManager.createKey(EntityMissile.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> RANGE = EntityDataManager.createKey(EntityMissile.class, DataSerializers.VARINT);
    public BlockPos target;
    public int blastOrdinal = -1;
    public int range = -1;
    public boolean isItem = false;

    public EntityMissile(EntityType<? extends EntityMissile> type, World worldIn) {
	super(type, worldIn);
	preventEntitySpawning = true;
    }

    public EntityMissile(World worldIn) {
	this(DeferredRegisters.ENTITY_MISSILE.get(), worldIn);
    }

    public void setBlastType(SubtypeBlast explosive) {
	blastOrdinal = explosive.ordinal();
    }

    public SubtypeBlast getBlastType() {
	return blastOrdinal == -1 ? null : SubtypeBlast.values()[blastOrdinal];
    }

    @Override
    protected void registerData() {
	dataManager.register(TYPE, -1);
	dataManager.register(RANGE, -1);
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
	return super.getRenderBoundingBox().expand(20, 20, 20);
    }

    @Override
    public void tick() {
	if (getMotion().length() > 0) {
	    rotationPitch = (float) (Math
		    .atan(getMotion().getY() / Math.sqrt(getMotion().getX() * getMotion().getX() + getMotion().getZ() * getMotion().getZ())) * 180.0D
		    / Math.PI);
	    rotationYaw = (float) (Math.atan2(getMotion().getX(), getMotion().getZ()) * 180.0D / Math.PI);
	} else {
	    rotationPitch = rotationYaw = 90;
	}
	if (ticksExisted < 10 && !isItem) {
	    setPosition(getPosX() + getMotion().x, getPosY() + getMotion().y, getPosZ() + getMotion().z);
	} else {
	    move(MoverType.SELF, getMotion());
	}
	if (!world.isRemote) {
	    dataManager.set(TYPE, blastOrdinal);
	    dataManager.set(RANGE, range);
	} else {
	    blastOrdinal = dataManager.get(TYPE);
	    range = dataManager.get(RANGE);
	}
	if (!world.isRemote) {
	    if (onGround || collidedHorizontally || collidedVertically
		    || !isItem && target != null && getPosY() < target.getY() && getMotion().getY() < 0) {
		setDead();
		if (blastOrdinal != -1) {
		    SubtypeBlast explosive = SubtypeBlast.values()[blastOrdinal];
		    Blast b = Blast.createFromSubtype(explosive, world, getPosition());
		    if (b != null) {
			b.performExplosion();
		    }
		}
	    }
	    if (!isItem && getPosY() > 500) {
		if (target == null) {
		    setDead();
		} else {
		    setPosition(target.getX(), 499, target.getZ());
		    setMotion(0, -3f, 0);
		}
	    }
	}
	if (!isItem && target != null && getMotion().y < 3 && getMotion().y >= 0) {
	    setMotion(getMotion().add(0, 0.02, 0));
	}
	for (int i = 0; i < 5; i++) {
	    float str = world.rand.nextFloat() * 0.25f;
	    float ranX = str * (world.rand.nextFloat() - 0.5f);
	    float ranY = str * (world.rand.nextFloat() - 0.5f);
	    float ranZ = str * (world.rand.nextFloat() - 0.5f);
	    float x = (float) (getPosX() - getSize(getPose()).width / 1.0f);
	    float y = (float) (getPosY() + getSize(getPose()).height / 1.0f);
	    float z = (float) (getPosZ() - getSize(getPose()).width / 1.0f);
	    world.addParticle(ParticleTypes.LARGE_SMOKE, x + ranX, y + ranY, z + ranZ, -getMotion().x + ranX, -getMotion().y - 0.075f + ranY,
		    -getMotion().z + ranZ);
	}
    }

    @Override
    protected boolean canBeRidden(Entity entityIn) {
	return true;
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
	compound.putInt("type", blastOrdinal);
	compound.putInt("range", range);
	compound.putBoolean("isItem", isItem);
	if (target != null) {
	    compound.putInt("targetX", target.getX());
	    compound.putInt("targetY", target.getY());
	    compound.putInt("targetZ", target.getZ());
	} else {
	    compound.putInt("targetX", 0);
	    compound.putInt("targetY", 0);
	    compound.putInt("targetZ", 0);
	}

    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
	blastOrdinal = compound.getInt("type");
	range = compound.getInt("range");
	isItem = compound.getBoolean("isItem");
	if (blastOrdinal != -1) {
	    setBlastType(getBlastType());
	}
	target = new BlockPos(compound.getInt("targetX"), compound.getInt("targetY"), compound.getInt("targetZ"));
    }

    @Override
    public IPacket<?> createSpawnPacket() {
	return NetworkHooks.getEntitySpawningPacket(this);
    }
}
