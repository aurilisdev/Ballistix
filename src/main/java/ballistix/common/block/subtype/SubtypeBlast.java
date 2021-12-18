package ballistix.common.block.subtype;

import ballistix.common.blast.BlastAntimatter;
import ballistix.common.blast.BlastAttractive;
import ballistix.common.blast.BlastBreaching;
import ballistix.common.blast.BlastChemical;
import ballistix.common.blast.BlastCondensive;
import ballistix.common.blast.BlastContagious;
import ballistix.common.blast.BlastDarkmatter;
import ballistix.common.blast.BlastDebilitation;
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
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public enum SubtypeBlast implements ISubtype {
	obsidian(BlastObsidian.class, 120, false), condensive(BlastCondensive.class, 30, true), attractive(BlastAttractive.class, 30, true),
	repulsive(BlastRepulsive.class, 30, true), incendiary(BlastIncendiary.class, 80, true), shrapnel(BlastShrapnel.class, 40, true),
	debilitation(BlastDebilitation.class, 80, true), chemical(BlastChemical.class, 100, true), breaching(BlastBreaching.class, 5, false),
	thermobaric(BlastThermobaric.class, 100, false), contagious(BlastContagious.class, 100, false),
	fragmentation(BlastFragmentation.class, 100, false),
	landmine(BlastLandmine.class, 5, false, (a, b, c, d) -> Shapes.create(0, 0, 0, 1, 3 / 16.0, 1)), nuclear(BlastNuclear.class, 200, false),
	antimatter(BlastAntimatter.class, 400, false), largeantimatter(BlastLargeAntimatter.class, 600, false),
	darkmatter(BlastDarkmatter.class, 400, false);

	public final Class<?> blastClass;
	public final int fuse;
	public final boolean hasGrenade;
	public final QuadFunction<VoxelShape, BlockState, BlockGetter, BlockPos, CollisionContext> shape;

	SubtypeBlast(Class<?> blastClass, int fuse, boolean hasGrenade,
			QuadFunction<VoxelShape, BlockState, BlockGetter, BlockPos, CollisionContext> shape) {
		this.blastClass = blastClass;
		this.fuse = fuse;
		this.hasGrenade = hasGrenade;
		this.shape = shape;
	}

	SubtypeBlast(Class<?> blastClass, int fuse, boolean hasGrenade) {
		this(blastClass, fuse, hasGrenade, (a, b, c, d) -> Shapes.block());
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
