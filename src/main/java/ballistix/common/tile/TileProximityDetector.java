package ballistix.common.tile;

import java.util.ArrayList;
import java.util.List;

import ballistix.common.inventory.container.ContainerProximityDetector;
import ballistix.common.settings.BallistixConstants;
import ballistix.common.tile.turret.GenericTileTurret;
import ballistix.registers.BallistixItems;
import ballistix.registers.BallistixTiles;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
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

    public TileProximityDetector() {
        super(BallistixTiles.TILE_PROXIMITYDETECTOR.get());
        addComponent(new ComponentTickable(this).tickServer(this::tickServer));
        addComponent(new ComponentElectrodynamic(this, false, true).setInputDirections(BlockEntityUtils.MachineDirection.BOTTOM).voltage(VoltaicCapabilities.DEFAULT_VOLTAGE).maxJoules(BallistixConstants.PROXIMITYDETECTOR_USAGEPERTICK * 20));
        addComponent(new ComponentForgeEnergy(this));
        addComponent(new ComponentContainerProvider("proximitydetector", this).createMenu((id, inv) -> new ContainerProximityDetector(id, inv, new Inventory(0), getCoordsArray())));
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
        
        BlockPos inverted = new BlockPos(-minCorner.getValue().getX(), -minCorner.getValue().getY(), -minCorner.getValue().getZ());

        AxisAlignedBB box = new AxisAlignedBB(getBlockPos().offset(inverted), getBlockPos().offset(maxCorner.getValue()).offset(1, 1, 1));

        if (usingWhitelist.getValue()) {

            if (whitelistedPlayers.getValue().isEmpty()) {
                redstoneSignal.setValue(0);
                return;
            }

            List<PlayerEntity> players = level.getEntitiesOfClass(PlayerEntity.class, box);

            if (players.isEmpty()) {
                redstoneSignal.setValue(0);
                return;
            }

            for (PlayerEntity player : players) {
                if (!player.isAlive()) {
                    continue;
                }

                ItemStack inHand = player.getItemInHand(Hand.MAIN_HAND);
                if (inHand.isEmpty()) {
                    inHand = player.getItemInHand(Hand.OFF_HAND);
                    if (!inHand.isEmpty() && inHand.getItem() == BallistixItems.ITEM_SCANNER.get()) {
                        continue;
                    }
                } else if (inHand.getItem() == BallistixItems.ITEM_SCANNER.get()) {
                    continue;
                }


                for (String name : whitelistedPlayers.getValue()) {
                    if (player.getName().getString().equals(name)) {
                        redstoneSignal.setValue(15);
                        return;
                    }
                }
            }

        } else {

            Class<? extends LivingEntity> type = mode == GenericTileTurret.TargetingMode.ONLY_PLAYERS ? PlayerEntity.class : LivingEntity.class;

            List<? extends LivingEntity> entities = level.getEntitiesOfClass(type, box);

            if (entities.isEmpty()) {
                redstoneSignal.setValue(0);
                return;
            }

            for (LivingEntity entity : entities) {

                if (!entity.isAlive()) {
                    continue;
                }

                ItemStack inHand = entity.getItemInHand(Hand.MAIN_HAND);
                if (inHand.isEmpty()) {
                    inHand = entity.getItemInHand(Hand.OFF_HAND);
                    if (!inHand.isEmpty() && inHand.getItem() == BallistixItems.ITEM_SCANNER.get()) {
                        continue;
                    }
                } else if (inHand.getItem() == BallistixItems.ITEM_SCANNER.get()) {
                    continue;
                }


                if (entity instanceof PlayerEntity) {
                	PlayerEntity player = (PlayerEntity) entity;
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


        }
	redstoneSignal.setValue(0);

    }

    @Override
    public void setPlacedBy(LivingEntity player, ItemStack stack) {
        super.setPlacedBy(player, stack);
        if (player instanceof PlayerEntity) {
            whitelistedPlayers.addValue(((PlayerEntity) player).getName().getString());
        }
    }

    @Override
    public int getSignal(Direction dir) {
        return redstoneSignal.getValue();
    }
}
