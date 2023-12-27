package ballistix.client.render.tile;

import java.util.Random;

import com.mojang.blaze3d.matrix.MatrixStack;

import ballistix.common.item.ItemMissile;
import ballistix.common.tile.TileMissileSilo;
import electrodynamics.client.render.tile.AbstractTileRenderer;
import electrodynamics.prefab.tile.components.IComponentType;
import electrodynamics.prefab.tile.components.type.ComponentInventory;
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

		switch (type) {
		case 1: {
			IBakedModel closerange = Minecraft.getInstance().getModelManager().getModel(ballistix.client.ClientRegister.MODEL_MISSILEMEDIUMRANGE);
			matrixStackIn.translate(0.5f, 2.6f, 0.5f);
		    matrixStackIn.scale(1.5f, 2.5f, 1.5f);
			Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateBlock(tileEntityIn.getLevel(), closerange, tileEntityIn.getBlockState(), tileEntityIn.getBlockPos(), matrixStackIn, bufferIn.getBuffer(RenderType.solid()), false, tileEntityIn.getLevel().random, new Random().nextLong(), 0);
			break;
		}
		case 0: {
			IBakedModel closerange = Minecraft.getInstance().getModelManager().getModel(ballistix.client.ClientRegister.MODEL_MISSILECLOSERANGE);
			matrixStackIn.translate(0.5f, 1.51f, 0.5f);
		    matrixStackIn.scale(1.25f, 1.5f, 1.25f);
			Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateBlock(tileEntityIn.getLevel(), closerange, tileEntityIn.getBlockState(), tileEntityIn.getBlockPos(), matrixStackIn, bufferIn.getBuffer(RenderType.solid()), false, tileEntityIn.getLevel().random, new Random().nextLong(), 0);
			break;
		}
		case 2: {
			IBakedModel closerange = Minecraft.getInstance().getModelManager().getModel(ballistix.client.ClientRegister.MODEL_MISSILELONGRANGE);
			matrixStackIn.translate(0.5f, 4.1f, 0.5f);
		    matrixStackIn.scale(2f, 4f, 2f);
			Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateBlock(tileEntityIn.getLevel(), closerange, tileEntityIn.getBlockState(), tileEntityIn.getBlockPos(), matrixStackIn, bufferIn.getBuffer(RenderType.solid()), false, tileEntityIn.getLevel().random, new Random().nextLong(), 0);
			break;
		}
		default:
			break;
		}
	}
}