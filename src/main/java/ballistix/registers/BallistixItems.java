package ballistix.registers;

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
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import voltaic.api.registration.BulkRegistryObject;
import voltaic.common.blockitem.BlockItemDescriptable;
import voltaic.common.item.ItemVoltaic;

public class BallistixItems {

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Ballistix.ID);

	public static final BulkRegistryObject<BlockItemDescriptable, SubtypeBallistixMachine> ITEMS_BALLISTIXMACHINE = new BulkRegistryObject<>(SubtypeBallistixMachine.values(), subtype -> ITEMS.register(subtype.tag(), () -> new BlockItemDescriptable(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(subtype), new Item.Properties(), () -> BallistixCreativeTabs.MAIN)));
	public static final BulkRegistryObject<BlockItemDescriptable, SubtypeBlast> ITEMS_EXPLOSIVE = new BulkRegistryObject<>(SubtypeBlast.values(), subtype -> ITEMS.register(subtype.tag(), () -> new BlockItemDescriptable(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(subtype), new Item.Properties(), () -> BallistixCreativeTabs.MAIN)));
	public static final BulkRegistryObject<ItemGrenade, SubtypeGrenade> ITEMS_GRENADE = new BulkRegistryObject<>(SubtypeGrenade.values(), subtype -> ITEMS.register(subtype.tag(), () -> new ItemGrenade(subtype.explosiveType)));
	public static final BulkRegistryObject<ItemMinecart, SubtypeMinecart> ITEMS_MINECART = new BulkRegistryObject<>(SubtypeMinecart.values(), subtype -> ITEMS.register(subtype.tag(), () -> new ItemMinecart(subtype.explosiveType)));
	public static final BulkRegistryObject<ItemMissile, SubtypeMissile> ITEMS_MISSILE = new BulkRegistryObject<>(SubtypeMissile.values(), subtype -> ITEMS.register(subtype.tag(), () -> new ItemMissile(subtype)));

	public static final RegistryObject<ItemVoltaic> ITEM_AAMISSILE = ITEMS.register("aamissile", () -> new ItemAAMissile(new Item.Properties().stacksTo(10), () -> BallistixCreativeTabs.MAIN, BallistixConstants.SAM_CHANCE_TO_DESTROY));
	public static final RegistryObject<ItemVoltaic> ITEM_AAMISSILEMK2 = ITEMS.register("aamissilemk2", () -> new ItemAAMissile(new Item.Properties().stacksTo(5), () -> BallistixCreativeTabs.MAIN, BallistixConstants.ANTIBALLISTICMISSILE_CHANCE_TO_DESTROY));
	public static final RegistryObject<ItemVoltaic> ITEM_BULLET = ITEMS.register("bullet", () -> new ItemVoltaic(new Item.Properties().stacksTo(64), () -> BallistixCreativeTabs.MAIN));
	public static final RegistryObject<ItemVoltaic> ITEM_DUSTPOISON = ITEMS.register("dustpoison", () -> new ItemVoltaic(new Item.Properties(), () -> BallistixCreativeTabs.MAIN));
	public static final RegistryObject<ItemRocketLauncher> ITEM_ROCKETLAUNCHER = ITEMS.register("rocketlauncher", ItemRocketLauncher::new);
	public static final RegistryObject<ItemRadarGun> ITEM_RADARGUN = ITEMS.register("radargun", ItemRadarGun::new);
	public static final RegistryObject<ItemTracker> ITEM_TRACKER = ITEMS.register("tracker", ItemTracker::new);
	public static final RegistryObject<ItemScanner> ITEM_SCANNER = ITEMS.register("scanner", ItemScanner::new);
	public static final RegistryObject<ItemLaserDesignator> ITEM_LASERDESIGNATOR = ITEMS.register("laserdesignator", ItemLaserDesignator::new);
	public static final RegistryObject<ItemDefuser> ITEM_DEFUSER = ITEMS.register("defuser", ItemDefuser::new);

}
