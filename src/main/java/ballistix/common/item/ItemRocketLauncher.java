package ballistix.common.item;

import ballistix.api.missile.MissileManager;
import ballistix.api.missile.virtual.VirtualMissile;
import ballistix.common.block.BlockExplosive;
import ballistix.common.block.subtype.SubtypeMissile;
import ballistix.common.settings.Constants;
import ballistix.registers.BallistixCreativeTabs;
import ballistix.registers.BallistixItems;
import ballistix.registers.BallistixSounds;
import electrodynamics.common.blockitem.types.BlockItemDescriptable;
import electrodynamics.common.item.ItemElectrodynamics;
import electrodynamics.registers.ElectrodynamicsDataComponentTypes;
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

public class ItemRocketLauncher extends ItemElectrodynamics {

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
        return slotChanged;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if (level.isClientSide) {
            return;
        }
        int timeRemaining = stack.getOrDefault(ElectrodynamicsDataComponentTypes.TIMER, 0);
        if (timeRemaining > 0) {
            timeRemaining--;
            stack.set(ElectrodynamicsDataComponentTypes.TIMER, timeRemaining);
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level world, LivingEntity entityLiving, int timeLeft) {

        if (world.isClientSide || !(entityLiving instanceof Player)) {
            return;
        }

        if (stack.getOrDefault(ElectrodynamicsDataComponentTypes.TIMER, 0) > 0) {
            return;
        }

        Player player = (Player) entityLiving;

        if(!player.isCreative())
        stack.set(ElectrodynamicsDataComponentTypes.TIMER, Constants.ROCKET_LAUNCHER_COOLDOWN_TICKS);

        int blastOrdinal = 0;

        boolean hasExplosive = false;

        boolean hasRange = false;

        ItemStack ex = ItemStack.EMPTY;

        ItemStack missile = ex;

        for (ItemStack st : player.getInventory().items) {
            Item it = st.getItem();
            if (!hasExplosive && it instanceof BlockItemDescriptable bl) {
                if (bl.getBlock() instanceof BlockExplosive exs) {
                    blastOrdinal = exs.explosive.ordinal();
                    hasExplosive = true;
                    ex = st;
                }
            }
            if (!hasRange && it == BallistixItems.ITEMS_MISSILE.getValue(SubtypeMissile.closerange)) {
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
                    new Vec3(entityLiving.getX(), entityLiving.getY() + entityLiving.getEyeHeight() * 0.8, entityLiving.getZ()),
                    //
                    new Vec3(entityLiving.getLookAngle().x, entityLiving.getLookAngle().y, entityLiving.getLookAngle().z),
                    //
                    1.0F,
                    //
                    true,
                    //
                    0,
                    //
                    0,
                    //
                    BlockPos.ZERO,
                    //
                    0,
                    //
                    blastOrdinal,
                    //
                    false,
                    //
                    0
                    //
            );

            MissileManager.addMissile(world.dimension(), virtualMissile);

            world.playSound(null, player.blockPosition().above(), BallistixSounds.SOUND_MISSILE_ROCKETLAUNCHER.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        }

    }
}