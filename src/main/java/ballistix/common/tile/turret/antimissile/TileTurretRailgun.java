package ballistix.common.tile.turret.antimissile;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import com.mojang.datafixers.util.Pair;

import ballistix.api.missile.MissileManager;
import ballistix.api.missile.virtual.VirtualProjectile;
import ballistix.api.turret.ITarget;
import ballistix.common.inventory.container.ContainerRailgunTurret;
import ballistix.common.settings.Constants;
import ballistix.common.tile.turret.antimissile.util.TileTurretAntimissileProjectile;
import ballistix.registers.BallistixTiles;
import electrodynamics.common.item.ItemUpgrade;
import electrodynamics.common.tags.ElectrodynamicsTags;
import electrodynamics.prefab.properties.Property;
import electrodynamics.prefab.properties.PropertyTypes;
import electrodynamics.prefab.tile.components.IComponentType;
import electrodynamics.prefab.tile.components.type.ComponentContainerProvider;
import electrodynamics.prefab.tile.components.type.ComponentInventory;
import electrodynamics.prefab.tile.components.type.ComponentTickable;
import electrodynamics.prefab.utilities.BlockEntityUtils;
import electrodynamics.registers.ElectrodynamicsSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class TileTurretRailgun extends TileTurretAntimissileProjectile {

    public final Property<Integer> cooldown = property(new Property<>(PropertyTypes.INTEGER, "cooldown", 0));
    public final Property<Boolean> outOfAmmo = property(new Property<>(PropertyTypes.BOOLEAN, "noammo", false));
    public final Property<Boolean> targetingEntity = property(new Property<>(PropertyTypes.BOOLEAN, "targetingentity", false));
    public final Property<Boolean> onlyTargetPlayers = property(new Property<>(PropertyTypes.BOOLEAN, "onlytargetplayers", false));

    private LivingEntity livingTarget = null;

    public TileTurretRailgun(BlockPos worldPos, BlockState blockState) {
        super(BallistixTiles.TILE_RAILGUNTURRET.get(), worldPos, blockState, Constants.RAILGUN_TURRET_BASE_RANGE, 0, Constants.RAILGUN_TURRET_USAGEPERTICK, Constants.RAILGUN_TURRET_ROTATIONSPEEDRADIANS, Constants.RAILGUN_INNACCURACY);
    }

    @Override
    public ComponentInventory getInventory() {
        return new ComponentInventory(this, ComponentInventory.InventoryBuilder.newInv().inputs(1).upgrades(3)).setDirectionsBySlot(0, BlockEntityUtils.MachineDirection.values()).valid((index, stack, inv) -> {

            if (index == 0) {
                return stack.is(ElectrodynamicsTags.Items.ROD_STEEL);
            } else if (index >= inv.getUpgradeSlotStartIndex()) {
                return stack.getItem() instanceof ItemUpgrade upgrade && inv.isUpgradeValid(upgrade.subtype);
            } else {
                return false;
            }

        });
    }

    @Override
    public ComponentContainerProvider getContainer() {
        return new ComponentContainerProvider("container.railgunturret", this).createMenu((id, player) -> new ContainerRailgunTurret(id, player, getComponent(IComponentType.Inventory), getCoordsArray()));
    }

    @Override
    public void tickServerActive(ComponentTickable tickable) {
        if (cooldown.get() > 0) {
            cooldown.set(cooldown.get() - 1);
        }
    }

    @Override
    public void fireTickServer(long ticks) {

        if (cooldown.get() > 0) {
            return;
        }

        ComponentInventory inv = getComponent(IComponentType.Inventory);

        ItemStack missile = inv.getItem(0);

        if (missile.isEmpty()) {
            outOfAmmo.set(true);
            return;
        }

        outOfAmmo.set(false);

        Pair<Vec3, Vec3> projectileVals = getProjectileTrajectoryFromInaccuracy(inaccuracy, baseRange, inaccuracyMultiplier.get(), getProjectileLaunchPosition(), getTargetPosition(getTarget(ticks)));

        Vec3 rotvec = projectileVals.getSecond();

        VirtualProjectile.VirtualRailgunRound railgunround = new VirtualProjectile.VirtualRailgunRound(getProjectileSpeed(), getProjectileLaunchPosition(), projectileVals.getFirst(), currentRange.get().floatValue(), new Vector3f((float) rotvec.x, (float) rotvec.y, (float) rotvec.z));

        MissileManager.addRailgunRound(level.dimension(), railgunround);

        inv.removeItem(0, 1);

        level.playSound(null, getBlockPos(), ElectrodynamicsSounds.SOUND_RAILGUNKINETIC.get(), SoundSource.BLOCKS, 2.0F, 1.0F);

        cooldown.set(Constants.RAILGUN_TURRET_COOLDOWN);

    }

    @Override
    public Vec3 getProjectileLaunchPosition() {
        BlockPos above = getBlockPos();
        return new Vec3(above.getX() + 0.5, above.getY() + 0.875, above.getZ() + 0.5);
    }

    @Override
    public double getMinElevation() {
        return -0.5;
    }

    @Override
    public double getMaxElevation() {
        return 0.5;
    }

    @Override
    public float getProjectileSpeed() {
        return 5;
    }

    @Nullable
    @Override
    public ITarget getTarget(long ticks) {

        targetingEntity.set(false);

        ITarget target = super.getTarget(ticks);

        if(target != null) {
            livingTarget = null;
            return target;
        }

        if(livingTarget != null && (livingTarget.isRemoved() || livingTarget.isDeadOrDying())) {
            livingTarget = null;
        }

        if(ticks % 5 == 0) {

            LivingEntity selected = null;
            double lastMag = 0;

            Class<? extends LivingEntity> type = onlyTargetPlayers.get() ? Player.class : LivingEntity.class;

            for(LivingEntity entity : level.getEntitiesOfClass(type, new AABB(getBlockPos()).inflate(currentRange.get() / 4.0))) {
                if(raycastToBlockPos(level, getBlockPos(), entity.blockPosition().above()).isEmpty() && !(entity instanceof Player player && (player.isCreative() || whitelistedPlayers.get().contains(player.getName().getString()))) && !entity.isDeadOrDying() && !entity.isRemoved()) {
                    double deltaX = entity.getX() - getBlockPos().getX();
                    double deltaY = entity.getY() - getBlockPos().getY();
                    double deltaZ = entity.getZ() - getBlockPos().getZ();

                    double mag = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

                    if(selected == null) {
                        selected = entity;
                        lastMag = mag;
                    } else if(mag < lastMag){
                        selected = entity;
                    }
                }
            }

            livingTarget = selected;
        }

        if(livingTarget != null) {
            targetingEntity.set(true);
            return new ITarget.TargetLivingEntity(livingTarget);
        }

        return null;
    }

    @Override
    public boolean isValidPlacement() {
        return !targetingEntity.get() || super.isValidPlacement();
    }

}
