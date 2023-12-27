package ballistix.common.block.subtype;

import electrodynamics.api.ISubtype;

public enum SubtypeMissile implements ISubtype {

	closerange, mediumrange, longrange;

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