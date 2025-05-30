package ballistix.client.render.entity;

import java.util.HashMap;

import com.mojang.blaze3d.matrix.MatrixStack;

import ballistix.api.blast.IBlast;
import ballistix.client.event.RegisterBlastRenderersEvent;
import ballistix.common.entity.EntityBlast;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.ResourceLocation;

public class RenderBlast extends EntityRenderer<EntityBlast> {
	
	public static final HashMap<ResourceLocation, RegisterBlastRenderersEvent.BlastRenderer> RENDERERS = new HashMap<>();

	public RenderBlast(EntityRendererManager renderManagerIn) {
		super(renderManagerIn);
		shadowRadius = 0.5F;
	}

	@Override
	public void render(EntityBlast entityIn, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int packedLightIn) {
		IBlast blast = entityIn.getBlastType();

        if(blast == null) {
            super.render(entityIn, entityYaw, partialTicks, matrixStack, bufferIn, packedLightIn);
            return;
        }
        
        RegisterBlastRenderersEvent.BlastRenderer renderer = RENDERERS.get(blast.id());
        
        if(renderer == null) {
            super.render(entityIn, entityYaw, partialTicks, matrixStack, bufferIn, packedLightIn);
            return;
        }
        
        matrixStack.pushPose();
        
        renderer.render(entityIn, entityYaw, partialTicks, matrixStack, bufferIn, packedLightIn);
        
		matrixStack.popPose();
		
		super.render(entityIn, entityYaw, partialTicks, matrixStack, bufferIn, packedLightIn);
	}

	@Override
	public boolean shouldRender(EntityBlast b, ClippingHelper f, double x, double y, double z) {
		return true;
	}

	@Override
	public ResourceLocation getTextureLocation(EntityBlast entity) {
		return AtlasTexture.LOCATION_BLOCKS;
	}
}
