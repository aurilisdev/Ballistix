package ballistix.client.shake;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ballistix.Ballistix;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

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
	 * This method should be called in a client tick or render event, to apply the yaw/pitch offsets to the player’s view.
	 */
	@SubscribeEvent
	public static void onClientTick(ClientTickEvent event) {

		if (event.phase == Phase.END) {
			return;
		}

		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null || mc.isPaused()) {
			return;
		}

		long currentTime = mc.level.getGameTime(); // or System.currentTimeMillis(), whichever is consistent
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
		if (mc.player.isOnGround()) {
			mc.player.setYRot(mc.player.getYRot() + totalYawOffset);
			mc.player.setXRot(mc.player.getXRot() + totalPitchOffset);
		}
	}

	public static CameraShakeEffect createBlastSourcedEffect(double blastDuration, double blastSize, long worldTime, Vec3 position) {
		double dist = Minecraft.getInstance().player.position().distanceTo(position);
		double damp = Math.exp(-2 * dist / blastSize);
		double amplitude = blastSize / 3.0 * damp;
		long seed = (long) (position.x + position.y * 26 + position.z * 36);
		CameraShakeEffect effect = new CameraShakeEffect(blastDuration, blastDuration / 4.0, blastDuration / 4.0, amplitude, 40, worldTime, seed);
		return effect;
	}
}
