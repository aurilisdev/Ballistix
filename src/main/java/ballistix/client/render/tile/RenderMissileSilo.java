package ballistix.client.render.tile;

import java.util.Random;

import com.mojang.blaze3d.vertex.PoseStack;

import ballistix.common.tile.TileMissileSilo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;

public class RenderMissileSilo implements BlockEntityRenderer<TileMissileSilo> {

    public RenderMissileSilo(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(TileMissileSilo tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn,
	    int combinedOverlayIn) {
	int type = tileEntityIn.range;
	if (type == 1) {
	    BakedModel closerange = Minecraft.getInstance().getModelManager().getModel(ballistix.client.ClientRegister.MODEL_MISSILEMEDIUMRANGE);
	    matrixStackIn.translate(0.5f, 2.6f, 0.5f);
	    matrixStackIn.scale(1.5f, 2.5f, 1.5f);
	    Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateBlock(tileEntityIn.getLevel(), closerange,
		    tileEntityIn.getBlockState(), tileEntityIn.getBlockPos(), matrixStackIn, bufferIn.getBuffer(RenderType.solid()), false,
		    tileEntityIn.getLevel().random, new Random().nextLong(), 0);
	} else if (type == 0) {
	    BakedModel closerange = Minecraft.getInstance().getModelManager().getModel(ballistix.client.ClientRegister.MODEL_MISSILECLOSERANGE);
	    matrixStackIn.translate(0.5f, 1.51f, 0.5f);
	    matrixStackIn.scale(1.25f, 1.5f, 1.25f);
	    Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateBlock(tileEntityIn.getLevel(), closerange,
		    tileEntityIn.getBlockState(), tileEntityIn.getBlockPos(), matrixStackIn, bufferIn.getBuffer(RenderType.solid()), false,
		    tileEntityIn.getLevel().random, new Random().nextLong(), 0);
	} else if (type == 2) {
	    BakedModel closerange = Minecraft.getInstance().getModelManager().getModel(ballistix.client.ClientRegister.MODEL_MISSILELONGRANGE);
	    matrixStackIn.translate(0.5f, 4.1f, 0.5f);
	    matrixStackIn.scale(2f, 4f, 2f);
	    Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateBlock(tileEntityIn.getLevel(), closerange,
		    tileEntityIn.getBlockState(), tileEntityIn.getBlockPos(), matrixStackIn, bufferIn.getBuffer(RenderType.solid()), false,
		    tileEntityIn.getLevel().random, new Random().nextLong(), 0);
	}
    }
}
