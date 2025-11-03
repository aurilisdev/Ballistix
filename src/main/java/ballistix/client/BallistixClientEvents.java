package ballistix.client;

import java.util.ArrayList;
import java.util.List;

import ballistix.client.event.HandlerDetectorLines;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import voltaic.client.event.AbstractLevelStageHandler;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class BallistixClientEvents {

    private static final List<AbstractLevelStageHandler> LEVEL_STAGE_RENDER_HANDLERS = new ArrayList<>();

    public static void init() {
        LEVEL_STAGE_RENDER_HANDLERS.add(HandlerDetectorLines.INSTANCE);
    }

    @SubscribeEvent
    public static void handleRenderEvents(RenderLevelStageEvent event) {
        LEVEL_STAGE_RENDER_HANDLERS.forEach(handler -> {
            if (handler.shouldRender(event.getStage())) {
                handler.render(event.getCamera(), event.getFrustum(), event.getLevelRenderer(), event.getPoseStack(), event.getProjectionMatrix(), Minecraft.getInstance(), event.getRenderTick(), event.getPartialTick());
            }
        });
    }

    @SubscribeEvent
    public static void wipeRenderHashes(ClientPlayerNetworkEvent.LoggingOut event) {
        Player player = event.getPlayer();
        if (player != null) {
            LEVEL_STAGE_RENDER_HANDLERS.forEach(AbstractLevelStageHandler::clear);
        }
    }

}
