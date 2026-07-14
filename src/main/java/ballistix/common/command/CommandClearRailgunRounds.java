package ballistix.common.command;

import com.mojang.brigadier.CommandDispatcher;

import ballistix.Ballistix;
import ballistix.api.missile.MissileManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;

public class CommandClearRailgunRounds {

	public static void register(CommandDispatcher<CommandSource> dispatcher) {

        dispatcher.register(Commands.literal(Ballistix.ID).requires(source -> source.hasPermission(4)).then(Commands.literal("wipeallrailgunrounds").executes(source -> {

            MissileManager.wipeAllRailgunRounds();
            source.getSource().sendSuccess(new StringTextComponent("wiped"), true);
            return 1;
        })));


    }


}
