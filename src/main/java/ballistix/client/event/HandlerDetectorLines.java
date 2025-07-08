package ballistix.client.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import voltaic.client.event.AbstractLevelStageHandler;

import java.util.HashMap;
import java.util.Map.Entry;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

public class HandlerDetectorLines extends AbstractLevelStageHandler {

    public static final HandlerDetectorLines INSTANCE = new HandlerDetectorLines();

    private final HashMap<BlockPos, AxisAlignedBB> outlines = new HashMap<>();

    @Override
    public void render(WorldRenderer context, MatrixStack stack, float partialTicks, Matrix4f projectionMatrix, long finishTimeNano) {

    	Minecraft minecraft = Minecraft.getInstance();
		IRenderTypeBuffer.Impl buffer = minecraft.renderBuffers().bufferSource();
		IVertexBuilder builder = buffer.getBuffer(RenderType.LINES);
		Vector3d camPos = minecraft.gameRenderer.getMainCamera().getPosition();

		stack.pushPose();
		stack.translate(-camPos.x, -camPos.y, -camPos.z);

		for (Entry<BlockPos, AxisAlignedBB> en : outlines.entrySet()) {
			AxisAlignedBB box = en.getValue().deflate(0.001);
			WorldRenderer.renderLineBox(stack, builder, box, 1.0F, 1.0F, 1.0F, 1.0F);
		}

		buffer.endBatch(RenderType.LINES);
		stack.popPose();

    }

    @Override
    public void clear() {
        outlines.clear();
    }

    public static boolean containsLines(BlockPos pos) {
        return INSTANCE.outlines.containsKey(pos);
    }

    public static void addLines(BlockPos pos, AxisAlignedBB lines) {
        INSTANCE.outlines.put(pos, lines);
    }

    public static void removeLines(BlockPos pos) {
        INSTANCE.outlines.remove(pos);
    }

}
