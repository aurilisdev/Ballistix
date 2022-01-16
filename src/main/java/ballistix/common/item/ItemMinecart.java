package ballistix.common.item;

import ballistix.References;
import ballistix.common.block.subtype.SubtypeBlast;
import net.minecraft.world.item.Item;

public class ItemMinecart extends Item {
	private SubtypeBlast explosive;

	public ItemMinecart(SubtypeBlast explosive) {
		super(new Item.Properties().tab(References.BALLISTIXTAB).stacksTo(1));
		this.explosive = explosive;
	}

	public SubtypeBlast getExplosive() {
		return explosive;
	}
}
