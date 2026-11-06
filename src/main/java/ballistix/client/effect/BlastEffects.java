package ballistix.client.effect;

import java.util.ArrayList;
import java.util.List;

import ballistix.Ballistix;
import ballistix.client.effect.BlastEffectParticle.Shape;
import ballistix.client.shake.CameraShakeManager;
import ballistix.common.packet.type.client.particle.PacketStartBlastEffects;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.network.registration.NetworkRegistry;

@EventBusSubscriber(modid = Ballistix.ID, value = Dist.CLIENT)
public final class BlastEffects {

    public enum Style {
	CONDENSIVE(2.5, 3.5f, 19, 0, 0xFFFFA047, 0xFF5C4A43, 0xFFC1A991),
	BREACHING(5, 5.5f, 29, 0, 0xFFFFB259, 0xFF554842, 0xFFC3AE98),
	OBSIDIAN(10, 7f, 42, 0, 0xFFFFA452, 0xFF51444A, 0xFFAA9D99),
	THERMOBARIC(30, 14f, 96, 0, 0xFFFF822B, 0xFF603A2D, 0xFFB69B82),
	NUCLEAR(45, 27f, 155, 660, 0xFFFF9338, 0xFF4C403B, 0xFFAA9D91),
	ANTIMATTER(45, 22f, 118, 280, 0xFF5BDCFF, 0xFF36485F, 0xFF97ADBD),
	LARGE_ANTIMATTER(100, 35f, 216, 500, 0xFFC487FF, 0xFF514067, 0xFFAAA0B8);

	private final double referenceRadius;
	private final float core;
	private final float dustRange;
	private final int aftermathTicks;
	private final int heat;
	private final int smoke;
	private final int dust;

	Style(double referenceRadius, float core, float dustRange, int aftermathTicks, int heat, int smoke, int dust) {
	    this.referenceRadius = referenceRadius;
	    this.core = core;
	    this.dustRange = dustRange;
	    this.aftermathTicks = aftermathTicks;
	    this.heat = heat;
	    this.smoke = smoke;
	    this.dust = dust;
	}
    }

    private static final int MAX_SEQUENCES = 6;
    private static final int FLASH_TICKS = 12;
    private static final float FLASH_HOLD_TICKS = 1.5f;
    private static final int WHITE = 0xFFFFF4E4;
    private static final ResourceLocation GLOW = Ballistix.rl("blast_glow");
    private static final ResourceLocation RING = Ballistix.rl("blast_ring");
    private static final List<ResourceLocation> SMOKE = List.of(
	    Ballistix.rl("blastsmoke_0"), Ballistix.rl("blastsmoke_1"), Ballistix.rl("blastsmoke_2"),
	    Ballistix.rl("blastsmoke_3"), Ballistix.rl("blastsmoke_4"), Ballistix.rl("blastsmoke_5"),
	    Ballistix.rl("blastsmoke_6"), Ballistix.rl("blastsmoke_7"));
    private static final List<BlastEffects> ACTIVE = new ArrayList<>();

    private final ClientLevel level;
    private final Vec3 origin;
    private final Style style;
    private final RandomSource random;
    private final Vec3 wind;
    private final float scale;
    private final float core;
    private final float detail;
    private final int dustTicks;
    private final int aftermathTicks;
    private final List<Vec3> ejecta = new ArrayList<>();
    private int age;
    private boolean shakeStarted;

    private BlastEffects(ClientLevel level, Vec3 origin, Style style, double radius, ParticleStatus particles) {
	this.level = level;
	this.origin = origin;
	this.style = style;
	random = RandomSource.create(level.random.nextLong());
	wind = direction(random.nextDouble() * Mth.TWO_PI, 0);
	scale = Mth.clamp((float) Math.sqrt(radius / style.referenceRadius), 0.55f, 2.25f);
	core = style.core * scale;
	detail = switch (particles) {
	    case ALL -> 1f;
	    case DECREASED -> 0.57f;
	    case MINIMAL -> 0.23f;
	};
	dustTicks = (int) (24 + style.core * 0.95f);
	aftermathTicks = Math.round(style.aftermathTicks * Mth.clamp(scale, 0.75f, 1.6f));
	int count = count(12 + (int) style.core);
	for (int i = 0; i < count; i++) {
	    double angle = (i + random.nextDouble()) / count * Mth.TWO_PI;
	    double speed = (0.55 + random.nextDouble() * 0.85) * (0.5 + core * 0.125);
	    ejecta.add(new Vec3(Math.cos(angle) * speed,
		    (0.28 + random.nextDouble() * 1.18) * (0.45 + core * 0.075), Math.sin(angle) * speed));
	}
    }

