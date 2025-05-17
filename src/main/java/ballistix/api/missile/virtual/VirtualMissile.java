package ballistix.api.missile.virtual;

import java.util.UUID;

import javax.annotation.Nullable;

import ballistix.api.silo.ILauncherPlatform;
import ballistix.api.silo.ILauncherSupportFrame;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import ballistix.api.blast.IHasCustomRender;
import ballistix.common.blast.Blast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.entity.EntityBlast;
import ballistix.common.entity.EntityMissile;
import ballistix.common.settings.BallistixConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import voltaic.api.multiblock.subnodebased.TileMultiSubnode;
import voltaic.prefab.utilities.BlockEntityUtils;

public class VirtualMissile {

    public static final int MAX_CRUISING_ALTITUDE = 500;
    public static final int WORLD_BUILD_HEIGHT = 320;
    public static final int ARC_TURN_HEIGHT_MIN = 400;

    public static final Codec<VirtualMissile> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            //
            Vec3.CODEC.fieldOf("position").forGetter(instance0 -> instance0.position),
            //
            Vec3.CODEC.fieldOf("movement").forGetter(instance0 -> instance0.deltaMovement),
            //
            Codec.FLOAT.fieldOf("speed").forGetter(instance0 -> instance0.speed),
            //
            Codec.FLOAT.fieldOf("health").forGetter(instance0 -> instance0.health),
            //
            Codec.BOOL.fieldOf("hasexploded").forGetter(instance0 -> instance0.hasExploded),
            //
            UUIDUtil.CODEC.fieldOf("id").forGetter(instance0 -> instance0.id),
            //
            Codec.INT.fieldOf("tickcount").forGetter(instance0 -> instance0.tickCount),
            //
            MissileTargetData.CODEC.fieldOf("targetdata").forGetter(instance0 -> instance0.targetData),
            //
            MissileEntityData.CODEC.fieldOf("entitydata").forGetter(instance0 -> instance0.entityData),
            //
            MissilePayloadData.CODEC.fieldOf("payloaddata").forGetter(instance0 -> instance0.payloadData)
            //
    ).apply(instance, VirtualMissile::new));

    public Vec3 position = Vec3.ZERO;
    public Vec3 deltaMovement = Vec3.ZERO;
    public float speed = 0.0F;
    public float health = BallistixConstants.MISSILE_HEALTH;
    private final UUID id;
    private boolean hasExploded = false;
    public final MissileEntityData entityData;
    public final MissileTargetData targetData;
    public final MissilePayloadData payloadData;

    private int tickCount = 0;

    @Nullable
    public EntityBlast blastEntity;

    private VirtualMissile(Vec3 pos, Vec3 deltaMovement, float speed, float health, boolean hasExploded, UUID id, int tickCount, MissileTargetData targetData, MissileEntityData entityData, MissilePayloadData payloadData) {

        this.position = pos;
        this.deltaMovement = deltaMovement;
        this.speed = speed;
        this.hasExploded = hasExploded;
        this.health = health;
        this.id = id;
        this.tickCount = tickCount;

        this.targetData = targetData;
        this.entityData = entityData;
        this.payloadData = payloadData;
    }

    public VirtualMissile(Vec3 startPos, Vec3 initialMovement, float initialSpeed, boolean isItem, float startX, float startZ, BlockPos target, int missileType, int blastOrdinal, int frequency, boolean usingAirburst) {

        this.position = startPos;
        this.deltaMovement = initialMovement;
        this.speed = initialSpeed;
        this.id = UUID.randomUUID();

        this.targetData = new MissileTargetData(startX, startZ, target, false, usingAirburst);
        this.entityData = new MissileEntityData(false, -1);
        this.payloadData = new MissilePayloadData(missileType, blastOrdinal, frequency, isItem);

    }

    //ticks on server only
    public void tick(ServerLevel level) {

        tickCount++;

        if (health <= 0) {
            level.playSound(null, blockPosition(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 2.0F, 1.0F);
            hasExploded = true;
            return;
        }

        if ((!payloadData.isItem && targetData.target.equals(BlockEntityUtils.OUT_OF_REACH)) || payloadData.blastOrdinal == -1) {
            hasExploded = true;
            return;
        }

        if (hasExploded) {
            return;
        }

        if (blastEntity != null) {
            if (blastEntity.isRemoved() || blastEntity.getBlast().hasStarted) {
                hasExploded = true;
            }
            return;
        }

        BlockPos collisionPos = projectMovementForCollision(level);

        if ((collisionPos != null || (targetData.usingAirburst && targetData.pastHalfwayPoint && position.y <= targetData.target.getY())) && (payloadData.isItem || !isInValidBlockstate(collisionPos, level)) || position.y <= level.getMinBuildHeight()) {

            SubtypeBlast explosive = SubtypeBlast.values()[payloadData.blastOrdinal];

            if(collisionPos == null) {
                collisionPos = targetData.target;
            }

            Blast b = explosive.createBlast(level, collisionPos);

            if (b != null) {

                if (b.isInstantaneous() && !(b instanceof IHasCustomRender)) {
                    b.performExplosion();

                    hasExploded = true;

                } else {
                    blastEntity = b.performExplosion();
                    if(!targetData.usingAirburst || (targetData.usingAirburst && position.y > targetData.target.getY())) {
                        position = new Vec3(position.x - speed * deltaMovement.x, position.y - speed * deltaMovement.y, position.z - speed * deltaMovement.z);
                    }
                }
                return;

            }

        }

        if (!payloadData.isItem) {

            float iDeltaX = targetData.target.getX() - targetData.startX;
            float iDeltaZ = targetData.target.getZ() - targetData.startZ;

            float initialDistance = (float) Math.sqrt(iDeltaX * iDeltaX + iDeltaZ * iDeltaZ);
            float halfwayDistance = initialDistance / 2.0F;

            float deltaX = (float) (position.x - targetData.startX);
            float deltaZ = (float) (position.z - targetData.startZ);

            float distanceTraveled = (float) Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

            if (targetData.usingAirburst && distanceTraveled >= halfwayDistance) {
                targetData.pastHalfwayPoint = true;
            }

            double maxRadii = MAX_CRUISING_ALTITUDE - ARC_TURN_HEIGHT_MIN;

            float turnRadius = (float) Mth.clamp(halfwayDistance, 0.001F, maxRadii);

            float deltaY = (float) (position.y - ARC_TURN_HEIGHT_MIN);

            float phi = 0;
            float signY = 1;

            if (halfwayDistance <= maxRadii) {

                if (position.y >= ARC_TURN_HEIGHT_MIN && distanceTraveled < halfwayDistance) {

                    phi = (float) Math.asin(Mth.clamp(deltaY / turnRadius, 0, 1));

                } else if (distanceTraveled >= halfwayDistance) {

                    phi = (float) Math.asin(Mth.clamp((initialDistance - distanceTraveled) / turnRadius, 0, 1));
                    signY = -1;

                } else if (distanceTraveled >= initialDistance) {

                    signY = -1;

                }

                float x = (float) ((iDeltaX / initialDistance) * Math.sin(phi));
                float z = (float) ((iDeltaZ / initialDistance) * Math.sin(phi));

                deltaMovement = new Vec3(x, Math.cos(phi) * signY, z);

            } else {


                if (position.y >= ARC_TURN_HEIGHT_MIN && distanceTraveled < halfwayDistance) {

                    if (distanceTraveled <= turnRadius) {

                        phi = (float) Math.asin(Mth.clamp(deltaY / turnRadius, 0, 1));

                    } else {

                        phi = (float) (Math.PI / 2.0);

                    }

                } else if (distanceTraveled >= halfwayDistance) {

                    if (distanceTraveled >= initialDistance - turnRadius) {

                        phi = (float) Math.asin(Mth.clamp((initialDistance - distanceTraveled) / turnRadius, 0, 1));
                        signY = -1;

                    } else {

                        phi = (float) (Math.PI / 2.0);

                    }

                } else if (distanceTraveled >= initialDistance) {

                    signY = -1;

                }

                float x = (float) (iDeltaX / initialDistance * Math.sin(phi));
                float z = (float) (iDeltaZ / initialDistance * Math.sin(phi));

                deltaMovement = new Vec3(x, Math.cos(phi) * signY, z);

            }

        }

        if (blastEntity == null) {
            position = new Vec3(position.x + speed * deltaMovement.x, position.y + speed * deltaMovement.y, position.z + speed * deltaMovement.z);
        }

        if (!payloadData.isItem && !targetData.target.equals(BlockEntityUtils.OUT_OF_REACH) && speed < 3.0F) {
            speed += 0.02F;
        }

        if (!entityData.isSpawned && level.hasChunkAt(blockPosition()) && level.isPositionEntityTicking(blockPosition())) {

            EntityMissile missile = new EntityMissile(level);
            missile.setPos(position);
            missile.setDeltaMovement(deltaMovement);
            missile.missileType = payloadData.missileType;
            missile.speed = speed;
            missile.id = id;
            missile.isItem = payloadData.isItem;
            missile.target = targetData.target;
            missile.startX = targetData.startX;
            missile.startZ = targetData.startZ;

            if (level.addFreshEntity(missile)) {
                setSpawned(true, missile.getId());
            }

        }

        if (entityData.isSpawned && (!level.hasChunkAt(blockPosition()) || level.getEntity(entityData.entityId) == null)) {
            setSpawned(false, -1);
        }

    }

    private boolean isInValidBlockstate(BlockPos pos, ServerLevel world) {

        if(pos == null) {
            return true;
        }

        BlockEntity blockentity = world.getBlockEntity(pos);

        if(blockentity instanceof ILauncherPlatform || blockentity instanceof ILauncherSupportFrame) {
            return true;
        }

        if(blockentity instanceof TileMultiSubnode) {
            TileMultiSubnode subnode = (TileMultiSubnode) blockentity;
            BlockEntity owner = world.getBlockEntity(subnode.parentPos.getValue());

            return owner instanceof ILauncherPlatform || owner instanceof ILauncherSupportFrame;
        }

        return false;

    }

    public BlockPos blockPosition() {
        return new BlockPos((int) Math.floor(position.x), (int) Math.floor(position.y), (int) Math.floor(position.z));
    }

    public UUID getId() {
        return id;
    }

    public boolean hasExploded() {
        return hasExploded;
    }

    public void setSpawned(boolean spawned, int id) {
        entityData.isSpawned = spawned;
        entityData.entityId = id;
    }

    public AABB getBoundingBox() {
        return new AABB(position.x - 0.5F, position.y, position.z - 0.5F, position.x + 0.5F, position.y + 1.0F, position.z + 0.5F);
    }

    @Nullable
    public BlockPos projectMovementForCollision(ServerLevel world) {

        Vec3 currPos = position.scale(1.0);

        int iterations = Math.abs((int) Math.ceil(speed));

        BlockPos pos;
        BlockState state;

        for (int i = 0; i < iterations; i++) {

            pos = new BlockPos((int) Math.floor(currPos.x), (int) Math.floor(currPos.y), (int) Math.floor(currPos.z));
            state = world.getBlockState(pos);

            if (state.getCollisionShape(world, blockPosition()).isEmpty() || isInValidBlockstate(pos, world)) {
                currPos.add(deltaMovement);
                continue;
            }

            return pos;

        }

        return null;

    }

    public static class MissileEntityData {

        public static final Codec<MissileEntityData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                //
                Codec.BOOL.fieldOf("isspawned").forGetter(instance0 -> instance0.isSpawned),
                //
                Codec.INT.fieldOf("entityid").forGetter(instance0 -> instance0.entityId)
                //
        ).apply(instance, MissileEntityData::new));

        public boolean isSpawned = false;
        public int entityId = -1;

        public MissileEntityData(boolean isSpawned, int entityId) {
            this.entityId = entityId;
            this.isSpawned = isSpawned;
        }


    }

    public static class MissileTargetData {

        public static final Codec<MissileTargetData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                //
                Codec.FLOAT.fieldOf("startx").forGetter(instance0 -> instance0.startX),
                //
                Codec.FLOAT.fieldOf("startz").forGetter(instance0 -> instance0.startZ),
                //
                BlockPos.CODEC.fieldOf("target").forGetter(instance0 -> instance0.target),
                //
                Codec.BOOL.fieldOf("pasthalfwaypoint").forGetter(instance0 -> instance0.pastHalfwayPoint),
                //
                Codec.BOOL.fieldOf("usingairburst").forGetter(instance0 -> instance0.usingAirburst)
                //
        ).apply(instance, MissileTargetData::new));

        public final float startX;
        public final float startZ;
        public final BlockPos target;
        public boolean pastHalfwayPoint = false;
        public final boolean usingAirburst;

        public MissileTargetData(float startX, float startZ, BlockPos target, boolean pastHalfway, boolean usingAirburst) {
            this.startX = startX;
            this.startZ = startZ;
            this.target = target;
            this.pastHalfwayPoint = pastHalfway;
            this.usingAirburst = usingAirburst;
        }

    }

    public static class MissilePayloadData {

        public static final Codec<MissilePayloadData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                //
                Codec.INT.fieldOf("missiletype").forGetter(instance0 -> instance0.missileType),
                //
                Codec.INT.fieldOf("blastordinal").forGetter(instance0 -> instance0.blastOrdinal),
                //
                Codec.INT.fieldOf("frequency").forGetter(instance0 -> instance0.frequency),
                //
                Codec.BOOL.fieldOf("isitem").forGetter(instance0 -> instance0.isItem)
                //
        ).apply(instance, MissilePayloadData::new));

        public final int missileType;
        public final int blastOrdinal;
        public final int frequency;
        private final boolean isItem;

        public MissilePayloadData(int missileType, int blastOrdinal, int frequency, boolean isItem) {
            this.missileType = missileType;
            this.blastOrdinal = blastOrdinal;
            this.frequency = frequency;
            this.isItem = isItem;
        }

    }

}
