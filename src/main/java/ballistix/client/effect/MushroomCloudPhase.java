package ballistix.client.effect;

import javax.annotation.Nullable;

import ballistix.client.effect.particle.BlastParticle;
import ballistix.client.effect.particle.ParticleKind;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

/** The mushroom cloud of a nuclear blast: a stem rising from the ground under the blast, and a rolling cap on top. */
final class MushroomCloudPhase extends BlastPhase {

    private static final int PUFF_MAX_LIFETIME = 240;
    private static final int START_TICK = 6;
    private static final int LATE_STAGE_TICK = 150;
    private static final int EARLY_INTERVAL = 3;
    private static final int LATE_INTERVAL = 6;
    private static final int FALLING_SMOKE_START_TICK = 39;
    private static final int MIN_STEM_SECTIONS = 4;
    private static final int MAX_STEM_SECTIONS = 36;
    private static final int EMBER_ORANGE = 0xFFAF702E;

    private final CraterGround ground;

    MushroomCloudPhase(BlastScene scene, CraterGround ground) {
	super(scene);
	this.ground = ground;
    }

    @Override
    void tick(int age) {
	int interval = age < LATE_STAGE_TICK ? EARLY_INTERVAL : LATE_INTERVAL;
	if (age >= START_TICK && age <= endTick() && age % interval == 0)
	    emitCloud(age);
    }

    @Override
    int endTick() {
	return timeline.aftermathTicks();
    }

    private void emitCloud(int age) {
	float late = Mth.clamp((age - LATE_STAGE_TICK) / (float) (endTick() - LATE_STAGE_TICK), 0, 1);
	double headHeight = scale * (9 + 69 * (1 - Math.exp(-age / 37.0)) + late * 28);
	double capRadius = scale * (9 + 37 * (1 - Math.exp(-age / 47.0)) + late * 15);
	float heat = 1 - Mth.clamp((age - 12) / 85f, 0, 1);
	emitStem(headHeight, heat);
	emitCap(age, cloudAxis(headHeight), capRadius, late, heat);
    }

    /** Spaces puffs by distance so the stem stays filled as the cap rises. */
    private void emitStem(double headHeight, float heat) {
	Vec3 baseOffset = ground.stemBase().subtract(origin);
	double stemLength = headHeight - baseOffset.y;
	float spacing = scale * (11 - 5 * detail);
	int sections = Mth.clamp(Mth.ceil(stemLength / spacing), MIN_STEM_SECTIONS, MAX_STEM_SECTIONS);
	for (int i = 0; i <= sections; i++) {
	    double heightJitter = i == 0 || i == sections ? 0 : (random.nextDouble() - 0.5) * 0.5;
	    double fraction = (i + heightJitter) / sections;
	    Vec3 centre = cloudAxis(headHeight * fraction).add(baseOffset.scale(1 - fraction));
	    double angle = random.nextDouble() * Mth.TWO_PI;
	    double width = scale * (10.5 + 4 * Math.pow(1 - fraction, 4) + 4 * Math.pow(fraction, 6));
	    double offset = Math.sqrt(random.nextDouble()) * width * (i == 0 ? 0.18 : 0.52);
	    Vec3 pos = centre.add(Math.cos(angle) * offset, 0, Math.sin(angle) * offset);
	    Vec3 velocity = new Vec3(Math.cos(angle) * 0.05, 0.16 + random.nextDouble() * 0.11, Math.sin(angle) * 0.05)
		    .scale(scale);
	    float size = (float) (width * (0.62 + random.nextDouble() * 0.23));
	    if (i == 0) {
		puff(ParticleKind.SMOKE, pos, velocity, size, 140 + random.nextInt(101), palette.dust(),
			palette.smoke());
	    } else {
		emitCloudPuff(pos, velocity, size, heat * (0.36f + random.nextFloat() * 0.64f));
	    }
	}
    }

    private void emitCap(int age, Vec3 head, double capRadius, float late, float heat) {
	int puffs = count(Math.round(40 - 16 * late * late));
	for (int i = 0; i < puffs; i++) {
	    double azimuth = (i + random.nextDouble()) * Mth.TWO_PI / puffs;
	    double tubeAngle = random.nextDouble() * Mth.TWO_PI;
	    double cos = Math.cos(tubeAngle);
	    double sin = Math.sin(tubeAngle);
	    double ringRadius = capRadius * (0.66 + 0.30 * cos);
	    double y = capRadius * sin * 0.31;
	    boolean interior = i % 3 == 0;
	    // Keeps smoke inside the rolling rim as the crown expands.
	    if (interior) {
		double fill = random.nextDouble();
		ringRadius = capRadius * 0.72 * Math.sqrt(fill);
		y = capRadius * (0.15 + (2 * random.nextDouble() - 1) * 0.24 * Math.sqrt(1 - fill));
	    }
	    double xDir = Math.cos(azimuth);
	    double zDir = Math.sin(azimuth);
	    Vec3 pos = head.add(xDir * ringRadius, y, zDir * ringRadius);
	    double outward = scale * (interior ? 0.07 : 0.22 + sin * 0.17);
	    double rise = scale * (interior ? 0.11 : 0.16 - cos * 0.22);
	    Vec3 velocity = new Vec3(xDir * outward, rise, zDir * outward).add(wind.scale(scale * 0.05));
	    float size = (float) (capRadius * (0.145 + random.nextDouble() * 0.15));
	    emitCloudPuff(pos, velocity, size, heat * (0.31f + random.nextFloat() * 0.69f));
	    if (age >= FALLING_SMOKE_START_TICK && i % 5 == 1)
		emitFallingSmoke(pos, xDir, zDir);
	}
    }

