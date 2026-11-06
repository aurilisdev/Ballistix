package ballistix.client.effect;

import java.util.ArrayList;
import java.util.List;

import ballistix.Ballistix;
import ballistix.client.effect.particle.BlastParticleRenderer;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.packet.type.client.particle.PacketCraterGround;
import ballistix.common.packet.type.client.particle.PacketStartBlastEffects;
import ballistix.common.settings.BallistixConfig;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.network.registration.NetworkRegistry;

/**
 * Entry point for the visual effects of the larger blasts. The server announces a detonation and reports the crater
 * it carves; each announced blast becomes a {@link BlastSequence} that runs here until it has finished.
 * The HUD renders the strongest active {@link BlastStyle.ScreenFlash} in the current level.
 */
@EventBusSubscriber(modid = Ballistix.ID, value = Dist.CLIENT)
public final class BlastEffects {

    private static final int MAX_SEQUENCES = 6;
    private static final List<BlastSequence> RUNNING = new ArrayList<>();

    private BlastEffects() {
    }

    /** Plays the effects locally, for a server that does not announce detonations itself. */
    public static void startClientFallback(ClientLevel level, BlockPos position, SubtypeBlast blast, double radius) {
	if (!serverSends(PacketStartBlastEffects.TYPE))
	    start(level, position, blast, radius);
    }

    public static void start(ClientLevel level, BlockPos position, SubtypeBlast blast, double radius) {
	BlastStyle style = BlastStyle.of(blast);
	if (style == null || !Double.isFinite(radius) || radius <= 0)
	    return;

	RUNNING.removeIf(sequence -> !sequence.isIn(level));
	if (RUNNING.size() >= MAX_SEQUENCES)
	    RUNNING.removeFirst();
	BlastScene scene = new BlastScene(level, Vec3.atCenterOf(position), style, radius,
		Minecraft.getInstance().options.particles().get());
	RUNNING.add(new BlastSequence(scene, serverSends(PacketCraterGround.TYPE)));
    }

    /** Passes crater floors the server has just carved on to the newest blast they belong to. */
    public static void reportCraterFloors(ClientLevel level, BlockPos origin, SubtypeBlast blast,
	    List<BlockPos> floors) {
	BlastStyle style = BlastStyle.of(blast);
	if (style == null)
	    return;

	Vec3 centre = Vec3.atCenterOf(origin);
	for (int i = RUNNING.size() - 1; i >= 0; i--) {
	    BlastSequence sequence = RUNNING.get(i);
	    if (sequence.matches(level, centre, style) && sequence.acceptsCraterFloors()) {
		sequence.reportCraterFloors(floors);
		return;
	    }
	}
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
	Minecraft minecraft = Minecraft.getInstance();
	BlastParticleRenderer.tick(minecraft.level, minecraft.isPaused());
	RUNNING.removeIf(sequence -> !sequence.isIn(minecraft.level));
	if (!minecraft.isPaused())
	    RUNNING.removeIf(BlastSequence::tick);
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Pre event) {
	if (RUNNING.isEmpty())
	    return;
	Minecraft minecraft = Minecraft.getInstance();
	float flashStrength = BallistixConfig.INSTANCE.BLAST_SCREEN_FLASH_STRENGTH.get().floatValue();
	float screenEffectScale = minecraft.options.screenEffectScale().get().floatValue();
	if (screenEffectScale == 0 || flashStrength == 0)
	    return;

	Camera camera = minecraft.gameRenderer.getMainCamera();
	float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
	float strength = 0;
	for (BlastSequence sequence : RUNNING) {
	    if (sequence.isIn(minecraft.level))
		strength = Math.max(strength, strengthOf(sequence, camera, partialTick));
	}

	int opacity = Mth.clamp(Math.round(255 * strength * screenEffectScale * flashStrength), 0, 255);
	if (opacity > 0) {
	    GuiGraphics graphics = event.getGuiGraphics();
	    graphics.fill(0, 0, graphics.guiWidth(), graphics.guiHeight(), opacity << 24 | 0xFFFFFF);
	}
    }

    private static float strengthOf(BlastSequence sequence, Camera camera, float partialTick) {
	BlastStyle.ScreenFlash flash = sequence.scene().style.screenFlash;
	if (flash.isNone() || sequence.age() > flash.ticks())
	    return 0;

	float core = sequence.scene().core;
	Vec3 offset = sequence.scene().origin.subtract(camera.getPosition());
	double distance = offset.length();
	double facing = distance < 1 ? 1 : offset.normalize().dot(new Vec3(camera.getLookVector()));
	double reach = core * 9 + 18;
	double visibility = Math.max(0, 1 - distance / reach) * (0.24 + 0.76 * Math.max(0, facing));
	float time = Math.max(0, sequence.age() - 1 + partialTick);
	float fade = 1 - Mth.clamp((time - flash.holdTicks()) / (flash.ticks() - flash.holdTicks()), 0, 1);
	float peak = Math.min(1, 0.35f + core / 32);
	return (float) (visibility * fade * fade * peak);
    }

    private static boolean serverSends(CustomPacketPayload.Type<?> payload) {
	var connection = Minecraft.getInstance().getConnection();
	return connection != null && NetworkRegistry.hasChannel(connection, payload.id());
    }
}
