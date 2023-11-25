package ballistix.common.item;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import ballistix.References;
import ballistix.prefab.utils.BallistixTextUtils;
import electrodynamics.prefab.item.ElectricItemProperties;
import electrodynamics.prefab.item.ItemElectric;
import electrodynamics.prefab.utilities.object.TransferPack;
import electrodynamics.registers.ElectrodynamicsItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = References.ID, bus = Bus.FORGE)
public class ItemTracker extends ItemElectric {

	public static final String X = "target_x";
	public static final String Z = "target_z";

	public static final String UUID = "uuid";

	public static HashMap<ServerLevel, HashSet<Integer>> validuuids = new HashMap<>();

	public ItemTracker() {
		super((ElectricItemProperties) new ElectricItemProperties().capacity(1666666.66667).receive(TransferPack.joulesVoltage(1666666.66667 / (120.0 * 20.0), 120)).extract(TransferPack.joulesVoltage(1666666.66667 / (120.0 * 20.0), 120)).stacksTo(1).tab(References.BALLISTIXTAB), item -> ElectrodynamicsItems.ITEM_BATTERY.get());
	}

	@Override
	public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		Component name = BallistixTextUtils.tooltip("tracker.none");
		if (hasTarget(stack)) {
			Entity entity = worldIn.getEntity(getUUID(stack));
			if (entity != null) {
				name = entity.getName();
			}
		}
		tooltip.add(BallistixTextUtils.tooltip("tracker.tracking", name).withStyle(ChatFormatting.DARK_GRAY));
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
		super.inventoryTick(stack, level, entity, slot, selected);
		if (level instanceof ServerLevel slevel) {
			if ((selected || entity instanceof Player player && player.getOffhandItem() == stack) && hasTarget(stack)) {
				int uuid = getUUID(stack);
				if (validuuids.containsKey(level) && validuuids.get(level).contains(uuid)) {
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
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
		if (player != null && player.getLevel() instanceof ServerLevel server && getJoulesStored(stack) >= 150) {
			Inventory inv = player.getInventory();
			inv.removeItem(stack);
			setUUID(stack, entity.getId());
			HashSet<Integer> set = validuuids.getOrDefault(server, new HashSet<>());
			set.add(entity.getId());
			validuuids.put(server, set);
			if (hand == InteractionHand.MAIN_HAND) {
				inv.setItem(inv.selected, stack);
			} else {
				inv.offhand.set(0, stack);
			}
			extractPower(stack, 150, false);
		}
		return InteractionResult.PASS;
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
		CompoundTag tag = stack.getOrCreateTag();
		tag.remove(X);
		tag.remove(Z);
		tag.remove(UUID);
	}

	public static boolean hasTargetCoords(ItemStack stack) {
		CompoundTag tag = stack.getOrCreateTag();
		return tag.contains(X) && tag.contains(Z);
	}

	public static boolean hasTarget(ItemStack stack) {
		return stack.getOrCreateTag().contains(UUID);
	}

	@OnlyIn(Dist.CLIENT)
	public static float getAngle(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int par) {
		Entity sourceEntity = entity != null ? (Entity) entity : stack.getEntityRepresentation();
		if (sourceEntity == null) {
			return 0F;
		}

		double targetX = getX(stack);
		double targetZ = getZ(stack);

		double angleOfSource = 0.0D;
		if (entity instanceof Player player && player.isLocalPlayer()) {
			angleOfSource = entity.getYRot();
		} else if (sourceEntity instanceof ItemFrame itemFrameEntity) {
			Direction direction = itemFrameEntity.getDirection();
			int j = direction.getAxis().isVertical() ? 90 * direction.getAxisDirection().getStep() : 0;
			angleOfSource = Mth.wrapDegrees(180 + direction.get2DDataValue() * 90L + itemFrameEntity.getRotation() * 45L + j);
		} else if (sourceEntity instanceof ItemEntity item) {
			angleOfSource = 180.0F - item.getSpin(0.5F) / ((float) Math.PI * 2F) * 360.0F;
		} else if (entity != null) {
			angleOfSource = entity.yBodyRot;
		}

		double rawAngleToTarget = Math.atan2(targetZ - sourceEntity.getZ(), targetX - sourceEntity.getX()) / ((float) Math.PI * 2F);
		double adjustedAngleToTarget = 0.5D - (Mth.positiveModulo(angleOfSource / 360.0D, 1.0D) - 0.25D - rawAngleToTarget);

		return Mth.positiveModulo((float) adjustedAngleToTarget, 1.0F);
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged || !oldStack.getItem().equals(newStack.getItem());
	}

	@SubscribeEvent
	public static void tick(ServerTickEvent event) {
		for (Entry<ServerLevel, HashSet<Integer>> en : validuuids.entrySet()) {
			Iterator<Integer> it = en.getValue().iterator();
			while (it.hasNext()) {
				int uuid = it.next();
				Entity ent = en.getKey().getEntity(uuid);
				if (ent == null || ent.isRemoved()) {
					it.remove();
				}
			}
		}
	}
}
