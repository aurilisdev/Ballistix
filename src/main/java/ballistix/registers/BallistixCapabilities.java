package ballistix.registers;

import ballistix.Ballistix;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import voltaic.Voltaic;
import voltaic.prefab.item.CapabilityForgeEnergyItem;
import voltaic.prefab.tile.GenericTile;
import voltaic.registers.VoltaicCapabilities;

@EventBusSubscriber(modid = Ballistix.ID, bus = EventBusSubscriber.Bus.MOD)
public class BallistixCapabilities {

    @SubscribeEvent
    public static void register(RegisterCapabilitiesEvent event) {

        BallistixTiles.BLOCK_ENTITY_TYPES.getEntries().forEach(entry -> {
            event.registerBlockEntity(VoltaicCapabilities.CAPABILITY_ELECTRODYNAMIC_BLOCK, (BlockEntityType<? extends GenericTile>) entry.get(), (tile, context) -> tile.getElectrodynamicCapability(context));
            event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, (BlockEntityType<? extends GenericTile>) entry.get(), (tile, context) -> tile.getFluidHandlerCapability(context));
            event.registerBlockEntity(VoltaicCapabilities.CAPABILITY_GASHANDLER_BLOCK, (BlockEntityType<? extends GenericTile>) entry.get(), (tile, context) -> tile.getGasHandlerCapability(context));
            event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, (BlockEntityType<? extends GenericTile>) entry.get(), (tile, context) -> tile.getItemHandlerCapability(context));
            event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, (BlockEntityType<? extends GenericTile>) entry.get(), (tile, context) -> tile.getForgeEnergyCapability(context));
        });

        event.registerItem(Capabilities.EnergyStorage.ITEM, (itemstack, context) -> Voltaic.isElectroLoaded() ? null : new CapabilityForgeEnergyItem(itemstack, true, false), BallistixItems.ITEM_DEFUSER.get(), BallistixItems.ITEM_LASERDESIGNATOR.get(), BallistixItems.ITEM_RADARGUN.get(), BallistixItems.ITEM_SCANNER.get(), BallistixItems.ITEM_TRACKER.get());
    }

}
