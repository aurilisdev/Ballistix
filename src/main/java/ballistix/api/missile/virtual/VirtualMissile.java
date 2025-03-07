package ballistix.api.missile.virtual;

import java.util.UUID;

import javax.annotation.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import ballistix.api.blast.IHasCustomRender;
import ballistix.common.blast.Blast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.entity.EntityBlast;
import ballistix.common.entity.EntityMissile;
import ballistix.common.settings.Constants;
import electrodynamics.prefab.utilities.BlockEntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class VirtualMissile {

    public static final int MAX_CRUISING_ALTITUDE = 500;
    public static final int WORLD_BUILD_HEIGHT = 320;
    public static final int ARC_TURN_HEIGHT_MIN = 400;

    public static final Codec<VirtualMissile> CODEC = RecordCodecBuilder.create(instance -> instance.group(Vec3.CODEC.fieldOf("position").forGetter(instance0 -> instance0.position), Vec3.CODEC.fieldOf("movement").forGetter(instance0 -> instance0.deltaMovement), Codec.FLOAT.fieldOf("speed").forGetter(instance0 -> instance0.speed), Codec.BOOL.fieldOf("isitem").forGetter(instance0 -> instance0.isItem), Codec.FLOAT.fieldOf("startx").forGetter(instance0 -> instance0.startX), Codec.FLOAT.fieldOf("startz").forGetter(instance0 -> instance0.startZ), BlockPos.CODEC.fieldOf("target").forGetter(instance0 -> instance0.target), Codec.FLOAT.fieldOf("health").forGetter(instance0 -> instance0.health), Codec.INT.fieldOf("missiletype").forGetter(instance0 -> instance0.missileType), Codec.INT.fieldOf("blasttype").forGetter(instance0 -> instance0.blastOrdinal), Codec.BOOL.fieldOf("hasexploded").forGetter(instance0 -> instance0.hasExploded), UUIDUtil.CODEC.fieldOf("id").forGetter(instance0 -> instance0.id), Codec.BOOL.fieldOf("isspawned").forGetter(instance0 -> instance0.isSpawned), Codec.INT.fieldOf("frequency").forGetter(instance0 -> instance0.frequency), Codec.INT.fieldOf("entityid").forGetter(instance0 -> instance0.entityId)).apply(instance, VirtualMissile::new));

    public Vec3 position = Vec3.ZERO;
    public Vec3 deltaMovement = Vec3.ZERO;
    public float speed = 0.0F;

    private final boolean isItem;
    private boolean isSpawned = false;
    private final float startX;
    private final float startZ;
    private final BlockPos target;
    public float health = Constants.MISSILE_HEALTH;
    public final int missileType;
    public final int blastOrdinal;
    private boolean hasExploded = false;
    private final UUID id;
    private int tickCount = 0;
    public final int frequency;
    private int entityId = -1;

    @Nullable
    public EntityBlast blastEntity;

    private VirtualMissile(Vec3 startPos, Vec3 initialMovement, float initialSpeed, boolean isItem, float startX, float startZ, BlockPos target, float initialHealth, int missileType, int blastOrdinal, boolean hasExploded, UUID id, boolean isSpawned, int frequency, int entityId) {

        position = startPos;
        deltaMovement = initialMovement;
        speed = initialSpeed;
        this.isItem = isItem;

        this.startX = startX;
        this.startZ = startZ;
        this.target = target;
        health = initialHealth;
        this.missileType = missileType;
        this.blastOrdinal = blastOrdinal;
        this.hasExploded = hasExploded;
        this.id = id;
        this.isSpawned = isSpawned;
        this.frequency = frequency;
        this.entityId = entityId;

    }

    public VirtualMissile(Vec3 startPos, Vec3 initialMovement, float initialSpeed, boolean isItem, float startX, float startZ, BlockPos target, int missileType, int blastOrdinal, boolean isSpawned, int frequency) {

        position = startPos;
        deltaMovement = initialMovement;
        speed = initialSpeed;
        this.isItem = isItem;

        this.startX = startX;
        this.startZ = startZ;
        this.target = target;
        this.missileType = missileType;
        this.blastOrdinal = blastOrdinal;
        id = UUID.randomUUID();
        this.isSpawned = isSpawned;
        this.frequency = frequency;

    }

    //ticks on server only
    public void tick(ServerLevel level) {

        tickCount++;

        if (health <= 0) {
            level.playSound(null, blockPosition(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 2.0F, 1.0F);
            hasExploded = true;
            return;
        }

        if ((!isItem && target.equals(BlockEntityUtils.OUT_OF_REACH)) || blastOrdinal == -1) {
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

        if (collisionPos != null && (isItem || tickCount > 20) || position.y <= level.getMinBuildHeight()) {

            SubtypeBlast explosive = SubtypeBlast.values()[blastOrdinal];


            Blast b = explosive.createBlast(level, collisionPos);

            if (b != null) {

                if (b.isInstantaneous() && !(b instanceof IHasCustomRender)) {
                    b.performExplosion();

                    hasExploded = true;

                } else {
                    blastEntity = b.performExplosion();
                    position = new Vec3(position.x - speed * deltaMovement.x, position.y - speed * deltaMovement.y, position.z - speed * deltaMovement.z);
                }
                return;

            }

        }

        if (!isItem) {

            float iDeltaX = target.getX() - startX;
            float iDeltaZ = target.getZ() - startZ;

            float initialDistance = (float) Math.sqrt(iDeltaX * iDeltaX + iDeltaZ * iDeltaZ);
            float halfwayDistance = initialDistance / 2.0F;

            float deltaX = (float) (position.x - startX);
            float deltaZ = (float) (position.z - startZ);

            float distanceTraveled = (float) Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

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

        if (!isItem && !target.equals(BlockEntityUtils.OUT_OF_REACH) && speed < 3.0F) {
            speed += 0.02F;
        }

        if (!isSpawned && level.hasChunkAt(blockPosition()) && level.isPositionEntityTicking(blockPosition())) {

            EntityMissile missile = new EntityMissile(level);
            missile.setPos(position);
            missile.setDeltaMovement(deltaMovement);
            missile.missileType = missileType;
            missile.speed = speed;
            missile.id = id;
            missile.isItem = isItem;
            missile.target = target;
            missile.startX = startX;
            missile.startZ = startZ;

            if (level.addFreshEntity(missile)) {
                setSpawned(true, missile.getId());
            }

        }

        if (isSpawned && (!level.hasChunkAt(blockPosition()) || level.getEntity(entityId) == null)) {
            setSpawned(false, -1);
        }

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
        isSpawned = spawned;
        entityId = id;
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

            if (state.getCollisionShape(world, blockPosition()).isEmpty()) {
                currPos.add(deltaMovement);
                continue;
            }

            return pos;

        }

        return null;

    }

}
