package ballistix.common.entity;

import java.util.List;

import ballistix.DeferredRegisters;
import ballistix.api.damage.DamageSourceShrapnel;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Explosion;
import net.minecraft.world.Explosion.Mode;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntityShrapnel extends ThrowableEntity {
    private static final DataParameter<Boolean> ISEXPLOSIVE = EntityDataManager.createKey(EntityShrapnel.class, DataSerializers.BOOLEAN);
    public boolean isExplosive = false;

    public EntityShrapnel(EntityType<? extends EntityShrapnel> type, World worldIn) {
	super(type, worldIn);
    }

    public EntityShrapnel(World worldIn) {
	this(DeferredRegisters.ENTITY_SHRAPNEL.get(), worldIn);
    }

    @Override
    protected void registerData() {
	dataManager.register(ISEXPLOSIVE, false);
    }

    @Override
    public void tick() {
	if (!world.isRemote) {
	    dataManager.set(ISEXPLOSIVE, isExplosive);
	} else {
	    isExplosive = dataManager.get(ISEXPLOSIVE);
	}
	if (!hasNoGravity()) {
	    this.setMotion(getMotion().add(0.0D, -0.04D, 0.0D));
	}
	this.forceSetPosition(getPosX() + getMotion().x, getPosY() + getMotion().y, getPosZ() + getMotion().z);
	EntitySize size = getSize(Pose.STANDING);
	setBoundingBox(new AxisAlignedBB(getPosX() - size.width * 2, getPosY() - size.height * 2, getPosZ() - size.width * 2,
		getPosX() + size.width * 2, getPosY() + size.height * 2, getPosZ() + size.width * 2));
	if (onGround || ticksExisted > (isExplosive ? 400 : 100) || world.getBlockState(getPosition()).getMaterial().blocksMovement()) {
	    remove();
	}
	if (!world.isRemote) {
	    List<LivingEntity> livings = world.getEntitiesWithinAABB(LivingEntity.class, getBoundingBox());
	    for (LivingEntity living : livings) {
		living.attackEntityFrom(DamageSourceShrapnel.INSTANCE, 10);
		remove();
	    }
	}
    }

    @Override
    public void func_234612_a_(Entity entity, float pitch, float yaw, float par4, float force, float par6) {
	float f = -MathHelper.sin(yaw * ((float) Math.PI / 180F)) * MathHelper.cos(pitch * ((float) Math.PI / 180F));
	float f1 = -MathHelper.sin((pitch + par4) * ((float) Math.PI / 180F));
	float f2 = MathHelper.cos(yaw * ((float) Math.PI / 180F)) * MathHelper.cos(pitch * ((float) Math.PI / 180F));
	shoot(f, f1, f2, force, par6);
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
	compound.putBoolean("type", isExplosive);
    }

    @Override
    public void remove() {
	if (isExplosive) {
	    Explosion ex = new Explosion(world, this, DamageSourceShrapnel.INSTANCE, null, getPosX(), getPosY(), getPosZ(), 2, true, Mode.DESTROY);
	    if (!world.isRemote) {
		int explosionRadius = 2;
		for (int i = -explosionRadius; i <= explosionRadius; i++) {
		    for (int j = -explosionRadius; j <= explosionRadius; j++) {
			for (int k = -explosionRadius; k <= explosionRadius; k++) {
			    int idistance = i * i + j * j + k * k;
			    if (idistance <= explosionRadius * explosionRadius && world.rand.nextFloat()
				    * (explosionRadius * explosionRadius) < explosionRadius * explosionRadius * 1.85 - idistance) {
				BlockPos pos = new BlockPos(getPosX() + i, getPosY() + j, getPosZ() + k);
				BlockState block = world.getBlockState(pos);
				if (block != Blocks.AIR.getDefaultState() && block != Blocks.VOID_AIR.getDefaultState()
					&& block.getExplosionResistance(world, pos, ex) < explosionRadius * 2 * world.rand.nextFloat()) {
				    block.onBlockExploded(world, pos, ex);
				}
			    }
			}
		    }
		}
	    }
	    ex.doExplosionB(true);
	}
	super.remove();
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
	isExplosive = compound.getBoolean("type");
    }

    @Override
    public IPacket<?> createSpawnPacket() {
	return NetworkHooks.getEntitySpawningPacket(this);
    }
}
