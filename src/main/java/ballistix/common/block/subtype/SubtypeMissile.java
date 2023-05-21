package ballistix.common.block.subtype;

import electrodynamics.api.ISubtype;

public enum SubtypeMissile implements ISubtype {

	closerange(3000), mediumrange(10000), longrange(-1);
	
	public final int range;
	
	private SubtypeMissile(int range) {
		this.range = range;
	}

	@Override
	public String forgeTag() {
		return "missile/" + name();
	}

	@Override
	public boolean isItem() {
		return true;
	}

	@Override
	public String tag() {
		return "missile" + name();
	}
	
}
