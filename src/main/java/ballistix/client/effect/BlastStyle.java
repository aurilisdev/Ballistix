package ballistix.client.effect;

import javax.annotation.Nullable;

import ballistix.common.block.subtype.SubtypeBlast;

/** Per-blast tuning of the visual sequence. Only the blasts listed here have one. */
public enum BlastStyle {
    CONDENSIVE(new Extent(2.5, 3.5f, 19), new Palette(0xFFFFA047, 0xFF5C4A43, 0xFFC1A991), new Timing(14, 0.65f, 0, 0),
	    ScreenFlash.NONE, 0.4f),
    BREACHING(new Extent(5, 5.5f, 29), new Palette(0xFFFFC77E, 0xFF554842, 0xFFC3AE98), new Timing(20, 1f, 0, 0),
	    ScreenFlash.NONE, 1f),
    OBSIDIAN(new Extent(10, 7f, 42), new Palette(0xFFFFC469, 0xFF51444A, 0xFFAA9D99), new Timing(20, 1f, 0, 0),
	    ScreenFlash.NONE, 1f),
    THERMOBARIC(new Extent(30, 14f, 96), new Palette(0xFFFFBB50, 0xFF603A2D, 0xFFB69B82), new Timing(20, 1f, 0, 260),
	    ScreenFlash.NONE, 1f),
    NUCLEAR(new Extent(45, 27f, 155), new Palette(0xFFFFD26F, 0xFF807971, 0xFF918980), new Timing(48, 2.5f, 660, 1800),
	    new ScreenFlash(100, 10), 1f),
    ANTIMATTER(new Extent(45, 22f, 118), new Palette(0xFFA6EBFF, 0xFF50677C, 0xFFA9BFCC),
	    new Timing(40, 2.25f, 280, 1500), new ScreenFlash(150, 15), 1f),
    LARGE_ANTIMATTER(new Extent(100, 35f, 216), new Palette(0xFF6BAEFF, 0xFF344C69, 0xFF8EAAC6),
	    new Timing(56, 2.5f, 500, 2200), new ScreenFlash(200, 20), 1f);

    /** Sizes at the reference radius; the sequence scales them to the configured radius. */
    public record Extent(double referenceRadius, float core, float frontRange) {
    }

    public record Palette(int heat, int smoke, int dust) {
    }

    /** Lengths in ticks at the reference radius. Zero aftermath or crater ticks mean the phase is absent. */
    public record Timing(int fireballTicks, float lifetimeScale, int aftermathTicks, int craterLingerTicks) {
    }

    public record ScreenFlash(int ticks, int holdTicks) {
	public static final ScreenFlash NONE = new ScreenFlash(0, 0);

	public boolean isNone() {
	    return ticks == 0;
	}
    }

    public final Extent extent;
    public final Palette palette;
    public final Timing timing;
    public final ScreenFlash screenFlash;
    public final float particleDensity;

    BlastStyle(Extent extent, Palette palette, Timing timing, ScreenFlash screenFlash, float particleDensity) {
	this.extent = extent;
	this.palette = palette;
	this.timing = timing;
	this.screenFlash = screenFlash;
	this.particleDensity = particleDensity;
    }

    public boolean isAntimatter() {
	return this == ANTIMATTER || this == LARGE_ANTIMATTER;
    }

    /** Nuclear and antimatter blasts keep a cloud going afterwards, and the server reports the crater they carve. */
    public boolean hasAftermath() {
	return timing.aftermathTicks() > 0;
    }

    @Nullable
    public static BlastStyle of(SubtypeBlast blast) {
	return switch (blast) {
	case condensive -> CONDENSIVE;
	case breaching -> BREACHING;
	case obsidian -> OBSIDIAN;
	case thermobaric -> THERMOBARIC;
	case nuclear -> NUCLEAR;
	case antimatter -> ANTIMATTER;
	case largeantimatter -> LARGE_ANTIMATTER;
	default -> null;
	};
    }
}
