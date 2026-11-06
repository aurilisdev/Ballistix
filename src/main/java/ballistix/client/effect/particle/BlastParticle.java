package ballistix.client.effect.particle;

import javax.annotation.Nullable;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * A billboard drawn in one batch by {@link BlastParticleRenderer} rather than by the particle engine, so that
 * blast particles can be depth sorted together and exceed the engine's particle cap.
 */
public final class BlastParticle extends TextureSheetParticle {

    /** Fraction of the haze sprite's size that sits below the floor-following centre. */
    public static final float GROUND_HAZE_OFFSET = 0.50f;
    private static final float GROUND_HAZE_ASPECT = 0.38f;
    private static final float ROLL_PER_TICK = 0.011f;
    private static final int RING_SEGMENTS = 48;
    private static final float RING_INNER_RADIUS = 0.60f;
    private static final float RING_OUTER_RADIUS = 0.98f;
    private static final float HAZE_APPEAR_TICKS = 18f;
    private static final float HAZE_LIFTED_OPACITY = 0.47f;
    private static final int LIGHT_REFRESH_MASK = 7;

    private final ParticleKind kind;
    private final ResourceLocation sprite;
    private final float size;
    private final int hotColor;
    private final int coolColor;
    private final float brightness;
    private final float renderExtent;
    private int minBlockLight;
    private int light;
    private int heatTicks;
    @Nullable
    private HazeUpdraft updraft;

    public BlastParticle(ClientLevel level, ParticleKind kind, Vec3 pos, Vec3 velocity, float size, int lifetime,
	    int hotColor, int coolColor) {
	super(level, pos.x, pos.y, pos.z);
	this.kind = kind;
	this.size = size;
	this.lifetime = lifetime;
	this.hotColor = hotColor;
	this.coolColor = coolColor;
	sprite = kind.pickSprite(level.random);
	heatTicks = lifetime;
	brightness = kind.selfLit ? 1 : 0.8f + level.random.nextFloat() * 0.2f;
	xd = velocity.x;
	yd = velocity.y;
	zd = velocity.z;
	friction = kind.drag;
	gravity = kind.gravity;
	hasPhysics = false;
	quadSize = size;
	renderExtent = size * Math.max(1, kind.growth) * 1.5f;
	if (kind.tumbles()) {
	    oRoll = roll = level.random.nextFloat() * Mth.TWO_PI;
	}
	updateLight();
    }

    /** Sets how many ticks the hot phase lasts, independently of how long the particle lives. */
    public BlastParticle heatTicks(int heatTicks) {
	this.heatTicks = Math.max(1, heatTicks);
	return this;
    }

    public BlastParticle minBlockLight(int minBlockLight) {
	this.minBlockLight = minBlockLight;
	updateLight();
	return this;
    }

    public void pullIntoColumn(Vec3 centre, double liftRadius, double floor, double top) {
	updraft = new HazeUpdraft(centre, liftRadius, floor, top, x, z, lifetime);
    }

    @Override
    public ParticleRenderType getRenderType() {
	return ParticleRenderType.NO_RENDER;
    }

    @Override
    public void tick() {
	if (updraft != null)
	    steerInUpdraft(updraft);
	super.tick();
	if (kind.tumbles()) {
	    oRoll = roll;
	    roll += ROLL_PER_TICK;
	}
	if (((age + lifetime) & LIGHT_REFRESH_MASK) == 0)
	    updateLight();
    }

    private void steerInUpdraft(HazeUpdraft draft) {
	double dx = draft.centre.x - x;
	double dz = draft.centre.z - z;
	double distance = Math.hypot(dx, dz);
	// The sideways pull stays constant; only the vertical lift eases in below.
	double pull = draft.pullSpeed / Math.max(distance, draft.liftRadius * 0.65);
	xd = Mth.lerp(0.1, xd, (dx - dz * 0.19) * pull);
	zd = Mth.lerp(0.1, zd, (dz + dx * 0.19) * pull);

	double groundHeight = draft.groundHeight(size);
	int nextX = Mth.floor(x + xd);
	int nextZ = Mth.floor(z + zd);
	if (y <= groundHeight + 1 && (nextX != Mth.floor(x) || nextZ != Mth.floor(z))) {
	    if (draft.followFloor(level, nextX, nextZ, size)) {
		groundHeight = draft.groundHeight(size);
	    } else {
		xd = 0;
		zd = 0;
	    }
	}

	double turn = Mth.clamp((age / (double) lifetime - 0.06) / 0.16, 0, 1);
	double lift = turn * (1 - Mth.clamp(distance / draft.liftRadius, 0, 1));
	double followGround = Mth.clamp((groundHeight - y) * 0.14, -0.24, 0.24);
	double headroom = Mth.clamp((draft.top - groundHeight - size * GROUND_HAZE_ASPECT) / size, 0, 1);
	draft.easeLiftTowards((float) (lift * headroom));
	double halfHeight = size * kind.growth * Mth.lerp(draft.lift(), GROUND_HAZE_ASPECT, 1f);
	double clearance = draft.top - y - halfHeight;
	yd = Math.min(Mth.lerp(0.1, yd, Mth.lerp(lift, followGround, draft.riseSpeed)), clearance);
    }

    private void updateLight() {
	if (!kind.selfLit || kind == ParticleKind.FIRE) {
	    light = super.getLightColor(0);
	    if (LightTexture.block(light) < minBlockLight)
		light = LightTexture.pack(minBlockLight, LightTexture.sky(light));
	}
    }

