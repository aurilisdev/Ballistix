package ballistix.common.item;

import ballistix.References;
import ballistix.api.entity.IDefusable;
import electrodynamics.prefab.item.ElectricItemProperties;
import electrodynamics.prefab.item.ItemElectric;
import electrodynamics.prefab.utilities.object.TransferPack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteractSpecific;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = References.ID, bus = Bus.FORGE)
public class ItemDefuser extends ItemElectric {

	public ItemDefuser() {
		super((ElectricItemProperties) new ElectricItemProperties().capacity(1666666.66667).receive(TransferPack.joulesVoltage(1666666.66667 / (120.0 * 20.0), 120)).extract(TransferPack.joulesVoltage(1666666.66667 / (120.0 * 20.0), 120)).stacksTo(1).tab(References.BALLISTIXTAB));
	}

	@SubscribeEvent
	public static void onInteractWithEntity(EntityInteractSpecific event) {

		World world = event.getWorld();

		if (world.isClientSide) {
			return;
		}

		PlayerEntity playerIn = event.getPlayer();
		Entity entity = event.getTarget();

		ItemStack stack = playerIn.getItemInHand(event.getHand());

		boolean validItem = stack.getItem() instanceof ItemDefuser && ((ItemDefuser) stack.getItem()).getJoulesStored(stack) >= 150;

		if (!validItem) {
			return;
		}

		ItemDefuser defuser = (ItemDefuser) stack.getItem();

		if (entity instanceof IDefusable) {
			IDefusable defuse = (IDefusable) entity;
			defuser.extractPower(stack, 150, false);
			defuse.defuse();

		} else if (entity instanceof TNTEntity) {

			TNTEntity tnt = (TNTEntity) entity;
			entity.remove(false);

			ItemEntity item = new ItemEntity(world, tnt.blockPosition().getX() + 0.5, tnt.blockPosition().getY() + 0.5, tnt.blockPosition().getZ() + 0.5, new ItemStack(Items.TNT));
			defuser.extractPower(stack, 150, false);
			world.addFreshEntity(item);

		}
	}
}