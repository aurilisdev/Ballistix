package ballistix.api.missile.virtual;

import java.util.UUID;

import ballistix.client.particle.ParticleOptionsMissileSmoke;
import ballistix.common.settings.Constants;
import ballistix.common.tile.radar.TileFireControlRadar;
import ballistix.common.tile.turret.GenericTileTurret;
import ballistix.common.tile.turret.antimissile.TileTurretSAM;
import electrodynamics.Electrodynamics;
import electrodynamics.prefab.utilities.BlockEntityUtils;
import net.minecraft.client.Minecraft;
import org.joml.Vector3f;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import ballistix.api.missile.MissileManager;
import ballistix.common.entity.EntityBullet;
import ballistix.common.entity.EntityRailgunRound;
import ballistix.common.entity.EntitySAM;
import ballistix.registers.BallistixDamageTypes;
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

import javax.annotation.Nullable;

public abstract class VirtualProjectile {

    public float speed;
    public Vec3 position;
    public Vec3 deltaMovement;
    public final float range;
    public Vector3f rotation;
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
        this.entityId = entityId;
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

        BlockPos projected = projectMovementForCollision(level);

        if(projected != null) {
            BlockState state = level.getBlockState(projected);

            if (!state.getCollisionShape(level, projected).isEmpty() && tickCount > 5) {
                level.destroyBlock(projected, false);
                hasExploded = true;
                return;
            }
        }

        updatePosition(level);

        AABB box = getBoundingBox().inflate(speed);

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

