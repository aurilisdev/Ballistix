package ballistix.common.block.subtype;

import electrodynamics.api.ISubtype;

public enum SubtypeMissile implements ISubtype {

	closerange(1), mediumrange(2), longrange(3);

	public int tier;

	SubtypeMissile(int tier) {
		this.tier = tier;
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
