package ballistix.client.event;

import java.util.HashMap;

import com.mojang.blaze3d.vertex.PoseStack;

import ballistix.api.blast.IBlast;
import ballistix.client.render.entity.RenderBlast;
import ballistix.common.entity.EntityBlast;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

public class RegisterBlastRenderersEvent extends Event implements IModBusEvent {

    private final HashMap<ResourceLocation, BlastRenderer> renderers = new HashMap<>();

    public void register(IBlast blast, BlastRenderer renderer) {
        renderers.put(blast.id(), renderer);
    }

    public void process() {
        RenderBlast.RENDERERS.clear();
        RenderBlast.RENDERERS.putAll(renderers);
    }

    public static interface BlastRenderer {

        void render(EntityBlast entityIn, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource bufferIn, int packedLightIn);
    }

}
