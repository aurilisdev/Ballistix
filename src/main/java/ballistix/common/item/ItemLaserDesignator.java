package ballistix.common.item;

import java.util.List;

import ballistix.References;
import ballistix.common.network.SiloRegistry;
import ballistix.common.tile.TileMissileSilo;
import electrodynamics.common.tile.TileMultiSubnode;
import electrodynamics.prefab.item.ElectricItemProperties;
import electrodynamics.prefab.item.ItemElectric;
import electrodynamics.prefab.utilities.UtilitiesMath;
import electrodynamics.prefab.utilities.object.Location;
import electrodynamics.prefab.utilities.object.TransferPack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
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
	super((ElectricItemProperties) new ElectricItemProperties().capacity(10000).receive(TransferPack.joulesVoltage(500, 120))
		.extract(TransferPack.joulesVoltage(500, 120)).stacksTo(1).tab(References.BALLISTIXTAB));
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
	BlockEntity ent = context.getLevel().getBlockEntity(context.getClickedPos());
	TileMissileSilo silo = ent instanceof TileMissileSilo s ? s : null;
	if (ent instanceof TileMultiSubnode node) {
	    BlockEntity core = node.nodePos.getTile(node.getLevel());
	    if (core instanceof TileMissileSilo c) {
		silo = c;
	    }
	}
	if (silo != null) {
	    CompoundTag nbt = stack.getOrCreateTag();
	    nbt.putInt("freq", silo.frequency);
	}
	return super.onItemUseFirst(stack, context);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
	if (!worldIn.isClientSide) {
	    Location trace = UtilitiesMath.getRaytracedBlock(playerIn);
	    if (trace != null) {
		CompoundTag nbt = playerIn.getItemBySlot(handIn == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND)
			.getOrCreateTag();
		if (nbt.contains("freq")) {
		    int freq = nbt.getInt("freq");
		    if (freq != 0) {
			for (TileMissileSilo silo : SiloRegistry.getSilos(freq)) {
			    silo.target = new Location(trace);
			    silo.shouldLaunch = true;
			}
		    }
		}
		extractPower(playerIn.getItemBySlot(handIn == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND), 150,
			false);
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
	Location trace = UtilitiesMath.getRaytracedBlock(entityIn);
	if (!worldIn.isClientSide && entityIn instanceof Player player) {
	    if (isSelected && trace != null) {
		player.displayClientMessage(new TranslatableComponent("message.radargun.text", trace.toString()), true);
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
		    tooltip.add(new TranslatableComponent("tooltip.laserdesignator.frequency", freq));
		}
	    }
	}
    }
}
