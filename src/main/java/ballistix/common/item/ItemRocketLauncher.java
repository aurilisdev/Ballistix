package ballistix.common.item;

import ballistix.api.blast.IBlast;
import ballistix.api.missile.MissileManager;
import ballistix.api.missile.virtual.VirtualMissile;
import ballistix.common.blast.util.Blast;
import ballistix.common.block.subtype.SubtypeMissile;
import ballistix.common.settings.BallistixConstants;
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
import voltaic.prefab.utilities.NBTUtils;

public class ItemRocketLauncher extends ItemVoltaic {
    public static final float ROCKET_LAUNCHER_SPEED = 2.5F;

    public ItemRocketLauncher() {
	super(new Item.Properties().stacksTo(1), () -> BallistixCreativeTabs.MAIN);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
	return UseAnim.NONE;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
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
	int timeRemaining = stack.getOrCreateTag().getInt(NBTUtils.TIMER);
	if (timeRemaining > 0) {
	    timeRemaining--;
	    stack.getOrCreateTag().putInt(NBTUtils.TIMER, timeRemaining);
	}
    }

    @Override
    public void releaseUsing(ItemStack stack, Level world, LivingEntity entityLiving, int timeLeft) {

	if (world.isClientSide || !(entityLiving instanceof Player)
		|| (stack.getOrCreateTag().getInt(NBTUtils.TIMER) > 0)) {
	    return;
	}

	Player player = (Player) entityLiving;

	if (!player.isCreative())
	    stack.getOrCreateTag().putInt(NBTUtils.TIMER, BallistixConstants.ROCKET_LAUNCHER_COOLDOWN_TICKS);

	IBlast blast = null;

	boolean hasExplosive = false;

	boolean hasRange = false;

	ItemStack ex = ItemStack.EMPTY;

	ItemStack missile = ex;

	for (ItemStack st : player.getInventory().items) {
	    Item it = st.getItem();
	    IBlast bl = Blast.ITEM_TO_BLAST_MAP.get(it);
	    if (!hasExplosive && bl != null && (player.isCreative() || bl.tier() <= 1)) {
		blast = bl;
		hasExplosive = true;
		ex = st;
	    }
	    if (!hasRange && (it == BallistixItems.ITEMS_MISSILE.getValue(SubtypeMissile.tier1)
		    || player.isCreative() && (it == BallistixItems.ITEMS_MISSILE.getValue(SubtypeMissile.tier1)
			    || it == BallistixItems.ITEMS_MISSILE.getValue(SubtypeMissile.tier2)
			    || it == BallistixItems.ITEMS_MISSILE.getValue(SubtypeMissile.tier3)))) {
		hasRange = true;
		missile = st;
	    }
	    if (hasRange && hasExplosive) {
		break;
	    }
	}
	if (hasExplosive && hasRange) {

	    VirtualMissile virtualMissile = new VirtualMissile(
		    //
		    new Vec3(entityLiving.getX(), entityLiving.getY() + entityLiving.getEyeHeight() * 0.8,
			    entityLiving.getZ()),
		    //
		    new Vec3(entityLiving.getLookAngle().x, entityLiving.getLookAngle().y,
			    entityLiving.getLookAngle().z),
		    //
		    ROCKET_LAUNCHER_SPEED,
		    //
		    VirtualMissile.FlightPath.ROCKET_LAUNCHER,
		    //
		    0,
		    //
		    0,
		    //
		    BlockPos.ZERO,
		    //
		    ((ItemMissile) missile.getItem()).missile.ordinal() + 1,
		    //
		    blast,
		    //
		    0,
		    //
		    false
	    //
	    );

	    ex.shrink(1);
	    missile.shrink(1);

	    MissileManager.addMissile(world.dimension(), virtualMissile);

	    world.playSound(null, player.blockPosition().above(), BallistixSounds.SOUND_MISSILE_ROCKETLAUNCHER.get(),
		    SoundSource.BLOCKS, 1.0F, 1.0F);
	}

    }
}