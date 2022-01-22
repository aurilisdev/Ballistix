package ballistix.common.item;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.UUID;

import ballistix.References;
import electrodynamics.prefab.item.ElectricItemProperties;
import electrodynamics.prefab.item.ItemElectric;
import electrodynamics.prefab.utilities.object.TransferPack;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemScanner extends ItemElectric {

	public ItemScanner() {
		super((ElectricItemProperties) new ElectricItemProperties().capacity(10000).receive(TransferPack.joulesVoltage(500, 120))
				.extract(TransferPack.joulesVoltage(500, 120)).stacksTo(1).tab(References.BALLISTIXTAB));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
		boolean action = false;
		ItemStack stack = playerIn.getItemBySlot(handIn == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
		for (Entry<ServerLevel, HashSet<UUID>> en : ItemTracker.validuuids.entrySet()) {
			Iterator<UUID> it = en.getValue().iterator();
			while (it.hasNext()) {
				UUID uuid = it.next();
				if (uuid == playerIn.getUUID() && getJoulesStored(stack) >= 150) {
					it.remove();
					action = true;
					extractPower(stack, 150, false);
				}
			}
		}
		if (action) {
			playerIn.displayClientMessage(new TranslatableComponent("message.scanner.cleared"), true);
		} else {
			playerIn.displayClientMessage(new TranslatableComponent("message.scanner.none"), true);
		}
		return super.use(worldIn, playerIn, handIn);
	}
}
