package ballistix.common.item;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.UUID;

import ballistix.prefab.utils.BallistixTextUtils;
import ballistix.registers.BallistixCreativeTabs;
import electrodynamics.prefab.item.ElectricItemProperties;
import electrodynamics.prefab.item.ItemElectric;
import electrodynamics.prefab.utilities.object.TransferPack;
import electrodynamics.registers.ElectrodynamicsItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemScanner extends ItemElectric {

	public static final double USAGE = 150.0;

	public ItemScanner() {
		super((ElectricItemProperties) new ElectricItemProperties().capacity(1666666.66667).receive(TransferPack.joulesVoltage(1666666.66667 / (120.0 * 20.0), 120)).extract(TransferPack.joulesVoltage(1666666.66667 / (120.0 * 20.0), 120)).stacksTo(1), () -> BallistixCreativeTabs.MAIN.get(), item -> ElectrodynamicsItems.ITEM_BATTERY.get());
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
		boolean action = false;
		ItemStack stack = playerIn.getItemInHand(handIn);
		for (Entry<ServerLevel, HashSet<UUID>> en : ItemTracker.validuuids.entrySet()) {
			Iterator<UUID> it = en.getValue().iterator();
			while (it.hasNext()) {
				UUID uuid = it.next();
				if (uuid == playerIn.getUUID() && getJoulesStored(stack) >= USAGE) {
					it.remove();
					action = true;
					extractPower(stack, USAGE, false);
				}
			}
		}

		if (worldIn.isClientSide) {
			if (action) {
				playerIn.displayClientMessage(BallistixTextUtils.chatMessage("scanner.cleared"), true);
			} else {
				playerIn.displayClientMessage(BallistixTextUtils.chatMessage("scanner.none"), true);
			}
		}

		return super.use(worldIn, playerIn, handIn);
	}
}
