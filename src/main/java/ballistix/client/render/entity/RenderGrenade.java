package ballistix.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;

import ballistix.DeferredRegisters;
import ballistix.common.block.SubtypeBlast;
import ballistix.common.entity.EntityGrenade;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderGrenade extends EntityRenderer<EntityGrenade> {
    private ItemEntity itemEntity;
    private ItemEntityRenderer itemRenderer;

    public RenderGrenade(Context renderManagerIn) {
	super(renderManagerIn);
	itemRenderer = new ItemEntityRenderer(renderManagerIn);
	shadowRadius = 0.15F;
	shadowStrength = 0.75F;
    }

    @Override
    public void render(EntityGrenade entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn,
	    int packedLightIn) {
	SubtypeBlast subtype = entityIn.getExplosiveType();
	if (subtype != null) {
	    matrixStackIn.pushPose();
	    if (itemEntity == null) {
		itemEntity = new ItemEntity(EntityType.ITEM, entityIn.level);
	    }
	    itemEntity.setPos(entityIn.getX(), entityIn.getY(), entityIn.getZ());
	    itemEntity.setItem(new ItemStack(DeferredRegisters.SUBTYPEITEM_MAPPINGS.get(subtype)));
	    itemRenderer.render(itemEntity, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
	    matrixStackIn.popPose();
	}
	super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    @Deprecated
    public ResourceLocation getTextureLocation(EntityGrenade entity) {
	return InventoryMenu.BLOCK_ATLAS;
    }
}
