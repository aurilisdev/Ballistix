package ballistix.common.block;

import ballistix.common.blast.BlastAttractive;
import ballistix.common.blast.BlastBreaching;
import ballistix.common.blast.BlastCondensive;
import ballistix.common.blast.BlastIncendiary;
import ballistix.common.blast.BlastObsidian;
import ballistix.common.blast.BlastRepulsive;
import electrodynamics.api.subtype.Subtype;

public enum SubtypeExplosive implements Subtype {
	obsidian(BlastObsidian.class, 120, false), condensive(BlastCondensive.class, 30, true), attractive(BlastAttractive.class, 30, true), repulsive(BlastRepulsive.class, 30, true),
	incendiary(BlastIncendiary.class, 80, true), breaching(BlastBreaching.class, 5, false);
	public final Class<?> blastClass;
	public final int fuse;
	public final boolean hasGrenade;

	private SubtypeExplosive(Class<?> blastClass, int fuse, boolean hasGrenade) {
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
