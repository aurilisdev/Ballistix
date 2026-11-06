package ballistix.client.effect;

import javax.annotation.Nullable;

import ballistix.client.effect.particle.ParticleKind;
import net.minecraft.core.BlockPos;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.Vec3;

/**
 * Emits over the crater floor while the blast lingers. This class picks where to emit and how much; subclasses
 * decide what each spot produces.
 */
abstract class CraterPhase extends BlastPhase {

    static final int MIN_BLOCK_LIGHT = 7;
    private static final int START_TICK = 20;
    private static final int INTERVAL = 5;
    private static final float DENSITY_DECAY = 0.8f;

    final CraterGround ground;
    final boolean serverReported;
    final Vec3 windDrift;
    int currentAge;

    CraterPhase(BlastScene scene, CraterGround ground) {
	super(scene);
	this.ground = ground;
	serverReported = ground.isServerReported();
	windDrift = wind.scale(0.025 * scale);
    }

    @Override
    final void tick(int age) {
	if (age < START_TICK || age > ground.endTick() || age % INTERVAL != 0)
	    return;
	if (serverReported && ground.hasNoFloors())
	    return;

	currentAge = age;
	prepare();
	float density = density();
	int samples = count((int) Math.round(fullSampleCount() * density));
	int floorCount = ground.floorCount();
	int start = serverReported ? random.nextInt(floorCount) : 0;
	if (serverReported)
	    samples = Math.min(samples, floorCount);

	for (int i = 0; i < samples; i++) {
	    BlockPos block = serverReported ? ground.floorAt((start + i * floorCount / samples) % floorCount)
		    : ground.sampleGround(random);
	    CraterSpot spot = block == null ? null : spotAt(block, i);
	    if (spot != null)
		emit(spot, density);
	}
    }

    @Override
    int endTick() {
	return ground.endTick();
    }

    /** Called once per emission tick before any spot is sampled. */
    void prepare() {
    }

    /** Height reached above the given floor, which bounds how much room is searched for above it. */
    abstract double topOf(double floorY);

    abstract void emit(CraterSpot spot, float density);

    /** How strongly to emit this tick, fading from 1 as the crater settles. */
    float density() {
	float progress = serverReported ? 1 - (ground.endTick() - currentAge) / (float) ground.lingerTicks()
		: (currentAge - START_TICK) / (float) (ground.lingerTicks() - START_TICK);
	return 1 - DENSITY_DECAY * (serverReported ? progress * progress : progress);
    }

    final void emitEmber(CraterSpot spot, float density) {
	if (hasFineDetail() && random.nextFloat() < 0.25f * density) {
	    int life = (serverReported ? 40 : 24) + random.nextInt(34);
	    puff(ParticleKind.GLOW, spot.pos().add(0, 0.1, 0), windDrift.add(0, 0.032, 0), spot.size() * 0.16f, life,
		    palette.heat(), palette.smoke());
	}
    }

    private double fullSampleCount() {
	return serverReported ? Math.min(30, 18 + ground.radius() * 0.12) : Math.min(26, 12 + ground.radius() * 0.16);
    }

    @Nullable
    private CraterSpot spotAt(BlockPos block, int index) {
	double floorY = ground.floorY(block);
	if (Double.isNaN(floorY))
	    return null;

	float size = (0.65f + random.nextFloat() * 0.65f) * (0.6f + core * 0.075f);
	double top = topOf(floorY);
	double ceiling = serverReported ? ground.ceilingY(block, top + 3 * size + 4 * scale)
		: Double.POSITIVE_INFINITY;
	double clearance = ceiling - floorY;
	if (clearance <= 0)
	    return null;

	size = (float) Math.min(size, clearance * 0.32);
	double x = block.getX() + (serverReported ? 0.2 + random.nextDouble() * 0.6 : 0.5);
	double z = block.getZ() + (serverReported ? 0.2 + random.nextDouble() * 0.6 : 0.5);
	Vec3 pos = new Vec3(x, floorY + (serverReported ? size * 0.4 : 0.15), z);
	return new CraterSpot(block, index, pos, floorY, top, ceiling, size);
    }

    /** A point on the crater floor where a crater phase emits, with how much room there is above it. */
    record CraterSpot(BlockPos block, int index, Vec3 pos, double floorY, double top, double ceiling, float size) {

	double clearance() {
	    return ceiling - floorY;
	}
    }

