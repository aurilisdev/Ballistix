package ballistix.common.tile.turret.antimissile;

import ballistix.api.missile.MissileManager;
import ballistix.api.missile.virtual.VirtualProjectile;

import javax.annotation.Nullable;

import com.mojang.datafixers.util.Pair;

import ballistix.api.turret.ITarget;
import ballistix.common.inventory.container.ContainerRailgunTurret;
import ballistix.common.settings.Constants;
import ballistix.common.tile.turret.antimissile.util.TileTurretAntimissileProjectile;
import ballistix.registers.BallistixBlockTypes;
import electrodynamics.common.item.ItemUpgrade;
import electrodynamics.common.tags.ElectrodynamicsTags;
import electrodynamics.prefab.properties.Property;
import electrodynamics.prefab.properties.PropertyType;
import electrodynamics.prefab.tile.components.IComponentType;
import electrodynamics.prefab.tile.components.type.ComponentContainerProvider;
import electrodynamics.prefab.tile.components.type.ComponentInventory;
import electrodynamics.prefab.tile.components.type.ComponentTickable;
import electrodynamics.registers.ElectrodynamicsSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

public class TileTurretRailgun extends TileTurretAntimissileProjectile {

    public final Property<Integer> cooldown = property(new Property<>(PropertyType.Integer, "cooldown", 0));
    public final Property<Boolean> outOfAmmo = property(new Property<>(PropertyType.Boolean, "noammo", false));
    public final Property<Boolean> targetingEntity = property(new Property<>(PropertyType.Boolean, "targetingentity", false));
    public final Property<Boolean> onlyTargetPlayers = property(new Property<>(PropertyType.Boolean, "onlytargetplayers", false));

    private LivingEntity livingTarget = null;

    public TileTurretRailgun() {
        super(BallistixBlockTypes.TILE_RAILGUNTURRET.get(), Constants.RAILGUN_TURRET_BASE_RANGE, 0, Constants.RAILGUN_TURRET_USAGEPERTICK, Constants.RAILGUN_TURRET_ROTATIONSPEEDRADIANS, Constants.RAILGUN_INNACCURACY);
    }

    @Override
    public ComponentInventory getInventory() {
        return new ComponentInventory(this, ComponentInventory.InventoryBuilder.newInv().inputs(1).upgrades(3)).setDirectionsBySlot(0, Direction.values()).valid((index, stack, inv) -> {

            if (index == 0) {
                return Ingredient.of(ElectrodynamicsTags.Items.ROD_STEEL).test(stack);
            } else if (index >= inv.getUpgradeSlotStartIndex()) {
                return stack.getItem() instanceof ItemUpgrade && inv.isUpgradeValid(((ItemUpgrade) stack.getItem()).subtype);
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

        Pair<Vector3d, Vector3d> projectileVals = getProjectileTrajectoryFromInaccuracy(inaccuracy, baseRange, inaccuracyMultiplier.get(), getProjectileLaunchPosition(), getTargetPosition(getTarget(ticks)));

        Vector3d rotvec = projectileVals.getSecond();

        VirtualProjectile.VirtualRailgunRound railgunround = new VirtualProjectile.VirtualRailgunRound(getProjectileSpeed(), getProjectileLaunchPosition(), projectileVals.getFirst(), currentRange.get().floatValue(), new Vector3f((float) rotvec.x, (float) rotvec.y, (float) rotvec.z));

        MissileManager.addRailgunRound(level.dimension(), railgunround);

        inv.removeItem(0, 1);

        level.playSound(null, getBlockPos(), ElectrodynamicsSounds.SOUND_RAILGUNKINETIC.get(), SoundCategory.BLOCKS, 2.0F, 1.0F);

        cooldown.set(Constants.RAILGUN_TURRET_COOLDOWN);

    }

    @Override
    public Vector3d getProjectileLaunchPosition() {
        BlockPos above = getBlockPos();
        return new Vector3d(above.getX() + 0.5, above.getY() + 0.875, above.getZ() + 0.5);
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

        if(livingTarget != null && (livingTarget.removed || livingTarget.isDeadOrDying())) {
            livingTarget = null;
        }

        if(ticks % 5 == 0) {

            LivingEntity selected = null;
            double lastMag = 0;

            Class<? extends LivingEntity> type = onlyTargetPlayers.get() ? PlayerEntity.class : LivingEntity.class;

            for(LivingEntity entity : level.getEntitiesOfClass(type, new AxisAlignedBB(getBlockPos()).inflate(currentRange.get() / 4.0))) {
                if(raycastToBlockPos(level, getBlockPos(), entity.blockPosition().above()).isEmpty() && !(entity instanceof PlayerEntity && (((PlayerEntity) entity).isCreative() || whitelistedPlayers.get().contains(((PlayerEntity) entity).getName().getString()))) && !entity.isDeadOrDying() && !entity.removed) {
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
