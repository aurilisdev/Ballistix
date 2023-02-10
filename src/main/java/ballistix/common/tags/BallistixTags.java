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

		private static void init() {
		}

		private static TagKey<Item> forgeTag(String name) {
			return ItemTags.create(new ResourceLocation("forge", name));
		}

	}

}
