package ballistix.client.render.tile;

import java.util.Random;

import com.mojang.blaze3d.vertex.PoseStack;

import ballistix.common.item.ItemMissile;
import ballistix.common.tile.TileLauncherControlPanelT1;
import electrodynamics.client.render.tile.AbstractTileRenderer;
import electrodynamics.prefab.tile.components.IComponentType;
import electrodynamics.prefab.tile.components.type.ComponentInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

public class RenderMissileSilo extends AbstractTileRenderer<TileLauncherControlPanelT1> {

	public RenderMissileSilo(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void render(TileLauncherControlPanelT1 tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {

		ItemStack stack = tileEntityIn.<ComponentInventory>getComponent(IComponentType.Inventory).getItem(0);

		if (stack.isEmpty()) {
			return;
		}

		int type = ((ItemMissile) stack.getItem()).missile.ordinal();

		if(type == -1) {
			return;
		}

		matrixStackIn.pushPose();

		BakedModel model;

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

		Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateBlock(tileEntityIn.getLevel(), model, tileEntityIn.getBlockState(), tileEntityIn.getBlockPos(), matrixStackIn, bufferIn.getBuffer(RenderType.solid()), false, tileEntityIn.getLevel().random, new Random().nextLong(), 0);

		matrixStackIn.popPose();
	}

	@Override
	public AABB getRenderBoundingBox(TileLauncherControlPanelT1 blockEntity) {
		return super.getRenderBoundingBox(blockEntity).inflate(10);
	}
}
