package ballistix.client;

import ballistix.DeferredRegisters;
import ballistix.client.render.entity.RenderExplosive;
import ballistix.client.render.entity.RenderGrenade;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

@OnlyIn(Dist.CLIENT)
public class ClientRegister {

	public static void setup() {
		RenderingRegistry.registerEntityRenderingHandler(DeferredRegisters.ENTITY_EXPLOSIVE.get(), RenderExplosive::new);
		RenderingRegistry.registerEntityRenderingHandler(DeferredRegisters.ENTITY_GRENADE.get(), RenderGrenade::new);
	}

	public static boolean shouldMultilayerRender(RenderType type) {
		return type == RenderType.getTranslucent() || type == RenderType.getSolid();
	}

}
