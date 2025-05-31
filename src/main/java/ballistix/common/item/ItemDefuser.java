package ballistix.common.item;

import ballistix.Ballistix;
import ballistix.api.entity.IDefusable;
import ballistix.registers.BallistixCreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import voltaic.prefab.item.ElectricItemProperties;
import voltaic.prefab.item.ItemElectric;
import voltaic.prefab.utilities.object.TransferPack;

@EventBusSubscriber(modid = Ballistix.ID, bus = EventBusSubscriber.Bus.FORGE)
public class ItemDefuser extends ItemElectric {

	public static final double USAGE = 150;

	public ItemDefuser() {
		super((ElectricItemProperties) new ElectricItemProperties().capacity(1666666.66667).receive(TransferPack.joulesVoltage(1666666.66667 / (120.0 * 20.0), 120)).extract(TransferPack.joulesVoltage(1666666.66667 / (120.0 * 20.0), 120)).stacksTo(1), () -> BallistixCreativeTabs.MAIN);
	}

	@SubscribeEvent
	public static void onInteractWithEntity(PlayerInteractEvent.EntityInteractSpecific event) {

		World world = event.getWorld();

		if (world.isClientSide) {
			return;
		}

		PlayerEntity playerIn = event.getPlayer();
		Entity entity = event.getTarget();

		ItemStack stack = playerIn.getItemInHand(event.getHand());

		boolean validItem = stack.getItem() instanceof ItemDefuser && ((ItemDefuser) stack.getItem()).getJoulesStored(stack) >= USAGE;

		if (!validItem) {
			return;
		}

		ItemDefuser defuser = (ItemDefuser) stack.getItem();

		if (entity instanceof IDefusable) {

			defuser.extractPower(stack, USAGE, false);
			((IDefusable) entity).defuse();

		} else if (entity instanceof TNTEntity) {
			TNTEntity tnt = (TNTEntity) entity;
			entity.remove(false);

			ItemEntity item = new ItemEntity(world, tnt.blockPosition().getX() + 0.5, tnt.blockPosition().getY() + 0.5, tnt.blockPosition().getZ() + 0.5, new ItemStack(Items.TNT));
			defuser.extractPower(stack, 150, false);
			world.addFreshEntity(item);

		}
	}
	
	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return oldStack.getItem() != newStack.getItem();
	}
	
}
