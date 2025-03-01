package ballistix.common.command;

import com.mojang.brigadier.CommandDispatcher;

import ballistix.References;
import ballistix.api.missile.MissileManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;

public class CommandClearBullets {

	public static void register(CommandDispatcher<CommandSource> dispatcher) {

        dispatcher.register(Commands.literal(References.ID).requires(source -> source.hasPermission(4)).then(Commands.literal("wipeallbullets").executes(source -> {

            MissileManager.wipeAllBullets();
            source.getSource().sendSuccess(new StringTextComponent("wiped"), true);
            return 1;
        })));


    }


}
