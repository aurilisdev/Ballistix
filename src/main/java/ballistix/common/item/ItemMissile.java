package ballistix.common.item;

import ballistix.References;
import ballistix.common.block.subtype.SubtypeMissile;
import net.minecraft.world.item.Item;

public class ItemMissile extends Item {

	public final SubtypeMissile missile;

	public ItemMissile(SubtypeMissile missile) {
		super(new Item.Properties().tab(References.BALLISTIXTAB));
		this.missile = missile;
	}

}
