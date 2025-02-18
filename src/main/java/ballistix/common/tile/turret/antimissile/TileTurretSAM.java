package ballistix.common.tile.turret.antimissile;

import ballistix.api.missile.MissileManager;
import ballistix.api.missile.virtual.VirtualProjectile;
import org.joml.Vector3f;

import com.mojang.datafixers.util.Pair;

import ballistix.common.inventory.container.ContainerSAMTurret;
import ballistix.common.settings.Constants;
import ballistix.common.tile.turret.antimissile.util.TileTurretAntimissileProjectile;
import ballistix.registers.BallistixBlockTypes;
import ballistix.registers.BallistixItems;
import ballistix.registers.BallistixSounds;
import electrodynamics.common.item.ItemUpgrade;
import electrodynamics.prefab.properties.Property;
import electrodynamics.prefab.properties.PropertyType;
import electrodynamics.prefab.tile.components.IComponentType;
import electrodynamics.prefab.tile.components.type.ComponentContainerProvider;
import electrodynamics.prefab.tile.components.type.ComponentInventory;
import electrodynamics.prefab.tile.components.type.ComponentTickable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class TileTurretSAM extends TileTurretAntimissileProjectile {

    public final Property<Integer> cooldown = property(new Property<>(PropertyType.Integer, "cooldown", 0));
    public final Property<Boolean> outOfAmmo = property(new Property<>(PropertyType.Boolean, "noammo", false));

    public TileTurretSAM(BlockPos worldPos, BlockState blockState) {
        super(BallistixBlockTypes.TILE_SAMTURRET.get(), worldPos, blockState, Constants.SAM_TURRET_BASE_RANGE, 100, Constants.SAM_TURRET_USAGEPERTICK, Constants.SAM_TURRET_ROTATIONSPEEDRADIANS, Constants.SAM_INNACCURACY);
    }

    @Override
    public ComponentInventory getInventory() {
        return new ComponentInventory(this, ComponentInventory.InventoryBuilder.newInv().inputs(1).upgrades(3)).setDirectionsBySlot(0, Direction.values()).valid((index, stack, inv) -> {

            if (index == 0) {
                return stack.is(BallistixItems.ITEM_AAMISSILE.get());
            } else if (index >= inv.getUpgradeSlotStartIndex()) {
                return stack.getItem() instanceof ItemUpgrade upgrade && inv.isUpgradeValid(upgrade.subtype);
            } else {
                return false;
            }

        });
    }

    @Override
    public ComponentContainerProvider getContainer() {
        return new ComponentContainerProvider("container.samturret", this).createMenu((id, player) -> new ContainerSAMTurret(id, player, getComponent(IComponentType.Inventory), getCoordsArray()));
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

        VirtualProjectile.VirtualSAM sam = new VirtualProjectile.VirtualSAM(getProjectileSpeed(), getProjectileLaunchPosition(), projectileVals.getFirst(), currentRange.get().floatValue(), new Vector3f((float) rotvec.x, (float) rotvec.y, (float) rotvec.z));

        MissileManager.addSAM(level.dimension(), sam);

        level.playSound(null, getBlockPos().above(), BallistixSounds.SOUND_MISSILE_ROCKETLAUNCHER.get(), SoundSource.BLOCKS, 1.0F, 1.0F);

        cooldown.set(Constants.SAM_TURRET_COOLDOWN);

        inv.removeItem(0, 1);

    }

    @Override
    public Vec3 getProjectileLaunchPosition() {
        BlockPos above = getBlockPos().above();
        return new Vec3(above.getX() + 0.5, above.getY() + 0.5, above.getZ() + 0.5);
    }

    @Override
    public float getProjectileSpeed() {
        return 3.0F;
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
    public AABB getRenderBoundingBox() {
    	return super.getRenderBoundingBox().inflate(3);
    }

}
