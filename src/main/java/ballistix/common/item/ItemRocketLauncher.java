package ballistix.common.item;

import ballistix.DeferredRegisters;
import ballistix.References;
import ballistix.common.block.BlockExplosive;
import ballistix.common.entity.EntityMissile;
import electrodynamics.common.blockitem.BlockItemDescriptable;
import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class ItemRocketLauncher extends Item {

    public ItemRocketLauncher() {
	super(new Item.Properties().group(References.BALLISTIXTAB).maxStackSize(1));
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
	return UseAction.NONE;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
	return Integer.MAX_VALUE;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
	ItemStack itemstack = playerIn.getHeldItem(handIn);
	playerIn.setActiveHand(handIn);
	return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, LivingEntity entityLiving, int timeLeft) {
	if (!world.isRemote && entityLiving instanceof PlayerEntity) {
	    PlayerEntity pl = (PlayerEntity) entityLiving;
	    int blastOrdinal = 0;
	    boolean hasExplosive = false;
	    boolean hasRange = false;
	    ItemStack ex = ItemStack.EMPTY;
	    ItemStack missile = ex;
	    for (ItemStack st : pl.inventory.mainInventory) {
		Item it = st.getItem();
		if (!hasExplosive && it instanceof BlockItemDescriptable) {
		    Block bl = ((BlockItemDescriptable) it).getBlock();
		    if (bl instanceof BlockExplosive) {
			blastOrdinal = ((BlockExplosive) bl).explosive.ordinal();
			hasExplosive = true;
			ex = st;
		    }
		}
		if (!hasRange && it == DeferredRegisters.ITEM_MISSILECLOSERANGE.get()) {
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
		EntityMissile miss = new EntityMissile(world);
		miss.setLocationAndAngles(entityLiving.getPosX(), entityLiving.getPosY() + entityLiving.getEyeHeight() * 0.8, entityLiving.getPosZ(),
			entityLiving.rotationYaw, entityLiving.rotationPitch);
		miss.setMotion(entityLiving.getLookVec().x * 2, entityLiving.getLookVec().y * 2, entityLiving.getLookVec().z * 2);
		miss.blastOrdinal = blastOrdinal;
		miss.range = 0;
		miss.isItem = true;
		world.addEntity(miss);
	    }
	}
    }
}