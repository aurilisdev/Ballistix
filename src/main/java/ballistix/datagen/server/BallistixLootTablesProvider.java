package ballistix.datagen.server;

import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.registers.BallistixBlockTypes;
import ballistix.registers.BallistixBlocks;
import electrodynamics.datagen.server.ElectrodynamicsLootTablesProvider;
import net.minecraft.data.DataGenerator;

public class BallistixLootTablesProvider extends ElectrodynamicsLootTablesProvider {

	public BallistixLootTablesProvider(DataGenerator generator) {
		super(generator);
	}

	@Override
	protected void addTables() {

		for (SubtypeBlast blast : SubtypeBlast.values()) {
			addSimpleBlock(BallistixBlocks.getBlock(blast));
		}

		addITable(BallistixBlocks.blockMissileSilo, BallistixBlockTypes.TILE_MISSILESILO);
		addSimpleBlock(BallistixBlocks.blockRadar);
		addSimpleBlock(BallistixBlocks.blockFireControlRadar);
		addSimpleBlock(BallistixBlocks.blockEsmTower);
		addSimpleBlock(BallistixBlocks.blockSamTurret);
		addSimpleBlock(BallistixBlocks.blockCiwsTurret);
		addSimpleBlock(BallistixBlocks.blockLaserTurret);
		addSimpleBlock(BallistixBlocks.blockRailgunTurret);

	}

}
