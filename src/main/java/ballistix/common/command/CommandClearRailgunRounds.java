package ballistix.common.command;

import com.mojang.brigadier.CommandDispatcher;

import ballistix.References;
import ballistix.api.missile.MissileManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class CommandClearRailgunRounds {

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(Commands.literal(References.ID).requires(source -> source.hasPermission(4)).then(Commands.literal("wipeallrailgunrounds").executes(source -> {

            MissileManager.wipeAllRailgunRounds();
            source.getSource().sendSuccess(() -> Component.literal("wiped"), true);
            return 1;
        })));


    }


}
