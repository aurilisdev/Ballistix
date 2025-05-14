package ballistix.registers;

import java.util.ArrayList;
import java.util.List;

import ballistix.Ballistix;
import ballistix.common.block.subtype.SubtypeBallistixMachine;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.block.subtype.SubtypeMissile;
import ballistix.common.item.ItemAAMissile;
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
import ballistix.common.settings.BallistixConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import voltaic.Voltaic;
import voltaic.api.creativetab.CreativeTabSupplier;
import voltaic.api.registration.BulkRegistryObject;
import voltaic.common.blockitem.BlockItemDescriptable;
import voltaic.common.item.ItemUpgrade;
import voltaic.common.item.ItemVoltaic;
import voltaic.common.item.subtype.SubtypeItemUpgrade;

public class BallistixItems {

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, Ballistix.ID);

	public static final BulkRegistryObject<BlockItemDescriptable, SubtypeBallistixMachine> ITEMS_BALLISTIXMACHINE = new BulkRegistryObject<>(SubtypeBallistixMachine.values(), subtype -> ITEMS.register(subtype.tag(), () -> new BlockItemDescriptable(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(subtype), new Item.Properties(), BallistixCreativeTabs.MAIN)));
	public static final BulkRegistryObject<BlockItemDescriptable, SubtypeBlast> ITEMS_EXPLOSIVE = new BulkRegistryObject<>(SubtypeBlast.values(), subtype -> ITEMS.register(subtype.tag(), () -> new BlockItemDescriptable(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(subtype), new Item.Properties(), BallistixCreativeTabs.MAIN)));
	public static final BulkRegistryObject<ItemGrenade, SubtypeGrenade> ITEMS_GRENADE = new BulkRegistryObject<>(SubtypeGrenade.values(), subtype -> ITEMS.register(subtype.tag(), () -> new ItemGrenade(subtype)));
	public static final BulkRegistryObject<ItemMinecart, SubtypeMinecart> ITEMS_MINECART = new BulkRegistryObject<>(SubtypeMinecart.values(), subtype -> ITEMS.register(subtype.tag(), () -> new ItemMinecart(subtype)));
	public static final BulkRegistryObject<ItemMissile, SubtypeMissile> ITEMS_MISSILE = new BulkRegistryObject<>(SubtypeMissile.values(), subtype -> ITEMS.register(subtype.tag(), () -> new ItemMissile(subtype)));

	public static final RegistryObject<ItemVoltaic> ITEM_AAMISSILE = ITEMS.register("aamissile", () -> new ItemAAMissile(new Item.Properties().stacksTo(10), () -> BallistixCreativeTabs.MAIN.get(), BallistixConstants.SAM_CHANCE_TO_DESTROY));
	public static final RegistryObject<ItemVoltaic> ITEM_AAMISSILEMK2 = ITEMS.register("aamissilemk2", () -> new ItemAAMissile(new Item.Properties().stacksTo(5), () -> BallistixCreativeTabs.MAIN.get(), BallistixConstants.ANTIBALLISTICMISSILE_CHANCE_TO_DESTROY));
	public static final RegistryObject<ItemVoltaic> ITEM_BULLET = ITEMS.register("bullet", () -> new ItemVoltaic(new Item.Properties().stacksTo(64), BallistixCreativeTabs.MAIN));
	public static final RegistryObject<ItemVoltaic> ITEM_DUSTPOISON = ITEMS.register("dustpoison", () -> new ItemVoltaic(new Item.Properties(), BallistixCreativeTabs.MAIN));
	public static final RegistryObject<ItemRocketLauncher> ITEM_ROCKETLAUNCHER = ITEMS.register("rocketlauncher", ItemRocketLauncher::new);
	public static final RegistryObject<ItemRadarGun> ITEM_RADARGUN = ITEMS.register("radargun", ItemRadarGun::new);
	public static final RegistryObject<ItemTracker> ITEM_TRACKER = ITEMS.register("tracker", ItemTracker::new);
	public static final RegistryObject<ItemScanner> ITEM_SCANNER = ITEMS.register("scanner", ItemScanner::new);
	public static final RegistryObject<ItemLaserDesignator> ITEM_LASERDESIGNATOR = ITEMS.register("laserdesignator", ItemLaserDesignator::new);
	public static final RegistryObject<ItemDefuser> ITEM_DEFUSER = ITEMS.register("defuser", ItemDefuser::new);
	public static final RegistryObject<ItemUpgrade> ITEM_RANGEUPGRADE = ITEMS.register("rangeupgrade", () -> new ItemUpgrade(new Item.Properties(), SubtypeItemUpgrade.range, BallistixCreativeTabs.MAIN) {

		@Override
		public void addCreativeModeItems(CreativeModeTab tab, List<ItemStack> items) {

			if(Voltaic.isElectroLoaded()) {
				return;
			}

			super.addCreativeModeItems(tab, items);
		}
	});

	@EventBusSubscriber(value = Dist.CLIENT, modid = Ballistix.ID, bus = EventBusSubscriber.Bus.MOD)
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
