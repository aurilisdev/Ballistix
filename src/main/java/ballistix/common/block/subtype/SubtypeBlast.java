package ballistix.common.block.subtype;

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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public enum SubtypeBlast implements ISubtype {
	obsidian(BlastObsidian.class, 120, false, true),
	condensive(BlastCondensive.class, 30, true, true),
	attractive(BlastAttractive.class, 30, true, true),
	repulsive(BlastRepulsive.class, 30, true, true),
	incendiary(BlastIncendiary.class, 80, true, true),
	shrapnel(BlastShrapnel.class, 40, true, true),
	debilitation(BlastDebilitation.class, 80, true, true),
	chemical(BlastChemical.class, 100, true, true),
	emp(BlastEMP.class, 80, false, true),
	breaching(BlastBreaching.class, 5, false, true),
	thermobaric(BlastThermobaric.class, 100, false, true),
	contagious(BlastContagious.class, 100, false, true),
	fragmentation(BlastFragmentation.class, 100, false, true),
	landmine(BlastLandmine.class, 5, false, false, (a, b, c, d) -> VoxelShapes.box(0, 0, 0, 1, 3 / 16.0, 1)),
	nuclear(BlastNuclear.class, 200, false, true),
	antimatter(BlastAntimatter.class, 400, false, true),
	largeantimatter(BlastLargeAntimatter.class, 600, false, true),
	darkmatter(BlastDarkmatter.class, 400, false, true);

	public final Class<?> blastClass;
	public final int fuse;
	public final boolean hasGrenade;
	public final boolean hasMinecart;
	public final QuadFunction<VoxelShape, BlockState, IBlockReader, BlockPos, ISelectionContext> shape;

	SubtypeBlast(Class<?> blastClass, int fuse, boolean hasGrenade, boolean hasMinecart, QuadFunction<VoxelShape, BlockState, IBlockReader, BlockPos, ISelectionContext> shape) {
		this.blastClass = blastClass;
		this.fuse = fuse;
		this.hasGrenade = hasGrenade;
		this.shape = shape;
		this.hasMinecart = hasMinecart;
	}

	SubtypeBlast(Class<?> blastClass, int fuse, boolean hasGrenade, boolean hasMinecart) {
		this(blastClass, fuse, hasGrenade, hasMinecart, (a, b, c, d) -> VoxelShapes.block());
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
}