package ballistix.registers;

import java.util.ArrayList;
import java.util.List;

import ballistix.References;
import ballistix.common.block.subtype.SubtypeBallistixMachine;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.block.subtype.SubtypeMissile;
import ballistix.common.item.ItemDefuser;
import ballistix.common.item.ItemGrenade;
import ballistix.common.item.ItemGrenade.SubtypeGrenade;
import ballistix.common.item.ItemLaserDesignator;
import ballistix.common.item.ItemMinecart;
import ballistix.common.item.ItemMinecart.SubtypeMinecart;
import ballistix.common.item.ItemMissile;
import ballistix.common.item.ItemRadarGun;
import ballistix.common.item.ItemRocketLauncher;
import ballistix.common.item.ItemScanner;
import ballistix.common.item.ItemTracker;
import electrodynamics.api.creativetab.CreativeTabSupplier;
import electrodynamics.api.registration.BulkDeferredHolder;
import electrodynamics.common.blockitem.types.BlockItemDescriptable;
import electrodynamics.common.item.ItemElectrodynamics;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BallistixItems {

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, References.ID);

	public static final BulkDeferredHolder<Item, BlockItemDescriptable, SubtypeBallistixMachine> ITEMS_BALLISTIXMACHINE = new BulkDeferredHolder<>(SubtypeBallistixMachine.values(), subtype -> ITEMS.register(subtype.tag(), () -> new BlockItemDescriptable(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(subtype), new Item.Properties(), BallistixCreativeTabs.MAIN)));
	public static final BulkDeferredHolder<Item, BlockItemDescriptable, SubtypeBlast> ITEMS_EXPLOSIVE = new BulkDeferredHolder<>(SubtypeBlast.values(), subtype -> ITEMS.register(subtype.tag(), () -> new BlockItemDescriptable(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(subtype), new Item.Properties(), BallistixCreativeTabs.MAIN)));
	public static final BulkDeferredHolder<Item, ItemGrenade, SubtypeGrenade> ITEMS_GRENADE = new BulkDeferredHolder<>(SubtypeGrenade.values(), subtype -> ITEMS.register(subtype.tag(), () -> new ItemGrenade(subtype)));
	public static final BulkDeferredHolder<Item, ItemMinecart, SubtypeMinecart> ITEMS_MINECART = new BulkDeferredHolder<>(SubtypeMinecart.values(), subtype -> ITEMS.register(subtype.tag(), () -> new ItemMinecart(subtype)));
	public static final BulkDeferredHolder<Item, ItemMissile, SubtypeMissile> ITEMS_MISSILE = new BulkDeferredHolder<>(SubtypeMissile.values(), subtype -> ITEMS.register(subtype.tag(), () -> new ItemMissile(subtype)));

	public static final DeferredHolder<Item, ItemElectrodynamics> ITEM_AAMISSILE = ITEMS.register("aamissile", () -> new ItemElectrodynamics(new Item.Properties().stacksTo(10), BallistixCreativeTabs.MAIN));
	public static final DeferredHolder<Item, ItemElectrodynamics> ITEM_AAMISSILEMK2 = ITEMS.register("aamissilemk2", () -> new ItemElectrodynamics(new Item.Properties().stacksTo(5), BallistixCreativeTabs.MAIN));
	public static final DeferredHolder<Item, ItemElectrodynamics> ITEM_BULLET = ITEMS.register("bullet", () -> new ItemElectrodynamics(new Item.Properties().stacksTo(64), BallistixCreativeTabs.MAIN));
	public static final DeferredHolder<Item, ItemElectrodynamics> ITEM_DUSTPOISON = ITEMS.register("dustpoison", () -> new ItemElectrodynamics(new Item.Properties(), BallistixCreativeTabs.MAIN));
	public static final DeferredHolder<Item, ItemRocketLauncher> ITEM_ROCKETLAUNCHER = ITEMS.register("rocketlauncher", ItemRocketLauncher::new);
	public static final DeferredHolder<Item, ItemRadarGun> ITEM_RADARGUN = ITEMS.register("radargun", ItemRadarGun::new);
	public static final DeferredHolder<Item, ItemTracker> ITEM_TRACKER = ITEMS.register("tracker", ItemTracker::new);
	public static final DeferredHolder<Item, ItemScanner> ITEM_SCANNER = ITEMS.register("scanner", ItemScanner::new);
	public static final DeferredHolder<Item, ItemLaserDesignator> ITEM_LASERDESIGNATOR = ITEMS.register("laserdesignator", ItemLaserDesignator::new);
	public static final DeferredHolder<Item, ItemDefuser> ITEM_DEFUSER = ITEMS.register("defuser", ItemDefuser::new);

	@EventBusSubscriber(value = Dist.CLIENT, modid = References.ID, bus = EventBusSubscriber.Bus.MOD)
	private static class BallistixCreativeRegistry {

		@SubscribeEvent
		public static void registerItems(BuildCreativeModeTabContentsEvent event) {

			ITEMS.getEntries().forEach(reg -> {

				CreativeTabSupplier supplier = (CreativeTabSupplier) reg.get();

				if (supplier.hasCreativeTab() && supplier.isAllowedInCreativeTab(event.getTab())) {
					List<ItemStack> toAdd = new ArrayList<>();
					supplier.addCreativeModeItems(event.getTab(), toAdd);
					event.acceptAll(toAdd);
				}

			});

		}

	}

}
