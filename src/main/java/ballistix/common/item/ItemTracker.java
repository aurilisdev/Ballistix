package ballistix.common.item;

import javax.annotation.Nullable;

import ballistix.References;
import electrodynamics.prefab.item.ElectricItemProperties;
import electrodynamics.prefab.item.ItemElectric;
import electrodynamics.prefab.utilities.object.TransferPack;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemTracker extends ItemElectric {

	public ItemTracker() {
		super((ElectricItemProperties) new ElectricItemProperties().capacity(10000).receive(TransferPack.joulesVoltage(500, 120))
				.extract(TransferPack.joulesVoltage(500, 120)).stacksTo(1).tab(References.BALLISTIXTAB));
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
		super.inventoryTick(stack, level, entity, slot, selected);
		if (level instanceof ServerLevel slevel) {
			CompoundTag tag = stack.getOrCreateTag();
			if ((selected || entity instanceof Player player && player.getOffhandItem() == stack) && tag.contains("uuid")) {
				Entity ent = slevel.getEntity(tag.getUUID("uuid"));
				if (ent != null) {
					tag.putDouble("target_x", ent.position().x);
					tag.putDouble("target_z", ent.position().z);
				}
			}
		}
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
		if (player != null && !player.level.isClientSide) {
			Inventory inv = player.getInventory();
			inv.removeItem(stack);
			stack.getOrCreateTag().putUUID("uuid", entity.getUUID());
			if (hand == InteractionHand.MAIN_HAND) {
				inv.setItem(inv.selected, stack);
			} else {
				inv.offhand.set(0, stack);
			}
		}
		return InteractionResult.PASS;
	}

	public static boolean hasTarget(ItemStack stack) {
		CompoundTag tag = stack.getOrCreateTag();
		return tag.contains("target_x") && tag.contains("target_z");
	}

	public static float getAngle(ItemStack itemStack, @Nullable ClientLevel level, @Nullable LivingEntity livingEntity, int par) {
		Entity sourceEntity = livingEntity != null ? (Entity) livingEntity : itemStack.getEntityRepresentation();
		if (sourceEntity == null) {
			return 0F;
		}

		CompoundTag itemStackData = itemStack.getOrCreateTag();
		double targetX = itemStackData.contains("target_x") ? itemStackData.getDouble("target_x") : 0D;
		double targetZ = itemStackData.contains("target_z") ? itemStackData.getDouble("target_z") : 0D;

		double angleOfSource = 0.0D;
		if (livingEntity instanceof Player player && player.isLocalPlayer()) {
			angleOfSource = livingEntity.getYRot();
		} else if (sourceEntity instanceof ItemFrame itemFrameEntity) {
			Direction direction = itemFrameEntity.getDirection();
			int j = direction.getAxis().isVertical() ? 90 * direction.getAxisDirection().getStep() : 0;
			angleOfSource = Mth.wrapDegrees(180 + direction.get2DDataValue() * 90L + itemFrameEntity.getRotation() * 45L + j);
		} else if (sourceEntity instanceof ItemEntity item) {
			angleOfSource = 180.0F - item.getSpin(0.5F) / ((float) Math.PI * 2F) * 360.0F;
		} else if (livingEntity != null) {
			angleOfSource = livingEntity.yBodyRot;
		}

		double rawAngleToTarget = Math.atan2(targetZ - sourceEntity.getZ(), targetX - sourceEntity.getX()) / ((float) Math.PI * 2F);
		double adjustedAngleToTarget = 0.5D - (Mth.positiveModulo(angleOfSource / 360.0D, 1.0D) - 0.25D - rawAngleToTarget);

		return Mth.positiveModulo((float) adjustedAngleToTarget, 1.0F);
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged || !oldStack.getItem().equals(newStack.getItem());
	}
}
