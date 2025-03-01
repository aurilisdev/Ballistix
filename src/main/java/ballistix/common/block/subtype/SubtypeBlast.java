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
import electrodynamics.prefab.utilities.object.FunctionalInterfaces.QuadFunction;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public enum SubtypeBlast implements ISubtype {
	//Tier 0
	obsidian(BlastObsidian::new, 120),
	//Tier 1
	condensive(BlastCondensive::new, 30),
	attractive(BlastAttractive::new, 30),
	repulsive(BlastRepulsive::new, 30),
	incendiary(BlastIncendiary::new, 80),
	shrapnel(BlastShrapnel::new, 40),
	chemical(BlastChemical::new, 100),
	//Tier 2
	fragmentation(BlastFragmentation::new, 100),
	contagious(BlastContagious::new, 100),
	breaching(BlastBreaching::new, 5),
	thermobaric(BlastThermobaric::new, 100),
	debilitation(BlastDebilitation::new, 80),
	//Tier 3
	emp(BlastEMP::new, 80),
	nuclear(BlastNuclear::new, 200),
	//Tier 4
	antimatter(BlastAntimatter::new, 400),
	largeantimatter(BlastLargeAntimatter::new, 600),
	darkmatter(BlastDarkmatter::new, 400),
    landmine(BlastLandmine::new, 5, (a, b, c, d) -> VoxelShapes.create(new AxisAlignedBB(0, 0, 0, 1, 3 / 16.0, 1)));
		
	public final Blast.BlastFactory<?> factory;
	public final int fuse;
	public final QuadFunction<VoxelShape, BlockState, IBlockReader, BlockPos, ISelectionContext> shape;

	SubtypeBlast(Blast.BlastFactory<?> factory, int fuse, QuadFunction<VoxelShape, BlockState, IBlockReader, BlockPos, ISelectionContext> shape) {
		this.factory = factory;
		this.fuse = fuse;
		this.shape = shape;
	}

	SubtypeBlast(Blast.BlastFactory<?> factory, int fuse) {
		this(factory, fuse, (a, b, c, d) -> VoxelShapes.block());
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

	public Blast createBlast(World world, BlockPos pos) {
		return factory.create(world, pos);
	}
}