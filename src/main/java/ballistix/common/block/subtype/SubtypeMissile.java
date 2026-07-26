package ballistix.common.block.subtype;


import ballistix.common.settings.BallistixConstants;
import voltaic.api.ISubtype;

public enum SubtypeMissile implements ISubtype {

	tier1(1), tier2(2), tier3(3), cluster(3), clustershard(-1);

	private int tier;

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
	
	public int tier() {
		return tier;
	}
	    public int range() {
		switch (tier) {
		case 1:
		    return BallistixConstants.LAUNCHER_PLATFORM_RANGE_T1;
		case 2:
		    return BallistixConstants.LAUNCHER_PLATFORM_RANGE_T2;
		case 3:
		    return BallistixConstants.LAUNCHER_PLATFORM_RANGE_T3;
		case -1:
		    return BallistixConstants.LAUNCHER_PLATFORM_RANGE_T3;
		}
		return BallistixConstants.LAUNCHER_PLATFORM_RANGE_T3;
	    }
}
