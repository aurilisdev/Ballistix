package ballistix.client;

import ballistix.DeferredRegisters;
import ballistix.References;
import ballistix.client.render.entity.RenderBlast;
import ballistix.client.render.entity.RenderExplosive;
import ballistix.client.render.entity.RenderGrenade;
import ballistix.client.render.entity.RenderShrapnel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

@OnlyIn(Dist.CLIENT)
public class ClientRegister {
	public static final ResourceLocation TEXTURE_SHRAPNEL = new ResourceLocation(References.ID + ":textures/model/shrapnel.png");

	public static void setup() {
		RenderingRegistry.registerEntityRenderingHandler(DeferredRegisters.ENTITY_EXPLOSIVE.get(), RenderExplosive::new);
		RenderingRegistry.registerEntityRenderingHandler(DeferredRegisters.ENTITY_GRENADE.get(), RenderGrenade::new);
		RenderingRegistry.registerEntityRenderingHandler(DeferredRegisters.ENTITY_BLAST.get(), RenderBlast::new);
		RenderingRegistry.registerEntityRenderingHandler(DeferredRegisters.ENTITY_SHRAPNEL.get(), RenderShrapnel::new);
	}

	public static boolean shouldMultilayerRender(RenderType type) {
		return type == RenderType.getTranslucent() || type == RenderType.getSolid();
	}

}
