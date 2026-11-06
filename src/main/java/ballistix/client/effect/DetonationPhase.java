package ballistix.client.effect;

import java.util.ArrayList;
import java.util.List;

import ballistix.client.effect.particle.ParticleKind;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

/** The first moments of a blast: the flash, the expanding fireball and the debris thrown out of it. */
final class DetonationPhase extends BlastPhase {

    private static final int WHITE = 0xFFFFF4E4;
    private static final int FIREBALL_START_TICK = 2;
    private static final int FIREBALL_PEAK_TICK = 20;
    private static final int FIREBALL_PEAK_INTERVAL = 2;
    private static final int FIREBALL_TAIL_INTERVAL = 4;
    private static final int FIREBALL_GLOW_TICKS = 8;
    private static final int NUCLEAR_FIREBALL_SHADE = 0xFF4C403B;
    private static final float NUCLEAR_FIREBALL_LIFETIME_SCALE = 3.25f;
    private static final int EJECTA_TICKS = 36;
    private static final int EJECTA_INTERVAL = 2;
    private static final int EJECTA_FIRE_TICKS = 9;
    private static final float SECOND_RING_MIN_DETAIL = 0.5f;
    private static final float SECOND_RING_MIN_CORE = 20;

    private final List<Vec3> ejectaVelocities = new ArrayList<>();

    DetonationPhase(BlastScene scene) {
	super(scene);
	int count = count(12 + (int) style.extent.core());
	for (int i = 0; i < count; i++) {
	    double angle = (i + random.nextDouble()) / count * Mth.TWO_PI;
	    double speed = (0.55 + random.nextDouble() * 0.85) * (0.5 + core * 0.125);
	    ejectaVelocities.add(new Vec3(Math.cos(angle) * speed,
		    (0.28 + random.nextDouble() * 1.18) * (0.45 + core * 0.075), Math.sin(angle) * speed));
	}
    }

    @Override
    void tick(int age) {
	if (age == 0)
	    emitFlash();
	if (isFireballTick(age))
	    emitFireball(age);
	if (age <= EJECTA_TICKS && age % EJECTA_INTERVAL == 0)
	    emitEjecta(age);
    }

    @Override
    int endTick() {
	return Math.max(timeline.fireballTicks(), EJECTA_TICKS);
    }

    private boolean isFireballTick(int age) {
	int interval = age <= FIREBALL_PEAK_TICK ? FIREBALL_PEAK_INTERVAL : FIREBALL_TAIL_INTERVAL;
	return age >= FIREBALL_START_TICK && age <= timeline.fireballTicks() && age % interval == 0;
    }

    private void emitFlash() {
	int heat = palette.heat();
	puff(ParticleKind.GLOW, origin.add(0, core * 0.32, 0), Vec3.ZERO, core * 1.7f, lifetime(17), heat, heat);
	puff(ParticleKind.GLOW, origin.add(0, core * 0.25, 0), Vec3.ZERO, core * 0.98f, lifetime(11), WHITE, heat);
	puff(ParticleKind.GLOW, origin.add(0, core * 0.14, 0), Vec3.ZERO, core * 0.52f, 7, 0xFFFFFFFF, WHITE);
	puff(ParticleKind.RING, origin.add(0, core * 0.12, 0), Vec3.ZERO, core * 0.9f, lifetime(22), WHITE, heat);

	if (detail >= SECOND_RING_MIN_DETAIL && style.extent.core() >= SECOND_RING_MIN_CORE) {
	    puff(ParticleKind.RING, origin.add(0, core * 0.85, 0), new Vec3(0, core * 0.037, 0), core * 0.63f,
		    lifetime(23), WHITE, heat);
	}
    }

    private void emitFireball(int age) {
	int tailLength = timeline.fireballTicks() - FIREBALL_PEAK_TICK;
	float tail = age <= FIREBALL_PEAK_TICK ? 0 : (age - FIREBALL_PEAK_TICK) / (float) tailLength;
	int puffs = count(Math.round((14 + (int) style.extent.core()) * (1 - 0.82f * tail)));
	float expansion = core * (0.22f + (Math.min(age, FIREBALL_PEAK_TICK) + 7 * tail) * 0.044f);
	int coolShade = style == BlastStyle.NUCLEAR ? NUCLEAR_FIREBALL_SHADE : palette.smoke();
	for (int i = 0; i < puffs; i++) {
	    Vec3 outward = BlastScene.direction(random.nextDouble() * Mth.TWO_PI, -0.12 + random.nextDouble() * 1.69);
	    Vec3 pos = origin.add(0, core * 0.14, 0)
		    .add(outward.scale(expansion * (0.34 + random.nextDouble() * 0.66)));
	    Vec3 velocity = outward.scale((0.035 + random.nextDouble() * 0.035) * core).add(0, core * 0.012, 0);
	    float size = core * (0.14f + random.nextFloat() * 0.27f);
	    int heat = ARGB32.lerp(random.nextFloat() * 0.68f, palette.heat(), WHITE);
	    heat = ARGB32.lerp(tail * 0.55f, heat, coolShade);
	    int baseLife = 33 + random.nextInt(38);
	    // Colour and glow follow the base lifetime; only the smoke afterwards lingers longer.
	    puff(ParticleKind.FIRE, pos, velocity, size, fireballLifetime(baseLife), heat, palette.smoke())
		    .heatTicks(lifetime(baseLife));
	    if (age <= FIREBALL_GLOW_TICKS && i % 3 == 0) {
		puff(ParticleKind.GLOW, pos, velocity, size * 0.82f, lifetime(8 + random.nextInt(7)), WHITE,
			palette.heat());
	    }
	}
    }

    private int fireballLifetime(int ticks) {
	float lingerScale = style == BlastStyle.NUCLEAR ? NUCLEAR_FIREBALL_LIFETIME_SCALE : 1f;
	return Math.round(lifetime(ticks) * lingerScale);
    }

    private void emitEjecta(int age) {
	boolean burning = age < EJECTA_FIRE_TICKS;
	for (int i = 0; i < ejectaVelocities.size(); i++) {
	    Vec3 velocity = ejectaVelocities.get(i);
	    double time = 1 + age * 0.8;
	    double horizontal = time / (1 + time * 0.014);
	    double height = velocity.y * time - 0.032 * time * time;
	    if (height < -core * 0.2)
		continue;

	    Vec3 pos = origin.add(velocity.x * horizontal, height + core * 0.1, velocity.z * horizontal);
	    float size = (0.28f + random.nextFloat() * 0.55f) * (1 + core * 0.16f);
	    puff(burning ? ParticleKind.FIRE : ParticleKind.SMOKE, pos, velocity.scale(0.065).add(0, 0.075, 0), size,
		    lifetime(30 + random.nextInt(48)), burning ? palette.heat() : palette.dust(), palette.smoke());

	    if ((i + age / 2) % 2 == 0 && hasFineDetail()) {
		Vec3 scatter = new Vec3((random.nextDouble() - 0.5) * 0.8, random.nextDouble() * 0.55,
			(random.nextDouble() - 0.5) * 0.8);
		puff(ParticleKind.SPARK, pos, velocity.scale(0.28).add(scatter), size * 0.21f, 18 + random.nextInt(23),
			WHITE, palette.heat());
	    }
	}
    }
}
