package ballistix.common.tags;

import net.minecraft.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags.IOptionalNamedTag;

public class BallistixTags {

	public static void init() {
		Items.init();
	}

	public static class Items {

		public static final IOptionalNamedTag<Item> DUST_POISON = forgeTag("dusts/poison");

		private static void init() {
		}

		private static IOptionalNamedTag<Item> forgeTag(String name) {
			return ItemTags.createOptional(new ResourceLocation("forge", name));
		}

	}

}