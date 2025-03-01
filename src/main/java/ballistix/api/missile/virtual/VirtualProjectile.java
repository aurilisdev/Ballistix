package ballistix.api.missile.virtual;

import ballistix.api.damage.DamageSourceCiwsBullet;
import ballistix.api.damage.DamageSourceRailgunRound;
import ballistix.api.missile.MissileManager;
import ballistix.common.entity.EntityBullet;
import ballistix.common.entity.EntityRailgunRound;
import ballistix.common.entity.EntitySAM;
import electrodynamics.prefab.utilities.CodecUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.UUID;

public abstract class VirtualProjectile {

    public final float speed;
    public Vector3d position;
    public Vector3d deltaMovement;
    public final float range;
    public final Vector3f rotation;
    public final boolean canHitPlayers;
    public final UUID id;
    protected boolean hasExploded = false;
    protected boolean isSpawned = false;
    public float distanceTraveled = 0.0F;
    protected int tickCount = 0;
    protected int entityId = -1;

    protected VirtualProjectile(float speed, Vector3d position, Vector3d deltaMovement, float range, Vector3f rotation, boolean canHitPlayers, float distanceTraveled, UUID id, boolean hasExploded, boolean isSpawned, int entityId) {
        this(speed, position, deltaMovement, range, rotation, canHitPlayers, id);
        this.distanceTraveled = distanceTraveled;
        this.hasExploded = hasExploded;
        this.isSpawned = isSpawned;
    }

    public VirtualProjectile(float speed, Vector3d position, Vector3d deltaMovement, float range, Vector3f rotation, boolean canHitPlayers, UUID id) {
        this.speed = speed;
        this.position = position;
        this.deltaMovement = deltaMovement;
        this.range = range;
        this.rotation = rotation;
        this.canHitPlayers = canHitPlayers;
        this.id = id;
    }

