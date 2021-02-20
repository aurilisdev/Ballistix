package ballistix.common.item;

import ballistix.References;
import ballistix.common.entity.EntityMissile;
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
		if (!world.isRemote) {
			EntityMissile miss = new EntityMissile(world);
			miss.setLocationAndAngles(entityLiving.getPosX(), entityLiving.getPosY() + entityLiving.getEyeHeight() * 0.8, entityLiving.getPosZ(), entityLiving.rotationYaw, entityLiving.rotationPitch);
			miss.setVelocity(entityLiving.getLookVec().x, entityLiving.getLookVec().y, entityLiving.getLookVec().z);
			world.addEntity(miss);
			miss.range = 1;
			miss.blastOrdinal = 1;
			if (!(entityLiving instanceof PlayerEntity) || !((PlayerEntity) entityLiving).isCreative()) {
			}
		}
	}
}