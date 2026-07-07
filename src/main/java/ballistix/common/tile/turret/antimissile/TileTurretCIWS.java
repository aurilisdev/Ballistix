package ballistix.common.tile.turret.antimissile;

import org.jetbrains.annotations.Nullable;

import ballistix.api.missile.MissileManager;
import ballistix.api.missile.virtual.VirtualProjectile;
import ballistix.api.turret.ITarget;
import ballistix.common.inventory.container.ContainerCIWSTurret;
import ballistix.common.settings.BallistixConstants;
import ballistix.common.tile.turret.antimissile.util.TileTurretAntimissileProjectile;
import ballistix.registers.BallistixItems;
import ballistix.registers.BallistixSounds;
import ballistix.registers.BallistixTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import voltaic.common.item.ItemUpgrade;
import voltaic.prefab.properties.types.PropertyTypes;
import voltaic.prefab.properties.variant.SingleProperty;
import voltaic.prefab.sound.ITickableSound;
import voltaic.prefab.sound.SoundBarrierMethods;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.tile.components.type.ComponentContainerProvider;
import voltaic.prefab.tile.components.type.ComponentInventory;
import voltaic.prefab.tile.components.type.ComponentTickable;
import voltaic.prefab.utilities.BlockEntityUtils;

public class TileTurretCIWS extends TileTurretAntimissileProjectile implements ITickableSound {

    public final SingleProperty<Boolean> outOfAmmo = property(
	    new SingleProperty<>(PropertyTypes.BOOLEAN, "noammo", false));
    public final SingleProperty<Boolean> firing = property(
	    new SingleProperty<>(PropertyTypes.BOOLEAN, "isfiring", false));
    public final SingleProperty<Boolean> targetingEntity = property(
	    new SingleProperty<>(PropertyTypes.BOOLEAN, "targetingentity", false));

    private boolean isPlaying = false;
    private LivingEntity livingTarget = null;

    public TileTurretCIWS(BlockPos worldPos, BlockState blockState) {
	super(BallistixTiles.TILE_CIWSTURRET.get(), worldPos, blockState, BallistixConstants.CIWS_TURRET_BASE_RANGE, 0,
		BallistixConstants.CIWS_TURRET_USAGEPERTICK, BallistixConstants.CIWS_TURRET_ROTATIONSPEEDRADIANS,
		BallistixConstants.CIWS_INNACCURACY);
    }

    @Override
    public ComponentInventory getInventory() {
	return new ComponentInventory(this, ComponentInventory.InventoryBuilder.newInv().inputs(2).upgrades(3))
		.setDirectionsBySlot(0, BlockEntityUtils.MachineDirection.values()).valid((index, stack, inv) -> {

		    if (index < 2) {
			return stack.getItem() == BallistixItems.ITEM_BULLET.get();
		    } else if (index >= inv.getUpgradeSlotStartIndex()) {
			return stack.getItem() instanceof ItemUpgrade upgrade && inv.isUpgradeValid(upgrade.subtype);
		    } else {
			return false;
		    }

		});
    }

    @Override
    public ComponentContainerProvider getContainer() {
	return new ComponentContainerProvider("ciwsturret", this).createMenu((id, player) -> new ContainerCIWSTurret(id,
		player, getComponent(IComponentType.Inventory), getCoordsArray()));
    }

    @Override
    public void tickServerActive(ComponentTickable tickable) {
	if (!canFire.getValue()) {
	    firing.setValue(false);
	}
    }

    @Override
    public void fireTickServer(long ticks) {

	ComponentInventory inv = getComponent(IComponentType.Inventory);

	int slot = 0;

	ItemStack bul = inv.getItem(slot);

	if (bul.isEmpty()) {
	    slot = 1;
	    bul = inv.getItem(slot);
	}

	if (bul.isEmpty()) {
	    outOfAmmo.setValue(true);
	    firing.setValue(false);
	    return;
	}

	outOfAmmo.setValue(false);
	firing.setValue(true);

	Vec3 trajectory = getProjectileTrajectoryFromInaccuracy(inaccuracy, baseRange, inaccuracyMultiplier.getValue(),
		getProjectileLaunchPosition(), getTargetPosition(getTarget(ticks)));

	VirtualProjectile.VirtualBullet bullet = new VirtualProjectile.VirtualBullet(getProjectileSpeed(),
		getProjectileLaunchPosition(), trajectory, currentRange.getValue().floatValue());

	MissileManager.addBullet(level.dimension(), bullet);

	inv.removeItem(slot, 1);

    }

    @Override
    public void tickClient(ComponentTickable tickable) {
	if (shouldPlaySound() && !isPlaying) {
	    isPlaying = true;
	    SoundBarrierMethods.playTileSound(BallistixSounds.SOUND_CIWS_TURRETFIRING.get(), SoundSource.BLOCKS, this,
		    1.0F, 1.0F, true);
	}
    }

    @Override
    public Vec3 getProjectileLaunchPosition() {
	BlockPos above = getBlockPos();
	return new Vec3(above.getX() + 0.5, above.getY() + 0.8, above.getZ() + 0.5);
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
    public float getProjectileSpeed() {
	return 5.0F;
    }

    @Override
    public void setNotPlaying() {
	isPlaying = false;
    }

    @Override
    public boolean shouldPlaySound() {
	return firing.getValue();
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

    @Override
    public AABB getRenderBoundingBox() {
	return super.getRenderBoundingBox().inflate(3);
    }

}
