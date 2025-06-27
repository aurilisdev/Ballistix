package ballistix.client.render.entity;

import java.util.Random;

import com.mojang.blaze3d.matrix.MatrixStack;

import ballistix.common.entity.EntityFallingBlock;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RenderFallingBlock extends EntityRenderer<EntityFallingBlock> {

	public RenderFallingBlock(EntityRendererManager manager) {
		super(manager);
		this.shadowRadius = 0.5F;
	}

	@Override
	public void render(EntityFallingBlock entity, float yaw, float pitch, MatrixStack matrixstack, IRenderTypeBuffer buffer, int packedlight) {
		BlockState blockstate = entity.getBlockState();
		if (blockstate.getRenderShape() == BlockRenderType.MODEL) {
			World world = entity.getLevel();
			if (blockstate != world.getBlockState(entity.blockPosition()) && blockstate.getRenderShape() != BlockRenderType.INVISIBLE) {
				matrixstack.pushPose();
				BlockPos blockpos = new BlockPos(entity.getX(), entity.getBoundingBox().maxY, entity.getZ());
				matrixstack.translate(-0.5D, 0.0D, -0.5D);
				BlockRendererDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRenderer();
				for (net.minecraft.client.renderer.RenderType type : net.minecraft.client.renderer.RenderType.chunkBufferLayers()) {
					if (RenderTypeLookup.canRenderInLayer(blockstate, type)) {
						net.minecraftforge.client.ForgeHooksClient.setRenderLayer(type);
						blockrendererdispatcher.getModelRenderer().tesselateBlock(world, blockrendererdispatcher.getBlockModel(blockstate), blockstate, blockpos, matrixstack, buffer.getBuffer(type), false, new Random(), blockstate.getSeed(entity.getStartPos()), OverlayTexture.NO_OVERLAY);
					}
				}
				net.minecraftforge.client.ForgeHooksClient.setRenderLayer(null);
				matrixstack.popPose();
				super.render(entity, yaw, pitch, matrixstack, buffer, packedlight);
			}
		}
	}

	@Override
	public ResourceLocation getTextureLocation(EntityFallingBlock p_110775_1_) {
		return AtlasTexture.LOCATION_BLOCKS;
	}

}