    public static void startFromEntity(ClientLevel level, Vec3 origin, Style style, double radius) {
	// The local animation is a fallback for servers without the detonation payload.
	var connection = Minecraft.getInstance().getConnection();
	if (connection == null || !NetworkRegistry.hasChannel(connection, PacketStartBlastEffects.TYPE.id()))
	    start(level, origin, style, radius);
    }

    public static void start(ClientLevel level, Vec3 origin, Style style, double radius) {
	if (!Double.isFinite(radius) || radius <= 0)
	    return;
	ACTIVE.removeIf(effect -> effect.level != level);
	if (ACTIVE.size() >= MAX_SEQUENCES)
	    ACTIVE.removeFirst();
	ACTIVE.add(new BlastEffects(level, origin, style, radius, Minecraft.getInstance().options.particles().get()));
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
	Minecraft minecraft = Minecraft.getInstance();
	BlastParticleRenderer.tick(minecraft.level, minecraft.isPaused());
	ACTIVE.removeIf(effect -> effect.level != minecraft.level);
	if (!minecraft.isPaused())
	    ACTIVE.removeIf(effect -> effect.tick(minecraft));
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Pre event) {
	if (ACTIVE.isEmpty())
	    return;
	Minecraft minecraft = Minecraft.getInstance();
	float screenEffectScale = minecraft.options.screenEffectScale().get().floatValue();
	if (screenEffectScale == 0)
	    return;

	Camera camera = minecraft.gameRenderer.getMainCamera();
	float strength = 0;
	for (BlastEffects effect : ACTIVE) {
	    if (effect.level != minecraft.level || effect.age > FLASH_TICKS)
		continue;
	    Vec3 offset = effect.origin.subtract(camera.getPosition());
	    double distance = offset.length();
	    double facing = distance < 1 ? 1 : offset.normalize().dot(new Vec3(camera.getLookVector()));
	    double reach = effect.core * 9 + 18;
	    double visibility = Math.max(0, 1 - distance / reach) * (0.24 + 0.76 * Math.max(0, facing));
	    float time = Math.max(0, effect.age - 1 + event.getPartialTick().getGameTimeDeltaPartialTick(false));
	    float fade = 1 - Mth.clamp((time - FLASH_HOLD_TICKS) / (FLASH_TICKS - FLASH_HOLD_TICKS), 0, 1);
	    float peak = Math.min(1, 0.35f + effect.core / 32);
	    strength = Math.max(strength, (float) (visibility * fade * fade * peak));
	}
	int opacity = Math.round(255 * strength * screenEffectScale);
	if (opacity > 0) {
	    GuiGraphics graphics = event.getGuiGraphics();
	    graphics.fill(0, 0, graphics.guiWidth(), graphics.guiHeight(), opacity << 24 | 0xFFFFFF);
	}
    }

    private boolean tick(Minecraft minecraft) {
	if (age == 0)
	    flash();
	if (age >= 2 && age <= 20 && age % 2 == 0)
	    fireball();
	if (age <= 36 && age % 2 == 0)
	    ejecta();
	if (age <= dustTicks)
	    pressureFront(minecraft);
	if (age <= aftermathTicks) {
	    if (style == Style.NUCLEAR) {
		if (age >= 6 && age % (age < 150 ? 3 : 6) == 0)
		    mushroomCloud();
	    } else if (age >= 18 && age % 5 == 0) {
		antimatterAftermath();
	    }
	}

	int duration = Math.max(aftermathTicks, Math.max(dustTicks, 36));
	return ++age > duration;
    }

