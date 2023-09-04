package ballistix.common.item;

import ballistix.common.block.subtype.SubtypeMissile;
import ballistix.registers.BallistixCreativeTabs;
import electrodynamics.common.item.ItemElectrodynamics;
import net.minecraft.world.item.Item;

public class ItemMissile extends ItemElectrodynamics {

	public final SubtypeMissile missile;

	public ItemMissile(SubtypeMissile missile) {
		super(new Item.Properties(), () -> BallistixCreativeTabs.MAIN.get());
		this.missile = missile;
	}

}