    // only ticks on server
    public void tick(ServerWorld level) {
        boolean isClient = level.isClientSide();

        boolean isServer = !isClient;

        tickCount++;

        if (tickCount > 30 && deltaMovement.length() <= 0) {
            hasExploded = true;
            return;
        }

        if (distanceTraveled >= range + 5) {
            hasExploded = true;
            return;
        }

        if(hasExploded) {
            return;
        }

        Vector3d movement = deltaMovement;

        for (int i = 0; i < speed; i++) {

            position = new Vector3d(position.x + movement.x, position.y + movement.y, position.z + movement.z);


            BlockState state = level.getBlockState(blockPosition());

            if (!state.getCollisionShape(level, blockPosition()).isEmpty() && tickCount > 5) {
                level.destroyBlock(blockPosition(), false);
                hasExploded = true;
                return;
            }

            if (isServer) {
                AxisAlignedBB box = getBoundingBox().inflate(1);

                for (VirtualMissile missile : MissileManager.getMissilesForLevel(level.dimension())) {

                    if (!missile.hasExploded() && missile.getBoundingBox().intersects(box)) {
                        onHitMissile(level, missile);
                        hasExploded = true;
                        return;
                    }

                }

                if (canHitPlayers) {
                    LivingEntity selected = null;
                    double lastMag = 0;

                    for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, box)) {

                        double deltaX = entity.getX() - position.x;
                        double deltaY = entity.getY() - position.y;
                        double deltaZ = entity.getZ() - position.z;

                        double mag = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

                        if (selected == null) {
                            selected = entity;
                            lastMag = mag;
                        } else if (mag < lastMag) {
                            selected = entity;
                        }

                    }

                    if (selected != null) {
                        onHitLiving(level, selected);
                        hasExploded = true;
                        return;
                    }
                }


            }

        }

        distanceTraveled += speed;

        if(!isSpawned && level.hasChunkAt(blockPosition()) && level.getChunkSource().isEntityTickingChunk(new ChunkPos(blockPosition()))) {
            Entity entity = makeNewEntity(level);
            if(level.addFreshEntity(entity)) {
                setSpawned(true, entity.getId());
            }
        }

        if(isSpawned && (!level.hasChunkAt(blockPosition()) || level.getEntity(entityId) == null)) {
            setSpawned(false, -1);
        }
    }

    public abstract void onHitMissile(World world, VirtualMissile missile);

    public void onHitLiving(World world, LivingEntity entity) {

    }

    public abstract AxisAlignedBB getBoundingBox();

    public BlockPos blockPosition() {
        return new BlockPos((int) Math.floor(position.x), (int) Math.floor(position.y), (int) Math.floor(position.z));
    }

    public void setSpawned(boolean spawned, int id) {
        isSpawned = spawned;
        entityId = id;
    }

    public boolean hasExploded() {
        return hasExploded;
    }

    public abstract Entity makeNewEntity(World world);

    public static class VirtualBullet extends VirtualProjectile {

        public static final Codec<VirtualBullet> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.FLOAT.fieldOf("speed").forGetter(instance0 -> instance0.speed),
                CodecUtils.VEC3_CODEC.fieldOf("position").forGetter(instance0 -> instance0.position),
                CodecUtils.VEC3_CODEC.fieldOf("movement").forGetter(instance0 -> instance0.deltaMovement),
                Codec.FLOAT.fieldOf("range").forGetter(instance0 -> instance0.range),
                CodecUtils.VECTOR3F_CODEC.fieldOf("rotation").forGetter(instance0 -> instance0.rotation),
                Codec.FLOAT.fieldOf("distancetraveled").forGetter(instance0 -> instance0.distanceTraveled),
                CodecUtils.UUID_CODEC.fieldOf("id").forGetter(instance0 -> instance0.id),
                Codec.BOOL.fieldOf("hasexploded").forGetter(instance0 -> instance0.hasExploded),
                Codec.BOOL.fieldOf("hasspawned").forGetter(instance0 -> instance0.isSpawned),
                Codec.INT.fieldOf("entityid").forGetter(instance0 -> instance0.entityId)
        ).apply(instance, VirtualBullet::new));

        protected VirtualBullet(float speed, Vector3d position, Vector3d deltaMovement, float range, Vector3f rotation, float distanceTraveled, UUID id, boolean hasExploded, boolean isSpawned, int entityId) {
            super(speed, position, deltaMovement, range, rotation, true, distanceTraveled, id, hasExploded, isSpawned, entityId);
        }

        public VirtualBullet(float speed, Vector3d position, Vector3d deltaMovement, float range, Vector3f rotation) {
            super(speed, position, deltaMovement, range, rotation, true, UUID.randomUUID());
        }

        @Override
        public void onHitMissile(World world, VirtualMissile missile) {
            missile.health = missile.health - 1.0F;
        }

        @Override
        public void onHitLiving(World world, LivingEntity entity) {
            entity.hurt(DamageSourceCiwsBullet.INSTANCE, 10);
        }

        @Override
        public AxisAlignedBB getBoundingBox() {
            return new AxisAlignedBB(position.x - 0.05F, position.y, position.z - 0.05F, position.x + 0.05F, position.y + 0.1F, position.z + 0.05F);
        }

        @Override
        public Entity makeNewEntity(World world) {
            EntityBullet bullet = new EntityBullet(world);
            bullet.setPos(position.x, position.y, position.z);
            bullet.setDeltaMovement(deltaMovement);
            bullet.rotation = rotation;
            bullet.id = id;
            bullet.speed = speed;
            return bullet;
        }
    }

    public static class VirtualRailgunRound extends VirtualProjectile {

        public static final Codec<VirtualRailgunRound> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.FLOAT.fieldOf("speed").forGetter(instance0 -> instance0.speed),
                CodecUtils.VEC3_CODEC.fieldOf("position").forGetter(instance0 -> instance0.position),
                CodecUtils.VEC3_CODEC.fieldOf("movement").forGetter(instance0 -> instance0.deltaMovement),
                Codec.FLOAT.fieldOf("range").forGetter(instance0 -> instance0.range),
                CodecUtils.VECTOR3F_CODEC.fieldOf("rotation").forGetter(instance0 -> instance0.rotation),
                Codec.FLOAT.fieldOf("distancetraveled").forGetter(instance0 -> instance0.distanceTraveled),
                CodecUtils.UUID_CODEC.fieldOf("id").forGetter(instance0 -> instance0.id),
                Codec.BOOL.fieldOf("hasexploded").forGetter(instance0 -> instance0.hasExploded),
                Codec.BOOL.fieldOf("hasspawned").forGetter(instance0 -> instance0.isSpawned),
                Codec.INT.fieldOf("entityid").forGetter(instance0 -> instance0.entityId)
        ).apply(instance, VirtualRailgunRound::new));

        protected VirtualRailgunRound(float speed, Vector3d position, Vector3d deltaMovement, float range, Vector3f rotation, float distanceTraveled, UUID id, boolean hasExploded, boolean isSpawned, int entityId) {
            super(speed, position, deltaMovement, range, rotation, true, distanceTraveled, id, hasExploded, isSpawned, entityId);
        }
        public VirtualRailgunRound(float speed, Vector3d position, Vector3d deltaMovement, float range, Vector3f rotation) {
            super(speed, position, deltaMovement, range, rotation, true, UUID.randomUUID());
        }

        @Override
        public void onHitMissile(World world, VirtualMissile missile) {
            MissileManager.removeMissile(world.dimension(), missile.getId());
        }

        @Override
        public void onHitLiving(World world, LivingEntity entity) {
            entity.hurt(DamageSourceRailgunRound.INSTANCE, 20);
        }

        @Override
        public AxisAlignedBB getBoundingBox() {
            return new AxisAlignedBB(position.x - 0.05F, position.y, position.z - 0.05F, position.x + 0.05F, position.y + 0.1F, position.z + 0.05F);
        }

        @Override
        public Entity makeNewEntity(World world) {
            EntityRailgunRound railgunround = new EntityRailgunRound(world);
            railgunround.setPos(position.x, position.y, position.z);
            railgunround.setDeltaMovement(deltaMovement);
            railgunround.rotation = rotation;
            railgunround.id = id;
            railgunround.speed = speed;
            return railgunround;
        }
    }

    public static class VirtualSAM extends VirtualProjectile {

        public static final Codec<VirtualSAM> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.FLOAT.fieldOf("speed").forGetter(instance0 -> instance0.speed),
                CodecUtils.VEC3_CODEC.fieldOf("position").forGetter(instance0 -> instance0.position),
                CodecUtils.VEC3_CODEC.fieldOf("movement").forGetter(instance0 -> instance0.deltaMovement),
                Codec.FLOAT.fieldOf("range").forGetter(instance0 -> instance0.range),
                CodecUtils.VECTOR3F_CODEC.fieldOf("rotation").forGetter(instance0 -> instance0.rotation),
                Codec.FLOAT.fieldOf("distancetraveled").forGetter(instance0 -> instance0.distanceTraveled),
                CodecUtils.UUID_CODEC.fieldOf("id").forGetter(instance0 -> instance0.id),
                Codec.BOOL.fieldOf("hasexploded").forGetter(instance0 -> instance0.hasExploded),
                Codec.BOOL.fieldOf("hasspawned").forGetter(instance0 -> instance0.isSpawned),
                Codec.INT.fieldOf("entityid").forGetter(instance0 -> instance0.entityId)
        ).apply(instance, VirtualSAM::new));

        protected VirtualSAM(float speed, Vector3d position, Vector3d deltaMovement, float range, Vector3f rotation, float distanceTraveled, UUID id, boolean hasExploded, boolean isSpawned, int entityId) {
            super(speed, position, deltaMovement, range, rotation, true, distanceTraveled, id, hasExploded, isSpawned, entityId);
        }
        public VirtualSAM(float speed, Vector3d position, Vector3d deltaMovement, float range, Vector3f rotation) {
            super(speed, position, deltaMovement, range, rotation, false, UUID.randomUUID());
        }

        @Override
        public void onHitMissile(World world, VirtualMissile missile) {
            MissileManager.removeMissile(world.dimension(), missile.getId());
        }

        @Override
        public AxisAlignedBB getBoundingBox() {
            return new AxisAlignedBB(position.x - 0.25F, position.y, position.z - 0.25F, position.x + 0.25F, position.y + 0.5F, position.z + 0.25F);
        }

        @Override
        public Entity makeNewEntity(World world) {
            EntitySAM sam = new EntitySAM(world);
            sam.setPos(position.x, position.y, position.z);
            sam.setDeltaMovement(deltaMovement);
            sam.rotation = rotation;
            sam.id = id;
            sam.speed = speed;
            return sam;
        }
    }


}
