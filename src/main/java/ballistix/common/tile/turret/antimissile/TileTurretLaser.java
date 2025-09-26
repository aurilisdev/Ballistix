package ballistix.common.tile.turret.antimissile;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ballistix.api.missile.virtual.VirtualMissile;
import ballistix.api.turret.ITarget;
import ballistix.common.inventory.container.ContainerLaserTurret;
import ballistix.common.settings.BallistixConstants;
import ballistix.common.tile.radar.TileFireControlRadar;
import ballistix.common.tile.turret.antimissile.util.TileTurretAntimissile;
import ballistix.registers.BallistixDamageTypes;
import ballistix.registers.BallistixSounds;
import ballistix.registers.BallistixTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import voltaic.prefab.properties.types.PropertyTypes;
import voltaic.prefab.properties.variant.SingleProperty;
import voltaic.prefab.sound.ITickableSound;
import voltaic.prefab.sound.SoundBarrierMethods;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.tile.components.type.ComponentContainerProvider;
import voltaic.prefab.tile.components.type.ComponentInventory;
import voltaic.prefab.tile.components.type.ComponentTickable;

public class TileTurretLaser extends TileTurretAntimissile implements ITickableSound {

    public final SingleProperty<Vec3> targetPos = property(new SingleProperty<>(PropertyTypes.VEC3, "targetposition", TileFireControlRadar.OUT_OF_REACH));
    public final SingleProperty<Boolean> targetingEntity = property(new SingleProperty<>(PropertyTypes.BOOLEAN, "targetingentity", false));
    public final SingleProperty<Double> heat = property(new SingleProperty<>(PropertyTypes.DOUBLE, "heat", 0.0));
    public final SingleProperty<Boolean> overheated = property(new SingleProperty<>(PropertyTypes.BOOLEAN, "overheated", false));
    public final SingleProperty<Boolean> firing = property(new SingleProperty<>(PropertyTypes.BOOLEAN, "isfiring", false));

    private LivingEntity livingTarget = null;
    private boolean isPlaying = false;

    public TileTurretLaser(BlockPos worldPos, BlockState blockState) {
        super(BallistixTiles.TILE_LASERTURRET.get(), worldPos, blockState, BallistixConstants.LASER_TURRET_BASE_RANGE, 0, BallistixConstants.LASER_TURRET_USAGEPERTICK, BallistixConstants.LASER_TURRET_ROTATIONSPEEDRADIANS, 0);
    }

    @Override
    public ComponentInventory getInventory() {
        return new ComponentInventory(this);
    }

    @Override
    public ComponentContainerProvider getContainer() {
        return new ComponentContainerProvider("laserturret", this).createMenu((id, player) -> new ContainerLaserTurret(id, player, getComponent(IComponentType.Inventory), getCoordsArray()));
    }

    @Override
    public void tickServer(ComponentTickable tickable) {
        if (heat.getValue() > 0) {
            heat.setValue(heat.getValue() - 1.0);
        }
        if (!canFire.getValue()) {
            firing.setValue(false);
        }
        if (heat.getValue() < BallistixConstants.LASER_TURRET_COOLTHRESHHOLD) {
            overheated.setValue(false);
            firing.setValue(false);
        }
        super.tickServer(tickable);
    }

    @Override
    public void tickServerActive(ComponentTickable tickable) {

    }

    @Override
    public void fireTickServer(long ticks) {

        if (overheated.getValue()) {
            firing.setValue(false);
            return;
        }

        firing.setValue(true);

        double distanceToTarget = TileFireControlRadar.getDistanceToMissile(getProjectileLaunchPosition(), target.getTargetLocation());

        double multiplier = 1.0 - (distanceToTarget / currentRange.getValue());

        float damage = (float) (multiplier * BallistixConstants.LASER_TURRET_BASE_DAMAGE);

        if (livingTarget == null) {

            VirtualMissile missile = (VirtualMissile) target.getTarget();

            missile.health -= damage;

        } else {

            livingTarget.hurt(BallistixDamageTypes.LASER_TURRET, damage);
            livingTarget.setSecondsOnFire(10);

        }

        heat.setValue(heat.getValue() + 2.0);

        if (heat.getValue() > BallistixConstants.LASER_TURRET_MAXHEAT) {
            overheated.setValue(true);
        }


    }

    @Override
    public void tickClient(ComponentTickable tickable) {
        if (shouldPlaySound() && !isPlaying) {
            isPlaying = true;
            SoundBarrierMethods.playTileSound(BallistixSounds.SOUND_LASER_TURRETFIRING.get(), SoundSource.BLOCKS, this, 1.0F, 1.0F, true);
        }
        if (overheated.getValue() && level.random.nextDouble() < 0.5) {
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
        targetingEntity.setValue(false);

        ITarget target = super.getTarget(ticks);
        
        TargetingMode mode = TargetingMode.values()[entityTargetingMode.getValue()];

        if (target != null && raycastToBlockPos(level, getProjectileLaunchPosition(), target.getTargetLocation()).isEmpty()) {

            livingTarget = null;
            targetPos.setValue(target.getTargetLocation());
            return target;

        }
        
        if(mode == TargetingMode.NONE) {
            livingTarget = null;
            targetPos.setValue(TileFireControlRadar.OUT_OF_REACH);
            return null;
        }

        if (livingTarget != null && (livingTarget.isRemoved() || livingTarget.isDeadOrDying())) {
            livingTarget = null;
        }

        if (ticks % 5 == 0) {

            LivingEntity selected = null;
            double lastMag = 0;

            Class<? extends LivingEntity> type = mode == TargetingMode.ONLY_PLAYERS ? Player.class : LivingEntity.class;

            for (LivingEntity entity : level.getEntitiesOfClass(type, new AABB(getBlockPos()).inflate(currentRange.getValue() / 4.0))) {
                if (raycastToBlockPos(level, getProjectileLaunchPosition(), entity.position().add(0, entity.getEyeHeight(), 0)).isEmpty() && !(entity instanceof Player player && (player.isCreative() || whitelistedPlayers.getValue().contains(player.getName().getString()))) && !entity.isDeadOrDying() && !entity.isRemoved()) {
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
            targetingEntity.setValue(true);
            targetPos.setValue(target.getTargetLocation());
            return target;
        }

        targetPos.setValue(TileFireControlRadar.OUT_OF_REACH);

        return null;
    }

    @Override
    public boolean isValidPlacement() {
        return targetingEntity.getValue() || super.isValidPlacement();
    }

    @Override
    public void setNotPlaying() {
        isPlaying = false;
    }

    @Override
    public boolean shouldPlaySound() {
        return firing.getValue() && !hasNoPower.getValue();
    }
    
    @Override
    public AABB getRenderBoundingBox() {
    	return INFINITE_EXTENT_AABB;
    }
}
