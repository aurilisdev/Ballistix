package ballistix.common.inventory.container;

import java.util.HashSet;

import ballistix.common.packet.NetworkHandler;
import ballistix.common.packet.type.client.PacketSetSearchRadarTrackedClient;
import ballistix.common.tile.radar.TileSearchRadar;
import ballistix.registers.BallistixMenuTypes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraftforge.fml.network.NetworkDirection;
import voltaic.prefab.inventory.container.types.GenericContainerBlockEntity;

public class ContainerSearchRadar extends GenericContainerBlockEntity<TileSearchRadar> {

    public ContainerSearchRadar(int id, PlayerInventory playerinv) {
        this(id, playerinv, new Inventory(0), new IntArray(3));
    }

    public ContainerSearchRadar(int id, PlayerInventory playerinv, IInventory inventory, IIntArray inventorydata) {
        super(BallistixMenuTypes.CONTAINER_SEARCHRADAR.get(), id, playerinv, inventory, inventorydata);
    }

    @Override
    public void addInventorySlots(IInventory container, PlayerInventory inventory) {

    }

    @Override
    public void addPlayerInventory(PlayerInventory playerinv) {

    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if(!getLevel().isClientSide() && getPlayer() != null && getSafeHost() != null) {
            PacketSetSearchRadarTrackedClient packet = new PacketSetSearchRadarTrackedClient(new HashSet<>(getSafeHost().detections), getSafeHost().getBlockPos());
            NetworkHandler.CHANNEL.sendTo(packet, ((ServerPlayerEntity) getPlayer()).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        }
    }
}
