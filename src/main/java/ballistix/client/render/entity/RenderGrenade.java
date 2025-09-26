package ballistix.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;

import ballistix.api.blast.IBlast;
import ballistix.common.blast.util.Blast;
import ballistix.common.entity.EntityGrenade;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class RenderGrenade extends EntityRenderer<EntityGrenade> {
	
    private ItemEntity itemEntity;
    private ItemRenderer itemRenderer;

    public RenderGrenade(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
        itemRenderer = new ItemRenderer(renderManagerIn, Minecraft.getInstance().getItemRenderer());
        shadowRadius = 0.15F;
        shadowStrength = 0.75F;
    }

    @Override
    public void render(EntityGrenade entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
    	IBlast blast = entityIn.getExplosiveType();
        if (blast != null) {
            matrixStackIn.pushPose();
            if (itemEntity == null) {
                itemEntity = new ItemEntity(EntityType.ITEM, entityIn.level);
            }
            itemEntity.setPos(entityIn.getX(), entityIn.getY(), entityIn.getZ());
            itemEntity.setItem(new ItemStack(Blast.BLAST_TO_GRENADE_MAP.get(blast)));
            matrixStackIn.translate(0, -0.5 / 16.0, 0);
            itemRenderer.render(itemEntity, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
            matrixStackIn.popPose();
        }
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    public ResourceLocation getTextureLocation(EntityGrenade entity) {
        return AtlasTexture.LOCATION_BLOCKS;
    }
}
