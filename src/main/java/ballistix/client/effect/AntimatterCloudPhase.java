package ballistix.client.effect;

import javax.annotation.Nullable;

import ballistix.client.effect.particle.ParticleKind;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

/** The cloud of an antimatter blast, which drifts and rises downwind of the origin and thins out over time. */
final class AntimatterCloudPhase extends BlastPhase {

    private static final int START_TICK = 18;
    private static final int INTERVAL = 5;

    AntimatterCloudPhase(BlastScene scene) {
	super(scene);
    }

    @Override
    void tick(int age) {
	if (age >= START_TICK && age <= endTick() && age % INTERVAL == 0)
	    emitCloud(age);
    }

    @Override
    int endTick() {
	return timeline.aftermathTicks();
    }

    /** Where the cloud is centred, or null once the cloud has dissipated. */
    @Nullable
    Vec3 centreWhileActive(int age) {
	return age <= endTick() ? centre(age) : null;
    }

    private Vec3 centre(int age) {
	float progress = age / (float) endTick();
	return origin.add(wind.scale(core * progress * 0.65)).add(0, core * (0.22 + 0.46 * progress), 0);
    }

    private void emitCloud(int age) {
	float density = 1 - 0.8f * (age / (float) endTick());
	double spread = core * (0.45 + 1.3 * (1 - Math.exp(-age / 42.0)));
	Vec3 centre = centre(age);
	int puffs = count(Math.round((style == BlastStyle.LARGE_ANTIMATTER ? 23 : 15) * density));
	for (int i = 0; i < puffs; i++) {
	    Vec3 outward = BlastScene.direction(random.nextDouble() * Mth.TWO_PI, -0.16 + random.nextDouble() * 1.13);
	    Vec3 pos = centre.add(outward.scale(spread * (0.25 + random.nextDouble() * 0.75)));
	    Vec3 drift = outward.scale(0.11 * scale).add(wind.scale(0.08 * scale)).add(0, 0.06 * scale, 0);
	    float size = core * (0.11f + random.nextFloat() * 0.14f);
	    int color = ARGB32.lerp(random.nextFloat() * 0.57f, palette.smoke(), palette.dust());
	    puff(ParticleKind.SMOKE, pos, drift, size, 102 + random.nextInt(75), color, palette.smoke());
	    if (i % 5 == 0 && random.nextFloat() < density)
		puff(ParticleKind.GLOW, pos, drift, size * 0.23f, 18 + random.nextInt(27), palette.heat(),
			palette.smoke());
	}
    }
}
