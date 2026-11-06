package ballistix.client.shake;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import ballistix.Ballistix;
import ballistix.common.settings.BallistixConfig;
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

    public static void startBlastShake(ClientLevel level, float coreRadius, double distanceFraction) {
	switchLevel(level);
	double heaviness = Mth.clamp((coreRadius - 8) / 27.0, 0, 1);
	double duration = 20 + 2.2 * coreRadius + 60 * heaviness;
	double peakAmplitude = (1.2 + 0.3 * coreRadius) * (1 + 0.75 * heaviness);
	double amplitude = peakAmplitude * Math.exp(-(1.6 - 0.7 * heaviness) * distanceFraction);
	ACTIVE_SHAKES.add(new CameraShakeEffect(duration, amplitude, level.getGameTime(), level.random.nextLong()));
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event) {
	ClientLevel level = Minecraft.getInstance().level;
	if (!switchLevel(level) && level != null && !ACTIVE_SHAKES.isEmpty()) {
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

	float screenEffectScale = minecraft.options.screenEffectScale().get().floatValue()
		* BallistixConfig.INSTANCE.BLAST_CAMERA_SHAKE_STRENGTH.get().floatValue();
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

	event.setPitch(event.getPitch() + Mth.clamp(pitch, -6, 6) * screenEffectScale);
	event.setRoll(event.getRoll() + Mth.clamp(roll, -10, 10) * screenEffectScale);
    }

    /** Drops the shakes of a previous world. Returns whether the level changed. */
    private static boolean switchLevel(@Nullable ClientLevel level) {
	if (shakeLevel == level)
	    return false;
	ACTIVE_SHAKES.clear();
	shakeLevel = level;
	return true;
    }
}
