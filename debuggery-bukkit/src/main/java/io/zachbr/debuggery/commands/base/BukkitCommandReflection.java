package io.zachbr.debuggery.commands.base;

import io.zachbr.debuggery.DebuggeryBukkit;
import io.zachbr.debuggery.commands.CommandReflection;
import io.zachbr.debuggery.reflection.MethodMap;
import io.zachbr.debuggery.util.CommandUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;

import java.util.Arrays;
import java.util.List;

public abstract class BukkitCommandReflection extends BukkitCommandBase {
    private final CommandReflection commandReflection;

    protected BukkitCommandReflection(String name, String permission, boolean requiresPlayer, boolean shouldShowInHelp, Class<?> clazz, DebuggeryBukkit plugin) {
        super(name, permission, requiresPlayer, shouldShowInHelp, plugin.getJavaPlugin());
        this.commandReflection = new CommandReflection(clazz, plugin);
    }

    @Override
    public boolean helpLogic(Audience sender, String[] args) {
        sender.sendMessage(Component.text("Uses reflection to call API methods built into Bukkit."));
        sender.sendMessage(Component.text("Try using the tab completion to see all available subcommands."));
        return true;
    }

    @Override
    public List<String> tabCompleteLogic(Audience sender, String[] args) {
        List<String> arguments = Arrays.asList(args);
        MethodMap reflectionMap = this.getCommandReflection().getAvailableMethods();

        return CommandUtil.getReflectiveCompletions(arguments, reflectionMap, this.commandReflection.getMapCache());
    }

    public CommandReflection getCommandReflection() {
        return commandReflection;
    }
}
