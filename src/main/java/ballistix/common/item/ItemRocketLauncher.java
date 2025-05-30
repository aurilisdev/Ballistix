package ballistix.common.item;

import ballistix.api.blast.IBlast;
import ballistix.api.missile.MissileManager;
import ballistix.api.missile.virtual.VirtualMissile;
import ballistix.common.blast.Blast;
import ballistix.common.block.subtype.SubtypeMissile;
import ballistix.common.settings.BallistixConstants;
import ballistix.registers.BallistixCreativeTabs;
import ballistix.registers.BallistixItems;
import ballistix.registers.BallistixSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import voltaic.common.item.ItemVoltaic;
import voltaic.prefab.utilities.NBTUtils;

public class ItemRocketLauncher extends ItemVoltaic {

    public ItemRocketLauncher() {
        super(new Item.Properties().stacksTo(1), () -> BallistixCreativeTabs.MAIN);
    }

    @Override
    public UseAction getUseAnimation(ItemStack stack) {
        return UseAction.NONE;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return Integer.MAX_VALUE;
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        playerIn.startUsingItem(handIn);
        return new ActionResult<>(ActionResultType.PASS, itemstack);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }

    @Override
    public void inventoryTick(ItemStack stack, World level, Entity entity, int slotId, boolean isSelected) {
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
    public void releaseUsing(ItemStack stack, World world, LivingEntity entityLiving, int timeLeft) {

        if (world.isClientSide || !(entityLiving instanceof PlayerEntity)) {
            return;
        }

        if (stack.getOrCreateTag().getInt(NBTUtils.TIMER) > 0) {
            return;
        }

        PlayerEntity player = (PlayerEntity) entityLiving;

        if (!player.isCreative()) stack.getOrCreateTag().putInt(NBTUtils.TIMER, BallistixConstants.ROCKET_LAUNCHER_COOLDOWN_TICKS);

        IBlast blast = null;

        boolean hasExplosive = false;

        boolean hasRange = false;

        ItemStack ex = ItemStack.EMPTY;

        ItemStack missile = ex;

        for (ItemStack st : player.inventory.items) {
            Item it = st.getItem();
            IBlast bl = Blast.ITEM_TO_BLAST_MAP.get(it);
            if (!hasExplosive && bl != null && (player.isCreative() || bl.tier() <= 1)) {
                blast = bl;
                hasExplosive = true;
                ex = st;
            }
            if (!hasRange && (it == BallistixItems.ITEMS_MISSILE.getValue(SubtypeMissile.tier1) || (player.isCreative() && (it == BallistixItems.ITEMS_MISSILE.getValue(SubtypeMissile.tier1) || it == BallistixItems.ITEMS_MISSILE.getValue(SubtypeMissile.tier2) || it == BallistixItems.ITEMS_MISSILE.getValue(SubtypeMissile.tier3))))) {
                hasRange = true;
                missile = st;
            }
            if (hasRange && hasExplosive) {
                break;
            }
        }
        if (hasExplosive && hasRange) {
            ex.shrink(1);
            missile.shrink(1);
            VirtualMissile virtualMissile = new VirtualMissile(
                    //
                    new Vector3d(entityLiving.getX(), entityLiving.getY() + entityLiving.getEyeHeight() * 0.8, entityLiving.getZ()),
                    //
                    new Vector3d(entityLiving.getLookAngle().x, entityLiving.getLookAngle().y, entityLiving.getLookAngle().z),
                    //
                    1.333F,
                    //
                    true,
                    //
                    0,
                    //
                    0,
                    //
                    BlockPos.ZERO,
                    //
                    missile.getItem() == BallistixItems.ITEMS_MISSILE.getValue(SubtypeMissile.tier2) ? 1 : missile.getItem() == BallistixItems.ITEMS_MISSILE.getValue(SubtypeMissile.tier3) ? 2 : 0,
                    //
                    blast,
                    //
                    0,
                    //
                    false
                    //
            );

            MissileManager.addMissile(world.dimension(), virtualMissile);

            world.playSound(null, player.blockPosition().above(), BallistixSounds.SOUND_MISSILE_ROCKETLAUNCHER.get(), SoundCategory.BLOCKS, 1.0F, 1.0F);
        }

    }
}