    private void flash() {
	puff(Shape.GLOW, origin.add(0, core * 0.32, 0), Vec3.ZERO, core * 1.7f, 17, style.heat,
		style.heat);
	puff(Shape.GLOW, origin.add(0, core * 0.25, 0), Vec3.ZERO, core * 0.98f, 11, WHITE, style.heat);
	puff(Shape.GLOW, origin.add(0, core * 0.14, 0), Vec3.ZERO, core * 0.52f, 7,
		0xFFFFFFFF, WHITE);
	puff(Shape.RING, origin.add(0, core * 0.12, 0), Vec3.ZERO, core * 0.9f, 22, WHITE,
		style.heat);

	if (detail >= 0.5f && style.core >= 20) {
	    puff(Shape.RING, origin.add(0, core * 0.85, 0), new Vec3(0, core * 0.037, 0),
		    core * 0.63f, 23, WHITE, style.heat);
	}
    }

    private void fireball() {
	int puffs = count(14 + (int) style.core);
	float expansion = core * (0.22f + age * 0.044f);
	for (int i = 0; i < puffs; i++) {
	    Vec3 outward = direction(random.nextDouble() * Mth.TWO_PI, -0.12 + random.nextDouble() * 1.69);
	    Vec3 pos = origin.add(0, core * 0.14, 0).add(outward.scale(expansion * (0.34 + random.nextDouble() * 0.66)));
	    Vec3 velocity = outward.scale((0.035 + random.nextDouble() * 0.035) * core).add(0, core * 0.012, 0);
	    float size = core * (0.14f + random.nextFloat() * 0.27f);
	    int heat = ARGB32.lerp(random.nextFloat() * 0.68f, style.heat, WHITE);
	    puff(Shape.FIRE, pos, velocity, size, 33 + random.nextInt(38), heat, style.smoke);
	    if (age <= 8 && i % 3 == 0) {
		puff(Shape.GLOW, pos, velocity, size * 0.82f, 8 + random.nextInt(7),
			WHITE, style.heat);
	    }
	}
    }

    private void ejecta() {
	for (int i = 0; i < ejecta.size(); i++) {
	    Vec3 velocity = ejecta.get(i);
	    double time = 1 + age * 0.8;
	    double horizontal = time / (1 + time * 0.014);
	    double height = velocity.y * time - 0.032 * time * time;
	    if (height < -core * 0.2)
		continue;

	    Vec3 pos = origin.add(velocity.x * horizontal, height + core * 0.1, velocity.z * horizontal);
	    float size = (0.28f + random.nextFloat() * 0.55f) * (1 + core * 0.16f);
	    puff(age < 9 ? Shape.FIRE : Shape.SMOKE, pos,
		    velocity.scale(0.065).add(0, 0.075, 0), size, 30 + random.nextInt(48),
		    age < 9 ? style.heat : style.dust, style.smoke);

	    if ((i + age / 2) % 2 == 0 && detail > 0.25f) {
		Vec3 scatter = new Vec3((random.nextDouble() - 0.5) * 0.8, random.nextDouble() * 0.55,
			(random.nextDouble() - 0.5) * 0.8);
		puff(Shape.SPARK, pos, velocity.scale(0.28).add(scatter), size * 0.21f,
			18 + random.nextInt(23), WHITE, style.heat);
	    }
	}
    }

    private void pressureFront(Minecraft minecraft) {
	double progress = age / (double) dustTicks;
	double range = style.dustRange * scale;
	double initialRadius = core * 0.25;
	double radius = initialRadius + range * (1 - Math.pow(1 - progress, 1.45));
	var viewer = minecraft.getCameraEntity();
	if (!shakeStarted && viewer != null) {
	    double distance = viewer.getEyePosition().distanceTo(origin);
	    if (distance <= radius) {
		shakeStarted = true;
		CameraShakeManager.startBlastShake(level, core, distance / (initialRadius + range));
	    }
	}
	if (age % 2 == 0)
	    dustFront(minecraft, radius);
    }

