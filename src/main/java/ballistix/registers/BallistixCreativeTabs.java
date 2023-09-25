package ballistix.registers;

import ballistix.References;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.prefab.utils.BallistixTextUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class BallistixCreativeTabs {

	public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, References.ID);

	public static final RegistryObject<CreativeModeTab> MAIN = CREATIVE_TABS.register("main", () -> CreativeModeTab.builder().title(BallistixTextUtils.creativeTab("main")).icon(() -> new ItemStack(BallistixBlocks.getBlock(SubtypeBlast.antimatter))).build());

}
