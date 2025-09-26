package ballistix.common.tile.turret.antimissile;

import javax.annotation.Nullable;

import ballistix.api.missile.MissileManager;
import ballistix.api.missile.virtual.VirtualProjectile;
import ballistix.api.turret.ITarget;
import ballistix.common.inventory.container.ContainerCIWSTurret;
import ballistix.common.settings.BallistixConstants;
import ballistix.common.tile.turret.antimissile.util.TileTurretAntimissileProjectile;
import ballistix.registers.BallistixItems;
import ballistix.registers.BallistixSounds;
import ballistix.registers.BallistixTiles;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
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

    public final SingleProperty<Boolean> outOfAmmo = property(new SingleProperty<>(PropertyTypes.BOOLEAN, "noammo", false));
    public final SingleProperty<Boolean> firing = property(new SingleProperty<>(PropertyTypes.BOOLEAN, "isfiring", false));
    public final SingleProperty<Boolean> targetingEntity = property(new SingleProperty<>(PropertyTypes.BOOLEAN, "targetingentity", false));

    private boolean isPlaying = false;
    private LivingEntity livingTarget = null;

    public TileTurretCIWS() {
        super(BallistixTiles.TILE_CIWSTURRET.get(), BallistixConstants.CIWS_TURRET_BASE_RANGE, 0, BallistixConstants.CIWS_TURRET_USAGEPERTICK, BallistixConstants.CIWS_TURRET_ROTATIONSPEEDRADIANS, BallistixConstants.CIWS_INNACCURACY);
    }

    @Override
    public ComponentInventory getInventory() {
        return new ComponentInventory(this, ComponentInventory.InventoryBuilder.newInv().inputs(2).upgrades(3)).setDirectionsBySlot(0, BlockEntityUtils.MachineDirection.values()).valid((index, stack, inv) -> {

            if (index < 2) {
                return stack.getItem() == BallistixItems.ITEM_BULLET.get();
            } else if (index >= inv.getUpgradeSlotStartIndex()) {
                return stack.getItem() instanceof ItemUpgrade && inv.isUpgradeValid(((ItemUpgrade) stack.getItem()).subtype);
            } else {
                return false;
            }

        });
    }

    @Override
    public ComponentContainerProvider getContainer() {
        return new ComponentContainerProvider("ciwsturret", this).createMenu((id, player) -> new ContainerCIWSTurret(id, player, getComponent(IComponentType.Inventory), getCoordsArray()));
    }

    @Override
    public void tickServerActive(ComponentTickable tickable) {
        if(!canFire.getValue()) {
            firing.setValue(false);
        }
    }

    @Override
    public void fireTickServer(long ticks) {

        ComponentInventory inv = getComponent(IComponentType.Inventory);

        int slot = 0;

        ItemStack bul = inv.getItem(slot);

        if(bul.isEmpty()) {
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

        Vector3d trajectory = getProjectileTrajectoryFromInaccuracy(inaccuracy, baseRange, inaccuracyMultiplier.getValue(), getProjectileLaunchPosition(), getTargetPosition(getTarget(ticks)));

        VirtualProjectile.VirtualBullet bullet = new VirtualProjectile.VirtualBullet(getProjectileSpeed(), getProjectileLaunchPosition(), trajectory, currentRange.getValue().floatValue());

        MissileManager.addBullet(level.dimension(), bullet);

        inv.removeItem(slot, 1);

    }

    @Override
    public void tickClient(ComponentTickable tickable) {
        if(shouldPlaySound() && !isPlaying) {
            isPlaying = true;
            SoundBarrierMethods.playTileSound(BallistixSounds.SOUND_CIWS_TURRETFIRING.get(), SoundCategory.BLOCKS, this, 1.0F, 1.0F, true);
        }
    }

    @Override
    public Vector3d getProjectileLaunchPosition() {
        BlockPos above = getBlockPos();
        return new Vector3d(above.getX() + 0.5, above.getY() + 0.8, above.getZ() + 0.5);
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

        if(target != null || mode == TargetingMode.NONE) {
            livingTarget = null;
            return target;
        }

        if(livingTarget != null && (!livingTarget.isAlive() || livingTarget.isDeadOrDying())) {
            livingTarget = null;
        }

        if(ticks % 5 == 0) {

            LivingEntity selected = null;
            double lastMag = 0;

            Class<? extends LivingEntity> type = mode == TargetingMode.ONLY_PLAYERS ? PlayerEntity.class : LivingEntity.class;

            for(LivingEntity entity : level.getEntitiesOfClass(type, new AxisAlignedBB(getBlockPos()).inflate(currentRange.getValue() / 4.0))) {
                if(raycastToBlockPos(level, getProjectileLaunchPosition(), entity.position().add(0, entity.getEyeHeight(), 0)).isEmpty() && !(entity instanceof PlayerEntity && (((PlayerEntity) entity).isCreative() || whitelistedPlayers.getValue().contains(((PlayerEntity) entity).getName().getString()))) && !entity.isDeadOrDying() && entity.isAlive()) {
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
    public AxisAlignedBB getRenderBoundingBox() {
    	return super.getRenderBoundingBox().inflate(3);
    }

}
