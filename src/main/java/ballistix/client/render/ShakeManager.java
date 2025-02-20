package ballistix.client.render;

import com.mojang.blaze3d.vertex.PoseStack;

import ballistix.References;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = References.ID, value = { Dist.CLIENT })
public class ShakeManager {
    private static int shakeTicksRemaining = 0;
    private static float currentIntensity = 0.0F;

    // Called by the packet handler
    public static void startShake(float intensity, int duration) {
	shakeTicksRemaining = duration;
	currentIntensity = intensity;
    }

    public static boolean isShaking() {
	return shakeTicksRemaining > 0;
    }

    public static void applyShakeIfActive(PoseStack poseStack, Level level) {
	if (shakeTicksRemaining > 0) {
	    shakeTicksRemaining--;
	}
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
	if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SKY ) {
	    if (ShakeManager.isShaking()) {
		// Do Shake
	    }
	}
    }
}
