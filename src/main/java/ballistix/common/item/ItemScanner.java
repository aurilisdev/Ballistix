package ballistix.common.item;

import ballistix.Ballistix;
import ballistix.common.world.TrackerSecurityData;
import ballistix.prefab.utils.BallistixTextUtils;
import ballistix.registers.BallistixCreativeTabs;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import voltaic.prefab.item.ElectricItemProperties;
import voltaic.prefab.item.ItemElectric;
import voltaic.prefab.utilities.object.TransferPack;

@EventBusSubscriber(modid = Ballistix.ID, bus = EventBusSubscriber.Bus.FORGE)
public class ItemScanner extends ItemElectric {

    public static final double USAGE = 150.0;

    public ItemScanner() {
	super((ElectricItemProperties) new ElectricItemProperties().capacity(1666666.66667)
		.receive(TransferPack.joulesVoltage(1666666.66667 / (120.0 * 20.0), 120))
		.extract(TransferPack.joulesVoltage(1666666.66667 / (120.0 * 20.0), 120)).stacksTo(1),
		() -> BallistixCreativeTabs.MAIN, item -> Items.AIR);
    }

    /*
     * Right-clicking the air scans the player holding the scanner.
     */
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {

	ItemStack stack = player.getItemInHand(hand);

	InteractionResult result = scanEntity(stack, player, player);

	return new InteractionResultHolder<>(result, stack);
    }

    public InteractionResult scanEntity(ItemStack stack, Player player, LivingEntity target) {

	if (getJoulesStored(stack) < USAGE) {
	    return InteractionResult.PASS;
	}

	if (player.level instanceof ServerLevel serverLevel) {

	    TrackerSecurityData.get(serverLevel.getServer()).incrementRevision(target.getUUID());

	    extractPower(stack, USAGE, false);

	    player.displayClientMessage(BallistixTextUtils
		    .chatMessage("scanner.cleared", target.getName().copy().withStyle(ChatFormatting.YELLOW))
		    .withStyle(ChatFormatting.WHITE), true);
	}

	return InteractionResult.sidedSuccess(player.level.isClientSide);
    }

    /*
     * Right-clicking another living entity scans that entity.
     */
    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {

	Player player = event.getEntity();
	ItemStack stack = player.getItemInHand(event.getHand());

	if (!(stack.getItem() instanceof ItemScanner scanner)) {
	    return;
	}

	if (!(event.getTarget() instanceof LivingEntity target)) {
	    return;
	}

	InteractionResult result = scanner.scanEntity(stack, player, target);

	if (result.consumesAction()) {
	    event.setCancellationResult(result);
	    event.setCanceled(true);
	}
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {

	return !oldStack.is(newStack.getItem());
    }

}
