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
import ballistix.common.entity.EntityShrapnel;
import ballistix.common.item.ItemGrenade;
import ballistix.common.tile.TileMissileSilo;
import electrodynamics.api.subtype.Subtype;
import electrodynamics.common.blockitem.BlockItemDescriptable;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class DeferredRegisters {
	public static final HashMap<Subtype, Item> SUBTYPEITEM_MAPPINGS = new HashMap<>();
	public static final HashMap<Subtype, Block> SUBTYPEBLOCK_MAPPINGS = new HashMap<>();
	public static final HashMap<Subtype, RegistryObject<Block>> SUBTYPEBLOCKREGISTER_MAPPINGS = new HashMap<>();
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, References.ID);
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, References.ID);
	public static final DeferredRegister<TileEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, References.ID);
	public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, References.ID);
	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, References.ID);
	public static BlockMissileSilo blockMissileSilo;

	static {
		for (SubtypeBlast subtype : SubtypeBlast.values()) {
			SUBTYPEBLOCKREGISTER_MAPPINGS.put(subtype, BLOCKS.register(subtype.tag(), supplier(new BlockExplosive(subtype), subtype)));
			ITEMS.register(subtype.tag(), supplier(new BlockItemDescriptable(SUBTYPEBLOCK_MAPPINGS.get(subtype), new Item.Properties().group(References.BALLISTIXTAB)), subtype));
		}
		for (SubtypeBlast subtype : SubtypeBlast.values()) {
			if (subtype.hasGrenade) {
				ItemGrenade grenade = new ItemGrenade(subtype);
				ITEMS.register("grenade" + subtype.tag(), supplier(grenade, subtype));
				SUBTYPEITEM_MAPPINGS.put(subtype, grenade);
			}
		}
		BLOCKS.register("missilesilo", supplier(blockMissileSilo = new BlockMissileSilo()));
		ITEMS.register("missilesilo", supplier(new BlockItemDescriptable(blockMissileSilo, new Item.Properties().group(References.BALLISTIXTAB))));

	}
	public static final RegistryObject<Item> ITEM_DUSTPOISON = ITEMS.register("dustpoison", supplier(new Item(new Item.Properties().group(References.BALLISTIXTAB))));
	public static final RegistryObject<TileEntityType<TileMissileSilo>> TILE_MISSILESILO = TILES.register("missilesilo",
			() -> new TileEntityType<>(TileMissileSilo::new, Sets.newHashSet(blockMissileSilo), null));
	public static final RegistryObject<EntityType<EntityExplosive>> ENTITY_EXPLOSIVE = ENTITIES.register("explosive",
			() -> EntityType.Builder.<EntityExplosive>create(EntityExplosive::new, EntityClassification.MISC).immuneToFire().size(1, 1).build(References.ID + ".explosive"));
	public static final RegistryObject<EntityType<EntityGrenade>> ENTITY_GRENADE = ENTITIES.register("grenade",
			() -> EntityType.Builder.<EntityGrenade>create(EntityGrenade::new, EntityClassification.MISC).immuneToFire().size(0.25f, 0.25f).build(References.ID + ".grenade"));
	public static final RegistryObject<EntityType<EntityBlast>> ENTITY_BLAST = ENTITIES.register("blast",
			() -> EntityType.Builder.<EntityBlast>create(EntityBlast::new, EntityClassification.MISC).immuneToFire().build(References.ID + ".blast"));
	public static final RegistryObject<EntityType<EntityShrapnel>> ENTITY_SHRAPNEL = ENTITIES.register("shrapnel",
			() -> EntityType.Builder.<EntityShrapnel>create(EntityShrapnel::new, EntityClassification.MISC).immuneToFire().size(0.5f, 0.5f).build(References.ID + ".shrapnel"));

	private static <T extends IForgeRegistryEntry<T>> Supplier<? extends T> supplier(T entry) {
		return () -> entry;
	}

	private static <T extends IForgeRegistryEntry<T>> Supplier<? extends T> supplier(T entry, Subtype en) {
		if (entry instanceof Block) {
			SUBTYPEBLOCK_MAPPINGS.put(en, (Block) entry);
		} else if (entry instanceof Item) {
			SUBTYPEITEM_MAPPINGS.put(en, (Item) entry);
		}
		return supplier(entry);
	}
}