    public void updatePosition(ServerLevel level) {
        position = new Vec3(position.x + deltaMovement.x * speed, position.y + deltaMovement.y * speed, position.z + deltaMovement.z * speed);
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

    @Nullable
    public BlockPos projectMovementForCollision(ServerLevel world) {

        Vec3 currPos = position.scale(1.0);

        int iterations = Math.abs((int) Math.ceil(speed));

        BlockPos pos;
        BlockState state;

        for (int i = 0; i < iterations; i++) {

            pos = new BlockPos((int) Math.floor(currPos.x), (int) Math.floor(currPos.y), (int) Math.floor(currPos.z));
            state = world.getBlockState(pos);

            if (state.getCollisionShape(world, blockPosition()).isEmpty()) {
                currPos.add(deltaMovement);
                continue;
            }

            return pos;

        }

        return null;

    }

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
                Codec.INT.fieldOf("entityid").forGetter(instance0 -> instance0.entityId),
                BlockPos.CODEC.fieldOf("radarpos").forGetter(instance0 -> instance0.radarPos)
        ).apply(instance, VirtualSAM::new));

        private BlockPos radarPos = BlockEntityUtils.OUT_OF_REACH;
        private TileFireControlRadar radar = null;

        protected VirtualSAM(float speed, Vec3 position, Vec3 deltaMovement, float range, Vector3f rotation, float distanceTraveled, UUID id, boolean hasExploded, boolean isSpawned, int entityId, BlockPos radarPos) {
            super(speed, position, deltaMovement, range, rotation, true, distanceTraveled, id, hasExploded, isSpawned, entityId);
            this.radarPos = radarPos;
        }
        public VirtualSAM(float speed, Vec3 position, Vec3 deltaMovement, float range, Vector3f rotation, BlockPos radarPos) {
            super(speed, position, deltaMovement, range, rotation, false, UUID.randomUUID());
            this.radarPos = radarPos;
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

        @Override
        public void updatePosition(ServerLevel level) {

            if(radarPos == null || radarPos.equals(BlockEntityUtils.OUT_OF_REACH) || speed < TileTurretSAM.MAX_SPEED / 4.0) {
                super.updatePosition(level);
                return;
            }

            if(radar == null && level.getBlockEntity(radarPos) instanceof TileFireControlRadar radar) {
                this.radar = radar;
            }

            if(radar != null && radar.isRemoved()) {
                radar = null;
            }

            if(radar == null || radar.isRemoved() || radar.tracking == null || radar.tracking.hasExploded()) {
                super.updatePosition(level);
                return;
            }

            VirtualMissile tracking = radar.tracking;

            float trackingSpeed = 0F;//radar.tracking.speed;
            Vec3 trackingVector = tracking.deltaMovement;

            double timeToIntercept = TileFireControlRadar.getTimeToIntercept(tracking.position, trackingVector, trackingSpeed, TileTurretSAM.MAX_SPEED, position);

            if (timeToIntercept <= 0) {
                super.updatePosition(level);
                return;
            }

            Vec3 interceptionPos = tracking.position.add(trackingVector.scale(trackingSpeed).scale(timeToIntercept));

            double deltaX = interceptionPos.x - position.x;
            double deltaY = interceptionPos.y - position.y;
            double deltaZ = interceptionPos.z - position.z;

            double sumXZ = deltaX * deltaX + deltaZ * deltaZ;

            double magXZ = Math.sqrt(sumXZ);

            if(magXZ <= 0) {
                magXZ = 1;
            }

            double thetaY = Math.atan(deltaY / magXZ);

            deltaMovement = new Vec3(deltaX, deltaY, deltaZ).normalize();
            Vec3 desiredRot = new Vec3(deltaX / magXZ, Math.sin(thetaY), deltaZ / magXZ);

            double thetaDesiredXZ = GenericTileTurret.getXZAngleRadians(desiredRot);
            double thetaCurrXZ = GenericTileTurret.getXZAngleRadians(new Vec3(rotation.x, rotation.y, rotation.z));

            double angleDifXZ = thetaDesiredXZ - thetaCurrXZ;

            double dY = desiredRot.y - rotation.y;

            if (dY < 0) {
                rotation = rotation.add(0, (float) (-Math.cos(Constants.SAM_ENTITY_TURNINGSPEEDRADIANS) * 0.125), 0);
                if (rotation.y < desiredRot.y) {
                    rotation = new Vector3f(rotation.x, (float) desiredRot.y, rotation.z);
                }
            } else if (dY > 0) {
                rotation = rotation.add(0, (float) (Math.cos(Constants.SAM_ENTITY_TURNINGSPEEDRADIANS) * 0.125), 0);

                if (rotation.y > desiredRot.y) {
                    rotation = new Vector3f(rotation.x, (float) desiredRot.y, rotation.z);
                }
            }

            if (angleDifXZ >= 0) {

                thetaCurrXZ += Constants.SAM_ENTITY_TURNINGSPEEDRADIANS;

            } else {

                thetaCurrXZ -= Constants.SAM_ENTITY_TURNINGSPEEDRADIANS;

            }

            //thetaCurrXZ = getXZAngleRadians(rotation);

            if (angleDifXZ >= 0 && thetaCurrXZ > thetaDesiredXZ) {

                rotation = new Vector3f((float) desiredRot.x, rotation.y, (float) desiredRot.z);

            } else if (angleDifXZ < 0 && thetaCurrXZ < thetaDesiredXZ) {

                rotation = new Vector3f((float) desiredRot.x, rotation.y, (float) desiredRot.z);

            } else {
                rotation = new Vector3f((float) Math.cos(thetaCurrXZ), rotation.y, (float) Math.sin(thetaCurrXZ));
            }

            super.updatePosition(level);

        }

        @Override
        public void tick(ServerLevel level) {
            super.tick(level);

            if(speed < TileTurretSAM.MAX_SPEED) {
                speed += 0.02F;
            }

            float x = (float) position.x;
            float y = (float) position.y;
            float z = (float) position.z;
            float motionX = (float) (speed * deltaMovement.x);
            float motionY = (float) (speed * deltaMovement.y);
            float motionZ = (float) (speed * deltaMovement.z);
            x -= motionX;
            y -= motionY;
            z -= motionZ;
            for (int i = 0; i < 3; i++) {
                Minecraft.getInstance().particleEngine.createParticle(new ParticleOptionsMissileSmoke().setParameters(1, 1, 1, 0.3f * 1, 50, true), x, y, z,
                        -motionX * (0.4 + 0.2 * Electrodynamics.RANDOM.nextDouble()),
                        -motionY * (0.4 + 0.2 * Electrodynamics.RANDOM.nextDouble()),
                        -motionZ * (0.4 + 0.2 * Electrodynamics.RANDOM.nextDouble()));

            }

        }
    }


}