    private void dustFront(Minecraft minecraft, double radius) {
	int samples = count(Math.min(66, (int) (14 + radius * 0.43)));
	for (int i = 0; i < samples; i++) {
	    double angle = (i + random.nextDouble()) * Mth.TWO_PI / samples;
	    double distance = radius - random.nextDouble() * Math.min(radius, core * 0.65);
	    double dirX = Math.cos(angle);
	    double dirZ = Math.sin(angle);
	    int x = Mth.floor(origin.x + dirX * distance);
	    int z = Mth.floor(origin.z + dirZ * distance);
	    if (!level.hasChunk(x >> 4, z >> 4))
		continue;
	    int surface = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
	    if (surface <= level.getMinBuildHeight() || Math.abs(surface - origin.y) > 12 + core * 2.3)
		continue;

	    float size = (0.8f + random.nextFloat() * 1.1f) * (0.65f + core * 0.115f);
	    double push = (0.08 + random.nextDouble() * 0.15) * (1 + core * 0.06);
	    Vec3 pos = new Vec3(x + 0.5, surface + size * 0.20, z + 0.5);
	    Vec3 velocity = new Vec3(dirX * push, 0.045 + random.nextDouble() * 0.18, dirZ * push);
	    int color = ARGB32.lerp(random.nextFloat() * 0.35f, style.dust, style.smoke);
	    puff(Shape.DUST, pos, velocity, size, 38 + random.nextInt(50), color, style.dust);
	    if (age <= 18 && i % 6 == 0 && detail > 0.25f) {
		BlockPos ground = new BlockPos(x, surface - 1, z);
		BlockState state = level.getBlockState(ground);
		if (!state.isAir() && state.getFluidState().isEmpty()) {
		    Particle chip = minecraft.particleEngine.createParticle(
			    new BlockParticleOption(ParticleTypes.BLOCK, state).setPos(ground), pos.x, pos.y, pos.z,
			    dirX * push * 1.8, 0.65 + random.nextDouble() * 0.9, dirZ * push * 1.8);
		    if (chip != null)
			chip.scale(1.8f + core * 0.075f);
		}
	    }
	}
    }

    private void mushroomCloud() {
	float late = Mth.clamp((age - 150) / (float) (aftermathTicks - 150), 0, 1);
	float density = 1 - 0.78f * late;
	double headHeight = scale * (9 + 69 * (1 - Math.exp(-age / 37.0)) + late * 28);
	double capRadius = scale * (9 + 37 * (1 - Math.exp(-age / 47.0)) + late * 15);
	Vec3 head = cloudAxis(headHeight);
	float heat = 1 - Mth.clamp((age - 12) / 85f, 0, 1);
	int stemPuffs = count(Math.round(13 * density));
	for (int i = 0; i < stemPuffs; i++) {
	    double fraction = Mth.lerp((i + random.nextDouble()) / stemPuffs, late * 0.26, 1);
	    Vec3 centre = cloudAxis(headHeight * fraction);
	    double angle = random.nextDouble() * Mth.TWO_PI;
	    double width = scale * (5.5 + 7 * Math.pow(1 - fraction, 4) + 5 * Math.pow(fraction, 6));
	    double offset = Math.sqrt(random.nextDouble()) * width;
	    Vec3 pos = centre.add(Math.cos(angle) * offset, 0, Math.sin(angle) * offset);
	    Vec3 velocity = new Vec3(Math.cos(angle) * 0.07, 0.16 + random.nextDouble() * 0.23, Math.sin(angle) * 0.07).scale(scale);
	    cloudPuff(pos, velocity, (float) (width * (0.35 + random.nextDouble() * 0.39)),
		    heat * (0.36f + random.nextFloat() * 0.64f));
	}

	int capPuffs = count(Math.round(32 * density));
	for (int i = 0; i < capPuffs; i++) {
	    double azimuth = (i + random.nextDouble()) * Mth.TWO_PI / capPuffs;
	    double tubeAngle = random.nextDouble() * Mth.TWO_PI;
	    double cos = Math.cos(tubeAngle);
	    double sin = Math.sin(tubeAngle);
	    double ringRadius = capRadius * (0.66 + 0.30 * cos);
	    double y = capRadius * sin * 0.24;
	    // Fill the crown above the rolling rim so the cap reads as a mass from above and below.
	    if (i % 4 == 0) {
		ringRadius = capRadius * 0.68 * Math.sqrt(random.nextDouble());
		y = capRadius * (0.14 + 0.20 * Math.sqrt(1 - ringRadius / capRadius));
	    }
	    double xDir = Math.cos(azimuth);
	    double zDir = Math.sin(azimuth);
	    Vec3 pos = head.add(xDir * ringRadius, y, zDir * ringRadius);
	    double outward = scale * (0.22 + sin * 0.17);
	    Vec3 velocity = new Vec3(xDir * outward, scale * (0.16 - cos * 0.22), zDir * outward)
		    .add(wind.scale(scale * 0.05));
	    float size = (float) (capRadius * (0.125 + random.nextDouble() * 0.15));
	    cloudPuff(pos, velocity, size, heat * (0.31f + random.nextFloat() * 0.69f));
	    if (age >= 39 && i % 5 == 1) {
		Vec3 falling = new Vec3(xDir * 0.12, -0.24 - random.nextDouble() * 0.22, zDir * 0.12)
			.add(wind.scale(0.14));
		puff(Shape.SMOKE, pos, falling, scale * (0.25f + random.nextFloat() * 0.50f),
			96 + random.nextInt(75), 0xFF514543, 0xFF78665B);
	    }
	}
    }

