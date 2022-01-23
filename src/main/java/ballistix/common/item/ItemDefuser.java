package ballistix.common.item;

import ballistix.References;
import ballistix.api.entity.IDefusable;
import electrodynamics.prefab.item.ElectricItemProperties;
import electrodynamics.prefab.item.ItemElectric;
import electrodynamics.prefab.utilities.object.TransferPack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteractSpecific;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = References.ID, bus = Bus.FORGE)
public class ItemDefuser extends ItemElectric {

	public ItemDefuser() {
		super((ElectricItemProperties) new ElectricItemProperties().capacity(10000).receive(TransferPack.joulesVoltage(500, 120))
				.extract(TransferPack.joulesVoltage(500, 120)).stacksTo(1).tab(References.BALLISTIXTAB));
	}

	@SubscribeEvent
	public static void onInteractWithEntity(EntityInteractSpecific event) {
		if (!event.getWorld().isClientSide) {
			Entity entity = event.getTarget();
			if (entity instanceof IDefusable defuse) {
				Player playerIn = event.getPlayer();
				InteractionHand handIn = event.getHand();
				ItemStack stack = playerIn.getItemBySlot(handIn == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
				if (stack.getItem() instanceof ItemDefuser defuser && defuser.getJoulesStored(stack) >= 150) {
					defuser.extractPower(stack, 150, false);
					defuse.defuse();
				}

			}
		}
	}
}
