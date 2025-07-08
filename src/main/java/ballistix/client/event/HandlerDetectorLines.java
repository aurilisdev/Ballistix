package ballistix.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;

import voltaic.client.event.AbstractLevelStageHandler;

import java.util.HashMap;
import java.util.Map;

public class HandlerDetectorLines extends AbstractLevelStageHandler {

    public static final HandlerDetectorLines INSTANCE = new HandlerDetectorLines();

    private final HashMap<BlockPos, AABB> outlines = new HashMap<>();

    @Override
    public boolean shouldRender(RenderLevelStageEvent.Stage stage) {
        return stage == RenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS;
    }

    @Override
    public void render(Camera camera, Frustum frustum, LevelRenderer renderer, PoseStack poseStack, Matrix4f projectionMatrix, Minecraft minecraft, int renderTick, float partialTick) {

        MultiBufferSource.BufferSource buffer = minecraft.renderBuffers().bufferSource();
        VertexConsumer builder = buffer.getBuffer(RenderType.LINES);
        Vec3 camPos = camera.getPosition();

        poseStack.pushPose();
        poseStack.translate(-camPos.x, -camPos.y, -camPos.z);

        for (Map.Entry<BlockPos, AABB> en : outlines.entrySet()) {
            AABB box = en.getValue().deflate(0.001);
            LevelRenderer.renderLineBox(poseStack, builder, box, 1.0F, 1.0F, 1.0F, 1.0F);
        }

        buffer.endBatch(RenderType.LINES);
        poseStack.popPose();

    }

    @Override
    public void clear() {
        outlines.clear();
    }

    public static boolean containsLines(BlockPos pos) {
        return INSTANCE.outlines.containsKey(pos);
    }

    public static void addLines(BlockPos pos, AABB lines) {
        INSTANCE.outlines.put(pos, lines);
    }

    public static void removeLines(BlockPos pos) {
        INSTANCE.outlines.remove(pos);
    }

}
