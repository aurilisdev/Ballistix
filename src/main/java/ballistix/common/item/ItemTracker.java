package ballistix.common.item;

import java.util.List;
import javax.annotation.Nullable;

import ballistix.References;
import ballistix.common.event.ServerEventHandler;
import ballistix.prefab.utils.BallistixTextUtils;
import electrodynamics.prefab.item.ElectricItemProperties;
import electrodynamics.prefab.item.ItemElectric;
import electrodynamics.prefab.utilities.object.TransferPack;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ItemTracker extends ItemElectric {

	public static final String X = "target_x";
	public static final String Z = "target_z";

	public static final String UUID = "uuid";

	public ItemTracker() {
		super((ElectricItemProperties) new ElectricItemProperties().capacity(1666666.66667).receive(TransferPack.joulesVoltage(1666666.66667 / (120.0 * 20.0), 120)).extract(TransferPack.joulesVoltage(1666666.66667 / (120.0 * 20.0), 120)).stacksTo(1).tab(References.BALLISTIXTAB));
	}

	@Override
	public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		ITextComponent name = BallistixTextUtils.tooltip("tracker.none");
		if (hasTarget(stack)) {
			Entity entity = worldIn.getEntity(getUUID(stack));
			if (entity != null) {
				name = entity.getName();
			}
		}
		tooltip.add(BallistixTextUtils.tooltip("tracker.tracking", name).withStyle(TextFormatting.DARK_GRAY));
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
	}

	@Override
	public void inventoryTick(ItemStack stack, World level, Entity entity, int slot, boolean selected) {
		super.inventoryTick(stack, level, entity, slot, selected);
		if (level instanceof ServerWorld) {
			ServerWorld slevel = (ServerWorld) level;
			if ((selected || entity instanceof PlayerEntity && ((PlayerEntity) entity).getOffhandItem() == stack) && hasTarget(stack)) {
				int uuid = getUUID(stack);
				if (ServerEventHandler.isTracked(slevel, uuid)) {
					Entity ent = slevel.getEntity(uuid);
					if (ent != null) {
						setX(stack, ent.position().x);
						setZ(stack, ent.position().z);
					}
				} else {
					wipeData(stack);
				}
			}
		}
	}

	@Override
	public ActionResultType interactLivingEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
		if (player != null && player.level instanceof ServerWorld && getJoulesStored(stack) >= 150) {
			ServerWorld server = (ServerWorld) player.level;
			PlayerInventory inv = player.inventory;
			inv.removeItem(stack);
			setUUID(stack, entity.getId());
			ServerEventHandler.addTracked(server, entity.getId());
			if (hand == Hand.MAIN_HAND) {
				inv.setItem(inv.selected, stack);
			} else {
				inv.offhand.set(0, stack);
			}
			extractPower(stack, 150, false);
		}
		return ActionResultType.PASS;
	}

	public static double getX(ItemStack stack) {
		return stack.getOrCreateTag().getDouble(X);
	}

	public static double getZ(ItemStack stack) {
		return stack.getOrCreateTag().getDouble(Z);
	}

	public static int getUUID(ItemStack stack) {
		return stack.getOrCreateTag().getInt(UUID);
	}

	public static void setX(ItemStack stack, double x) {
		stack.getOrCreateTag().putDouble(X, x);
	}

	public static void setZ(ItemStack stack, double z) {
		stack.getOrCreateTag().putDouble(Z, z);
	}

	public static void setUUID(ItemStack stack, int uuid) {
		stack.getOrCreateTag().putInt(UUID, uuid);
	}

	public static void wipeData(ItemStack stack) {
		CompoundNBT tag = stack.getOrCreateTag();
		tag.remove(X);
		tag.remove(Z);
		tag.remove(UUID);
	}

	public static boolean hasTargetCoords(ItemStack stack) {
		CompoundNBT tag = stack.getOrCreateTag();
		return tag.contains(X) && tag.contains(Z);
	}

	public static boolean hasTarget(ItemStack stack) {
		return stack.getOrCreateTag().contains(UUID);
	}

	public static float getAngle(ItemStack stack, @Nullable ClientWorld level, @Nullable LivingEntity entity) {
		Entity sourceEntity = entity != null ? (Entity) entity : stack.getEntityRepresentation();
		if (sourceEntity == null) {
			return 0F;
		}

		double targetX = getX(stack);
		double targetZ = getZ(stack);

		double angleOfSource = 0.0D;
		if (entity instanceof PlayerEntity && ((PlayerEntity) entity).isLocalPlayer()) {
			angleOfSource = entity.yRot;
		} else if (sourceEntity instanceof ItemFrameEntity) {
			ItemFrameEntity itemFrameEntity = (ItemFrameEntity) sourceEntity;
			Direction direction = itemFrameEntity.getDirection();
			int j = direction.getAxis().isVertical() ? 90 * direction.getAxisDirection().getStep() : 0;
			angleOfSource = MathHelper.wrapDegrees(180 + direction.get2DDataValue() * 90L + itemFrameEntity.getRotation() * 45L + j);
		} else if (sourceEntity instanceof ItemEntity) {
			ItemEntity item = (ItemEntity) sourceEntity;
			angleOfSource = 180.0F - item.getSpin(0.5F) / ((float) Math.PI * 2F) * 360.0F;
		} else if (entity != null) {
			angleOfSource = entity.yBodyRot;
		}

		double rawAngleToTarget = Math.atan2(targetZ - sourceEntity.getZ(), targetX - sourceEntity.getX()) / ((float) Math.PI * 2F);
		double adjustedAngleToTarget = 0.5D - (MathHelper.positiveModulo(angleOfSource / 360.0D, 1.0D) - 0.25D - rawAngleToTarget);

		return MathHelper.positiveModulo((float) adjustedAngleToTarget, 1.0F);
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged || !oldStack.getItem().equals(newStack.getItem());
	}

}