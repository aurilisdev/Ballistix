package ballistix.api.missile.virtual;

import ballistix.api.missile.MissileManager;
import ballistix.common.entity.EntityBullet;
import ballistix.common.entity.EntityRailgunRound;
import ballistix.common.entity.EntitySAM;
import ballistix.registers.BallistixDamageTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.UUID;

public abstract class VirtualProjectile {

    public final float speed;
    public Vec3 position;
    public Vec3 deltaMovement;
    public final float range;
    public final Vector3f rotation;
    public final boolean canHitPlayers;
    public final UUID id;
    protected boolean hasExploded = false;
    protected boolean isSpawned = false;
    public float distanceTraveled = 0.0F;
    protected int tickCount = 0;
    protected int entityId = -1;

    protected VirtualProjectile(float speed, Vec3 position, Vec3 deltaMovement, float range, Vector3f rotation, boolean canHitPlayers, float distanceTraveled, UUID id, boolean hasExploded, boolean isSpawned, int entityId) {
        this(speed, position, deltaMovement, range, rotation, canHitPlayers, id);
        this.distanceTraveled = distanceTraveled;
        this.hasExploded = hasExploded;
        this.isSpawned = isSpawned;
    }

    public VirtualProjectile(float speed, Vec3 position, Vec3 deltaMovement, float range, Vector3f rotation, boolean canHitPlayers, UUID id) {
        this.speed = speed;
        this.position = position;
        this.deltaMovement = deltaMovement;
        this.range = range;
        this.rotation = rotation;
        this.canHitPlayers = canHitPlayers;
        this.id = id;
    }

    // only ticks on server
    public void tick(ServerLevel level) {
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

        Vec3 movement = deltaMovement;

        for (int i = 0; i < speed; i++) {

            position = new Vec3(position.x + movement.x, position.y + movement.y, position.z + movement.z);


            BlockState state = level.getBlockState(blockPosition());

            if (!state.getCollisionShape(level, blockPosition()).isEmpty() && tickCount > 5) {
                level.destroyBlock(blockPosition(), false);
                hasExploded = true;
                return;
            }

            if (isServer) {
                AABB box = getBoundingBox().inflate(1);

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

        if(!isSpawned && level.hasChunkAt(blockPosition()) && level.isPositionEntityTicking(blockPosition())) {
            Entity entity = makeNewEntity(level);
            if(level.addFreshEntity(entity)) {
                setSpawned(true, entity.getId());
            }
        }

        if(isSpawned && (!level.hasChunkAt(blockPosition()) || level.getEntity(entityId) == null)) {
            setSpawned(false, -1);
        }
    }

    public abstract void onHitMissile(Level world, VirtualMissile missile);

    public void onHitLiving(Level world, LivingEntity entity) {

    }

    public abstract AABB getBoundingBox();

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

    public abstract Entity makeNewEntity(Level world);

    public static class VirtualBullet extends VirtualProjectile {

        public static final Codec<VirtualBullet> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.FLOAT.fieldOf("speed").forGetter(instance0 -> instance0.speed),
                Vec3.CODEC.fieldOf("position").forGetter(instance0 -> instance0.position),
                Vec3.CODEC.fieldOf("movement").forGetter(instance0 -> instance0.deltaMovement),
                Codec.FLOAT.fieldOf("range").forGetter(instance0 -> instance0.range),
                ExtraCodecs.VECTOR3F.fieldOf("rotation").forGetter(instance0 -> instance0.rotation),
                Codec.FLOAT.fieldOf("distancetraveled").forGetter(instance0 -> instance0.distanceTraveled),
                UUIDUtil.CODEC.fieldOf("id").forGetter(instance0 -> instance0.id),
                Codec.BOOL.fieldOf("hasexploded").forGetter(instance0 -> instance0.hasExploded),
                Codec.BOOL.fieldOf("hasspawned").forGetter(instance0 -> instance0.isSpawned),
                Codec.INT.fieldOf("entityid").forGetter(instance0 -> instance0.entityId)
        ).apply(instance, VirtualBullet::new));

        protected VirtualBullet(float speed, Vec3 position, Vec3 deltaMovement, float range, Vector3f rotation, float distanceTraveled, UUID id, boolean hasExploded, boolean isSpawned, int entityId) {
            super(speed, position, deltaMovement, range, rotation, true, distanceTraveled, id, hasExploded, isSpawned, entityId);
        }

        public VirtualBullet(float speed, Vec3 position, Vec3 deltaMovement, float range, Vector3f rotation) {
            super(speed, position, deltaMovement, range, rotation, true, UUID.randomUUID());
        }

        @Override
        public void onHitMissile(Level world, VirtualMissile missile) {
            missile.health = missile.health - 1.0F;
        }

        @Override
        public void onHitLiving(Level world, LivingEntity entity) {
            entity.hurt(entity.damageSources().source(BallistixDamageTypes.CIWS_BULLET), 10);
        }

        @Override
        public AABB getBoundingBox() {
            return new AABB(position.x - 0.05F, position.y, position.z - 0.05F, position.x + 0.05F, position.y + 0.1F, position.z + 0.05F);
        }

        @Override
        public Entity makeNewEntity(Level world) {
            EntityBullet bullet = new EntityBullet(world);
            bullet.setPos(position);
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
                Vec3.CODEC.fieldOf("position").forGetter(instance0 -> instance0.position),
                Vec3.CODEC.fieldOf("movement").forGetter(instance0 -> instance0.deltaMovement),
                Codec.FLOAT.fieldOf("range").forGetter(instance0 -> instance0.range),
                ExtraCodecs.VECTOR3F.fieldOf("rotation").forGetter(instance0 -> instance0.rotation),
                Codec.FLOAT.fieldOf("distancetraveled").forGetter(instance0 -> instance0.distanceTraveled),
                UUIDUtil.CODEC.fieldOf("id").forGetter(instance0 -> instance0.id),
                Codec.BOOL.fieldOf("hasexploded").forGetter(instance0 -> instance0.hasExploded),
                Codec.BOOL.fieldOf("hasspawned").forGetter(instance0 -> instance0.isSpawned),
                Codec.INT.fieldOf("entityid").forGetter(instance0 -> instance0.entityId)
        ).apply(instance, VirtualRailgunRound::new));

