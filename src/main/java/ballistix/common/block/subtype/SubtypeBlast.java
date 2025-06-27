package ballistix.common.block.subtype;

import java.util.Locale;
import java.util.function.Supplier;

import ballistix.Ballistix;
import ballistix.api.blast.IBlast;
import ballistix.common.blast.tier1.BlastAnvil;
import ballistix.common.blast.tier1.BlastAttractive;
import ballistix.common.blast.tier1.BlastChemical;
import ballistix.common.blast.tier1.BlastCondensive;
import ballistix.common.blast.tier1.BlastIncendiary;
import ballistix.common.blast.tier1.BlastRepulsive;
import ballistix.common.blast.tier1.BlastShrapnel;
import ballistix.common.blast.tier2.BlastBreaching;
import ballistix.common.blast.tier2.BlastContagious;
import ballistix.common.blast.tier2.BlastDebilitation;
import ballistix.common.blast.tier2.BlastFragmentation;
import ballistix.common.blast.tier2.BlastSonic;
import ballistix.common.blast.tier2.BlastThermobaric;
import ballistix.common.blast.tier3.BlastAntimatter;
import ballistix.common.blast.tier3.BlastDarkmatter;
import ballistix.common.blast.tier3.BlastEMP;
import ballistix.common.blast.tier3.BlastEnder;
import ballistix.common.blast.tier3.BlastEndothermic;
import ballistix.common.blast.tier3.BlastHypersonic;
import ballistix.common.blast.tier3.BlastLargeAntimatter;
import ballistix.common.blast.tier3.BlastNuclear;
import ballistix.common.blast.tier3.BlastRejuvination;
import ballistix.common.blast.tierother.BlastLandmine;
import ballistix.common.blast.tierother.BlastObsidian;
import ballistix.common.blast.util.Blast;
import ballistix.common.block.BlockExplosive;
import ballistix.registers.BallistixBlocks;
import ballistix.registers.BallistixItems;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import voltaic.api.ISubtype;
import voltaic.common.block.voxelshapes.VoxelShapeProvider;

public enum SubtypeBlast implements ISubtype, IBlast {

	//Tier 0
    obsidian(BlastObsidian::new, 120, 1),
    //Tier 1
    condensive(BlastCondensive::new, 30, 1),
    attractive(BlastAttractive::new, 30, 1),
    repulsive(BlastRepulsive::new, 30, 1),
    incendiary(BlastIncendiary::new, 80, 1),
    shrapnel(BlastShrapnel::new, 40, 1),
    chemical(BlastChemical::new, 100, 1),
    anvil(BlastAnvil::new, 100, 1),
    //Tier 2
    fragmentation(BlastFragmentation::new, 100, 2),
    contagious(BlastContagious::new, 100, 2),
    breaching(BlastBreaching::new, 5, 2),
    thermobaric(BlastThermobaric::new, 100, 2),
    debilitation(BlastDebilitation::new, 80, 2),
    sonic(BlastSonic::new, 80, 2),
    //Tier 3
    emp(BlastEMP::new, 80, 3),
    nuclear(BlastNuclear::new, 200, 3),
    endothermic(BlastEndothermic::new, 80, 3),
    ender(BlastEnder::new, 100, 3),
    hypersonic(BlastHypersonic::new, 150, 3),
    rejuvination(BlastRejuvination::new, 400, 3),
    antimatter(BlastAntimatter::new, 400, 3),
    largeantimatter(BlastLargeAntimatter::new, 600, 3),
    darkmatter(BlastDarkmatter::new, 400, 3),
    //Other
    landmine(BlastLandmine::new, 5, VoxelShapeProvider.createOmni(Shapes.create(0, 0, 0, 16.0 / 16.0, 3.0 / 16.0, 16.0 / 16.0)), -1);

    private final Blast.BlastFactory<?> factory;
    private final int fuse;
    private final VoxelShapeProvider shape;
    private final int tier;

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

    @Override
    public int fuse() {
        return fuse;
    }

    @Override
    public Blast createBlast(Level world, BlockPos pos) {
        return factory.create(world, pos);
    }

    @Override
    public int tier() {
        return tier;
    }

    @Override
    public VoxelShapeProvider getShape() {
        return shape;
    }

    @Override
    public ResourceLocation id() {
        return Ballistix.rl(name().toLowerCase(Locale.ROOT));
    }

	@Override
	public Supplier<Item> getExplosiveItem() {
		return () -> BallistixItems.ITEMS_EXPLOSIVE.getValue(this);
	}

    @Override
    public Supplier<Block> getExplosiveBlock() {
        return () -> BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(this);
    }

    @Override
    public void onEntityInside(BlockState state, Level level, BlockPos pos, Entity ent) {
        if (this == SubtypeBlast.landmine) {
            BlockExplosive.explode(level, pos, this);
            level.removeBlock(pos, false);
        }
	}
}
