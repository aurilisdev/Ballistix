package ballistix.common.block.subtype;

import ballistix.common.blast.Blast;
import ballistix.common.blast.BlastAntimatter;
import ballistix.common.blast.BlastAttractive;
import ballistix.common.blast.BlastBreaching;
import ballistix.common.blast.BlastChemical;
import ballistix.common.blast.BlastCondensive;
import ballistix.common.blast.BlastContagious;
import ballistix.common.blast.BlastDarkmatter;
import ballistix.common.blast.BlastDebilitation;
import ballistix.common.blast.BlastEMP;
import ballistix.common.blast.BlastFragmentation;
import ballistix.common.blast.BlastIncendiary;
import ballistix.common.blast.BlastLandmine;
import ballistix.common.blast.BlastLargeAntimatter;
import ballistix.common.blast.BlastNuclear;
import ballistix.common.blast.BlastObsidian;
import ballistix.common.blast.BlastRepulsive;
import ballistix.common.blast.BlastShrapnel;
import ballistix.common.blast.BlastThermobaric;
import electrodynamics.api.ISubtype;
import electrodynamics.common.block.voxelshapes.VoxelShapeProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.Shapes;

public enum SubtypeBlast implements ISubtype {

	//Tier 0
	obsidian(BlastObsidian::new, 120, 1),
	//Tier 1
	condensive(BlastCondensive::new, 30, 1),
	attractive(BlastAttractive::new, 30, 1),
	repulsive(BlastRepulsive::new, 30, 1),
	incendiary(BlastIncendiary::new, 80, 1),
	shrapnel(BlastShrapnel::new, 40, 1),
	chemical(BlastChemical::new, 100, 1),
	//Tier 2
	fragmentation(BlastFragmentation::new, 100, 2),
	contagious(BlastContagious::new, 100, 2),
	breaching(BlastBreaching::new, 5, 2),
	thermobaric(BlastThermobaric::new, 100, 2),
	debilitation(BlastDebilitation::new, 80, 2),
	//Tier 3
	emp(BlastEMP::new, 80, 3),
	nuclear(BlastNuclear::new, 200, 3),
	//Tier 4
	antimatter(BlastAntimatter::new, 400, 3),
	largeantimatter(BlastLargeAntimatter::new, 600, 3),
	darkmatter(BlastDarkmatter::new, 400, 3),
	//Other
	landmine(BlastLandmine::new, 5, VoxelShapeProvider.createOmni(Shapes.create(0, 0, 0, 16.0 / 16.0, 3.0 / 16.0, 16.0 / 16.0)), -1);

	public final Blast.BlastFactory<?> factory;
	public final int fuse;
	public final VoxelShapeProvider shape;
	public final int tier;

	SubtypeBlast(Blast.BlastFactory<?> factory, int fuse, VoxelShapeProvider shape, int tier) {
		this.factory = factory;
		this.fuse = fuse;
		this.shape = shape;
		this.tier = tier;
	}

	SubtypeBlast(Blast.BlastFactory<?> factory, int fuse, int tier) {
		this(factory, fuse, VoxelShapeProvider.DEFAULT, tier);
	}

	@Override
	public String forgeTag() {
		return tag();
	}

	@Override
	public boolean isItem() {
		return true;
	}

	@Override
	public String tag() {
		return name();
	}

	public Blast createBlast(Level world, BlockPos pos) {
		return factory.create(world, pos);
	}
}
