package ballistix.common.item;

import ballistix.References;
import ballistix.common.block.SubtypeBlast;
import ballistix.common.entity.EntityGrenade;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class ItemGrenade extends Item {
	private SubtypeBlast explosive;

	public ItemGrenade(SubtypeBlast explosive) {
		super(new Item.Properties().group(References.BALLISTIXTAB).maxStackSize(16));
		this.explosive = explosive;
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.BOW;
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 60;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		ItemStack itemstack = playerIn.getHeldItem(handIn);
		playerIn.setActiveHand(handIn);
		return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack itemStack, World world, LivingEntity entityLiving, int timeLeft) {
		if (!world.isRemote) {
			world.playSound(null, entityLiving.getPosX(), entityLiving.getPosY(), entityLiving.getPosZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));

			float throwEnergy = (float) (getUseDuration(itemStack) - timeLeft) / (float) getUseDuration(itemStack) + 0.7f;

			EntityGrenade grenade = new EntityGrenade(world, 0, 0, 0);
			grenade.setLocationAndAngles(entityLiving.getPosX(), entityLiving.getPosY() + entityLiving.getEyeHeight() * 0.8, entityLiving.getPosZ(), entityLiving.rotationYaw, entityLiving.rotationPitch);
			grenade.setExplosiveType(explosive);
			grenade.func_234612_a_(entityLiving, entityLiving.rotationPitch - 20, entityLiving.rotationYaw, 0.0F, throwEnergy, 1.0F);
			world.addEntity(grenade);
			if (!(entityLiving instanceof PlayerEntity) || !((PlayerEntity) entityLiving).isCreative()) {
				itemStack.shrink(1);
			}
		}
	}
}
