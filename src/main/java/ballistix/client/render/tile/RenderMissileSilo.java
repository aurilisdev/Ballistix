package ballistix.client.render.tile;

import java.util.Random;

import com.mojang.blaze3d.matrix.MatrixStack;

import ballistix.common.tile.TileMissileSilo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;

public class RenderMissileSilo extends TileEntityRenderer<TileMissileSilo> {

	public RenderMissileSilo(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void render(TileMissileSilo tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		int type = tileEntityIn.range;
		if (type == 1) {
			IBakedModel closerange = Minecraft.getInstance().getModelManager().getModel(ballistix.client.ClientRegister.MODEL_MISSILEMEDIUMRANGE);
			matrixStackIn.translate(0.5f, 2.6f, 0.5f);
			matrixStackIn.scale(1.5f, 2.5f, 1.5f);
			Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelRenderer().renderModel(tileEntityIn.getWorld(), closerange, tileEntityIn.getBlockState(), tileEntityIn.getPos(), matrixStackIn,
					bufferIn.getBuffer(RenderType.getSolid()), false, tileEntityIn.getWorld().rand, new Random().nextLong(), 0);
		} else if (type == 0) {
			IBakedModel closerange = Minecraft.getInstance().getModelManager().getModel(ballistix.client.ClientRegister.MODEL_MISSILECLOSERANGE);
			matrixStackIn.translate(0.5f, 1.51f, 0.5f);
			matrixStackIn.scale(1.25f, 1.5f, 1.25f);
			Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelRenderer().renderModel(tileEntityIn.getWorld(), closerange, tileEntityIn.getBlockState(), tileEntityIn.getPos(), matrixStackIn,
					bufferIn.getBuffer(RenderType.getSolid()), false, tileEntityIn.getWorld().rand, new Random().nextLong(), 0);
		} else if (type == 2) {
			IBakedModel closerange = Minecraft.getInstance().getModelManager().getModel(ballistix.client.ClientRegister.MODEL_MISSILELONGRANGE);
			matrixStackIn.translate(0.5f, 4.1f, 0.5f);
			matrixStackIn.scale(2f, 4f, 2f);
			Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelRenderer().renderModel(tileEntityIn.getWorld(), closerange, tileEntityIn.getBlockState(), tileEntityIn.getPos(), matrixStackIn,
					bufferIn.getBuffer(RenderType.getSolid()), false, tileEntityIn.getWorld().rand, new Random().nextLong(), 0);
		}
	}
}
