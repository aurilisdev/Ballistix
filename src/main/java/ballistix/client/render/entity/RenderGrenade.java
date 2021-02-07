package ballistix.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;

import ballistix.DeferredRegisters;
import ballistix.common.block.SubtypeExplosive;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderGrenade extends EntityRenderer<EntityGrenade> {
	private ItemEntity itemEntity;
	private ItemRenderer itemRenderer;

	public RenderGrenade(EntityRendererManager renderManagerIn) {
		super(renderManagerIn);
		itemRenderer = new ItemRenderer(renderManagerIn, Minecraft.getInstance().getItemRenderer());
		shadowSize = 0.15F;
		shadowOpaque = 0.75F;
	}

	@Override
	public void render(EntityGrenade entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
		SubtypeExplosive subtype = entityIn.getExplosiveType();
		if (subtype != null) {
			matrixStackIn.push();
			if (itemEntity == null) {
				itemEntity = new ItemEntity(EntityType.ITEM, entityIn.world);
			}

			// Apply data from entity
			itemEntity.setWorld(entityIn.world);
			itemEntity.setPosition(entityIn.getPosX(), entityIn.getPosY(), entityIn.getPosZ());
			itemEntity.setItem(new ItemStack(DeferredRegisters.SUBTYPEITEM_MAPPINGS.get(subtype)));
			itemRenderer.render(itemEntity, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
			matrixStackIn.pop();
		}
		super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
	}

	@Override
	@SuppressWarnings("deprecation")
	public ResourceLocation getEntityTexture(EntityGrenade entity) {
		return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
	}
}
