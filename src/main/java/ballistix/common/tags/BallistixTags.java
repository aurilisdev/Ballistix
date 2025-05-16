package ballistix.common.tags;

import net.minecraft.item.Item;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.tags.ItemTags;
import voltaic.Voltaic;

public class BallistixTags {

	public static void init() {
		Items.init();
	}

	public static class Items {

		public static final INamedTag<Item> DUST_POISON = forgeTag("dusts/poison");
		public static final INamedTag<Item> CELL_ANTIMATTER_LARGE = forgeTag("cells/anti_matter_large");
		public static final INamedTag<Item> CELL_ANTIMATTER_VERY_LARGE = forgeTag("cells/anti_matter_very_large");
		public static final INamedTag<Item> CELL_DARK_MATTER = forgeTag("cells/dark_matter");
		public static final INamedTag<Item> FUELROD_URANIUM_HIGH_EN = forgeTag("fuel_rods/heuo2");

		private static void init() {
		}

		private static INamedTag<Item> forgeTag(String name) {
			return ItemTags.createOptional(Voltaic.forgerl(name));
		}

	}

}
