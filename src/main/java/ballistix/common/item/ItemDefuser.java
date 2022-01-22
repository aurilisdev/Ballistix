package ballistix.common.item;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.UUID;

import ballistix.References;
import ballistix.api.entity.IDefusable;
import electrodynamics.prefab.item.ElectricItemProperties;
import electrodynamics.prefab.item.ItemElectric;
import electrodynamics.prefab.utilities.object.TransferPack;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class ItemDefuser extends ItemElectric {

	public ItemDefuser() {
		super((ElectricItemProperties) new ElectricItemProperties().capacity(10000).receive(TransferPack.joulesVoltage(500, 120))
				.extract(TransferPack.joulesVoltage(500, 120)).stacksTo(1).tab(References.BALLISTIXTAB));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
		boolean action = false;
		for (Entry<ServerLevel, HashSet<UUID>> en : ItemTracker.validuuids.entrySet()) {
			Iterator<UUID> it = en.getValue().iterator();
			while (it.hasNext()) {
				UUID uuid = it.next();
				if (uuid == playerIn.getUUID()) {
					it.remove();
					action = true;
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

	@SubscribeEvent
	public static void onInteractWithEntity(EntityInteract event) {
		if (!event.getWorld().isClientSide) {
			Entity entity = event.getEntity();
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