    private Vec3 cloudAxis(double height) {
	return origin.add(wind.scale(height * 0.1)).add(Math.sin(height * 0.09) * scale * 1.5, height,
		Math.sin(height * 0.07) * scale * 1.7);
    }

    private void cloudPuff(Vec3 pos, Vec3 velocity, float size, float heat) {
	boolean hot = heat > 0.25f && random.nextFloat() < heat * 0.86f;
	int color = hot ? ARGB32.lerp(heat, 0xFFA04928, 0xFFFFAA52)
		: ARGB32.lerp(random.nextFloat() * 0.65f, 0xFF362D2B, 0xFF9A8170);
	puff(hot ? Shape.FIRE : Shape.SMOKE, pos, velocity, size, 140 + random.nextInt(101),
		color, style.smoke);
    }

    private void antimatterAftermath() {
	float progress = age / (float) aftermathTicks;
	float density = 1 - 0.8f * progress;
	double spread = core * (0.45 + 1.3 * (1 - Math.exp(-age / 42.0)));
	Vec3 centre = origin.add(wind.scale(core * progress * 0.65)).add(0, core * (0.22 + 0.46 * progress), 0);
	int puffs = count(Math.round((style == Style.LARGE_ANTIMATTER ? 23 : 15) * density));
	for (int i = 0; i < puffs; i++) {
	    Vec3 outward = direction(random.nextDouble() * Mth.TWO_PI, -0.16 + random.nextDouble() * 1.13);
	    Vec3 pos = centre.add(outward.scale(spread * (0.25 + random.nextDouble() * 0.75)));
	    Vec3 drift = outward.scale(0.11 * scale).add(wind.scale(0.08 * scale)).add(0, 0.06 * scale, 0);
	    float size = core * (0.11f + random.nextFloat() * 0.14f);
	    int color = ARGB32.lerp(random.nextFloat() * 0.57f, style.smoke, style.dust);
	    puff(Shape.SMOKE, pos, drift, size, 102 + random.nextInt(75), color, style.smoke);
	    if (i % 5 == 0 && random.nextFloat() < density) {
		puff(Shape.GLOW, pos, drift, size * 0.23f, 18 + random.nextInt(27), style.heat, style.smoke);
	    }
	}
    }

    private void puff(Shape shape, Vec3 pos, Vec3 velocity, float size,
	    int life, int hotColor, int coolColor) {
	ResourceLocation sprite = switch (shape) {
	    case GLOW, SPARK -> GLOW;
	    case RING -> RING;
	    case SMOKE, DUST, FIRE -> SMOKE.get(random.nextInt(SMOKE.size()));
	};
	BlastParticleRenderer.add(new BlastEffectParticle(level, sprite, shape, pos, velocity, size,
		life, hotColor, coolColor));
    }

    private int count(int fullCount) {
	return Math.max(1, Math.round(fullCount * detail));
    }

    private static Vec3 direction(double azimuth, double elevation) {
	double horizontal = Math.cos(elevation);
	return new Vec3(Math.cos(azimuth) * horizontal, Math.sin(elevation), Math.sin(azimuth) * horizontal);
    }
}
