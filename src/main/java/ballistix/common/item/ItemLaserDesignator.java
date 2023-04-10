package ballistix.common.item;

import java.util.List;

import ballistix.References;
import ballistix.common.network.SiloRegistry;
import ballistix.common.tile.TileMissileSilo;
import ballistix.prefab.utils.TextUtils;
import electrodynamics.common.tile.TileMultiSubnode;
import electrodynamics.prefab.item.ElectricItemProperties;
import electrodynamics.prefab.item.ItemElectric;
import electrodynamics.prefab.utilities.math.MathUtils;
import electrodynamics.prefab.utilities.object.Location;
import electrodynamics.prefab.utilities.object.TransferPack;
import electrodynamics.registers.ElectrodynamicsItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ItemLaserDesignator extends ItemElectric {

	public ItemLaserDesignator() {
		super((ElectricItemProperties) new ElectricItemProperties().capacity(1666666.66667).receive(TransferPack.joulesVoltage(1666666.66667 / (120.0 * 20.0), 120)).extract(TransferPack.joulesVoltage(1666666.66667 / (120.0 * 20.0), 120)).stacksTo(1).tab(References.BALLISTIXTAB), item -> ElectrodynamicsItems.ITEM_BATTERY.get());
	}

	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
		BlockEntity ent = context.getLevel().getBlockEntity(context.getClickedPos());
		TileMissileSilo silo = ent instanceof TileMissileSilo s ? s : null;
		if (ent instanceof TileMultiSubnode node && node.nodePos.get() != null) {
			BlockEntity core = node.getLevel().getBlockEntity(node.nodePos.get());
			if (core instanceof TileMissileSilo c) {
				silo = c;
			}
		}
		if (silo != null) {
			CompoundTag nbt = stack.getOrCreateTag();
			nbt.putInt("freq", silo.frequency.get());
			context.getPlayer().sendSystemMessage(TextUtils.chatMessage("laserdesignator.setfrequency", silo.frequency));
		}
		return super.onItemUseFirst(stack, context);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
		ItemStack stack = playerIn.getItemBySlot(handIn == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
		if (!worldIn.isClientSide && getJoulesStored(stack) >= 150) {
			Location trace = MathUtils.getRaytracedBlock(playerIn);
			if (trace != null) {
				CompoundTag nbt = stack.getOrCreateTag();
				if (nbt.contains("freq")) {
					int freq = nbt.getInt("freq");
					if (freq != 0) {
						for (TileMissileSilo silo : SiloRegistry.getSilos(freq)) {
							silo.target.set(trace.toBlockPos());
							silo.shouldLaunch = true;
							playerIn.sendSystemMessage(TextUtils.chatMessage("laserdesignator.launch", new Location(silo.getBlockPos()) + " -> " + silo.target));
						}
					}
				}
				extractPower(stack, 150, false);
			}
		}
		return super.use(worldIn, playerIn, handIn);
	}

	public static ServerLevel getFromNBT(ServerLevel base, String str) {
		for (ServerLevel world : base.getLevel().getServer().getAllLevels()) {
			if (world.dimension().location().getPath().equalsIgnoreCase(str)) {
				return world;
			}
		}
		return null;
	}

	@Override
	public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
		Location trace = MathUtils.getRaytracedBlock(entityIn);
		if (!worldIn.isClientSide && entityIn instanceof Player player) {
			if (isSelected && trace != null) {
				player.displayClientMessage(TextUtils.chatMessage("radargun.text", trace.toString()), true);
			}
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		if (stack.hasTag()) {
			CompoundTag nbt = stack.getTag();
			if (nbt.contains("freq")) {
				int freq = nbt.getInt("freq");
				if (freq != 0) {
					tooltip.add(TextUtils.chatMessage("laserdesignator.frequency", freq));
				}
			}
		}
	}
}
