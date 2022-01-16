package ballistix.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;

import ballistix.DeferredRegisters;
import ballistix.common.entity.EntityMinecart;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.MinecartModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderMinecart extends EntityRenderer<EntityMinecart> {
	private static final ResourceLocation MINECART_LOCATION = new ResourceLocation("textures/entity/minecart.png");
	protected final EntityModel<EntityMinecart> model;

	public RenderMinecart(EntityRendererProvider.Context context) {
		super(context);
		this.shadowRadius = 0.7F;
		this.model = new MinecartModel<>(context.bakeLayer(ModelLayers.MINECART));
	}

	@Override
	public void render(EntityMinecart entity, float yaw, float partial, PoseStack stack, MultiBufferSource source, int light) {
		super.render(entity, yaw, partial, stack, source, light);
		stack.pushPose();
		long i = entity.getId() * 493286711L;
		i = i * i * 4392167121L + i * 98761L;
		float f = (((i >> 16 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
		float f1 = (((i >> 20 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
		float f2 = (((i >> 24 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
		stack.translate(f, f1, f2);
		double d0 = Mth.lerp(partial, entity.xOld, entity.getX());
		double d1 = Mth.lerp(partial, entity.yOld, entity.getY());
		double d2 = Mth.lerp(partial, entity.zOld, entity.getZ());
		Vec3 vec3 = entity.getPos(d0, d1, d2);
		float f3 = Mth.lerp(partial, entity.xRotO, entity.getXRot());
		if (vec3 != null) {
			Vec3 vec31 = entity.getPosOffs(d0, d1, d2, 0.3F);
			Vec3 vec32 = entity.getPosOffs(d0, d1, d2, -0.3F);
			if (vec31 == null) {
				vec31 = vec3;
			}

			if (vec32 == null) {
				vec32 = vec3;
			}

			stack.translate(vec3.x - d0, (vec31.y + vec32.y) / 2.0D - d1, vec3.z - d2);
			Vec3 vec33 = vec32.add(-vec31.x, -vec31.y, -vec31.z);
			if (vec33.length() != 0.0D) {
				vec33 = vec33.normalize();
				yaw = (float) (Math.atan2(vec33.z, vec33.x) * 180.0D / Math.PI);
				f3 = (float) (Math.atan(vec33.y) * 73.0D);
			}
		}

		stack.translate(0.0D, 0.375D, 0.0D);
		stack.mulPose(Vector3f.YP.rotationDegrees(180.0F - yaw));
		stack.mulPose(Vector3f.ZP.rotationDegrees(-f3));
		float f5 = entity.getHurtTime() - partial;
		float f6 = entity.getDamage() - partial;
		if (f6 < 0.0F) {
			f6 = 0.0F;
		}

		if (f5 > 0.0F) {
			stack.mulPose(Vector3f.XP.rotationDegrees(Mth.sin(f5) * f5 * f6 / 10.0F * entity.getHurtDir()));
		}

		int j = entity.getDisplayOffset();
		if (entity.getExplosiveType() != null) {
			BlockState blockstate = DeferredRegisters.SUBTYPEBLOCK_MAPPINGS.get(entity.getExplosiveType()).defaultBlockState();
			if (blockstate != null) {
				if (blockstate.getRenderShape() != RenderShape.INVISIBLE) {
					stack.pushPose();
					stack.scale(0.75F, 0.75F, 0.75F);
					stack.translate(-0.5D, (j - 8) / 16.0F, 0.5D);
					stack.mulPose(Vector3f.YP.rotationDegrees(90.0F));
					renderMinecartContents(entity, partial, blockstate, stack, source, light);
					stack.popPose();
				}
			}
		}

		stack.scale(-1.0F, -1.0F, 1.0F);
		model.setupAnim(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
		VertexConsumer vertexconsumer = source.getBuffer(model.renderType(getTextureLocation(entity)));
		model.renderToBuffer(stack, vertexconsumer, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
		stack.popPose();
	}

	@Override
	public ResourceLocation getTextureLocation(EntityMinecart entity) {
		return MINECART_LOCATION;
	}

	protected void renderMinecartContents(EntityMinecart entity, float p1, BlockState state, PoseStack stack, MultiBufferSource source, int p2) {
		int i = entity.getFuse();
		if (i > -1 && i - p1 + 1.0F < 10.0F) {
			float f = 1.0F - (i - p1 + 1.0F) / 10.0F;
			f = Mth.clamp(f, 0.0F, 1.0F);
			f *= f;
			f *= f;
			float f1 = 1.0F + f * 0.3F;
			stack.scale(f1, f1, f1);
		}
		RenderExplosive.renderTntFlash(state, stack, source, p2, i > -1 && i / 5 % 2 == 0);
	}
}
