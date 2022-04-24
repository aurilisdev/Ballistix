package ballistix;

import java.util.HashMap;

import com.google.common.base.Supplier;
import com.google.common.collect.Sets;

import ballistix.common.block.BlockExplosive;
import ballistix.common.block.BlockMissileSilo;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.entity.EntityBlast;
import ballistix.common.entity.EntityExplosive;
import ballistix.common.entity.EntityGrenade;
import ballistix.common.entity.EntityMinecart;
import ballistix.common.entity.EntityMissile;
import ballistix.common.entity.EntityShrapnel;
import ballistix.common.inventory.container.ContainerMissileSilo;
import ballistix.common.item.ItemDefuser;
import ballistix.common.item.ItemGrenade;
import ballistix.common.item.ItemLaserDesignator;
import ballistix.common.item.ItemMinecart;
import ballistix.common.item.ItemRadarGun;
import ballistix.common.item.ItemRocketLauncher;
import ballistix.common.item.ItemScanner;
import ballistix.common.item.ItemTracker;
import ballistix.common.tile.TileMissileSilo;
import electrodynamics.api.ISubtype;
import electrodynamics.common.blockitem.BlockItemDescriptable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.RegistryObject;

public class DeferredRegisters {
	public static final HashMap<ISubtype, RegistryObject<Item>> SUBTYPEITEMREGISTER_MAPPINGS = new HashMap<>();
	public static final HashMap<ISubtype, RegistryObject<Item>> SUBTYPEMINECARTMAPPINGS = new HashMap<>();
	public static final HashMap<ISubtype, RegistryObject<Block>> SUBTYPEBLOCKREGISTER_MAPPINGS = new HashMap<>();
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, References.ID);
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, References.ID);
	public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, References.ID);
	public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, References.ID);
	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, References.ID);
	public static BlockMissileSilo blockMissileSilo;

	static {
		for (SubtypeBlast subtype : SubtypeBlast.values()) {
			SUBTYPEBLOCKREGISTER_MAPPINGS.put(subtype, BLOCKS.register(subtype.tag(), supplier(() -> new BlockExplosive(subtype), subtype)));
			ITEMS.register(subtype.tag(), supplier(() -> new BlockItemDescriptable(() -> getSafeBlock(subtype), new Item.Properties().tab(References.BALLISTIXTAB)), subtype));
		}
		for (SubtypeBlast subtype : SubtypeBlast.values()) {
			if (subtype.hasGrenade) {
				SUBTYPEITEMREGISTER_MAPPINGS.put(subtype, ITEMS.register("grenade" + subtype.tag(), supplier(() -> new ItemGrenade(subtype), subtype)));
			}
		}
		for (SubtypeBlast subtype : SubtypeBlast.values()) {
			if (subtype.hasMinecart) {
				SUBTYPEMINECARTMAPPINGS.put(subtype, ITEMS.register("minecart" + subtype.tag(), supplier(() -> new ItemMinecart(subtype))));
			}
		}
		BLOCKS.register("missilesilo", supplier(() -> blockMissileSilo = new BlockMissileSilo()));
		ITEMS.register("missilesilo", supplier(() -> new BlockItemDescriptable(() -> blockMissileSilo, new Item.Properties().tab(References.BALLISTIXTAB))));

	}
	public static final RegistryObject<Item> ITEM_DUSTPOISON = ITEMS.register("dustpoison", supplier(() -> new Item(new Item.Properties().tab(References.BALLISTIXTAB))));
	public static final RegistryObject<Item> ITEM_MISSILECLOSERANGE = ITEMS.register("missilecloserange", supplier(() -> new Item(new Item.Properties().tab(References.BALLISTIXTAB))));
	public static final RegistryObject<Item> ITEM_MISSILEMEDIUMRANGE = ITEMS.register("missilemediumrange", supplier(() -> new Item(new Item.Properties().tab(References.BALLISTIXTAB))));
	public static final RegistryObject<Item> ITEM_MISSILELONGRANGE = ITEMS.register("missilelongrange", supplier(() -> new Item(new Item.Properties().tab(References.BALLISTIXTAB))));
	public static final RegistryObject<Item> ITEM_ROCKETLAUNCHER = ITEMS.register("rocketlauncher", supplier(ItemRocketLauncher::new));
	public static final RegistryObject<Item> ITEM_RADARGUN = ITEMS.register("radargun", supplier(ItemRadarGun::new));
	public static final RegistryObject<Item> ITEM_TRACKER = ITEMS.register("tracker", supplier(ItemTracker::new));
	public static final RegistryObject<Item> ITEM_SCANNER = ITEMS.register("scanner", supplier(ItemScanner::new));
	public static final RegistryObject<Item> ITEM_LASERDESIGNATOR = ITEMS.register("laserdesignator", supplier(ItemLaserDesignator::new));
	public static final RegistryObject<Item> ITEM_DEFUSER = ITEMS.register("defuser", supplier(ItemDefuser::new));
	public static final RegistryObject<BlockEntityType<TileMissileSilo>> TILE_MISSILESILO = TILES.register("missilesilo", () -> new BlockEntityType<>(TileMissileSilo::new, Sets.newHashSet(blockMissileSilo), null));
	public static final RegistryObject<MenuType<ContainerMissileSilo>> CONTAINER_MISSILESILO = CONTAINERS.register("missilesilo", () -> new MenuType<>(ContainerMissileSilo::new));
	public static final RegistryObject<EntityType<EntityExplosive>> ENTITY_EXPLOSIVE = ENTITIES.register("explosive", () -> EntityType.Builder.<EntityExplosive>of(EntityExplosive::new, MobCategory.MISC).fireImmune().sized(1, 1).clientTrackingRange(10).build(References.ID + ".explosive"));
	public static final RegistryObject<EntityType<EntityGrenade>> ENTITY_GRENADE = ENTITIES.register("grenade", () -> EntityType.Builder.<EntityGrenade>of(EntityGrenade::new, MobCategory.MISC).fireImmune().sized(0.25f, 0.55f).build(References.ID + ".grenade"));
	public static final RegistryObject<EntityType<EntityMinecart>> ENTITY_MINECART = ENTITIES.register("minecart", () -> EntityType.Builder.<EntityMinecart>of(EntityMinecart::new, MobCategory.MISC).fireImmune().clientTrackingRange(8).sized(0.98F, 0.7F).build(References.ID + ".minecart"));
	public static final RegistryObject<EntityType<EntityBlast>> ENTITY_BLAST = ENTITIES.register("blast", () -> EntityType.Builder.<EntityBlast>of(EntityBlast::new, MobCategory.MISC).fireImmune().build(References.ID + ".blast"));
	public static final RegistryObject<EntityType<EntityShrapnel>> ENTITY_SHRAPNEL = ENTITIES.register("shrapnel", () -> EntityType.Builder.<EntityShrapnel>of(EntityShrapnel::new, MobCategory.MISC).fireImmune().sized(0.5f, 0.5f).build(References.ID + ".shrapnel"));
	public static final RegistryObject<EntityType<EntityMissile>> ENTITY_MISSILE = ENTITIES.register("missile", () -> EntityType.Builder.<EntityMissile>of(EntityMissile::new, MobCategory.MISC).fireImmune().sized(0, 0).build(References.ID + ".missile"));

	private static <T extends ForgeRegistryEntry<T>> Supplier<? extends T> supplier(Supplier<? extends T> entry) {
		return entry;
	}

	private static <T extends ForgeRegistryEntry<T>> Supplier<? extends T> supplier(Supplier<? extends T> entry, ISubtype en) {
		return entry;
	}

	public static Block getSafeBlock(ISubtype type) {
		return SUBTYPEBLOCKREGISTER_MAPPINGS.get(type).get();
	}
}
