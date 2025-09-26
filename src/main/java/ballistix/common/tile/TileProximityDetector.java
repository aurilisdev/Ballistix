package ballistix.common.tile;

import ballistix.common.inventory.container.ContainerProximityDetector;
import ballistix.common.settings.BallistixConstants;
import ballistix.common.tile.turret.GenericTileTurret;
import ballistix.registers.BallistixItems;
import ballistix.registers.BallistixTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import voltaic.prefab.properties.types.PropertyTypes;
import voltaic.prefab.properties.variant.ListProperty;
import voltaic.prefab.properties.variant.SingleProperty;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.tile.components.type.ComponentContainerProvider;
import voltaic.prefab.tile.components.type.ComponentElectrodynamic;
import voltaic.prefab.tile.components.type.ComponentForgeEnergy;
import voltaic.prefab.tile.components.type.ComponentTickable;
import voltaic.prefab.utilities.BlockEntityUtils;
import voltaic.registers.VoltaicCapabilities;

import java.util.ArrayList;
import java.util.List;

public class TileProximityDetector extends GenericTile {

    public final ListProperty<String> whitelistedPlayers = property(new ListProperty<>(PropertyTypes.STRING_LIST, "whitelistedplayers", new ArrayList<>()));
    public final SingleProperty<Integer> entityTargetingMode = property(new SingleProperty<>(PropertyTypes.INTEGER, "entitytargetingmode", 0));
    public final SingleProperty<Boolean> usingWhitelist = property(new SingleProperty<>(PropertyTypes.BOOLEAN, "usingwhitelist", false));
    public final SingleProperty<BlockPos> minCorner = property(new SingleProperty<>(PropertyTypes.BLOCK_POS, "mincorner", BlockPos.ZERO));
    public final SingleProperty<BlockPos> maxCorner = property(new SingleProperty<>(PropertyTypes.BLOCK_POS, "maxcorner", BlockPos.ZERO));
    public final SingleProperty<Integer> redstoneSignal = property(new SingleProperty<>(PropertyTypes.INTEGER, "redstonesignal", 0)).onChange((prop, old) -> {
        if(level != null && !level.isClientSide) {
            level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
        }
    }).onTileLoaded(prop -> {
        if(level != null && !level.isClientSide) {
            level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
        }
    });

    public TileProximityDetector(BlockPos worldPos, BlockState blockState) {
        super(BallistixTiles.TILE_PROXIMITYDETECTOR.get(), worldPos, blockState);
        addComponent(new ComponentTickable(this).tickServer(this::tickServer));
        addComponent(new ComponentElectrodynamic(this, false, true).setInputDirections(BlockEntityUtils.MachineDirection.BOTTOM).voltage(VoltaicCapabilities.DEFAULT_VOLTAGE).maxJoules(BallistixConstants.PROXIMITYDETECTOR_USAGEPERTICK * 20));
        addComponent(new ComponentForgeEnergy(this));
        addComponent(new ComponentContainerProvider("proximitydetector", this).createMenu((id, inv) -> new ContainerProximityDetector(id, inv, new SimpleContainer(0), getCoordsArray())));
    }

    public void tickServer(ComponentTickable componentTickable) {

        ComponentElectrodynamic electro = getComponent(IComponentType.Electrodynamic);

        if (electro.getJoulesStored() < BallistixConstants.PROXIMITYDETECTOR_USAGEPERTICK) {
            redstoneSignal.setValue(0);
            return;
        }

        GenericTileTurret.TargetingMode mode = GenericTileTurret.TargetingMode.values()[entityTargetingMode.getValue()];

        if (mode == GenericTileTurret.TargetingMode.NONE) {
            redstoneSignal.setValue(0);
            return;
        }

        electro.joules(electro.getJoulesStored() - BallistixConstants.PROXIMITYDETECTOR_USAGEPERTICK);

        AABB box = AABB.encapsulatingFullBlocks(getBlockPos().offset(minCorner.getValue().multiply(-1)), getBlockPos().offset(maxCorner.getValue()));

        if (usingWhitelist.getValue()) {

            if (whitelistedPlayers.getValue().isEmpty()) {
                redstoneSignal.setValue(0);
                return;
            }

            List<Player> players = level.getEntitiesOfClass(Player.class, box);

            if (players.isEmpty()) {
                redstoneSignal.setValue(0);
                return;
            }

            for (Player player : players) {
                if (!player.isAlive()) {
                    continue;
                }

                ItemStack inHand = player.getItemInHand(InteractionHand.MAIN_HAND);
                if (inHand.isEmpty()) {
                    inHand = player.getItemInHand(InteractionHand.OFF_HAND);
                    if (!inHand.isEmpty() && inHand.is(BallistixItems.ITEM_SCANNER)) {
                        continue;
                    }
                } else if (inHand.is(BallistixItems.ITEM_SCANNER)) {
                    continue;
                }


                for (String name : whitelistedPlayers.getValue()) {
                    if (player.getName().getString().equals(name)) {
                        redstoneSignal.setValue(15);
                        return;
                    }
                }
            }

            redstoneSignal.setValue(0);

        } else {

            Class<? extends LivingEntity> type = mode == GenericTileTurret.TargetingMode.ONLY_PLAYERS ? Player.class : LivingEntity.class;

            List<? extends LivingEntity> entities = level.getEntitiesOfClass(type, box);

            if (entities.isEmpty()) {
                redstoneSignal.setValue(0);
                return;
            }

            for (LivingEntity entity : entities) {

                if (!entity.isAlive()) {
                    continue;
                }

                ItemStack inHand = entity.getItemInHand(InteractionHand.MAIN_HAND);
                if (inHand.isEmpty()) {
                    inHand = entity.getItemInHand(InteractionHand.OFF_HAND);
                    if (!inHand.isEmpty() && inHand.is(BallistixItems.ITEM_SCANNER)) {
                        continue;
                    }
                } else if (inHand.is(BallistixItems.ITEM_SCANNER)) {
                    continue;
                }


                if (entity instanceof Player player) {
                    boolean whitelisted = false;
                    for (String name : whitelistedPlayers.getValue()) {
                        if (player.getName().getString().equals(name)) {
                            whitelisted = true;
                            break;
                        }
                    }
                    if(!whitelisted) {
                        redstoneSignal.setValue(15);
                        return;
                    }

                } else {
                    redstoneSignal.setValue(15);
                    return;
                }

            }

            redstoneSignal.setValue(0);


        }

    }

    @Override
    public void setPlacedBy(LivingEntity player, ItemStack stack) {
        super.setPlacedBy(player, stack);
        if (player instanceof Player pl) {
            whitelistedPlayers.addValue(pl.getName().getString());
        }
    }

    @Override
    public int getSignal(Direction dir) {
        return redstoneSignal.getValue();
    }
}
