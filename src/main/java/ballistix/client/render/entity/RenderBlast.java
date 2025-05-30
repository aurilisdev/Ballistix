package ballistix.client.render.entity;

import ballistix.api.blast.IBlast;
import ballistix.client.event.RegisterBlastRenderersEvent;
import com.mojang.blaze3d.vertex.PoseStack;

import ballistix.common.entity.EntityBlast;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

import java.util.HashMap;

public class RenderBlast extends EntityRenderer<EntityBlast> {

    public static final HashMap<ResourceLocation, RegisterBlastRenderersEvent.BlastRenderer> RENDERERS = new HashMap<>();

    public RenderBlast(Context renderManagerIn) {
        super(renderManagerIn);
        shadowRadius = 0.5F;
    }

    @Override
    public void render(EntityBlast entityIn, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource bufferIn, int packedLightIn) {

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
    public boolean shouldRender(EntityBlast b, Frustum f, double x, double y, double z) {
        return true;
    }

    @Override
    public ResourceLocation getTextureLocation(EntityBlast entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}
