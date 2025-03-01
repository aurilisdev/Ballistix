package ballistix.client.render.tile;

import java.util.Random;

import com.mojang.blaze3d.matrix.MatrixStack;

import ballistix.common.tile.radar.TileSearchRadar;
import electrodynamics.client.render.tile.AbstractTileRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Quaternion;

public class RenderRadar extends AbstractTileRenderer<TileSearchRadar> {

	public RenderRadar(TileEntityRendererDispatcher context) {
		super(context);
	}

	@Override
	public void render(TileSearchRadar tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {

		IBakedModel radardish = getModel(ballistix.client.ClientRegister.MODEL_RADARDISH);

		float partial = (float) (partialTicks * tileEntityIn.clientRotationSpeed);

		double yRot = tileEntityIn.clientRotation + partial;

		Direction facing = tileEntityIn.getFacing();

		if(facing == Direction.EAST || facing == Direction.WEST) {
			yRot += facing.toYRot();
		} else if(facing == Direction.SOUTH) {
			yRot -= 180.0;
		}

		matrixStackIn.translate(7.75 / 16.0, 11.0 / 16.0, 7.75 / 16.0);
		matrixStackIn.mulPose(new Quaternion(0, (float) -yRot, 0, true));
		// matrixStackIn.mulPose(new Quaternion(0,(float) ((tileEntityIn.savedTickRotation + partial)), 0, true));
		Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateBlock(tileEntityIn.getLevel(), radardish, tileEntityIn.getBlockState(), tileEntityIn.getBlockPos(), matrixStackIn, bufferIn.getBuffer(RenderType.solid()), false, tileEntityIn.getLevel().random, new Random().nextLong(), 0);
	}

}
