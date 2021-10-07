package ballistix.common.item;

import java.util.List;

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

public class ItemRadarGun extends ItemElectric {

    public ItemRadarGun() {
	super((ElectricItemProperties) new ElectricItemProperties().capacity(10000)
		.extract(TransferPack.joulesVoltage(500, 120)).maxStackSize(1));
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
	    int x = nbt.getInt("xCoord");
	    int y = nbt.getInt("yCoord");
	    int z = nbt.getInt("zCoord");
	    silo.target = new Location(x, y, z);
	}
	return super.onItemUseFirst(stack, context);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
	Location trace = UtilitiesMath.getRaytracedBlock(playerIn);
	if (trace != null) {
	    CompoundNBT nbt = playerIn.getActiveItemStack().getOrCreateTag();
	    nbt.putInt("xCoord", trace.intX());
	    nbt.putInt("yCoord", trace.intY());
	    nbt.putInt("zCoord", trace.intZ());
	    nbt.putString("world", worldIn.getDimensionKey().getLocation().getPath());
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
	    if (isSelected) {
		player.sendStatusMessage(new TranslationTextComponent("message.radargun.text", trace.toString()), true);
	    }
	}
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
	super.addInformation(stack, worldIn, tooltip, flagIn);
	if (stack.hasTag()) {
	    CompoundNBT nbt = stack.getTag();
	    int x = nbt.getInt("xCoord");
	    int y = nbt.getInt("yCoord");
	    int z = nbt.getInt("zCoord");
	    String world = nbt.getString("world");
	    tooltip.add(
		    new TranslationTextComponent("tooltip.radargun.linked", world + ", " + x + ", " + y + ", " + z));
	} else {
	    tooltip.add(new TranslationTextComponent("tooltip.radargun.notag"));
	}
    }
}
