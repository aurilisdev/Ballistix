package ballistix.client.event;

import ballistix.api.blast.IBlast;
import ballistix.client.render.entity.RenderBlast;
import ballistix.common.entity.EntityBlast;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.lifecycle.IModBusEvent;

import java.util.HashMap;

import com.mojang.blaze3d.matrix.MatrixStack;

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

        void render(EntityBlast entityIn, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int packedLightIn);
    }

}
