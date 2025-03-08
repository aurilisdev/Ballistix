package ballistix.common.tile.silo;

import ballistix.api.missile.virtual.VirtualProjectile;
import ballistix.common.tile.radar.TileFireControlRadar;
import ballistix.common.tile.turret.antimissile.util.TileTurretAntimissile;
import ballistix.registers.BallistixItems;
import org.jetbrains.annotations.Nullable;

import ballistix.api.missile.MissileManager;
import ballistix.api.missile.virtual.VirtualMissile;
import ballistix.api.silo.ILauncherControlPanel;
import ballistix.api.silo.ILauncherPlatform;
import ballistix.common.block.BlockExplosive;
import ballistix.common.block.subtype.SubtypeBallistixMachine;
import ballistix.common.inventory.container.ContainerLauncherPlatformT1;
import ballistix.common.inventory.container.ContainerLauncherPlatformT2;
import ballistix.common.inventory.container.ContainerLauncherPlatformT3;
import ballistix.common.item.ItemMissile;
import ballistix.common.settings.Constants;
import ballistix.registers.BallistixSounds;
import ballistix.registers.BallistixTiles;
import electrodynamics.api.multiblock.subnodebased.parent.IMultiblockParentBlock;
import electrodynamics.api.multiblock.subnodebased.parent.IMultiblockParentTile;
import electrodynamics.common.blockitem.types.BlockItemDescriptable;
import electrodynamics.common.tile.TileMultiSubnode;
import electrodynamics.prefab.properties.Property;
import electrodynamics.prefab.properties.PropertyTypes;
import electrodynamics.prefab.tile.GenericTile;
import electrodynamics.prefab.tile.components.IComponentType;
import electrodynamics.prefab.tile.components.type.ComponentContainerProvider;
import electrodynamics.prefab.tile.components.type.ComponentInventory;
import electrodynamics.prefab.tile.components.type.ComponentInventory.InventoryBuilder;
import electrodynamics.prefab.tile.components.type.ComponentPacketHandler;
import electrodynamics.prefab.tile.components.type.ComponentTickable;
import electrodynamics.prefab.utilities.BlockEntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;

public class TileLauncherPlatformT1 extends GenericTile implements ILauncherPlatform, IMultiblockParentTile {

    public static final int MISSILE_SLOT = 0;
    public static final int EXPLOSIVE_SLOT = 1;

    public static final int COOLDOWN = 100;

    public Property<Boolean> hasExplosive = property(new Property<>(PropertyTypes.BOOLEAN, "hasexplosive", false));
    public Property<Boolean> hasMissile = property(new Property<>(PropertyTypes.BOOLEAN, "hasmissile", false));
    public Property<Boolean> hasSam = property(new Property<>(PropertyTypes.BOOLEAN, "hassam", false));

    public TileLauncherPlatformT1(BlockPos pos, BlockState state) {
        this(BallistixTiles.TILE_LAUNCHER_PLATFORM_TIER1.get(), pos, state);
    }

    public TileLauncherPlatformT1(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        int tier = getTier();
        addComponent(new ComponentTickable(this));
        addComponent(new ComponentInventory(this, InventoryBuilder.newInv().inputs(2)).setDirectionsBySlot(0, BlockEntityUtils.MachineDirection.values()).setDirectionsBySlot(1, BlockEntityUtils.MachineDirection.values()).valid(this::isItemValidForSlot));
        addComponent(new ComponentPacketHandler(this));
        if (tier == 1) {
            addComponent(new ComponentContainerProvider("container.launcherplatformtier" + tier, this).createMenu((id, player) -> new ContainerLauncherPlatformT1(id, player, getComponent(IComponentType.Inventory), getCoordsArray())));
        } else if (tier == 2) {
            addComponent(new ComponentContainerProvider("container.launcherplatformtier" + tier, this).createMenu((id, player) -> new ContainerLauncherPlatformT2(id, player, getComponent(IComponentType.Inventory), getCoordsArray())));
        } else if (tier == 3) {
            addComponent(new ComponentContainerProvider("container.launcherplatformtier" + tier, this).createMenu((id, player) -> new ContainerLauncherPlatformT3(id, player, getComponent(IComponentType.Inventory), getCoordsArray())));
        }

    }

    @Override
    public int getRange() {
        return Constants.LAUNCHER_PLATFORM_RANGE_T1;
    }

    @Override
    public int getTier() {
        return 1;
    }

