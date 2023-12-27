package ballistix.registers;

import static ballistix.registers.BallistixBlocks.blockMissileSilo;
import static ballistix.registers.UnifiedBallistixRegister.getSafeBlock;
import static electrodynamics.registers.UnifiedElectrodynamicsRegister.supplier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ballistix.References;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.block.subtype.SubtypeMissile;
import ballistix.common.item.ItemDefuser;
import ballistix.common.item.ItemGrenade;
import ballistix.common.item.ItemGrenade.SubtypeGrenade;
import ballistix.common.item.ItemLaserDesignator;
import ballistix.common.item.ItemMinecart;
import ballistix.common.item.ItemMissile;
import ballistix.common.item.ItemMinecart.SubtypeMinecart;
import ballistix.common.item.ItemRadarGun;
import ballistix.common.item.ItemRocketLauncher;
import ballistix.common.item.ItemScanner;
import ballistix.common.item.ItemTracker;
import electrodynamics.api.ISubtype;
import electrodynamics.common.blockitem.BlockItemDescriptable;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BallistixItems {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, References.ID);

	public static final HashMap<ISubtype, RegistryObject<Item>> SUBTYPEITEMREGISTER_MAPPINGS = new HashMap<>();

	public static final RegistryObject<Item> ITEM_DUSTPOISON = ITEMS.register("dustpoison", supplier(() -> new Item(new Item.Properties().tab(References.BALLISTIXTAB))));
	public static final RegistryObject<Item> ITEM_ROCKETLAUNCHER = ITEMS.register("rocketlauncher", supplier(ItemRocketLauncher::new));
	public static final RegistryObject<Item> ITEM_RADARGUN = ITEMS.register("radargun", supplier(ItemRadarGun::new));
	public static final RegistryObject<Item> ITEM_TRACKER = ITEMS.register("tracker", supplier(ItemTracker::new));
	public static final RegistryObject<Item> ITEM_SCANNER = ITEMS.register("scanner", supplier(ItemScanner::new));
	public static final RegistryObject<Item> ITEM_LASERDESIGNATOR = ITEMS.register("laserdesignator", supplier(ItemLaserDesignator::new));
	public static final RegistryObject<Item> ITEM_DEFUSER = ITEMS.register("defuser", supplier(ItemDefuser::new));

	static {
		for (SubtypeBlast subtype : SubtypeBlast.values()) {
			ITEMS.register(subtype.tag(), supplier(() -> new BlockItemDescriptable(() -> getSafeBlock(subtype), new Item.Properties().tab(References.BALLISTIXTAB)), subtype));
		}
		for (SubtypeGrenade subtype : SubtypeGrenade.values()) {
			SUBTYPEITEMREGISTER_MAPPINGS.put(subtype, ITEMS.register(subtype.tag(), supplier(() -> new ItemGrenade(subtype), subtype)));
		}
		for (SubtypeMinecart subtype : SubtypeMinecart.values()) {
			SUBTYPEITEMREGISTER_MAPPINGS.put(subtype, ITEMS.register(subtype.tag(), supplier(() -> new ItemMinecart(subtype))));
		}
		for(SubtypeMissile missile : SubtypeMissile.values()) {
			SUBTYPEITEMREGISTER_MAPPINGS.put(missile, ITEMS.register(missile.tag(), supplier(() -> new ItemMissile(missile))));
		}
		ITEMS.register("missilesilo", supplier(() -> new BlockItemDescriptable(() -> blockMissileSilo, new Item.Properties().tab(References.BALLISTIXTAB))));
	}

	public static Item[] getAllItemForSubtype(ISubtype[] values) {
		List<Item> list = new ArrayList<>();
		for (ISubtype value : values) {
			list.add(SUBTYPEITEMREGISTER_MAPPINGS.get(value).get());
		}
		return list.toArray(new Item[] {});
	}

	public static Item getItem(ISubtype value) {
		return SUBTYPEITEMREGISTER_MAPPINGS.get(value).get();
	}

}
