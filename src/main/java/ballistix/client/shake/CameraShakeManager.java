package ballistix.client.shake;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import ballistix.Ballistix;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = Ballistix.ID, value = Dist.CLIENT)
public class CameraShakeManager {

    private static final List<CameraShakeEffect> ACTIVE_SHAKES = new ArrayList<>();
    @Nullable
    private static ClientLevel shakeLevel;

    private CameraShakeManager() {
    }

    public static void startBlastShake(ClientLevel level, float blastScale, double distanceFraction) {
	if (shakeLevel != level) {
	    ACTIVE_SHAKES.clear();
	    shakeLevel = level;
	}
	double duration = Mth.clamp(10 + 1.25 * blastScale, 12, 48);
	double amplitude = Math.min(6, 0.8 + 0.2 * blastScale) * Math.exp(-2.5 * distanceFraction);
	ACTIVE_SHAKES.add(new CameraShakeEffect(duration, amplitude, level.getGameTime(), level.random.nextLong()));
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event) {
	ClientLevel level = Minecraft.getInstance().level;
	if (shakeLevel != level) {
	    ACTIVE_SHAKES.clear();
	    shakeLevel = level;
	} else if (level != null && !ACTIVE_SHAKES.isEmpty()) {
	    long gameTime = level.getGameTime();
	    ACTIVE_SHAKES.removeIf(effect -> effect.isComplete(gameTime));
	}
    }

    @SubscribeEvent
    public static void onCameraAngles(ViewportEvent.ComputeCameraAngles event) {
	Minecraft minecraft = Minecraft.getInstance();
	ClientLevel level = minecraft.level;
	if (level == null || level != shakeLevel || ACTIVE_SHAKES.isEmpty())
	    return;

	float screenEffectScale = minecraft.options.screenEffectScale().get().floatValue();
	if (screenEffectScale == 0)
	    return;

	double gameTime = level.getGameTime() + event.getPartialTick();
	float pitch = 0;
	float roll = 0;
	for (CameraShakeEffect effect : ACTIVE_SHAKES) {
	    CameraShakeEffect.Offsets offsets = effect.getShakeOffsets(gameTime);
	    pitch += offsets.pitch();
	    roll += offsets.roll();
	}

	event.setPitch(event.getPitch() + Mth.clamp(pitch, -3, 3) * screenEffectScale);
	event.setRoll(event.getRoll() + Mth.clamp(roll, -5, 5) * screenEffectScale);
    }
}
