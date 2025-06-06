package ballistix.common.tile.silo;

import ballistix.api.blast.IBlast;
import ballistix.api.missile.virtual.VirtualProjectile;
import ballistix.common.blast.Blast;
import ballistix.common.tile.TileESMTower;
import ballistix.common.tile.radar.TileFireControlRadar;
import ballistix.common.tile.radar.TileSearchRadar;
import ballistix.common.tile.turret.antimissile.util.TileTurretAntimissile;
import ballistix.registers.BallistixItems;
import org.jetbrains.annotations.Nullable;

import ballistix.api.missile.MissileManager;
import ballistix.api.missile.virtual.VirtualMissile;
import ballistix.api.silo.ILauncherControlPanel;
import ballistix.api.silo.ILauncherPlatform;
import ballistix.common.block.subtype.SubtypeBallistixMachine;
import ballistix.common.inventory.container.ContainerLauncherPlatformT1;
import ballistix.common.inventory.container.ContainerLauncherPlatformT2;
import ballistix.common.inventory.container.ContainerLauncherPlatformT3;
import ballistix.common.item.ItemMissile;
import ballistix.common.settings.BallistixConstants;
import ballistix.registers.BallistixSounds;
import ballistix.registers.BallistixTiles;
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
import voltaic.api.multiblock.subnodebased.TileMultiSubnode;
import voltaic.api.multiblock.subnodebased.parent.IMultiblockParentBlock;
import voltaic.api.multiblock.subnodebased.parent.IMultiblockParentTile;
import voltaic.prefab.properties.types.PropertyTypes;
import voltaic.prefab.properties.variant.SingleProperty;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.tile.components.type.ComponentContainerProvider;
import voltaic.prefab.tile.components.type.ComponentInventory;
import voltaic.prefab.tile.components.type.ComponentPacketHandler;
import voltaic.prefab.tile.components.type.ComponentTickable;
import voltaic.prefab.utilities.BlockEntityUtils;

public class TileLauncherPlatformT1 extends GenericTile implements ILauncherPlatform, IMultiblockParentTile {

    public static final int MISSILE_SLOT = 0;
    public static final int EXPLOSIVE_SLOT = 1;

    public static final int COOLDOWN = 100;

    public SingleProperty<Boolean> hasExplosive = property(new SingleProperty<>(PropertyTypes.BOOLEAN, "hasexplosive", false));
    public SingleProperty<Boolean> hasMissile = property(new SingleProperty<>(PropertyTypes.BOOLEAN, "hasmissile", false));
    public SingleProperty<Boolean> hasSam = property(new SingleProperty<>(PropertyTypes.BOOLEAN, "hassam", false));

    public TileLauncherPlatformT1(BlockPos pos, BlockState state) {
        this(BallistixTiles.TILE_LAUNCHER_PLATFORM_TIER1.get(), pos, state);
    }

    public TileLauncherPlatformT1(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        int tier = getTier();
        addComponent(new ComponentTickable(this));
        addComponent(new ComponentInventory(this, ComponentInventory.InventoryBuilder.newInv().inputs(2)).setDirectionsBySlot(0, BlockEntityUtils.MachineDirection.values()).setDirectionsBySlot(1, BlockEntityUtils.MachineDirection.values()).valid(this::isItemValidForSlot));
        addComponent(new ComponentPacketHandler(this));
        if (tier == 1) {
            addComponent(new ComponentContainerProvider("launcherplatformtier" + tier, this).createMenu((id, player) -> new ContainerLauncherPlatformT1(id, player, getComponent(IComponentType.Inventory), getCoordsArray())));
        } else if (tier == 2) {
            addComponent(new ComponentContainerProvider("launcherplatformtier" + tier, this).createMenu((id, player) -> new ContainerLauncherPlatformT2(id, player, getComponent(IComponentType.Inventory), getCoordsArray())));
        } else if (tier == 3) {
            addComponent(new ComponentContainerProvider("launcherplatformtier" + tier, this).createMenu((id, player) -> new ContainerLauncherPlatformT3(id, player, getComponent(IComponentType.Inventory), getCoordsArray())));
        }

    }

    @Override
    public int getRange() {
        return BallistixConstants.LAUNCHER_PLATFORM_RANGE_T1;
    }

    @Override
    public int getTier() {
        return 1;
    }

