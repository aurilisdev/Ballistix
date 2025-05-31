package ballistix.common.item;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

import ballistix.prefab.utils.BallistixTextUtils;
import ballistix.registers.BallistixCreativeTabs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import voltaic.prefab.item.ElectricItemProperties;
import voltaic.prefab.item.ItemElectric;
import voltaic.prefab.utilities.object.TransferPack;

public class ItemScanner extends ItemElectric {

	public static final double USAGE = 150.0;

	public ItemScanner() {
		super((ElectricItemProperties) new ElectricItemProperties().capacity(1666666.66667).receive(TransferPack.joulesVoltage(1666666.66667 / (120.0 * 20.0), 120)).extract(TransferPack.joulesVoltage(1666666.66667 / (120.0 * 20.0), 120)).stacksTo(1), () -> BallistixCreativeTabs.MAIN);
	}

	@Override
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
		boolean action = false;
		ItemStack stack = playerIn.getItemInHand(handIn);
		for (Entry<ServerWorld, HashSet<Integer>> en : ItemTracker.validuuids.entrySet()) {
			Iterator<Integer> it = en.getValue().iterator();
			while (it.hasNext()) {
				int uuid = it.next();
				if (uuid == playerIn.getId() && getJoulesStored(stack) >= USAGE) {
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
	
	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return oldStack.getItem() != newStack.getItem();
	}
	
}
