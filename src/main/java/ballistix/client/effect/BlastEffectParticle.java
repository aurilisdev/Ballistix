package ballistix.client.effect;

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
import org.joml.Quaternionf;
import org.joml.Vector3f;

final class BlastEffectParticle extends TextureSheetParticle {

    enum Shape {
	SMOKE(1.65f, 0.91f, 0.58f, 0.98f, 0, false),
	DUST(1.9f, 0.79f, 0.37f, 0.95f, 0, false),
	FIRE(1.75f, 0.98f, 0.32f, 0.94f, 0, true),
	GLOW(1.6f, 0.96f, 0.08f, 1, 0, true),
	RAY(1.32f, 0.9f, 0.1f, 0.98f, 0, true),
	SPARK(0.35f, 1, 0.35f, 0.95f, 0.65f, true),
	RING(3.8f, 0.75f, 0.06f, 1, 0, true);

	final float growth;
	final float opacity;
	final float fadeStart;
	final float drag;
	final float gravity;
	final boolean lit;

	Shape(float growth, float opacity, float fadeStart, float drag, float gravity, boolean lit) {
	    this.growth = growth;
	    this.opacity = opacity;
	    this.fadeStart = fadeStart;
	    this.drag = drag;
	    this.gravity = gravity;
	    this.lit = lit;
	}
    }

    private static final int RING_SEGMENTS = 48;

    private final Shape shape;
    private final ResourceLocation spriteId;
    private final float size;
    private final int hotColor;
    private final int coolColor;
    private final float brightness;
    private final Vec3 rayDirection;
    private final float renderExtent;
    private int light;

    BlastEffectParticle(ClientLevel level, ResourceLocation spriteId, Shape shape, Vec3 pos, Vec3 velocity,
	    float size, int lifetime, int hotColor, int coolColor) {
	super(level, pos.x, pos.y, pos.z);
	this.shape = shape;
	this.spriteId = spriteId;
	this.size = size;
	this.lifetime = lifetime;
	this.hotColor = hotColor;
	this.coolColor = coolColor;
	brightness = shape.lit ? 1 : 0.8f + level.random.nextFloat() * 0.2f;
	xd = velocity.x;
	yd = velocity.y;
	zd = velocity.z;
	friction = shape.drag;
	gravity = shape.gravity;
	hasPhysics = false;
	quadSize = size;
	rayDirection = shape == Shape.RAY ? velocity.normalize() : Vec3.ZERO;
	renderExtent = size * Math.max(1, shape.growth) * (shape == Shape.RAY ? 13 : 1.5f);
	if (shape == Shape.SMOKE || shape == Shape.FIRE || shape == Shape.DUST) {
	    oRoll = roll = level.random.nextFloat() * Mth.TWO_PI;
	}
	updateLight();
    }

    @Override
    public ParticleRenderType getRenderType() {
	return ParticleRenderType.NO_RENDER;
    }

    @Override
    public void tick() {
	super.tick();
	if (shape == Shape.SMOKE || shape == Shape.FIRE || shape == Shape.DUST) {
	    oRoll = roll;
	    roll += 0.011f;
	}
	if (((age + lifetime) & 7) == 0)
	    updateLight();
    }

    private void updateLight() {
	if (!shape.lit || shape == Shape.FIRE)
	    light = super.getLightColor(0);
    }

    @Override
    protected int getLightColor(float partialTick) {
	if (shape == Shape.FIRE)
	    return age < lifetime * 0.4f ? LightTexture.FULL_BRIGHT : light;
	return shape.lit ? LightTexture.FULL_BRIGHT : light;
    }

    void render(VertexConsumer buffer, Camera camera, float partialTick, TextureAtlas atlas) {
	setSprite(atlas.getSprite(spriteId));
	super.render(buffer, camera, partialTick);
    }

    @Override
    public AABB getRenderBoundingBox(float partialTick) {
	return new AABB(x - renderExtent, y - renderExtent, z - renderExtent,
		x + renderExtent, y + renderExtent, z + renderExtent);
    }

    @Override
    protected void renderRotatedQuad(VertexConsumer buffer, Quaternionf rotation, float x, float y, float z,
	    float partialTick) {
	float progress = Mth.clamp((age + partialTick) / lifetime, 0, 1);
	float fade = 1 - Mth.clamp((progress - shape.fadeStart) / (1 - shape.fadeStart), 0, 1);
	float cooling = Math.min(1, progress * (shape == Shape.FIRE ? 1.7f : 1.15f));
	int color = ARGB32.lerp(cooling, hotColor, coolColor);
	rCol = ARGB32.red(color) * brightness / 255;
	gCol = ARGB32.green(color) * brightness / 255;
	bCol = ARGB32.blue(color) * brightness / 255;
	alpha = shape.opacity * fade;
	float growth = 1 - (1 - progress) * (1 - progress);
	quadSize = size * Mth.lerp(growth, 1, shape.growth);

	if (shape == Shape.RING) {
	    renderRing(buffer, x, y, z);
	    return;
	}
	if (shape != Shape.RAY) {
	    super.renderRotatedQuad(buffer, rotation, x, y, z, partialTick);
	    return;
	}

	Vector3f projected = rayDirection.toVector3f().rotate(new Quaternionf(rotation).conjugate());
	float projection = Mth.sqrt(projected.x * projected.x + projected.y * projected.y);
	rotation.rotateZ((float) Math.atan2(projected.y, projected.x));
	float length = quadSize * (1 + projection * 8);
	addRayVertex(buffer, rotation, x, y, z, length, -quadSize, getU1(), getV1());
	addRayVertex(buffer, rotation, x, y, z, length, quadSize, getU1(), getV0());
	addRayVertex(buffer, rotation, x, y, z, -length, quadSize, getU0(), getV0());
	addRayVertex(buffer, rotation, x, y, z, -length, -quadSize, getU0(), getV1());
    }

    private void addRayVertex(VertexConsumer buffer, Quaternionf rotation, float x, float y, float z,
	    float u, float v, float texU, float texV) {
	Vector3f vertex = new Vector3f(u, v, 0).rotate(rotation).add(x, y, z);
	buffer.addVertex(vertex.x, vertex.y, vertex.z).setUv(texU, texV).setColor(rCol, gCol, bCol, alpha)
		.setLight(LightTexture.FULL_BRIGHT);
    }

    private void renderRing(VertexConsumer buffer, float x, float y, float z) {
	float previousCos = 1;
	float previousSin = 0;
	for (int i = 1; i <= RING_SEGMENTS; i++) {
	    float angle = i * Mth.TWO_PI / RING_SEGMENTS;
	    float cos = Mth.cos(angle);
	    float sin = Mth.sin(angle);
	    addRingVertex(buffer, x, y, z, previousCos * 0.60f, previousSin * 0.60f);
	    addRingVertex(buffer, x, y, z, previousCos * 0.98f, previousSin * 0.98f);
	    addRingVertex(buffer, x, y, z, cos * 0.98f, sin * 0.98f);
	    addRingVertex(buffer, x, y, z, cos * 0.60f, sin * 0.60f);
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
