package io.zachbr.debuggery.reflection.types.handlers.bukkit.input;

import io.zachbr.debuggery.reflection.types.handlers.base.InputHandler;
import io.zachbr.debuggery.reflection.types.handlers.base.platform.PlatformSender;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CommandSenderInputHandler implements InputHandler<CommandSender> {
    @Override
    public @NotNull CommandSender instantiateInstance(String input, Class<? extends CommandSender> clazz, @Nullable PlatformSender<?> sender) throws Exception {
        if ("console".equalsIgnoreCase(input)) {
            return Bukkit.getServer().getConsoleSender();
        }

        return EntityInputHandler.getEntity(input, sender);
    }

    @Override
    public @NotNull Class<CommandSender> getRelevantClass() {
        return CommandSender.class;
    }
}
