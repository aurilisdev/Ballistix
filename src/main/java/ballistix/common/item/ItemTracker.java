package ballistix.common.item;

import java.util.UUID;

import ballistix.References;
import ballistix.common.tile.TileMissileSilo;
import electrodynamics.common.tile.TileMultiSubnode;
import electrodynamics.prefab.item.ElectricItemProperties;
import electrodynamics.prefab.item.ItemElectric;
import electrodynamics.prefab.utilities.object.TransferPack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ItemTracker extends ItemElectric {

	public ItemTracker() {
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
			context.getPlayer().sendMessage(new TranslatableComponent("message.laserdesignator.setfrequency", silo.frequency), UUID.randomUUID());
		}
		return super.onItemUseFirst(stack, context);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
		return super.use(worldIn, playerIn, handIn);
	}
}