    /**
     * Dust and smoke drifting over the crater, tinted by the ground and lifted
     * towards an antimatter cloud above it.
     */
    static final class Dust extends CraterPhase {

	private static final int LIFT_LAYERS = 2;

	@Nullable
	private final AntimatterCloudPhase cloud;
	@Nullable
	private Vec3 cloudCentre;

	Dust(BlastScene scene, CraterGround ground, @Nullable AntimatterCloudPhase cloud) {
	    super(scene, ground);
	    this.cloud = cloud;
	}

	@Override
	void prepare() {
	    cloudCentre = cloud == null ? null : cloud.centreWhileActive(currentAge);
	}

	@Override
	double topOf(double floorY) {
	    Vec3 cloudCentre2 = cloudCentre;
	    if (cloudCentre2 != null) {
		return Math.max(floorY, cloudCentre2.y);
	    }
	    return floorY;
	}

	@Override
	void emit(CraterSpot spot, float density) {
	    boolean smoke = random.nextInt(4) == 0;
	    double rise = smoke ? 0.045 + random.nextDouble() * 0.035 : 0.018;
	    rise = Math.min(rise, Math.max(0, spot.ceiling() - spot.pos().y - spot.size() * 1.9) * 0.02);
	    Vec3 drift = driftAt(spot, rise);
	    int dust = dustColorAt(spot);
	    int color = ARGB32.lerp(random.nextFloat() * 0.45f, dust, palette.smoke());
	    int life = serverReported ? 220 + random.nextInt(141) : 90 + random.nextInt(76);
	    puff(smoke ? ParticleKind.SMOKE : ParticleKind.DUST, spot.pos(), drift, spot.size(), life, color, dust)
		    .minBlockLight(MIN_BLOCK_LIGHT);
	    emitEmber(spot, density);
	    Vec3 lCloudCentre = cloudCentre;
	    if (lCloudCentre != null && spot.index() % 2 == 0)
		emitLiftedDust(spot, lCloudCentre, color);
	}

	private Vec3 driftAt(CraterSpot spot, double rise) {
	    if (!serverReported)
		return windDrift.add(0, rise, 0);
	    double crosswind = (random.nextDouble() - 0.5) * 0.06 * scale;
	    Vec3 gust = windDrift.scale(1.1 + random.nextDouble() * 1.5).add(-wind.z * crosswind, 0, wind.x * crosswind);
	    return gust.scale(Math.min(1, spot.clearance() / 4)).add(0, rise, 0);
	}

	private int dustColorAt(CraterSpot spot) {
	    int dust = palette.dust();
	    if (!serverReported)
		return dust;
	    MapColor groundColor = level.getBlockState(spot.block()).getMapColor(level, spot.block());
	    return groundColor == MapColor.NONE ? dust : ARGB32.lerp(0.25f, dust, 0xFF000000 | groundColor.col);
	}

	/**
	 * Fills the gap between the crater floor and the underside of the cloud with
	 * rising smoke.
	 */
	private void emitLiftedDust(CraterSpot spot, Vec3 cloudCentre, int color) {
	    Vec3 floor = spot.pos();
	    double dx = cloudCentre.x - floor.x;
	    double dz = cloudCentre.z - floor.z;
	    double edge = Mth.clamp(Math.hypot(dx, dz) / core - 1.1, 0, 1);
	    double rise = Math.max(3 * scale, cloudCentre.y - floor.y) * (1 - 0.7 * edge);
	    rise = Math.min(rise, spot.ceiling() - floor.y - spot.size() * 3);
	    if (rise <= spot.size())
		return;

	    Vec3 drift = wind.scale(0.03 * scale).add(dx * 0.002, 0, dz * 0.002);
	    for (int layer = 0; layer < LIFT_LAYERS; layer++) {
		double fraction = (layer + random.nextDouble()) / LIFT_LAYERS;
		Vec3 pos = floor.add(0, rise * fraction, 0);
		float size = spot.size() * (1 + 0.55f * (float) fraction);
		int tint = ARGB32.lerp((float) fraction * 0.7f, color, palette.smoke());
		double loftRise = Math.min(0.08 * scale, Math.max(0, spot.ceiling() - pos.y - size * 1.65) * 0.02);
		puff(ParticleKind.SMOKE, pos, drift.add(0, loftRise, 0), size, 112 + random.nextInt(69), tint,
			palette.smoke()).minBlockLight(MIN_BLOCK_LIGHT);
	    }
	}
    }
}