    @Override
    public int launch(ILauncherControlPanel controlPanel, boolean redstoneTriggered, int inaccuracy) {

        int cooldown = 0;

        if (redstoneTriggered && hasSam.getValue()) {

            ComponentInventory inv = getComponent(IComponentType.Inventory);
            BlockPos target = controlPanel.getTarget();

            if (
                //
                    level.getBlockEntity(target) instanceof TileFireControlRadar radar &&
                            //
                            TileTurretAntimissile.getDistanceToPos(getBlockPos(), radar.getBlockPos()) < BallistixConstants.MAX_DISTANCE_FROM_RADAR &&
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
                        BallistixConstants.FIRE_CONTROL_RADAR_RANGE * 3F,
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
        } else if (!hasSam.getValue()) {

            // we only want this boolean to hit before stepping in to ensure it doesn't launch and blow up stuff on accident!

            if (level.getBlockEntity(controlPanel.getTarget()) instanceof TileSearchRadar radar) {

                if (TileTurretAntimissile.getDistanceToPos(getBlockPos(), radar.getBlockPos()) <= BallistixConstants.MAX_DISTANCE_FROM_RADAR && redstoneTriggered && !radar.trackedEsmTowers.isEmpty()) {

                    for (TileESMTower tower : radar.trackedEsmTowers) {

                        if (tower != null && !tower.isRemoved() && launchMissile(tower.getBlockPos(), controlPanel.getFrequency())) {
                            cooldown = COOLDOWN * 5;
                            break;
                        }

                    }

                }


            } else {

                double length = inaccuracy * level.random.nextDouble();
                double angle = level.random.nextDouble() * 2 * Math.PI;
                int offsetX = (int) (length * Math.cos(angle));
                int offsetZ = (int) (length * Math.sin(angle));

                BlockPos pos = controlPanel.getTarget().offset(offsetX, 0, offsetZ);

                if(launchMissile(pos, controlPanel.getFrequency())) {
                    cooldown = COOLDOWN;
                }

            }


        }

        if (cooldown > 0) {
            level.playSound(null, getBlockPos(), BallistixSounds.SOUND_MISSILE_SILO.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        }


        return cooldown;

    }

    public boolean launchMissile(BlockPos target, int frequency) {
        ComponentInventory inv = getComponent(IComponentType.Inventory);

        ItemStack mis = inv.getItem(MISSILE_SLOT);

        ItemStack explosive = inv.getItem(EXPLOSIVE_SLOT);
        if (mis.getItem() instanceof ItemMissile itmissile && Blast.ITEM_TO_BLAST_MAP.get(explosive.getItem()) != null) {
            IBlast blast = Blast.ITEM_TO_BLAST_MAP.get(explosive.getItem());
            if (blast.tier() > itmissile.missile.tier() || itmissile.missile.tier() > getTier() || blast.tier() > getTier()) {
                return false;
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
                    target,
                    //
                    itmissile.missile.ordinal(),
                    //
                    blast,
                    //
                    frequency,
                    //
                    getTier() > 1
                    //
            );

            MissileManager.addMissile(level.dimension(), missile);

            inv.removeItem(MISSILE_SLOT, 1);
            inv.removeItem(EXPLOSIVE_SLOT, 1);

            return true;
        }
	return false;


    }

    protected boolean isItemValidForSlot(int index, ItemStack stack, ComponentInventory inv) {
        Item item = stack.getItem();
        if (index == 0) {
            return (item instanceof ItemMissile missile && missile.missile.tier() <= getTier()) || stack.is(BallistixItems.ITEM_AAMISSILEMK2);
        } else if (index == 1) {
            IBlast blast = Blast.ITEM_TO_BLAST_MAP.get(item);
            return blast != null && blast.tier() <= getTier() && blast.tier() > -1;
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
                hasMissile.setValue(false);
                hasSam.setValue(false);
                return;
            }

            boolean sam = missile.is(BallistixItems.ITEM_AAMISSILEMK2);

            if (missile.getItem() instanceof ItemMissile || sam) {

                hasMissile.setValue(true);

                hasSam.setValue(sam);

            } else {
                hasMissile.setValue(false);
                hasSam.setValue(false);
            }

        }
    }

    private void handleExplosive(ComponentInventory inv, int index) {
        if (index == 1 || index == -1) {
            ItemStack explosive = inv.getItem(1);
            if ((!explosive.isEmpty() && Blast.ITEM_TO_BLAST_MAP.get(explosive.getItem()) != null) || (explosive.isEmpty() && inv.getItem(MISSILE_SLOT).is(BallistixItems.ITEM_AAMISSILEMK2))) {
                hasExplosive.setValue(true);
            } else {
                hasExplosive.setValue(false);
            }

        }
    }

    @Override
    public boolean hasExplosive() {
        return hasExplosive.getValue();
    }

    @Override
    public boolean hasMissile() {
        return hasMissile.getValue();
    }

    @Override
    public boolean hasSAM() {
        return hasSam.getValue();
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
