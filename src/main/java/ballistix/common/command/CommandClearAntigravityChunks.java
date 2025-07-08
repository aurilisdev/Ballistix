package ballistix.common.command;

import ballistix.Ballistix;
import ballistix.registers.BallistixCapabilities;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;

import com.mojang.brigadier.CommandDispatcher;

public class CommandClearAntigravityChunks {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {

        dispatcher.register(Commands.literal(Ballistix.ID).requires(source -> source.hasPermission(4)).then(Commands.literal("wipeallantigravitychunks").executes(source -> {

            source.getSource().getLevel().getCapability(BallistixCapabilities.ANTIGRAVED_CHUNKS).ifPresent(cap -> cap.activeChunks.clear());
            source.getSource().sendSuccess(new StringTextComponent("wiped"), true);
            return 1;
        })));


    }

}
