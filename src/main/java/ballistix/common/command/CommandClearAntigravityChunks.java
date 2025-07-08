package ballistix.common.command;

import ballistix.Ballistix;
import ballistix.registers.BallistixAttachmentTypes;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class CommandClearAntigravityChunks {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(Commands.literal(Ballistix.ID).requires(source -> source.hasPermission(4)).then(Commands.literal("wipeallantigravitychunks").executes(source -> {

            source.getSource().getLevel().removeData(BallistixAttachmentTypes.ANTIGRAVED_CHUNKS);
            source.getSource().sendSuccess(() -> Component.literal("wiped"), true);
            return 1;
        })));


    }

}
