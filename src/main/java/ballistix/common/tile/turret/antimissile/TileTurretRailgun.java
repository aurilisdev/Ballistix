package ballistix.common.tile.turret.antimissile;

import org.jetbrains.annotations.Nullable;

import ballistix.api.missile.MissileManager;
import ballistix.api.missile.virtual.VirtualProjectile;
import ballistix.api.turret.ITarget;
import ballistix.common.inventory.container.ContainerRailgunTurret;
import ballistix.common.settings.BallistixConstants;
import ballistix.common.tile.turret.antimissile.util.TileTurretAntimissileProjectile;
import ballistix.registers.BallistixSounds;
import ballistix.registers.BallistixTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import voltaic.Voltaic;
import voltaic.common.item.ItemUpgrade;
import voltaic.common.tags.VoltaicTags;
import voltaic.prefab.properties.types.PropertyTypes;
import voltaic.prefab.properties.variant.SingleProperty;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.tile.components.type.ComponentContainerProvider;
import voltaic.prefab.tile.components.type.ComponentInventory;
import voltaic.prefab.tile.components.type.ComponentTickable;
import voltaic.prefab.utilities.BlockEntityUtils;

public class TileTurretRailgun extends TileTurretAntimissileProjectile {

    public final SingleProperty<Integer> cooldown = property(
	    new SingleProperty<>(PropertyTypes.INTEGER, "cooldown", 0));
    public final SingleProperty<Boolean> outOfAmmo = property(
	    new SingleProperty<>(PropertyTypes.BOOLEAN, "noammo", false));
    public final SingleProperty<Boolean> targetingEntity = property(
	    new SingleProperty<>(PropertyTypes.BOOLEAN, "targetingentity", false));

    private LivingEntity livingTarget = null;

    public TileTurretRailgun(BlockPos worldPos, BlockState blockState) {
	super(BallistixTiles.TILE_RAILGUNTURRET.get(), worldPos, blockState,
		BallistixConstants.RAILGUN_TURRET_BASE_RANGE, 0, BallistixConstants.RAILGUN_TURRET_USAGEPERTICK,
		BallistixConstants.RAILGUN_TURRET_ROTATIONSPEEDRADIANS, BallistixConstants.RAILGUN_INNACCURACY);
    }

    @Override
    public ComponentInventory getInventory() {
	return new ComponentInventory(this, ComponentInventory.InventoryBuilder.newInv().inputs(1).upgrades(3))
		.setDirectionsBySlot(0, BlockEntityUtils.MachineDirection.values()).valid((index, stack, inv) -> {

		    if (index == 0) {
			return Voltaic.isElectroLoaded() ? stack.is(VoltaicTags.Items.ROD_STEEL)
				: stack.is(Items.IRON_INGOT);
		    } else if (index >= inv.getUpgradeSlotStartIndex()) {
			return stack.getItem() instanceof ItemUpgrade upgrade && inv.isUpgradeValid(upgrade.subtype);
		    } else {
			return false;
		    }

		});
    }

    @Override
    public ComponentContainerProvider getContainer() {
	return new ComponentContainerProvider("railgunturret", this)
		.createMenu((id, player) -> new ContainerRailgunTurret(id, player,
			getComponent(IComponentType.Inventory), getCoordsArray()));
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

	Vec3 trajectory = getProjectileTrajectoryFromInaccuracy(inaccuracy, baseRange, inaccuracyMultiplier.getValue(),
		getProjectileLaunchPosition(), getTargetPosition(getTarget(ticks)));

	VirtualProjectile.VirtualRailgunRound railgunround = new VirtualProjectile.VirtualRailgunRound(
		getProjectileSpeed(), getProjectileLaunchPosition(), trajectory, currentRange.getValue().floatValue());

	MissileManager.addRailgunRound(level.dimension(), railgunround);

	inv.removeItem(0, 1);

	level.playSound(null, getBlockPos(), BallistixSounds.SOUND_RAILGUNKINETIC.get(), SoundSource.BLOCKS, 2.0F,
		1.0F);

	cooldown.setValue(BallistixConstants.RAILGUN_TURRET_COOLDOWN);

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

	targetingEntity.setValue(false);

	ITarget target = super.getTarget(ticks);

	TargetingMode mode = TargetingMode.values()[entityTargetingMode.getValue()];

	if (target != null || mode == TargetingMode.NONE) {
	    livingTarget = null;
	    return target;
	}

	if (!isLivingTargetValid(livingTarget, mode)) {
	    livingTarget = null;
	}

	if (ticks % 20 == 0) {
	    livingTarget = findLivingTarget(mode);
	}

	if (livingTarget != null) {
	    targetingEntity.setValue(true);
	    return new ITarget.TargetLivingEntity(livingTarget);
	}

	return null;
    }

    @Override
    public boolean isValidPlacement() {
	return targetingEntity.getValue() || super.isValidPlacement();
    }

}
