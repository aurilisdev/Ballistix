package ballistix.client.render.tile;

import java.util.Random;

import com.mojang.blaze3d.vertex.PoseStack;

import ballistix.api.silo.ILauncherPlatform;
import ballistix.client.BallistixClientRegister;
import ballistix.common.item.ItemMissile;
import ballistix.registers.BallistixItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import voltaic.client.render.AbstractTileRenderer;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.tile.components.type.ComponentInventory;

public class RenderLauncherPlatform<T extends GenericTile & ILauncherPlatform> extends AbstractTileRenderer<T> {

    public RenderLauncherPlatform(BlockEntityRendererProvider.Context context) {
	super(context);
    }

    @Override
    public void render(T tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn,
	    int combinedLightIn, int combinedOverlayIn) {

	ItemStack stack = tileEntityIn.<ComponentInventory>getComponent(IComponentType.Inventory).getItem(0);

	if (stack.isEmpty()) {
	    return;
	}

	if (stack.getItem() instanceof ItemMissile missile) {

	    matrixStackIn.pushPose();

	    BakedModel model;

	    if (missile.missile.ordinal() == 0) {

		model = Minecraft.getInstance().getModelManager().getModel(BallistixClientRegister.MODEL_MISSILETIER1);
		matrixStackIn.translate(0.5f, 0.87, 0.5f);
		matrixStackIn.scale(1f, 0.75f, 1f);

	    } else if (missile.missile.ordinal() == 1) {

		model = Minecraft.getInstance().getModelManager().getModel(BallistixClientRegister.MODEL_MISSILETIER2);
		matrixStackIn.translate(0.5f, 1.1f, 0.5f);
		matrixStackIn.scale(1f, 1f, 1f);

	    } else {
		if (missile.missile.ordinal() == 2) {

		    model = Minecraft.getInstance().getModelManager()
			    .getModel(BallistixClientRegister.MODEL_MISSILETIER3);

		} else {
		    model = Minecraft.getInstance().getModelManager()
			    .getModel(BallistixClientRegister.MODEL_MISSILECLUSTER);
		}
		matrixStackIn.translate(0.5f, 1.1f, 0.5f);
		matrixStackIn.scale(1f, 1.25f, 1f);
	    }

	    Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateBlock(tileEntityIn.getLevel(), model,
		    tileEntityIn.getBlockState(), tileEntityIn.getBlockPos(), matrixStackIn,
		    bufferIn.getBuffer(RenderType.solid()), false, tileEntityIn.getLevel().random,
		    new Random().nextLong(), 0);

	    matrixStackIn.popPose();

	} else if (stack.is(BallistixItems.ITEM_AAMISSILEMK2)) {

	    matrixStackIn.pushPose();

	    BakedModel model = Minecraft.getInstance().getModelManager()
		    .getModel(BallistixClientRegister.MODEL_AAMISSILE_MK2);

	    matrixStackIn.translate(0.5f, 1F, 0.5f);
	    // matrixStackIn.scale(1.5f, 2.5f, 1.5f);

	    Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateBlock(tileEntityIn.getLevel(), model,
		    tileEntityIn.getBlockState(), tileEntityIn.getBlockPos(), matrixStackIn,
		    bufferIn.getBuffer(RenderType.solid()), false, tileEntityIn.getLevel().random,
		    new Random().nextLong(), 0);

	    matrixStackIn.popPose();

	}

    }

    @Override
    public AABB getRenderBoundingBox(T blockEntity) {
	return super.getRenderBoundingBox(blockEntity).inflate(10);
    }
}