    @Override
    public int launch(ILauncherControlPanel controlPanel, boolean redstoneTriggered) {

        int cooldown = 0;

        ComponentInventory inv = getComponent(IComponentType.Inventory);

        ItemStack mis = inv.getItem(MISSILE_SLOT);

        if (redstoneTriggered && hasSam.get()) {

            BlockPos target = controlPanel.getTarget();

            if (
                //
                    level.getBlockEntity(target) instanceof TileFireControlRadar radar &&
                            //
                            TileTurretAntimissile.getDistanceToPos(getBlockPos(), radar.getBlockPos()) < Constants.MAX_DISTANCE_FROM_RADAR &&
                            //
                            radar.tracking != null &&
                            //
                            TileFireControlRadar.getDistanceToMissile(new Vec3(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ()), radar.tracking.position) > 100
                //
            ) {
                VirtualProjectile.VirtualSAM sam = new VirtualProjectile.VirtualSAM(
                        //
                        0.0F,
                        //
                        new Vec3(getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5),
                        //
                        new Vec3(0, 1, 0),
                        //
                        Constants.FIRE_CONTROL_RADAR_RANGE * 3F,
                        //
                        target,
                        //
                        1
                        //
                );

                MissileManager.addSAM(level.dimension(), sam);

                inv.removeItem(MISSILE_SLOT, 1);

                cooldown = COOLDOWN * 2;
            }
        } else if (!hasSam.get()) {
            ItemStack explosive = inv.getItem(EXPLOSIVE_SLOT);
            if (mis.getItem() instanceof ItemMissile itmissile && explosive.getItem() instanceof BlockItemDescriptable desc && desc.getBlock() instanceof BlockExplosive blexplosive) {
                if (blexplosive.explosive.tier > itmissile.missile.tier || itmissile.missile.tier > getTier() || blexplosive.explosive.tier > getTier()) {
                    return -1;
                }
                VirtualMissile missile = new VirtualMissile(
                        //
                        new Vec3(getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5),
                        //
                        new Vec3(0, 1, 0),
                        //
                        0.0F,
                        //
                        false,
                        //
                        getBlockPos().getX() + 0.5F,
                        //
                        getBlockPos().getZ() + 0.5F,
                        //
                        controlPanel.getTarget(),
                        //
                        itmissile.missile.ordinal(),
                        //
                        ((BlockExplosive) ((BlockItemDescriptable) explosive.getItem()).getBlock()).explosive.ordinal(),
                        //
                        controlPanel.getFrequency(),
                        //
                        getTier() > 1
                        //
                );

                MissileManager.addMissile(level.dimension(), missile);

                inv.removeItem(MISSILE_SLOT, 1);
                inv.removeItem(EXPLOSIVE_SLOT, 1);

                cooldown = COOLDOWN;
            } else {
                return -1;
            }
        }

        if(cooldown > 0) {
            level.playSound(null, getBlockPos(), BallistixSounds.SOUND_MISSILE_SILO.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        }



        return cooldown;

    }

    protected boolean isItemValidForSlot(int index, ItemStack stack, ComponentInventory inv) {
        Item item = stack.getItem();
        if (index == 0) {
            return (item instanceof ItemMissile missile && missile.missile.tier <= getTier()) || stack.is(BallistixItems.ITEM_AAMISSILEMK2);
        } else if (index == 1) {
            return item instanceof BlockItemDescriptable des && des.getBlock() instanceof BlockExplosive expl && expl.explosive.tier <= getTier() && expl.explosive.tier > -1;
        }
        return false;
    }

    @Override
    public void onInventoryChange(ComponentInventory inv, int index) {
        handleMissile(inv, index);
        handleExplosive(inv, index);
    }

    private void handleMissile(ComponentInventory inv, int index) {
        if (index == 0 || index == -1) {

            ItemStack missile = inv.getItem(0);

            if (missile.isEmpty()) {
                hasMissile.set(false);
                hasSam.set(false);
                return;
            }

            boolean sam = missile.is(BallistixItems.ITEM_AAMISSILEMK2);

            if (missile.getItem() instanceof ItemMissile || sam) {

                hasMissile.set(true);

                hasSam.set(sam);

            } else {
                hasMissile.set(false);
                hasSam.set(false);
            }

        }
    }

    private void handleExplosive(ComponentInventory inv, int index) {
        if (index == 1 || index == -1) {
            ItemStack explosive = inv.getItem(1);
            if ((!explosive.isEmpty() && explosive.getItem() instanceof BlockItemDescriptable blockItem && blockItem.getBlock() instanceof BlockExplosive) || (explosive.isEmpty() && inv.getItem(MISSILE_SLOT).is(BallistixItems.ITEM_AAMISSILEMK2))) {
                hasExplosive.set(true);
            } else {
                hasExplosive.set(false);
            }

        }
    }

    @Override
    public boolean hasExplosive() {
        return hasExplosive.get();
    }

    @Override
    public boolean hasMissile() {
        return hasMissile.get();
    }

    @Override
    public boolean hasSAM() {
        return hasSam.get();
    }

    @Override
    public IMultiblockParentBlock.SubnodeWrapper getSubNodes() {
        return SubtypeBallistixMachine.Subnodes.LAUNCHER_PLATFORM_TIER1;
    }

    @Override
    public void onSubnodeDestroyed(TileMultiSubnode tileMultiSubnode) {
        level.destroyBlock(worldPosition, true);
    }

    @Override
    public ItemInteractionResult onSubnodeUseWithItem(ItemStack used, Player player, InteractionHand hand, BlockHitResult hit, TileMultiSubnode subnode) {
        return useWithItem(used, player, hand, hit);
    }

    @Override
    public InteractionResult onSubnodeUseWithoutItem(Player player, BlockHitResult hit, TileMultiSubnode subnode) {
        return useWithoutItem(player, hit);
    }

    @Override
    public @Nullable IItemHandler getSubnodeItemHandlerCapability(TileMultiSubnode subnode, @Nullable Direction side) {
        return getItemHandlerCapability(side);
    }

    @Override
    public Direction getFacingDirection() {
        return getFacing();
    }

}
