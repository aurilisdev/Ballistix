package ballistix.registers;

import ballistix.References;
import ballistix.common.block.subtype.SubtypeMissile;
import ballistix.prefab.utils.BallistixTextUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BallistixCreativeTabs {

	public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, References.ID);

	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN = CREATIVE_TABS.register("main", () -> CreativeModeTab.builder().title(BallistixTextUtils.creativeTab("main")).icon(() -> new ItemStack(BallistixItems.ITEMS_MISSILE.getValue(SubtypeMissile.tier2))).build());

}
