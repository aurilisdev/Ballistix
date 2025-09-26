package ballistix.common.inventory.container;

import java.util.HashSet;

import ballistix.common.packet.NetworkHandler;
import ballistix.common.packet.type.client.PacketSetSearchRadarTrackedClient;
import ballistix.common.tile.radar.TileSearchRadar;
import ballistix.registers.BallistixMenuTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraftforge.network.NetworkDirection;
import voltaic.prefab.inventory.container.types.GenericContainerBlockEntity;

public class ContainerSearchRadar extends GenericContainerBlockEntity<TileSearchRadar> {

    public ContainerSearchRadar(int id, Inventory playerinv) {
        this(id, playerinv, new SimpleContainer(0), new SimpleContainerData(5));
    }

    public ContainerSearchRadar(int id, Inventory playerinv, Container inventory, ContainerData inventorydata) {
        super(BallistixMenuTypes.CONTAINER_SEARCHRADAR.get(), id, playerinv, inventory, inventorydata);
    }

    @Override
    public void addInventorySlots(Container container, Inventory inventory) {

    }

    @Override
    public void addPlayerInventory(Inventory playerinv) {

    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if(!getLevel().isClientSide() && getPlayer() != null && getSafeHost() != null) {
            PacketSetSearchRadarTrackedClient packet = new PacketSetSearchRadarTrackedClient(new HashSet<>(getSafeHost().detections), getSafeHost().getBlockPos());
            NetworkHandler.CHANNEL.sendTo(packet, ((ServerPlayer) getPlayer()).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        }
    }
}
