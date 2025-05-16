package ballistix.common.item;

import ballistix.common.block.subtype.SubtypeMissile;
import ballistix.registers.BallistixCreativeTabs;
import net.minecraft.item.Item;
import voltaic.common.item.ItemVoltaic;

public class ItemMissile extends ItemVoltaic {

	public final SubtypeMissile missile;

	public ItemMissile(SubtypeMissile missile) {
		super(new Item.Properties(), () -> BallistixCreativeTabs.MAIN);
		this.missile = missile;
	}
}
