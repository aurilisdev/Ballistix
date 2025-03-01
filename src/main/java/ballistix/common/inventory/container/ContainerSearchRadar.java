package ballistix.common.inventory.container;

import ballistix.common.packet.NetworkHandler;
import ballistix.common.packet.types.client.PacketSetSearchRadarTrackedClient;
import ballistix.common.tile.radar.TileSearchRadar;
import ballistix.registers.BallistixMenuTypes;
import electrodynamics.prefab.inventory.container.GenericContainerBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraftforge.fml.network.NetworkDirection;

import java.util.HashSet;

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
		if (!world.isClientSide() && getHostFromIntArray() != null) {
			PacketSetSearchRadarTrackedClient packet = new PacketSetSearchRadarTrackedClient(new HashSet<>(getHostFromIntArray().detections), getHostFromIntArray().getBlockPos());
			NetworkHandler.CHANNEL.sendTo(packet, ((ServerPlayerEntity) player).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
		}
	}
}
