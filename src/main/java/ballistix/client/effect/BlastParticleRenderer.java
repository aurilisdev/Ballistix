package ballistix.client.effect;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import javax.annotation.Nullable;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexSorting;

import ballistix.Ballistix;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@EventBusSubscriber(modid = Ballistix.ID, value = Dist.CLIENT)
final class BlastParticleRenderer {

    private static final int MAX_PARTICLES = 16_384;
    private static final Deque<BlastEffectParticle> PARTICLES = new ArrayDeque<>();
    private static final ByteBufferBuilder SORT_BUFFER = new ByteBufferBuilder(64 * 1024);
    @Nullable
    private static ClientLevel level;

    private BlastParticleRenderer() {
    }

    static void add(BlastEffectParticle particle) {
	if (PARTICLES.size() >= MAX_PARTICLES)
	    PARTICLES.removeFirst();
	PARTICLES.addLast(particle);
    }

    static void tick(@Nullable ClientLevel currentLevel, boolean paused) {
	if (level != currentLevel) {
	    PARTICLES.clear();
	    level = currentLevel;
	}
	if (paused)
	    return;
	Iterator<BlastEffectParticle> iterator = PARTICLES.iterator();
	while (iterator.hasNext()) {
	    BlastEffectParticle particle = iterator.next();
	    particle.tick();
	    if (!particle.isAlive())
		iterator.remove();
	}
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
	if (PARTICLES.isEmpty())
	    return;

	// Fabulous composites its particles and clouds by their depth. Fast and Fancy render
	// clouds into the main target, so the blast batch needs the depth they wrote there.
	boolean fabulous = event.getLevelRenderer().getParticlesTarget() != null;
	RenderLevelStageEvent.Stage stage = fabulous ? RenderLevelStageEvent.Stage.AFTER_PARTICLES
		: RenderLevelStageEvent.Stage.AFTER_WEATHER;
	if (event.getStage() != stage)
	    return;

	Minecraft minecraft = Minecraft.getInstance();
	if (minecraft.level != level
		|| !(minecraft.getTextureManager().getTexture(TextureAtlas.LOCATION_PARTICLES) instanceof TextureAtlas atlas))
	    return;

	Camera camera = event.getCamera();
	float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
	BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
	for (BlastEffectParticle particle : PARTICLES) {
	    if (event.getFrustum().isVisible(particle.getRenderBoundingBox(partialTick)))
		particle.render(buffer, camera, partialTick, atlas);
	}
	MeshData mesh = buffer.build();
	if (mesh == null)
	    return;

	// Camera-relative quad centres project onto the view axis for a back-to-front sort.
	// This also sorts each section of a shock ring against the cloud billboards.
	mesh.sortQuads(SORT_BUFFER, VertexSorting.byDistance(camera.getLookVector()::dot));

	ShaderInstance previousShader = RenderSystem.getShader();
	int previousTexture = RenderSystem.getShaderTexture(0);
	int previousLightTexture = RenderSystem.getShaderTexture(2);
	LightTexture lightTexture = minecraft.gameRenderer.lightTexture();
	lightTexture.turnOnLightLayer();
	RenderSystem.setShader(GameRenderer::getParticleShader);
	RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
	RenderSystem.enableBlend();
	RenderSystem.defaultBlendFunc();
	RenderSystem.enableDepthTest();
	RenderSystem.depthMask(fabulous);
	RenderSystem.disableCull();
	try {
	    BufferUploader.drawWithShader(mesh);
	} finally {
	    RenderSystem.enableCull();
	    RenderSystem.disableBlend();
	    lightTexture.turnOffLightLayer();
	    RenderSystem.setShaderTexture(2, previousLightTexture);
	    RenderSystem.setShaderTexture(0, previousTexture);
	    RenderSystem.setShader(() -> previousShader);
	}
    }
}
