package ballistix.common.item;

import ballistix.References;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.entity.EntityGrenade;
import electrodynamics.api.ISubtype;
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

	private SubtypeGrenade grenade;

	public ItemGrenade(SubtypeGrenade grenade) {
		super(new Item.Properties().tab(References.BALLISTIXTAB).stacksTo(16));
		this.grenade = grenade;
	}

	@Override
	public UseAction getUseAnimation(ItemStack stack) {
		return UseAction.BOW;
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 60;
	}

	@Override
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
		ItemStack itemstack = playerIn.getItemInHand(handIn);
		playerIn.startUsingItem(handIn);
		return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
	}

	@Override
	public void releaseUsing(ItemStack itemStack, World world, LivingEntity entityLiving, int timeLeft) {
		if (world.isClientSide) {
			return;
		}
		world.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), SoundEvents.TNT_PRIMED, SoundCategory.BLOCKS, 0.5F, 0.4F / (world.random.nextFloat() * 0.4F + 0.8F));

		float throwEnergy = (float) (getUseDuration(itemStack) - timeLeft) / (float) getUseDuration(itemStack) + 0.7f;

		EntityGrenade grenade = new EntityGrenade(world);
		grenade.moveTo(entityLiving.getX(), entityLiving.getY() + entityLiving.getEyeHeight() * 0.8, entityLiving.getZ(), entityLiving.yRot, entityLiving.xRot);
		grenade.setExplosiveType(this.grenade);
		grenade.shootFromRotation(entityLiving, entityLiving.xRot - 20, entityLiving.yRot, 0.0F, throwEnergy, 1.0F);
		world.addFreshEntity(grenade);
		if (entityLiving instanceof PlayerEntity && !((PlayerEntity) entityLiving).isCreative() || !(entityLiving instanceof PlayerEntity)) {
			itemStack.shrink(1);
		}
	}

	public enum SubtypeGrenade implements ISubtype {
		attractive(SubtypeBlast.attractive), chemical(SubtypeBlast.chemical), condensive(SubtypeBlast.condensive), debilitation(SubtypeBlast.debilitation), incendiary(SubtypeBlast.incendiary), repulsive(SubtypeBlast.repulsive), shrapnel(SubtypeBlast.shrapnel);

		public final SubtypeBlast explosiveType;

		SubtypeGrenade(SubtypeBlast explosive) {
			explosiveType = explosive;
		}

		@Override
		public String forgeTag() {
			return "grenades/" + name();
		}

		@Override
		public boolean isItem() {
			return true;
		}

		@Override
		public String tag() {
			return "grenade" + name();
		}

	}

}