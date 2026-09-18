package ballistix.common.item;

import ballistix.api.blast.IBlast;
import ballistix.api.missile.MissileManager;
import ballistix.api.missile.virtual.FlightPath;
import ballistix.api.missile.virtual.VirtualMissile;
import ballistix.common.blast.util.Blast;
import ballistix.common.block.subtype.SubtypeMissile;
import ballistix.common.settings.BallistixConfig;
import ballistix.registers.BallistixCreativeTabs;
import ballistix.registers.BallistixItems;
import ballistix.registers.BallistixSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import voltaic.common.item.ItemVoltaic;
import voltaic.registers.VoltaicDataComponentTypes;

public class ItemRocketLauncher extends ItemVoltaic {
    public static final float ROCKET_LAUNCHER_SPEED = 2.5F;

    public ItemRocketLauncher() {
	super(new Item.Properties().stacksTo(1), BallistixCreativeTabs.MAIN);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
	return UseAnim.NONE;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
	return Integer.MAX_VALUE;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
	ItemStack itemstack = playerIn.getItemInHand(handIn);
	playerIn.startUsingItem(handIn);
	return new InteractionResultHolder<>(InteractionResult.PASS, itemstack);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
	return !oldStack.is(newStack.getItem());
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
	super.inventoryTick(stack, level, entity, slotId, isSelected);
	if (level.isClientSide) {
	    return;
	}
	int timeRemaining = stack.getOrDefault(VoltaicDataComponentTypes.TIMER, 0);
	if (timeRemaining > 0) {
	    timeRemaining--;
	    stack.set(VoltaicDataComponentTypes.TIMER, timeRemaining);
	}
    }

    @Override
    public void releaseUsing(ItemStack usingStack, Level world, LivingEntity entityLiving, int timeLeft) {
	if (world.isClientSide || !(entityLiving instanceof Player player)
		|| usingStack.getOrDefault(VoltaicDataComponentTypes.TIMER, 0) > 0) {
	    return;
	}

	if (!player.isCreative())
	    usingStack.set(VoltaicDataComponentTypes.TIMER,
		    BallistixConfig.INSTANCE.ROCKET_LAUNCHER_COOLDOWN_TICKS.get());
	IBlast blast = null;
	ItemStack ex = ItemStack.EMPTY;
	ItemStack missile = ItemStack.EMPTY;

	boolean creative = player.isCreative();

	for (ItemStack stack : player.getInventory().items) {

	    Item item = stack.getItem();
	    IBlast candidate = Blast.ITEM_TO_BLAST_MAP.get(item);

	    if (blast == null && candidate != null && (creative || candidate.tier() <= 1)) {
		blast = candidate;
		ex = stack;
	    }

	    if (missile.isEmpty() && (item == BallistixItems.ITEMS_MISSILE.getValue(SubtypeMissile.tier1)
		    || creative && (item == BallistixItems.ITEMS_MISSILE.getValue(SubtypeMissile.tier2)
			    || item == BallistixItems.ITEMS_MISSILE.getValue(SubtypeMissile.tier3)))) {
		missile = stack;
	    }

	    if (blast != null && !missile.isEmpty())
		break;
	}

	if (blast != null && missile.getItem() instanceof ItemMissile missileItem) {

	    VirtualMissile virtualMissile = new VirtualMissile(
		    new Vec3(entityLiving.getX(), entityLiving.getY() + entityLiving.getEyeHeight() * 0.8,
			    entityLiving.getZ()),
		    entityLiving.getLookAngle(), ROCKET_LAUNCHER_SPEED, FlightPath.ROCKET_LAUNCHER, 0, 0, BlockPos.ZERO,
		    missileItem.missile.ordinal() + 1, blast, 0, false);

	    ex.shrink(1);
	    missile.shrink(1);

	    MissileManager.addMissile(world.dimension(), virtualMissile);

	    world.playSound(null, player.blockPosition().above(), BallistixSounds.SOUND_MISSILE_ROCKETLAUNCHER.get(),
		    SoundSource.BLOCKS, 1.0F, 1.0F);
	}

    }
}