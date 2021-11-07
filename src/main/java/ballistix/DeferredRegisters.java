package ballistix;

import java.util.HashMap;

import com.google.common.base.Supplier;
import com.google.common.collect.Sets;

import ballistix.common.block.BlockExplosive;
import ballistix.common.block.BlockMissileSilo;
import ballistix.common.block.SubtypeBlast;
import ballistix.common.entity.EntityBlast;
import ballistix.common.entity.EntityExplosive;
import ballistix.common.entity.EntityGrenade;
import ballistix.common.entity.EntityMissile;
import ballistix.common.entity.EntityShrapnel;
import ballistix.common.inventory.container.ContainerMissileSilo;
import ballistix.common.item.ItemGrenade;
import ballistix.common.item.ItemLaserDesignator;
import ballistix.common.item.ItemRadarGun;
import ballistix.common.item.ItemRocketLauncher;
import ballistix.common.tile.TileMissileSilo;
import electrodynamics.api.ISubtype;
import electrodynamics.common.blockitem.BlockItemDescriptable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class DeferredRegisters {
    public static final HashMap<ISubtype, Item> SUBTYPEITEM_MAPPINGS = new HashMap<>();
    public static final HashMap<ISubtype, Block> SUBTYPEBLOCK_MAPPINGS = new HashMap<>();
    public static final HashMap<ISubtype, RegistryObject<Block>> SUBTYPEBLOCKREGISTER_MAPPINGS = new HashMap<>();
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, References.ID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, References.ID);
    public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, References.ID);
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, References.ID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, References.ID);
    public static BlockMissileSilo blockMissileSilo = new BlockMissileSilo();

    static {
	for (SubtypeBlast subtype : SubtypeBlast.values()) {
	    SUBTYPEBLOCKREGISTER_MAPPINGS.put(subtype, BLOCKS.register(subtype.tag(), supplier(new BlockExplosive(subtype), subtype)));
	    ITEMS.register(subtype.tag(), supplier(
		    new BlockItemDescriptable(SUBTYPEBLOCK_MAPPINGS.get(subtype), new Item.Properties().tab(References.BALLISTIXTAB)), subtype));
	}
	for (SubtypeBlast subtype : SubtypeBlast.values()) {
	    if (subtype.hasGrenade) {
		ItemGrenade grenade = new ItemGrenade(subtype);
		ITEMS.register("grenade" + subtype.tag(), supplier(grenade, subtype));
		SUBTYPEITEM_MAPPINGS.put(subtype, grenade);
	    }
	}
	BLOCKS.register("missilesilo", supplier(blockMissileSilo));
	ITEMS.register("missilesilo", supplier(new BlockItemDescriptable(blockMissileSilo, new Item.Properties().tab(References.BALLISTIXTAB))));

    }
    public static final RegistryObject<Item> ITEM_DUSTPOISON = ITEMS.register("dustpoison",
	    supplier(new Item(new Item.Properties().tab(References.BALLISTIXTAB))));
    public static final RegistryObject<Item> ITEM_MISSILECLOSERANGE = ITEMS.register("missilecloserange",
	    supplier(new Item(new Item.Properties().tab(References.BALLISTIXTAB))));
    public static final RegistryObject<Item> ITEM_MISSILEMEDIUMRANGE = ITEMS.register("missilemediumrange",
	    supplier(new Item(new Item.Properties().tab(References.BALLISTIXTAB))));
    public static final RegistryObject<Item> ITEM_MISSILELONGRANGE = ITEMS.register("missilelongrange",
	    supplier(new Item(new Item.Properties().tab(References.BALLISTIXTAB))));
    public static final RegistryObject<Item> ITEM_ROCKETLAUNCHER = ITEMS.register("rocketlauncher", supplier(new ItemRocketLauncher()));
    public static final RegistryObject<Item> ITEM_RADARGUN = ITEMS.register("radargun", supplier(new ItemRadarGun()));
    public static final RegistryObject<Item> ITEM_LASERDESIGNATOR = ITEMS.register("laserdesignator", supplier(new ItemLaserDesignator()));
    public static final RegistryObject<BlockEntityType<TileMissileSilo>> TILE_MISSILESILO = TILES.register("missilesilo",
	    () -> new BlockEntityType<>(TileMissileSilo::new, Sets.newHashSet(blockMissileSilo), null));
    public static final RegistryObject<MenuType<ContainerMissileSilo>> CONTAINER_MISSILESILO = CONTAINERS.register("missilesilo",
	    () -> new MenuType<>(ContainerMissileSilo::new));
    public static final RegistryObject<EntityType<EntityExplosive>> ENTITY_EXPLOSIVE = ENTITIES.register("explosive", () -> EntityType.Builder
	    .<EntityExplosive>of(EntityExplosive::new, MobCategory.MISC).fireImmune().sized(1, 1).build(References.ID + ".explosive"));
    public static final RegistryObject<EntityType<EntityGrenade>> ENTITY_GRENADE = ENTITIES.register("grenade", () -> EntityType.Builder
	    .<EntityGrenade>of(EntityGrenade::new, MobCategory.MISC).fireImmune().sized(0.25f, 0.25f).build(References.ID + ".grenade"));
    public static final RegistryObject<EntityType<EntityBlast>> ENTITY_BLAST = ENTITIES.register("blast",
	    () -> EntityType.Builder.<EntityBlast>of(EntityBlast::new, MobCategory.MISC).fireImmune().build(References.ID + ".blast"));
    public static final RegistryObject<EntityType<EntityShrapnel>> ENTITY_SHRAPNEL = ENTITIES.register("shrapnel", () -> EntityType.Builder
	    .<EntityShrapnel>of(EntityShrapnel::new, MobCategory.MISC).fireImmune().sized(0.5f, 0.5f).build(References.ID + ".shrapnel"));
    public static final RegistryObject<EntityType<EntityMissile>> ENTITY_MISSILE = ENTITIES.register("missile", () -> EntityType.Builder
	    .<EntityMissile>of(EntityMissile::new, MobCategory.MISC).fireImmune().sized(0.5f, 0.5f).build(References.ID + ".missile"));

    private static <T extends IForgeRegistryEntry<T>> Supplier<? extends T> supplier(T entry) {
	return () -> entry;
    }

    private static <T extends IForgeRegistryEntry<T>> Supplier<? extends T> supplier(T entry, ISubtype en) {
	if (entry instanceof Block) {
	    SUBTYPEBLOCK_MAPPINGS.put(en, (Block) entry);
	} else if (entry instanceof Item) {
	    SUBTYPEITEM_MAPPINGS.put(en, (Item) entry);
	}
	return supplier(entry);
    }
}
