package ballistix.client.shake;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ballistix.Ballistix;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = Ballistix.ID, value = { Dist.CLIENT })
public class CameraShakeManager {

    private final static List<CameraShakeEffect> activeShakes = new ArrayList<>();

    private CameraShakeManager() {
    }

    /**
     * Call this when you want to add a new shake effect.
     */
    public static void addShake(CameraShakeEffect effect) {
	activeShakes.add(effect);
    }

    /**
     * This method should be called in a client tick or render event, to apply the
     * yaw/pitch offsets to the player’s view.
     */
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event) {

	Minecraft mc = Minecraft.getInstance();
	// guard against nullable fields (level/player may be annotated @Nullable)
	final var player = mc.player;
	final var level = mc.level;
	if (player == null || mc.isPaused() || level == null) {
	    return;
	}

	long currentTime = level.getGameTime(); // or System.currentTimeMillis(), whichever is consistent
	float totalYawOffset = 0f;
	float totalPitchOffset = 0f;

	Iterator<CameraShakeEffect> iterator = activeShakes.iterator();
	while (iterator.hasNext()) {
	    CameraShakeEffect effect = iterator.next();
	    if (effect.isComplete(currentTime)) {
		// remove finished shakes
		iterator.remove();
		continue;
	    }
	    float[] offsets = effect.getShakeOffsets(currentTime);
	    totalYawOffset += offsets[0];
	    totalPitchOffset += offsets[1];
	}

	// Now apply the offsets to the player’s rotation angles.
	// We can do a small clamp if we want, to avoid extreme sums if needed.
	if (player.onGround()) {
	    player.setYRot(player.getYRot() + totalYawOffset);
	    player.setXRot(player.getXRot() + totalPitchOffset);
	}
    }

    public static CameraShakeEffect createBlastSourcedEffect(double blastDuration, double blastSize, long worldTime,
	    Vec3 position) {
	Minecraft mc = Minecraft.getInstance();
	double dist;
	if (mc.player != null) {
	    dist = mc.player.position().distanceTo(position);
	} else {
	    // no player available (headless/client not ready) - make effect negligible
	    dist = Double.POSITIVE_INFINITY;
	}
	double damp = Math.exp(-2 * dist / blastSize);
	double amplitude = blastSize / 3.0 * damp;
	long seed = (long) (position.x + position.y * 26 + position.z * 36);
	CameraShakeEffect effect = new CameraShakeEffect(blastDuration, blastDuration / 4.0, blastDuration / 4.0,
		amplitude, 40, worldTime, seed);
	return effect;
    }
}
