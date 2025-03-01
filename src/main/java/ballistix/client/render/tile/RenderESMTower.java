package ballistix.client.render.tile;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.matrix.MatrixStack;

import ballistix.client.ClientRegister;
import ballistix.common.tile.TileESMTower;
import electrodynamics.client.render.tile.AbstractTileRenderer;
import electrodynamics.prefab.utilities.RenderingUtils;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;

public class RenderESMTower extends AbstractTileRenderer<TileESMTower> {

    public RenderESMTower(TileEntityRendererDispatcher context) {
        super(context);
    }

    @Override
    public void render(@Nonnull TileESMTower tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {

        matrixStackIn.pushPose();

        matrixStackIn.translate(0.5, 1.5, 0.5);

        RenderingUtils.renderModel(getModel(ClientRegister.MODEL_ESMTOWER), tileEntityIn, RenderType.solid(), matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);

        matrixStackIn.popPose();

    }

}
