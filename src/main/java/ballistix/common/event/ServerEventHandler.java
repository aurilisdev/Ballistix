package ballistix.common.event;

import ballistix.Ballistix;
import ballistix.common.command.CommandClearBullets;
import ballistix.common.command.CommandClearMissiles;
import ballistix.common.command.CommandClearRailgunRounds;
import ballistix.common.command.CommandClearSAMs;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = Ballistix.ID, bus = EventBusSubscriber.Bus.GAME)
public class ServerEventHandler {

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        CommandClearMissiles.register(event.getDispatcher());
        CommandClearBullets.register(event.getDispatcher());
        CommandClearRailgunRounds.register(event.getDispatcher());
        CommandClearSAMs.register(event.getDispatcher());
    }

}
