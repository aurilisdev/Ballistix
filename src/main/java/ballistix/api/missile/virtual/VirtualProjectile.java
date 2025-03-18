package ballistix.api.missile.virtual;

import java.util.UUID;

import ballistix.client.particle.ParticleOptionsMissileSmoke;
import ballistix.common.settings.Constants;
import ballistix.common.tile.radar.TileFireControlRadar;
import electrodynamics.Electrodynamics;
import electrodynamics.prefab.utilities.BlockEntityUtils;
import electrodynamics.registers.ElectrodynamicsSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Blocks;

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
    public final boolean canHitPlayers;
    public final UUID id;
    protected boolean hasExploded = false;
    protected boolean isSpawned = false;
    public float distanceTraveled = 0.0F;
    protected int tickCount = 0;
    protected int entityId = -1;

    protected VirtualProjectile(float speed, Vec3 position, Vec3 deltaMovement, float range, boolean canHitPlayers, float distanceTraveled, UUID id, boolean hasExploded, boolean isSpawned, int entityId) {
        this(speed, position, deltaMovement, range, canHitPlayers, id);
        this.distanceTraveled = distanceTraveled;
        this.hasExploded = hasExploded;
        this.isSpawned = isSpawned;
        this.entityId = entityId;
    }

    public VirtualProjectile(float speed, Vec3 position, Vec3 deltaMovement, float range, boolean canHitPlayers, UUID id) {
        this.speed = speed;
        this.position = position;
        this.deltaMovement = deltaMovement;
        this.range = range;
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
            onReachMaxDistance(level);
            hasExploded = true;
            return;
        }

        if(hasExploded) {
            return;
        }

        BlockPos projected = projectMovementForCollision(level);

        if(projected != null) {
            BlockState state = level.getBlockState(projected);

            if (!state.getCollisionShape(level, projected).isEmpty() && tickCount > getMinTicksForCollisionCheck(level)) {
                onHitBlock(level, projected);
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

    public abstract void onHitLiving(Level world, LivingEntity entity);

    public abstract void onHitBlock(Level world, BlockPos block);

    public void onReachMaxDistance(Level world) {

    }

    public abstract int getMinTicksForCollisionCheck(Level world);

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
                Codec.FLOAT.fieldOf("distancetraveled").forGetter(instance0 -> instance0.distanceTraveled),
                UUIDUtil.CODEC.fieldOf("id").forGetter(instance0 -> instance0.id),
                Codec.BOOL.fieldOf("hasexploded").forGetter(instance0 -> instance0.hasExploded),
                Codec.BOOL.fieldOf("hasspawned").forGetter(instance0 -> instance0.isSpawned),
                Codec.INT.fieldOf("entityid").forGetter(instance0 -> instance0.entityId)
        ).apply(instance, VirtualBullet::new));

        protected VirtualBullet(float speed, Vec3 position, Vec3 deltaMovement, float range, float distanceTraveled, UUID id, boolean hasExploded, boolean isSpawned, int entityId) {
            super(speed, position, deltaMovement, range, true, distanceTraveled, id, hasExploded, isSpawned, entityId);
        }

        public VirtualBullet(float speed, Vec3 position, Vec3 deltaMovement, float range) {
            super(speed, position, deltaMovement, range, true, UUID.randomUUID());
        }

        @Override
        public void onHitMissile(Level world, VirtualMissile missile) {
            missile.health = missile.health - 1.0F;
            if(missile.health <= 0) {
                world.playSound(null, blockPosition(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 2.0F, 1.0F);
            }
        }

        @Override
        public void onHitLiving(Level world, LivingEntity entity) {
            entity.hurt(entity.damageSources().source(BallistixDamageTypes.CIWS_BULLET), 10);
        }

        @Override
        public void onHitBlock(Level world, BlockPos block) {

        }

        @Override
        public int getMinTicksForCollisionCheck(Level world) {
            return 5;
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
                Codec.FLOAT.fieldOf("distancetraveled").forGetter(instance0 -> instance0.distanceTraveled),
                UUIDUtil.CODEC.fieldOf("id").forGetter(instance0 -> instance0.id),
                Codec.BOOL.fieldOf("hasexploded").forGetter(instance0 -> instance0.hasExploded),
                Codec.BOOL.fieldOf("hasspawned").forGetter(instance0 -> instance0.isSpawned),
                Codec.INT.fieldOf("entityid").forGetter(instance0 -> instance0.entityId)
        ).apply(instance, VirtualRailgunRound::new));

        protected VirtualRailgunRound(float speed, Vec3 position, Vec3 deltaMovement, float range, float distanceTraveled, UUID id, boolean hasExploded, boolean isSpawned, int entityId) {
            super(speed, position, deltaMovement, range, true, distanceTraveled, id, hasExploded, isSpawned, entityId);
        }
        public VirtualRailgunRound(float speed, Vec3 position, Vec3 deltaMovement, float range) {
            super(speed, position, deltaMovement, range, true, UUID.randomUUID());
        }

        @Override
        public void onHitMissile(Level world, VirtualMissile missile) {
            MissileManager.removeMissile(world.dimension(), missile.getId());
            world.playSound(null, blockPosition(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 2.0F, 1.0F);
        }

        @Override
        public void onHitLiving(Level world, LivingEntity entity) {
            entity.hurt(entity.damageSources().source(BallistixDamageTypes.RAILGUN_ROUND), 20);
        }

        @Override
        public void onHitBlock(Level world, BlockPos block) {
            BlockState state = world.getBlockState(block);
            if(state.getDestroySpeed(world, block) < 50.0F && !state.is(Blocks.BEDROCK)) {
                world.destroyBlock(block, false);
            }
            world.playSound(null, block, ElectrodynamicsSounds.SOUND_RODIMPACTINGGROUND.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        }

        @Override
        public int getMinTicksForCollisionCheck(Level world) {
            return 5;
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
                Codec.FLOAT.fieldOf("distancetraveled").forGetter(instance0 -> instance0.distanceTraveled),
                UUIDUtil.CODEC.fieldOf("id").forGetter(instance0 -> instance0.id),
                Codec.BOOL.fieldOf("hasexploded").forGetter(instance0 -> instance0.hasExploded),
                Codec.BOOL.fieldOf("hasspawned").forGetter(instance0 -> instance0.isSpawned),
                Codec.INT.fieldOf("entityid").forGetter(instance0 -> instance0.entityId),
                BlockPos.CODEC.fieldOf("radarpos").forGetter(instance0 -> instance0.radarPos),
                Codec.INT.fieldOf("variant").forGetter(instance0 -> instance0.variant)
        ).apply(instance, VirtualSAM::new));

        private BlockPos radarPos = BlockEntityUtils.OUT_OF_REACH;
        private final int variant;
        private TileFireControlRadar radar = null;

        protected VirtualSAM(float speed, Vec3 position, Vec3 deltaMovement, float range, float distanceTraveled, UUID id, boolean hasExploded, boolean isSpawned, int entityId, BlockPos radarPos, int variant) {
            super(speed, position, deltaMovement, range, true, distanceTraveled, id, hasExploded, isSpawned, entityId);
            this.radarPos = radarPos;
            this.variant = variant;
        }
        public VirtualSAM(float speed, Vec3 position, Vec3 deltaMovement, float range, BlockPos radarPos, int variant) {
            super(speed, position, deltaMovement, range, false, UUID.randomUUID());
            this.radarPos = radarPos;
            this.variant = variant;
        }

        @Override
        public void onHitMissile(Level world, VirtualMissile missile) {
            if(Electrodynamics.RANDOM.nextDouble() < (variant == 0 ? Constants.SAM_CHANCE_TO_DESTROY : Constants.ANTIBALLISTICMISSILE_CHANCE_TO_DESTROY)) {
                MissileManager.removeMissile(world.dimension(), missile.getId());
            }
            world.playSound(null, blockPosition(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 2.0F, 1.0F);
        }

        @Override
        public void onReachMaxDistance(Level world) {
            world.explode(null, null, null, position.x, position.y, position.z,2.0F, false, Level.ExplosionInteraction.BLOCK, ParticleTypes.EXPLOSION, ParticleTypes.EXPLOSION_EMITTER, SoundEvents.GENERIC_EXPLODE);
        }

        @Override
        public int getMinTicksForCollisionCheck(Level world) {
            return variant == 0 ? 5 : 20;
        }

        @Override
        public void onHitBlock(Level world, BlockPos block) {
            world.explode(null, null, null, position.x, position.y, position.z,2.0F, false, Level.ExplosionInteraction.BLOCK, ParticleTypes.EXPLOSION, ParticleTypes.EXPLOSION_EMITTER, SoundEvents.GENERIC_EXPLODE);
        }

        @Override
        public void onHitLiving(Level world, LivingEntity entity) {
            world.explode(null, null, null, position.x, position.y, position.z,2.0F, false, Level.ExplosionInteraction.BLOCK, ParticleTypes.EXPLOSION, ParticleTypes.EXPLOSION_EMITTER, SoundEvents.GENERIC_EXPLODE);
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
            sam.id = id;
            sam.speed = speed;
            sam.variant = variant;
            return sam;
        }

        @Override
        public void updatePosition(ServerLevel level) {

            float topSpeed = variant == 0 ? Constants.SAM_TOP_SPEED : Constants.ANTIBALLISTICMISSILE_TOP_SPEED;

            float minSpeed = variant == 0 ? topSpeed * Constants.SAM_MINTURNSPEED_PERC : topSpeed * Constants.ANTIBALLISTICMISSILE_MINTURNSPEED_PERC;

            if(radarPos == null || radarPos.equals(BlockEntityUtils.OUT_OF_REACH) || speed < minSpeed) {
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

            double timeToIntercept = TileFireControlRadar.getTimeToIntercept(tracking.position, trackingVector, trackingSpeed, topSpeed, position);

            if (timeToIntercept <= 0) {
                super.updatePosition(level);
                return;
            }

            Vec3 interceptionPos = tracking.position.add(trackingVector.scale(trackingSpeed).scale(timeToIntercept));

            double deltaX = interceptionPos.x - position.x;
            double deltaY = interceptionPos.y - position.y;
            double deltaZ = interceptionPos.z - position.z;
            
            Vec3 newDeltaMovement = new Vec3(deltaX, deltaY, deltaZ).normalize();
            
            double currAlpha = Math.atan2(deltaMovement.z, deltaMovement.x);
            double newAlpha = Math.atan2(newDeltaMovement.z, newDeltaMovement.x);
            
            double currXZMag = Math.sqrt(deltaMovement.x * deltaMovement.x + deltaMovement.z * deltaMovement.z);
            double newXZMag = Math.sqrt(newDeltaMovement.x * newDeltaMovement.x + newDeltaMovement.z * newDeltaMovement.z);
            
            double currBeta = Math.atan2(deltaMovement.y, currXZMag);
            double newBeta = Math.atan2(newDeltaMovement.y, newXZMag);

            double deltaAlpha = newAlpha - currAlpha;
            double deltaBeta = newBeta - currBeta;

            double turnRate = 0;

            if(variant == 0) {
                turnRate = Constants.SAM_ENTITY_TURNINGSPEEDRADIANS / 2.0;
            } else if (variant == 1) {
                turnRate = Constants.ANTIBALLISTICMISSILE_ENTITY_TURNINGSPEEDRADIANS / 2.0;
            }

            if(deltaAlpha > 0) {

                currAlpha += turnRate;

                if(currAlpha > newAlpha) {
                    currAlpha = newAlpha;
                }

            } else if (deltaAlpha < 0) {

                currAlpha -= turnRate;

                if(currAlpha < newAlpha) {
                    currAlpha = newAlpha;
                }

            }

            if(deltaBeta > 0) {

                currBeta += turnRate;

                if(currBeta > newBeta) {
                    currBeta = newBeta;
                }

            } else if (deltaBeta < 0) {

                currBeta -= turnRate;

                if(currBeta < newBeta) {
                    currBeta = newBeta;
                }

            }

            deltaMovement = new Vec3(Math.cos(currAlpha) * Math.cos(currBeta), Math.sin(currBeta), Math.sin(currAlpha) * Math.cos(currBeta)).normalize();
            
            super.updatePosition(level);

        }

        @Override
        public void tick(ServerLevel level) {
            super.tick(level);

            float topSpeed = variant == 0 ? Constants.SAM_TOP_SPEED : Constants.ANTIBALLISTICMISSILE_TOP_SPEED;

            if(speed < topSpeed) {
                speed += variant == 0 ? Constants.SAM_ACCELERATION : Constants.ANTIBALLISTICMISSILE_ACCELERATION;
            }

            if(speed >= topSpeed) {
                return;
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
                level.addParticle(new ParticleOptionsMissileSmoke().setParameters(1, 1, 1, 0.3f * 1, 50, true), x, y, z,
                        -motionX * (0.4 + 0.2 * Electrodynamics.RANDOM.nextDouble()),
                        -motionY * (0.4 + 0.2 * Electrodynamics.RANDOM.nextDouble()),
                        -motionZ * (0.4 + 0.2 * Electrodynamics.RANDOM.nextDouble()));

            }

        }
    }


}
