package ballistix.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;

import ballistix.common.entity.EntityBallistixFallingBlock;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

public class RenderBallistixFallingBlock extends EntityRenderer<EntityBallistixFallingBlock> {

    private final BlockRenderDispatcher dispatcher;

    public RenderBallistixFallingBlock(EntityRendererProvider.Context context) {
	super(context);
	this.shadowRadius = 0.5F;
	this.dispatcher = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(EntityBallistixFallingBlock entity, float entityYaw, float partialTicks, PoseStack poseStack,
	    MultiBufferSource buffer, int packedLight) {
	BlockState blockstate = entity.getBlockState();
	if (blockstate.getRenderShape() == RenderShape.MODEL) {
	    Level level = entity.level();
	    if (blockstate != level.getBlockState(entity.blockPosition())
		    && blockstate.getRenderShape() != RenderShape.INVISIBLE) {
		poseStack.pushPose();
		BlockPos blockpos = BlockPos.containing(entity.getX(), entity.getBoundingBox().maxY, entity.getZ());
		poseStack.translate(-0.5D, 0.0D, -0.5D);
		var model = this.dispatcher.getBlockModel(blockstate);
		for (var renderType : model.getRenderTypes(blockstate,
			RandomSource.create(blockstate.getSeed(entity.getStartPos())),
			net.minecraftforge.client.model.data.ModelData.EMPTY))
		    this.dispatcher.getModelRenderer().tesselateBlock(level, model, blockstate, blockpos, poseStack,
			    buffer.getBuffer(renderType), false, RandomSource.create(),
			    blockstate.getSeed(entity.getStartPos()), OverlayTexture.NO_OVERLAY,
			    net.minecraftforge.client.model.data.ModelData.EMPTY, renderType);
		poseStack.popPose();
		super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
	    }
	}
    }

    @Override
    public ResourceLocation getTextureLocation(EntityBallistixFallingBlock entity) {
	return TextureAtlas.LOCATION_BLOCKS;
    }

    @Override
    public boolean shouldRender(EntityBallistixFallingBlock livingEntity, Frustum camera, double camX, double camY,
	    double camZ) {
	return camera.isVisible(livingEntity.getBoundingBox());
    }
}
