package io.zachbr.debuggery.commands.base;

import io.zachbr.debuggery.DebuggeryVelocity;
import io.zachbr.debuggery.commands.CommandReflection;
import io.zachbr.debuggery.reflection.MethodMap;
import io.zachbr.debuggery.util.CommandUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class VelocityCommandReflection extends VelocityCommandBase {
    private final CommandReflection commandReflection;
    protected VelocityCommandReflection(String name, String permNode, boolean requiresPlayer, Class<?> clazz, DebuggeryVelocity plugin) {
        super(name, permNode, requiresPlayer);
        this.commandReflection = new CommandReflection(clazz, plugin);
    }

    @Override
    protected boolean helpLogic(@NotNull Audience sender, @NotNull String[] args) {
        super.helpLogic(sender, args);
        sender.sendMessage(Component.text("Uses reflection to call API methods built into Velocity."));
        sender.sendMessage(Component.text("Try using the tab completion to see all available subcommands."));
        return true;
    }

    @Override
    public List<String> tabCompleteLogic(@NotNull Audience sender, @NotNull String[] args) {
        List<String> arguments = List.of(args);
        MethodMap reflectionMap = this.commandReflection().getAvailableMethods();

        return CommandUtil.getReflectiveCompletions(arguments, reflectionMap, commandReflection().getMapCache());
    }

    protected CommandReflection commandReflection() {
        return this.commandReflection;
    }
}
