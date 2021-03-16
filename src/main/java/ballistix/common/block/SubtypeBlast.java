package ballistix.common.block;

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
import ballistix.common.blast.BlastNuclear;
import ballistix.common.blast.BlastObsidian;
import ballistix.common.blast.BlastRepulsive;
import ballistix.common.blast.BlastShrapnel;
import ballistix.common.blast.BlastThermobaric;
import electrodynamics.api.ISubtype;

public enum SubtypeBlast implements ISubtype {
    obsidian(BlastObsidian.class, 120, false), condensive(BlastCondensive.class, 30, true), attractive(BlastAttractive.class, 30, true),
    repulsive(BlastRepulsive.class, 30, true), incendiary(BlastIncendiary.class, 80, true), shrapnel(BlastShrapnel.class, 40, true),
    debilitation(BlastDebilitation.class, 80, true), chemical(BlastChemical.class, 100, true), breaching(BlastBreaching.class, 5, false),
    thermobaric(BlastThermobaric.class, 100, false), contagious(BlastContagious.class, 100, false),
    fragmentation(BlastFragmentation.class, 100, false), nuclear(BlastNuclear.class, 200, false), antimatter(BlastAntimatter.class, 400, false),
    darkmatter(BlastDarkmatter.class, 400, false);

    public final Class<?> blastClass;
    public final int fuse;
    public final boolean hasGrenade;

    private SubtypeBlast(Class<?> blastClass, int fuse, boolean hasGrenade) {
	this.blastClass = blastClass;
	this.fuse = fuse;
	this.hasGrenade = hasGrenade;
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
