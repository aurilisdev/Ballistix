package ballistix.client;

import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientRegister {

	public static void setup() {
	}

	public static boolean shouldMultilayerRender(RenderType type) {
		return type == RenderType.getTranslucent() || type == RenderType.getSolid();
	}

}
