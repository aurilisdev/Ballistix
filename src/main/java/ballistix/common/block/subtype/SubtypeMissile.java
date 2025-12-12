package ballistix.common.block.subtype;

import ballistix.common.settings.BallistixConfig;
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
	    return BallistixConfig.INSTANCE.LAUNCHER_PLATFORM_RANGE_T1.getAsInt();
	case 2:
	    return BallistixConfig.INSTANCE.LAUNCHER_PLATFORM_RANGE_T2.getAsInt();
	case 3:
	    return BallistixConfig.INSTANCE.LAUNCHER_PLATFORM_RANGE_T3.getAsInt();
	case -1:
	    return BallistixConfig.INSTANCE.LAUNCHER_PLATFORM_RANGE_T3.getAsInt();
	}
	return BallistixConfig.INSTANCE.LAUNCHER_PLATFORM_RANGE_T3.getAsInt();
    }

}
