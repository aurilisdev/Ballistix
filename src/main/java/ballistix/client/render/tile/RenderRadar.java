package ballistix.client.render.tile;

import java.util.Random;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;

import ballistix.common.tile.TileRadar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;

public class RenderRadar implements BlockEntityRenderer<TileRadar> {

	public RenderRadar(BlockEntityRendererProvider.Context context) {
	}

	@Override
	public void render(TileRadar tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {

		BakedModel radardish = Minecraft.getInstance().getModelManager().getModel(ballistix.client.ClientRegister.MODEL_RADARDISH);
		float partial = (float) (partialTicks * tileEntityIn.rotationSpeed);
		matrixStackIn.translate(7.75/ 16.0, 11.0/16.0, 7.75 / 16.0);
		matrixStackIn.mulPose(new Quaternion(0,(float) ((tileEntityIn.savedTickRotation + partial)), 0, true));
		Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateBlock(tileEntityIn.getLevel(), radardish, tileEntityIn.getBlockState(), tileEntityIn.getBlockPos(), matrixStackIn, bufferIn.getBuffer(RenderType.solid()), false, tileEntityIn.getLevel().random, new Random().nextLong(), 0);
	}

}
