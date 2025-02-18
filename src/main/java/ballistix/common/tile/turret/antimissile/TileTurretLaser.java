package ballistix.common.tile.turret.antimissile;

import ballistix.api.missile.virtual.VirtualMissile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ballistix.api.turret.ITarget;
import ballistix.common.inventory.container.ContainerLaserTurret;
import ballistix.common.settings.Constants;
import ballistix.common.tile.radar.TileFireControlRadar;
import ballistix.common.tile.turret.antimissile.util.TileTurretAntimissile;
import ballistix.registers.BallistixBlockTypes;
import ballistix.registers.BallistixDamageTypes;
import ballistix.registers.BallistixSounds;
import electrodynamics.prefab.properties.Property;
import electrodynamics.prefab.properties.PropertyType;
import electrodynamics.prefab.sound.SoundBarrierMethods;
import electrodynamics.prefab.sound.utils.ITickableSound;
import electrodynamics.prefab.tile.components.IComponentType;
import electrodynamics.prefab.tile.components.type.ComponentContainerProvider;
import electrodynamics.prefab.tile.components.type.ComponentInventory;
import electrodynamics.prefab.tile.components.type.ComponentTickable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class TileTurretLaser extends TileTurretAntimissile implements ITickableSound {

    public final Property<Vec3> targetPos = property(new Property<>(PropertyType.Vec3, "targetposition", TileFireControlRadar.OUT_OF_REACH));
    public final Property<Boolean> targetingEntity = property(new Property<>(PropertyType.Boolean, "targetingentity", false));
    public final Property<Double> heat = property(new Property<>(PropertyType.Double, "heat", 0.0));
    public final Property<Boolean> overheated = property(new Property<>(PropertyType.Boolean, "overheated", false));
    public final Property<Boolean> firing = property(new Property<>(PropertyType.Boolean, "isfiring", false));
    public final Property<Boolean> onlyTargetPlayers = property(new Property<>(PropertyType.Boolean, "onlytargetplayers", false));

    private LivingEntity livingTarget = null;
    private boolean isPlaying = false;

    public TileTurretLaser(BlockPos worldPos, BlockState blockState) {
        super(BallistixBlockTypes.TILE_LASERTURRET.get(), worldPos, blockState, Constants.LASER_TURRET_BASE_RANGE, 0, Constants.LASER_TURRET_USAGEPERTICK, Constants.LASER_TURRET_ROTATIONSPEEDRADIANS, 0);
    }

    @Override
    public ComponentInventory getInventory() {
        return new ComponentInventory(this);
    }

    @Override
    public ComponentContainerProvider getContainer() {
        return new ComponentContainerProvider("container.laserturret", this).createMenu((id, player) -> new ContainerLaserTurret(id, player, getComponent(IComponentType.Inventory), getCoordsArray()));
    }

    @Override
    public void tickServerActive(ComponentTickable tickable) {
        if (heat.get() > 0) {
            heat.set(heat.get() - 1.0);
        }
        if (!canFire.get()) {
            firing.set(false);
        }
        if (heat.get() < Constants.LASER_TURRET_COOLTHRESHHOLD) {
            overheated.set(false);
            firing.set(false);
        }
    }

    @Override
    public void fireTickServer(long ticks) {

        if (overheated.get()) {
            firing.set(false);
            return;
        }

        firing.set(true);

        double distanceToTarget = TileFireControlRadar.getDistanceToMissile(getProjectileLaunchPosition(), target.getTargetLocation());

        double multiplier = 1.0 - (distanceToTarget / currentRange.get());

        float damage = (float) (multiplier * Constants.LASER_TURRET_BASE_DAMAGE);

        if (livingTarget == null) {

            VirtualMissile missile = (VirtualMissile) target.getTarget();

            missile.health -= damage;

        } else {

            livingTarget.hurt(livingTarget.damageSources().source(BallistixDamageTypes.LASER_TURRET), damage);
            livingTarget.setSecondsOnFire(10);

        }

        heat.set(heat.get() + 2.0);

        if (heat.get() > Constants.LASER_TURRET_MAXHEAT) {
            overheated.set(true);
        }


    }

    @Override
    public void tickClient(ComponentTickable tickable) {
        if (shouldPlaySound() && !isPlaying) {
            isPlaying = true;
            SoundBarrierMethods.playTileSound(BallistixSounds.SOUND_LASER_TURRETFIRING.get(), SoundSource.BLOCKS, this, 1.0F, 1.0F, true);
        }
        if (overheated.get() && level.random.nextDouble() < 0.5) {
            level.addParticle(ParticleTypes.LARGE_SMOKE, getBlockPos().getX() + level.random.nextDouble(), getBlockPos().getY() + level.random.nextDouble(), getBlockPos().getZ() + level.random.nextDouble(), 0, 0, 0);
        }
    }

    @Override
    public Vec3 getProjectileLaunchPosition() {
        BlockPos above = getBlockPos();
        return new Vec3(above.getX() + 0.5, above.getY() + 1.03125, above.getZ() + 0.5);
    }

    @Nullable
    @Override
    public Vec3 getTargetPosition(@NotNull ITarget target) {
        return target.getTargetLocation();
    }

    @Override
    public double getMinElevation() {
        return -0.5;
    }

    @Override
    public double getMaxElevation() {
        return 1;
    }

    @Override
    public @Nullable ITarget getTarget(long ticks) {
        targetingEntity.set(false);

        ITarget target = super.getTarget(ticks);

        if (target != null && raycastToBlockPos(level, getBlockPos(), target.getTargetBlockPos()).isEmpty()) {

            livingTarget = null;
            targetPos.set(target.getTargetLocation());
            return target;

        }

        if (livingTarget != null && (livingTarget.isRemoved() || livingTarget.isDeadOrDying())) {
            livingTarget = null;
        }

        if (ticks % 5 == 0) {

            LivingEntity selected = null;
            double lastMag = 0;

            Class<? extends LivingEntity> type = onlyTargetPlayers.get() ? Player.class : LivingEntity.class;

            for (LivingEntity entity : level.getEntitiesOfClass(type, new AABB(getBlockPos()).inflate(currentRange.get() / 4.0))) {
                if (raycastToBlockPos(level, getBlockPos(), entity.blockPosition()).isEmpty() && !(entity instanceof Player player && (player.isCreative() || whitelistedPlayers.get().contains(player.getName().getString()))) && !entity.isDeadOrDying() && !entity.isRemoved()) {
                    double deltaX = entity.getX() - getBlockPos().getX();
                    double deltaY = entity.getY() - getBlockPos().getY();
                    double deltaZ = entity.getZ() - getBlockPos().getZ();

                    double mag = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

                    if (selected == null) {
                        selected = entity;
                        lastMag = mag;
                    } else if (mag < lastMag) {
                        selected = entity;
                    }
                }
            }

            livingTarget = selected;
        }

        if (livingTarget != null) {
            target = new ITarget.TargetLivingEntity(livingTarget);
            targetingEntity.set(true);
            targetPos.set(target.getTargetLocation());
            return target;
        }

        targetPos.set(TileFireControlRadar.OUT_OF_REACH);

        return null;
    }

    @Override
    public boolean isValidPlacement() {
        return !targetingEntity.get() || super.isValidPlacement();
    }

    @Override
    public void setNotPlaying() {
        isPlaying = false;
    }

    @Override
    public boolean shouldPlaySound() {
        return firing.get();
    }
    
    @Override
    public AABB getRenderBoundingBox() {
    	return INFINITE_EXTENT_AABB;
    }
    
}
