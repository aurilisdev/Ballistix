package ballistix.common.tile.turret.antimissile;

import java.util.UUID;

import ballistix.api.missile.MissileManager;
import ballistix.api.missile.virtual.VirtualMissile;
import ballistix.api.missile.virtual.VirtualProjectile;
import ballistix.common.inventory.container.ContainerSAMTurret;
import ballistix.common.settings.BallistixConfig;
import ballistix.common.tile.turret.antimissile.util.TileTurretAntimissileProjectile;
import ballistix.registers.BallistixItems;
import ballistix.registers.BallistixSounds;
import ballistix.registers.BallistixTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import voltaic.common.item.ItemUpgrade;
import voltaic.prefab.properties.types.PropertyTypes;
import voltaic.prefab.properties.variant.SingleProperty;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.tile.components.type.ComponentContainerProvider;
import voltaic.prefab.tile.components.type.ComponentInventory;
import voltaic.prefab.tile.components.type.ComponentTickable;
import voltaic.prefab.utilities.BlockEntityUtils;

public class TileTurretSAM extends TileTurretAntimissileProjectile {

    public final SingleProperty<Integer> cooldown = property(
	    new SingleProperty<>(PropertyTypes.INTEGER, "cooldown", 0));
    public final SingleProperty<Boolean> outOfAmmo = property(
	    new SingleProperty<>(PropertyTypes.BOOLEAN, "noammo", false));

    public TileTurretSAM(BlockPos worldPos, BlockState blockState) {
	super(BallistixTiles.TILE_SAMTURRET.get(), worldPos, blockState,
		BallistixConfig.INSTANCE.SAM_TURRET_BASE_RANGE.get(), 100,
		BallistixConfig.INSTANCE.SAM_TURRET_USAGEPERTICK.get(),
		BallistixConfig.INSTANCE.SAM_TURRET_ROTATIONSPEEDRADIANS.get(), 1);
    }

    @Override
    public ComponentInventory getInventory() {
	return new ComponentInventory(this, ComponentInventory.InventoryBuilder.newInv().inputs(1).upgrades(3))
		.setDirectionsBySlot(0, BlockEntityUtils.MachineDirection.values()).valid((index, stack, inv) -> {

		    if (index == 0) {
			return stack.is(BallistixItems.ITEM_AAMISSILE);
		    } else if (index >= inv.getUpgradeSlotStartIndex()) {
			return stack.getItem() instanceof ItemUpgrade upgrade && inv.isUpgradeValid(upgrade.subtype);
		    } else {
			return false;
		    }

		});
    }

    @Override
    public ComponentContainerProvider getContainer() {
	return new ComponentContainerProvider("samturret", this).createMenu((id, player) -> new ContainerSAMTurret(id,
		player, getComponent(IComponentType.Inventory), getCoordsArray()));
    }

    @Override
    public void tickServerActive(ComponentTickable tickable) {
	if (cooldown.getValue() > 0) {
	    cooldown.setValue(cooldown.getValue() - 1);
	}
    }

    @Override
    public void fireTickServer(long ticks) {

	if (cooldown.getValue() > 0) {
	    return;
	}

	ComponentInventory inv = getComponent(IComponentType.Inventory);

	ItemStack missile = inv.getItem(0);

	if (missile.isEmpty()) {
	    outOfAmmo.setValue(true);
	    return;
	}

	outOfAmmo.setValue(false);

	UUID targetId = target != null && target.getTarget() instanceof VirtualMissile m ? m.getId() : null;

	if (targetId == null) {
	    return;
	}
	VirtualProjectile.VirtualSAM sam = new VirtualProjectile.VirtualSAM(0.0F, getProjectileLaunchPosition(),
		targetMovement.getValue(), currentRange.getValue().floatValue(), boundFireControl.getValue(), 0,
		targetId);

	MissileManager.addSAM(level.dimension(), sam);

	level.playSound(null, getBlockPos().above(), BallistixSounds.SOUND_MISSILE_ROCKETLAUNCHER.get(),
		SoundSource.BLOCKS, 1.0F, 1.0F);

	cooldown.setValue(BallistixConfig.INSTANCE.SAM_TURRET_COOLDOWN.get());

	inv.removeItem(0, 1);

    }

    @Override
    public Vec3 getProjectileLaunchPosition() {
	BlockPos above = getBlockPos().above();
	return new Vec3(above.getX() + 0.5, above.getY() + 0.5, above.getZ() + 0.5);
    }

    @Override
    public float getProjectileSpeed() {
	return (float) BallistixConfig.INSTANCE.SAM_TOP_SPEED.getAsDouble();
    }

    @Override
    public double getMinElevation() {
	return -0.5;
    }

    @Override
    public double getMaxElevation() {
	return 1;
    }

}
