package ballistix.datagen.server;

import java.util.List;

import ballistix.References;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.registers.BallistixBlockTypes;
import ballistix.registers.BallistixBlocks;
import electrodynamics.datagen.server.ElectrodynamicsLootTablesProvider;
import net.minecraft.world.level.block.Block;

public class BallistixLootTablesProvider extends ElectrodynamicsLootTablesProvider {

	public BallistixLootTablesProvider() {
		super(References.ID);
	}

	@Override
	protected void generate() {

		for (SubtypeBlast blast : SubtypeBlast.values()) {
			addSimpleBlock(BallistixBlocks.getBlock(blast));
		}

		addMachineTable(BallistixBlocks.blockMissileSilo, BallistixBlockTypes.TILE_MISSILESILO, true, false, false, false, false);

	}

	@Override
	public List<Block> getExcludedBlocks() {
		return List.of(BallistixBlocks.blockRadar);
	}

}
