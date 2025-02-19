package ballistix.common.tags;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class BallistixTags {

	public static void init() {
		Items.init();
	}

	public static class Items {

		public static final TagKey<Item> DUST_POISON = forgeTag("dusts/poison");
		public static final TagKey<Item> CELL_ANTIMATTER_LARGE = forgeTag("cells/anti_matter_large");
		public static final TagKey<Item> CELL_ANTIMATTER_VERY_LARGE = forgeTag("cells/anti_matter_very_large");
		public static final TagKey<Item> CELL_DARK_MATTER = forgeTag("cells/dark_matter");
		public static final TagKey<Item> FUELROD_URANIUM_HIGH_EN = forgeTag("fuel_rods/heuo2");

		private static void init() {
		}

		private static TagKey<Item> forgeTag(String name) {
			return ItemTags.create(new ResourceLocation("forge", name));
		}

	}

}
