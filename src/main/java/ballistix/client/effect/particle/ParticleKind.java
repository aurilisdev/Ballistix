package ballistix.client.effect.particle;

import java.util.List;
import java.util.stream.IntStream;

import ballistix.Ballistix;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

/** The visual role of a blast particle: which sprite it uses and how it grows, fades and moves. */
public enum ParticleKind {
    SMOKE(1.65f, 0.91f, 0.58f, 0.98f, 0, false),
    DUST(1.9f, 0.79f, 0.37f, 0.95f, 0, false),
    HAZE(1.45f, 0.36f, 0.76f, 0.98f, 0, false),
    FIRE(1.75f, 0.98f, 0.32f, 0.94f, 0, true),
    GLOW(1.6f, 0.96f, 0.08f, 1, 0, true),
    SPARK(0.35f, 1, 0.35f, 0.95f, 0.65f, true),
    RING(3.8f, 0.75f, 0.06f, 1, 0, true);

    private static final ResourceLocation GLOW_SPRITE = Ballistix.rl("blast_glow");
    private static final ResourceLocation RING_SPRITE = Ballistix.rl("blast_ring");
    private static final List<ResourceLocation> SMOKE_SPRITES = IntStream.range(0, 8)
	    .mapToObj(variant -> Ballistix.rl("blastsmoke_" + variant)).toList();

    /** Size multiplier reached at the end of the particle's life. */
    final float growth;
    final float opacity;
    /** Fraction of the lifetime after which the particle starts fading out. */
    final float fadeStart;
    final float drag;
    final float gravity;
    final boolean selfLit;

    ParticleKind(float growth, float opacity, float fadeStart, float drag, float gravity, boolean selfLit) {
	this.growth = growth;
	this.opacity = opacity;
	this.fadeStart = fadeStart;
	this.drag = drag;
	this.gravity = gravity;
	this.selfLit = selfLit;
    }

    boolean tumbles() {
	return this == SMOKE || this == FIRE || this == DUST;
    }

    ResourceLocation pickSprite(RandomSource random) {
	return switch (this) {
	case GLOW, SPARK -> GLOW_SPRITE;
	case RING -> RING_SPRITE;
	case SMOKE, DUST, FIRE, HAZE -> SMOKE_SPRITES.get(random.nextInt(SMOKE_SPRITES.size()));
	};
    }
}
