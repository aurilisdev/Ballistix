package ballistix.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import ballistix.client.ClientRegister;
import ballistix.common.entity.EntityShrapnel;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderShrapnel extends EntityRenderer<EntityShrapnel> {
	public RenderShrapnel(EntityRendererManager renderManagerIn) {
		super(renderManagerIn);
		shadowRadius = 0.1F;
	}

	@Override
	public void render(EntityShrapnel entityShrapnel, float yaw, float partialticks, MatrixStack stack, IRenderTypeBuffer buffer, int light) {
		stack.pushPose();
		stack.mulPose(Vector3f.YP.rotationDegrees(MathHelper.lerp(partialticks, entityShrapnel.yRotO, entityShrapnel.yRot) - 90.0F));
		stack.mulPose(Vector3f.ZP.rotationDegrees(MathHelper.lerp(partialticks, entityShrapnel.xRotO, entityShrapnel.xRot)));
		stack.mulPose(Vector3f.XP.rotationDegrees(45.0F));
		stack.scale(0.05625F, 0.05625F, 0.05625F);
		stack.translate(-4.0D, 0.0D, 0.0D);
		IVertexBuilder vertexconsumer = buffer.getBuffer(RenderType.entityCutout(getTextureLocation(entityShrapnel)));
		MatrixStack.Entry pose = stack.last();
		Matrix4f matrix4f = pose.pose();
		Matrix3f matrix3f = pose.normal();
		vertex(matrix4f, matrix3f, vertexconsumer, -7, -2, -2, 0.0F, 0.15625F, -1, 0, 0, light);
		vertex(matrix4f, matrix3f, vertexconsumer, -7, -2, 2, 0.15625F, 0.15625F, -1, 0, 0, light);
		vertex(matrix4f, matrix3f, vertexconsumer, -7, 2, 2, 0.15625F, 0.3125F, -1, 0, 0, light);
		vertex(matrix4f, matrix3f, vertexconsumer, -7, 2, -2, 0.0F, 0.3125F, -1, 0, 0, light);
		vertex(matrix4f, matrix3f, vertexconsumer, -7, 2, -2, 0.0F, 0.15625F, 1, 0, 0, light);
		vertex(matrix4f, matrix3f, vertexconsumer, -7, 2, 2, 0.15625F, 0.15625F, 1, 0, 0, light);
		vertex(matrix4f, matrix3f, vertexconsumer, -7, -2, 2, 0.15625F, 0.3125F, 1, 0, 0, light);
		vertex(matrix4f, matrix3f, vertexconsumer, -7, -2, -2, 0.0F, 0.3125F, 1, 0, 0, light);
		for (int j = 0; j < 4; ++j) {
			stack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
			vertex(matrix4f, matrix3f, vertexconsumer, -8, -2, 0, 0.0F, 0.0F, 0, 1, 0, light);
			vertex(matrix4f, matrix3f, vertexconsumer, 8, -2, 0, 0.5F, 0.0F, 0, 1, 0, light);
			vertex(matrix4f, matrix3f, vertexconsumer, 8, 2, 0, 0.5F, 0.15625F, 0, 1, 0, light);
			vertex(matrix4f, matrix3f, vertexconsumer, -8, 2, 0, 0.0F, 0.15625F, 0, 1, 0, light);
		}

		stack.popPose();
		super.render(entityShrapnel, yaw, partialticks, stack, buffer, light);
	}

	public void vertex(Matrix4f matrix, Matrix3f normals, IVertexBuilder consumer, int pOffsetX, int pOffsetY, int pOffsetZ, float pTextureX, float pTextureY, int pNormalX, int ny, int nx, int pPackedLight) {
		consumer.vertex(matrix, pOffsetX, pOffsetY, pOffsetZ).color(255, 255, 255, 255).uv(pTextureX, pTextureY).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(pPackedLight).normal(normals, pNormalX, nx, ny).endVertex();
	}

	@Override
	public ResourceLocation getTextureLocation(EntityShrapnel entity) {
		return ClientRegister.TEXTURE_SHRAPNEL;
	}
}