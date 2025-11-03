package ballistix.common.command;

import com.mojang.brigadier.CommandDispatcher;

import ballistix.Ballistix;
import ballistix.api.missile.MissileManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class CommandClearBullets {

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(Commands.literal(Ballistix.ID).requires(source -> source.hasPermission(4)).then(Commands.literal("wipeallbullets").executes(source -> {

            MissileManager.wipeAllBullets();
            source.getSource().sendSuccess(() -> Component.literal("wiped"), true);
            return 1;
        })));


    }


}
