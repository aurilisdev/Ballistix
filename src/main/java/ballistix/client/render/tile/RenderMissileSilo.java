package ballistix.client.render.tile;

import java.util.Random;

import com.mojang.blaze3d.matrix.MatrixStack;

import ballistix.common.item.ItemMissile;
import ballistix.common.tile.TileMissileSilo;
import electrodynamics.client.render.tile.AbstractTileRenderer;
import electrodynamics.prefab.tile.components.IComponentType;
import electrodynamics.prefab.tile.components.type.ComponentInventory;
import electrodynamics.prefab.utilities.RenderingUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;

public class RenderMissileSilo extends AbstractTileRenderer<TileMissileSilo> {

	public RenderMissileSilo(TileEntityRendererDispatcher context) {
		super(context);
	}

	@Override
	public void render(TileMissileSilo tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {

		ItemStack stack = tileEntityIn.<ComponentInventory>getComponent(IComponentType.Inventory).getItem(0);

		if (stack.isEmpty()) {
			return;
		}

		int type = ((ItemMissile) stack.getItem()).missile.ordinal();

		if(type == -1) {
			return;
		}

		matrixStackIn.pushPose();

		IBakedModel model;

		if(type == 0) {

			model = Minecraft.getInstance().getModelManager().getModel(ballistix.client.ClientRegister.MODEL_MISSILECLOSERANGE);
			matrixStackIn.translate(0.5f, 0.8f, 0.5f);
			matrixStackIn.scale(1.25f, 1.5f, 1.25f);

		} else if (type == 1) {

			model = Minecraft.getInstance().getModelManager().getModel(ballistix.client.ClientRegister.MODEL_MISSILEMEDIUMRANGE);
			matrixStackIn.translate(0.5f, 1.3f, 0.5f);
			matrixStackIn.scale(1.5f, 2.5f, 1.5f);

		} else {

			model = Minecraft.getInstance().getModelManager().getModel(ballistix.client.ClientRegister.MODEL_MISSILELONGRANGE);
			matrixStackIn.translate(0.5f, 0.05f, 0.5f);
			matrixStackIn.scale(2f, 4f, 2f);

		}
		
		matrixStackIn.translate(0.5, 0.5, 0.5);
		
		RenderingUtils.renderModel(model, tileEntityIn, RenderType.solid(), matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);

		//Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateBlock(tileEntityIn.getLevel(), model, tileEntityIn.getBlockState(), tileEntityIn.getBlockPos(), matrixStackIn, bufferIn.getBuffer(RenderType.solid()), false, tileEntityIn.getLevel().random, new Random().nextLong(), 0);

		matrixStackIn.popPose();
	}
	
	@Override
	public boolean shouldRenderOffScreen(TileMissileSilo p_188185_1_) {
		return true;
	}

}