    private void emitFallingSmoke(Vec3 pos, double xDir, double zDir) {
	Vec3 falling = new Vec3(xDir * 0.12, -0.24 - random.nextDouble() * 0.22, zDir * 0.12).add(wind.scale(0.14));
	puff(ParticleKind.SMOKE, pos, falling, scale * (0.25f + random.nextFloat() * 0.50f), 96 + random.nextInt(75),
		palette.smoke(), palette.smoke());
    }

    private void emitCloudPuff(Vec3 pos, Vec3 velocity, float size, float heat) {
	boolean hot = heat > 0.25f && random.nextFloat() < heat * 0.86f;
	int color = hot ? ARGB32.lerp(heat, EMBER_ORANGE, palette.heat())
		: ARGB32.lerp(random.nextFloat() * 0.32f, palette.smoke(), palette.dust());
	puff(hot ? ParticleKind.FIRE : ParticleKind.SMOKE, pos, velocity, size,
		140 + random.nextInt(PUFF_MAX_LIFETIME - 139), color, palette.smoke());
    }

    /** The centreline of the column at a height above the origin, swaying and leaning with the wind. */
    private Vec3 cloudAxis(double height) {
	return origin.add(wind.scale(height * 0.1)).add(Math.sin(height * 0.09) * scale * 1.5, height,
		Math.sin(height * 0.07) * scale * 1.7);
    }

    /** Haze hugging the crater floor of a nuclear blast, which the mushroom cloud's stem draws upwards. */
    static final class GroundHaze extends CraterPhase {

	private static final float LATE_DENSITY = 0.45f;
	private static final double COLUMN_RADIUS_FACTOR = 0.52;

	@Nullable
	private Vec3 stemBase;

	GroundHaze(BlastScene scene, CraterGround ground) {
	    super(scene, ground);
	}

	@Override
	void prepare() {
	    stemBase = currentAge <= timeline.aftermathTicks() ? ground.stemBase() : null;
	}

	@Override
	double topOf(double floorY) {
	    Vec3 lStemBase = stemBase;
	    if(lStemBase != null) {
		return Math.max(floorY, lStemBase.y) + core * 1.25;
	    }
	    return floorY + core * 0.45;
	}

	@Override
	float density() {
	    float cloudAge = currentAge - timeline.aftermathTicks();
	    float cloudFade = Mth.clamp(cloudAge / PUFF_MAX_LIFETIME, 0, 1);
	    return super.density() * Mth.lerp(cloudFade, 1f, LATE_DENSITY);
	}

	@Override
	void emit(CraterSpot spot, float density) {
	    emitGroundHaze(spot);
	    emitEmber(spot, density);
	}

	private void emitGroundHaze(CraterSpot spot) {
	    double top = Math.min(spot.top(), spot.ceiling());
	    float size = core * (0.14f + scene.hazeRandom.nextFloat() * 0.08f) * Mth.lerp(detail, 1.3f, 1f);
	    size = (float) Math.min(size, (top - spot.floorY()) * 0.65);
	    Vec3 start = new Vec3(spot.pos().x, spot.floorY() + size * BlastParticle.GROUND_HAZE_OFFSET, spot.pos().z);
	    Vec3 drift = wind.scale(0.018 * scale);
	    Vec3 lStemBase = stemBase;
	    int life = lStemBase == null ? 160 + scene.hazeRandom.nextInt(81)
		    : Math.min(340 + scene.hazeRandom.nextInt(81),
			    timeline.aftermathTicks() - currentAge + PUFF_MAX_LIFETIME);
	    int tint = ARGB32.lerp(0.30f + scene.hazeRandom.nextFloat() * 0.5f, palette.smoke(), palette.dust());

	    BlastParticle haze = puff(ParticleKind.HAZE, start, drift, size, life, tint, palette.smoke())
		    .minBlockLight(MIN_BLOCK_LIGHT);
	    if (lStemBase != null)
		haze.pullIntoColumn(lStemBase, core * COLUMN_RADIUS_FACTOR, spot.floorY(), top);
	}
    }
}
