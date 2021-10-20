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
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ItemLaserDesignator extends ItemElectric {

    public ItemLaserDesignator() {
	super((ElectricItemProperties) new ElectricItemProperties().capacity(10000).extract(TransferPack.joulesVoltage(500, 120)).maxStackSize(1)
		.group(References.BALLISTIXTAB));
    }

    @Override
    public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
	TileEntity ent = context.getWorld().getTileEntity(context.getPos());
	TileMissileSilo silo = ent instanceof TileMissileSilo ? (TileMissileSilo) ent : null;
	if (ent instanceof TileMultiSubnode) {
	    TileMultiSubnode node = (TileMultiSubnode) ent;
	    TileEntity core = node.nodePos.getTile(node.getWorld());
	    if (core instanceof TileMissileSilo) {
		silo = (TileMissileSilo) core;
	    }
	}
	if (silo != null) {
	    CompoundNBT nbt = stack.getOrCreateTag();
	    nbt.putInt("freq", silo.frequency);
	}
	return super.onItemUseFirst(stack, context);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
	if (!worldIn.isRemote) {
	    Location trace = UtilitiesMath.getRaytracedBlock(playerIn);
	    if (trace != null) {
		CompoundNBT nbt = playerIn
			.getItemStackFromSlot(handIn == Hand.MAIN_HAND ? EquipmentSlotType.MAINHAND : EquipmentSlotType.OFFHAND)
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
		extractPower(playerIn.getItemStackFromSlot(handIn == Hand.MAIN_HAND ? EquipmentSlotType.MAINHAND : EquipmentSlotType.OFFHAND), 150,
			false);
	    }
	}
	return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    public static ServerWorld getFromNBT(ServerWorld base, String str) {
	for (ServerWorld world : base.getWorld().getServer().getWorlds()) {
	    if (world.getDimensionKey().getLocation().getPath().equalsIgnoreCase(str)) {
		return world;
	    }
	}
	return null;
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
	super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
	Location trace = UtilitiesMath.getRaytracedBlock(entityIn);
	if (!worldIn.isRemote && entityIn instanceof PlayerEntity) {
	    PlayerEntity player = (PlayerEntity) entityIn;
	    if (isSelected && trace != null) {
		player.sendStatusMessage(new TranslationTextComponent("message.radargun.text", trace.toString()), true);
	    }
	}
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
	super.addInformation(stack, worldIn, tooltip, flagIn);
	if (stack.hasTag()) {
	    CompoundNBT nbt = stack.getTag();
	    if (nbt.contains("freq")) {
		int freq = nbt.getInt("freq");
		if (freq != 0) {
		    tooltip.add(new TranslationTextComponent("tooltip.laserdesignator.frequency", freq));
		}
	    }
	}
    }
}