    @Override
    protected int getLightColor(float partialTick) {
	if (kind == ParticleKind.FIRE) {
	    // Fades from self-lit to world light across the heat window instead of switching at one tick.
	    float glow = 1 - smoothstep((heatProgress(partialTick) - 0.2f) / 0.45f);
	    return LightTexture.pack(Math.round(Mth.lerp(glow, LightTexture.block(light), 15)),
		    Math.round(Mth.lerp(glow, LightTexture.sky(light), 15)));
	}
	return kind.selfLit ? LightTexture.FULL_BRIGHT : light;
    }

    private float lifeProgress(float partialTick) {
	return Mth.clamp((age + partialTick) / lifetime, 0, 1);
    }

    private float heatProgress(float partialTick) {
	return Mth.clamp((age + partialTick) / heatTicks, 0, 1);
    }

    private static float smoothstep(float t) {
	t = Mth.clamp(t, 0, 1);
	return t * t * (3 - 2 * t);
    }

    public void render(VertexConsumer buffer, Camera camera, float partialTick, TextureAtlas atlas) {
	setSprite(atlas.getSprite(sprite));
	super.render(buffer, camera, partialTick);
    }

    @Override
    public AABB getRenderBoundingBox(float partialTick) {
	return new AABB(x - renderExtent, y - renderExtent, z - renderExtent, x + renderExtent, y + renderExtent,
		z + renderExtent);
    }

    @Override
    protected void renderRotatedQuad(VertexConsumer buffer, Quaternionf rotation, float x, float y, float z,
	    float partialTick) {
	float progress = lifeProgress(partialTick);
	float fade = 1 - Mth.clamp((progress - kind.fadeStart) / (1 - kind.fadeStart), 0, 1);
	float cooling = kind == ParticleKind.FIRE ? smoothstep(heatProgress(partialTick) * 1.7f)
		: Math.min(1, progress * 1.15f);
	int color = ARGB32.lerp(cooling, hotColor, coolColor);
	rCol = ARGB32.red(color) * brightness / 255;
	gCol = ARGB32.green(color) * brightness / 255;
	bCol = ARGB32.blue(color) * brightness / 255;
	alpha = kind.opacity * fade;
	float growth = 1 - (1 - progress) * (1 - progress);
	quadSize = size * Mth.lerp(growth, 1, kind.growth);

	switch (kind) {
	case RING -> renderRing(buffer, x, y, z);
	case HAZE -> renderHaze(buffer, rotation, x, y, z, partialTick, fade);
	default -> super.renderRotatedQuad(buffer, rotation, x, y, z, partialTick);
	}
    }

    private void renderHaze(VertexConsumer buffer, Quaternionf rotation, float x, float y, float z, float partialTick,
	    float fade) {
	float lift = updraft == null ? 0 : updraft.lift(partialTick);
	float appear = Mth.clamp((age + partialTick) / HAZE_APPEAR_TICKS, 0, 1);
	alpha = Mth.lerp(lift, kind.opacity, HAZE_LIFTED_OPACITY) * fade * appear;

	float height = quadSize * Mth.lerp(lift, GROUND_HAZE_ASPECT, 1f);
	int light = getLightColor(partialTick);
	Vector3f corner = new Vector3f();
	addHazeVertex(buffer, corner.set(-quadSize, -height, 0).rotate(rotation), x, y, z, getU1(), getV1(), light);
	addHazeVertex(buffer, corner.set(-quadSize, height, 0).rotate(rotation), x, y, z, getU1(), getV0(), light);
	addHazeVertex(buffer, corner.set(quadSize, height, 0).rotate(rotation), x, y, z, getU0(), getV0(), light);
	addHazeVertex(buffer, corner.set(quadSize, -height, 0).rotate(rotation), x, y, z, getU0(), getV1(), light);
    }

    private void addHazeVertex(VertexConsumer buffer, Vector3f corner, float x, float y, float z, float u, float v,
	    int light) {
	buffer.addVertex(x + corner.x, y + corner.y, z + corner.z).setUv(u, v).setColor(rCol, gCol, bCol, alpha)
		.setLight(light);
    }

    private void renderRing(VertexConsumer buffer, float x, float y, float z) {
	float previousCos = 1;
	float previousSin = 0;
	for (int i = 1; i <= RING_SEGMENTS; i++) {
	    float angle = i * Mth.TWO_PI / RING_SEGMENTS;
	    float cos = Mth.cos(angle);
	    float sin = Mth.sin(angle);
	    addRingVertex(buffer, x, y, z, previousCos * RING_INNER_RADIUS, previousSin * RING_INNER_RADIUS);
	    addRingVertex(buffer, x, y, z, previousCos * RING_OUTER_RADIUS, previousSin * RING_OUTER_RADIUS);
	    addRingVertex(buffer, x, y, z, cos * RING_OUTER_RADIUS, sin * RING_OUTER_RADIUS);
	    addRingVertex(buffer, x, y, z, cos * RING_INNER_RADIUS, sin * RING_INNER_RADIUS);
	    previousCos = cos;
	    previousSin = sin;
	}
    }

    private void addRingVertex(VertexConsumer buffer, float x, float y, float z, float dx, float dz) {
	float u = Mth.lerp((dx + 1) * 0.5f, getU0(), getU1());
	float v = Mth.lerp((dz + 1) * 0.5f, getV0(), getV1());
	buffer.addVertex(x + dx * quadSize, y, z + dz * quadSize).setUv(u, v).setColor(rCol, gCol, bCol, alpha)
		.setLight(LightTexture.FULL_BRIGHT);
    }
}