        protected VirtualRailgunRound(float speed, Vec3 position, Vec3 deltaMovement, float range, Vector3f rotation, float distanceTraveled, UUID id, boolean hasExploded, boolean isSpawned, int entityId) {
            super(speed, position, deltaMovement, range, rotation, true, distanceTraveled, id, hasExploded, isSpawned, entityId);
        }
        public VirtualRailgunRound(float speed, Vec3 position, Vec3 deltaMovement, float range, Vector3f rotation) {
            super(speed, position, deltaMovement, range, rotation, true, UUID.randomUUID());
        }

        @Override
        public void onHitMissile(Level world, VirtualMissile missile) {
            MissileManager.removeMissile(world.dimension(), missile.getId());
        }

        @Override
        public void onHitLiving(Level world, LivingEntity entity) {
            entity.hurt(entity.damageSources().source(BallistixDamageTypes.RAILGUN_ROUND), 20);
        }

        @Override
        public AABB getBoundingBox() {
            return new AABB(position.x - 0.05F, position.y, position.z - 0.05F, position.x + 0.05F, position.y + 0.1F, position.z + 0.05F);
        }

        @Override
        public Entity makeNewEntity(Level world) {
            EntityRailgunRound railgunround = new EntityRailgunRound(world);
            railgunround.setPos(position);
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
                Vec3.CODEC.fieldOf("position").forGetter(instance0 -> instance0.position),
                Vec3.CODEC.fieldOf("movement").forGetter(instance0 -> instance0.deltaMovement),
                Codec.FLOAT.fieldOf("range").forGetter(instance0 -> instance0.range),
                ExtraCodecs.VECTOR3F.fieldOf("rotation").forGetter(instance0 -> instance0.rotation),
                Codec.FLOAT.fieldOf("distancetraveled").forGetter(instance0 -> instance0.distanceTraveled),
                UUIDUtil.CODEC.fieldOf("id").forGetter(instance0 -> instance0.id),
                Codec.BOOL.fieldOf("hasexploded").forGetter(instance0 -> instance0.hasExploded),
                Codec.BOOL.fieldOf("hasspawned").forGetter(instance0 -> instance0.isSpawned),
                Codec.INT.fieldOf("entityid").forGetter(instance0 -> instance0.entityId)
        ).apply(instance, VirtualSAM::new));

        protected VirtualSAM(float speed, Vec3 position, Vec3 deltaMovement, float range, Vector3f rotation, float distanceTraveled, UUID id, boolean hasExploded, boolean isSpawned, int entityId) {
            super(speed, position, deltaMovement, range, rotation, true, distanceTraveled, id, hasExploded, isSpawned, entityId);
        }
        public VirtualSAM(float speed, Vec3 position, Vec3 deltaMovement, float range, Vector3f rotation) {
            super(speed, position, deltaMovement, range, rotation, false, UUID.randomUUID());
        }

        @Override
        public void onHitMissile(Level world, VirtualMissile missile) {
            MissileManager.removeMissile(world.dimension(), missile.getId());
        }

        @Override
        public AABB getBoundingBox() {
            return new AABB(position.x - 0.25F, position.y, position.z - 0.25F, position.x + 0.25F, position.y + 0.5F, position.z + 0.25F);
        }

        @Override
        public Entity makeNewEntity(Level world) {
            EntitySAM sam = new EntitySAM(world);
            sam.setPos(position);
            sam.setDeltaMovement(deltaMovement);
            sam.rotation = rotation;
            sam.id = id;
            sam.speed = speed;
            return sam;
        }
    }


}
