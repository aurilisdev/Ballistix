package ballistix.client.render.entity;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import ballistix.client.BallistixClientRegister;
import ballistix.common.entity.EntityShrapnel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import voltaic.prefab.utilities.math.Color;
import voltaic.prefab.utilities.math.MathUtils;

public class RenderShrapnel extends EntityRenderer<EntityShrapnel> {
	public RenderShrapnel(Context renderManagerIn) {
		super(renderManagerIn);
		shadowRadius = 0.1F;
	}

	@Override
	public void render(EntityShrapnel entityShrapnel, float yaw, float partialticks, PoseStack stack, MultiBufferSource buffer, int light) {
		stack.pushPose();
		stack.mulPose(MathUtils.rotVectorQuaternionDeg(Mth.lerp(partialticks, entityShrapnel.yRotO, entityShrapnel.getYRot()) - 90.0F, MathUtils.YP));
		stack.mulPose(MathUtils.rotVectorQuaternionDeg(Mth.lerp(partialticks, entityShrapnel.xRotO, entityShrapnel.getXRot()), MathUtils.ZP));
		stack.mulPose(MathUtils.rotVectorQuaternionDeg(45.0F, MathUtils.XP));
		// stack.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(partialticks, entityShrapnel.yRotO, entityShrapnel.getYRot()) - 90.0F));
		// stack.mulPose(Vector3f.ZP.rotationDegrees(Mth.lerp(partialticks, entityShrapnel.xRotO, entityShrapnel.getXRot())));
		// stack.mulPose(Vector3f.XP.rotationDegrees(45.0F));
		stack.scale(0.05625F, 0.05625F, 0.05625F);
		stack.translate(-4.0D, 0.0D, 0.0D);
		VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.entityCutout(getTextureLocation(entityShrapnel)));
		PoseStack.Pose pose = stack.last();
		Matrix4f matrix4f = pose.pose();
		vertex(matrix4f, pose, vertexconsumer, -7, -2, -2, 0.0F, 0.15625F, -1, 0, 0, light);
		vertex(matrix4f, pose, vertexconsumer, -7, -2, 2, 0.15625F, 0.15625F, -1, 0, 0, light);
		vertex(matrix4f, pose, vertexconsumer, -7, 2, 2, 0.15625F, 0.3125F, -1, 0, 0, light);
		vertex(matrix4f, pose, vertexconsumer, -7, 2, -2, 0.0F, 0.3125F, -1, 0, 0, light);
		vertex(matrix4f, pose, vertexconsumer, -7, 2, -2, 0.0F, 0.15625F, 1, 0, 0, light);
		vertex(matrix4f, pose, vertexconsumer, -7, 2, 2, 0.15625F, 0.15625F, 1, 0, 0, light);
		vertex(matrix4f, pose, vertexconsumer, -7, -2, 2, 0.15625F, 0.3125F, 1, 0, 0, light);
		vertex(matrix4f, pose, vertexconsumer, -7, -2, -2, 0.0F, 0.3125F, 1, 0, 0, light);
		for (int j = 0; j < 4; ++j) {
			stack.mulPose(MathUtils.rotVectorQuaternionDeg(90.0F, MathUtils.XP));
			// stack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
			vertex(matrix4f, pose, vertexconsumer, -8, -2, 0, 0.0F, 0.0F, 0, 1, 0, light);
			vertex(matrix4f, pose, vertexconsumer, 8, -2, 0, 0.5F, 0.0F, 0, 1, 0, light);
			vertex(matrix4f, pose, vertexconsumer, 8, 2, 0, 0.5F, 0.15625F, 0, 1, 0, light);
			vertex(matrix4f, pose, vertexconsumer, -8, 2, 0, 0.0F, 0.15625F, 0, 1, 0, light);
		}

		stack.popPose();
		super.render(entityShrapnel, yaw, partialticks, stack, buffer, light);
	}

	public void vertex(Matrix4f matrix, PoseStack.Pose pose, VertexConsumer consumer, int pOffsetX, int pOffsetY, int pOffsetZ, float pTextureX, float pTextureY, int pNormalX, int ny, int nx, int pPackedLight) {
		consumer.addVertex(matrix, pOffsetX, pOffsetY, pOffsetZ).setColor(Color.WHITE.color()).setUv(pTextureX, pTextureY).setOverlay(OverlayTexture.NO_OVERLAY).setLight(pPackedLight).setNormal(pose, pNormalX, nx, ny);
	}

	@Override
	public ResourceLocation getTextureLocation(EntityShrapnel entity) {
		return BallistixClientRegister.TEXTURE_SHRAPNEL;
	}
}
