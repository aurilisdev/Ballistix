package ballistix.common.inventory.container;

import ballistix.common.packet.NetworkHandler;
import ballistix.common.packet.types.client.PacketSetSearchRadarTrackedClient;
import ballistix.common.tile.radar.TileSearchRadar;
import ballistix.registers.BallistixMenuTypes;
import electrodynamics.prefab.inventory.container.GenericContainerBlockEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraftforge.network.NetworkDirection;

import java.util.HashSet;

public class ContainerSearchRadar extends GenericContainerBlockEntity<TileSearchRadar> {

	public ContainerSearchRadar(int id, Inventory playerinv) {
		this(id, playerinv, new SimpleContainer(0), new SimpleContainerData(3));
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
		if (!world.isClientSide() && getHostFromIntArray() != null) {
			PacketSetSearchRadarTrackedClient packet = new PacketSetSearchRadarTrackedClient(new HashSet<>(getHostFromIntArray().detections), getHostFromIntArray().getBlockPos());
			NetworkHandler.CHANNEL.sendTo(packet, ((ServerPlayer) player).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
		}
	}
}
