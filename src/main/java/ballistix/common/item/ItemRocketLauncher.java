package ballistix.common.item;

import java.util.HashMap;

import ballistix.References;
import ballistix.api.missile.MissileManager;
import ballistix.api.missile.virtual.VirtualMissile;
import ballistix.common.block.BlockExplosive;
import ballistix.common.block.subtype.SubtypeMissile;
import ballistix.common.entity.EntityMissile;
import ballistix.registers.BallistixItems;
import ballistix.registers.BallistixSounds;
import electrodynamics.common.blockitem.BlockItemDescriptable;
import electrodynamics.prefab.utilities.NBTUtils;
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

public class ItemRocketLauncher extends Item {
	
	private static HashMap<PlayerEntity, Long> millisecondMap = new HashMap<>();

	public ItemRocketLauncher() {
		super(new Item.Properties().tab(References.BALLISTIXTAB).stacksTo(1));
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

		PlayerEntity player = (PlayerEntity) entityLiving;

		long millisecond = System.currentTimeMillis();

		if (millisecond - millisecondMap.getOrDefault(player, 0L) <= 3000) {
			return;
		}

		millisecondMap.put(player, millisecond);

		int blastOrdinal = 0;

		boolean hasExplosive = false;

		boolean hasRange = false;

		ItemStack ex = ItemStack.EMPTY;

		ItemStack missile = ex;

		for (ItemStack st : player.inventory.items) {
			Item it = st.getItem();
			if (!hasExplosive && it instanceof BlockItemDescriptable) {
				BlockItemDescriptable bl = (BlockItemDescriptable) it;
				if (bl.getBlock() instanceof BlockExplosive) {
					blastOrdinal = ((BlockExplosive) bl.getBlock()).explosive.ordinal();
					hasExplosive = true;
					ex = st;
				}
			}
			if (!hasRange && it == BallistixItems.getItem(SubtypeMissile.closerange)) {
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
					2.0F,
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

			world.playSound(null, player.blockPosition().above(), BallistixSounds.SOUND_MISSILE_ROCKETLAUNCHER.get(), SoundCategory.BLOCKS, 1.0F, 1.0F);
		}

	}
}