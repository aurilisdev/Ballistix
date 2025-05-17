package ballistix.common.block.subtype;


import voltaic.api.ISubtype;

public enum SubtypeMissile implements ISubtype {

	tier1(0), tier2(1), tier3(2);

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
