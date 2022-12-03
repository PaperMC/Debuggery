/*
 * This file is part of Debuggery.
 *
 * Debuggery is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Debuggery is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Debuggery.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.zachbr.debuggery.commands.base;

import io.zachbr.debuggery.DebuggeryBukkit;
import io.zachbr.debuggery.reflection.MethodMap;
import io.zachbr.debuggery.reflection.MethodMapProvider;
import io.zachbr.debuggery.reflection.chain.ReflectionResult;
import io.zachbr.debuggery.reflection.types.InputException;
import io.zachbr.debuggery.reflection.types.handlers.base.platform.PlatformSender;
import io.zachbr.debuggery.util.CommandUtil;
import io.zachbr.debuggery.util.FancyExceptionWrapper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Base class for all commands that use reflection to dig into Bukkit's API
 */
public abstract class CommandReflection extends CommandBase {
    protected final DebuggeryBukkit debuggery;
    private final MethodMapProvider mapCache;
    private MethodMap availableMethods = MethodMap.EMPTY;

    protected CommandReflection(String name, String permission, boolean requiresPlayer, Class<?> clazz, DebuggeryBukkit plugin) {
        super(name, permission, requiresPlayer);
        this.debuggery = plugin;
        this.mapCache = plugin.getMethodMapProvider();
        updateReflectionClass(clazz);
    }

    @Override
    protected boolean helpLogic(CommandSender sender, String[] args) {
        sender.sendMessage(Component.text("Uses reflection to call API methods built into Bukkit."));
        sender.sendMessage(Component.text("Try using the tab completion to see all available subcommands."));
        return true;
    }

    /**
     * Handles all the reflection based command logic
     *
     * @param sender   sender to send information to
     * @param args     command arguments
     * @param instance instance of the class type
     * @return true if handled successfully
     */
    protected boolean doReflectionLookups(CommandSender sender, String[] args, Object instance) {
        // 0 args just return info on object itself

        if (args.length == 0) {
            final String out = getOutputStringFor(instance);
            if (out != null) {
                sender.sendMessage(out);
            }

            return true;
        }

        // more than 0 args, start chains

        Class<?> activeClass = availableMethods.getMappedClass();
        if (!activeClass.isInstance(instance)) {
            throw new IllegalArgumentException("Instance is of type: " + instance.getClass().getSimpleName() + "but was expecting: " + activeClass.getSimpleName());
        }
        final String inputMethod = args[0];

        if (!availableMethods.containsId(inputMethod)) {
            sender.sendMessage(Component.text("Unknown or unavailable method", NamedTextColor.RED));
            return true;
        }

        PlatformSender<?> platformSender = new PlatformSender<>(sender);
        ReflectionResult chainResult = debuggery.runReflectionChain(args, instance, platformSender);
        switch (chainResult.getType()) {
            case SUCCESS -> notifySenderOfSuccess(sender, chainResult);
            case INPUT_ERROR, UNHANDLED_EXCEPTION -> notifySenderOfException(sender, chainResult);
            case NULL_REFERENCE, UNKNOWN_REFERENCE, ARG_MISMATCH -> notifySenderOfResultReason(sender, chainResult);
            default ->
                    throw new IllegalArgumentException("Unhandled switch case for result of type: " + chainResult.getType());
        }

        return true;
    }

    private void notifySenderOfException(CommandSender sender, ReflectionResult chainResult) {
        Throwable ex = chainResult.getException();
        Objects.requireNonNull(ex);

        String errorMessage = ex instanceof InputException ? "Exception deducing proper types from your input!" : "Exception invoking method - See console for more details!";
        Throwable cause = ex.getCause() == null ? ex : ex.getCause();

        FancyExceptionWrapper.sendFancyChatException(sender, errorMessage, cause);

        cause.printStackTrace();
    }

    private void notifySenderOfResultReason(CommandSender sender, ReflectionResult chainResult) {
        Objects.requireNonNull(chainResult.getReason());
        sender.sendMessage(Component.text(chainResult.getReason(), NamedTextColor.RED));
    }

    private void notifySenderOfSuccess(CommandSender sender, ReflectionResult chainResult) {
        String output = getOutputStringFor(chainResult.getEndingInstance());
        if (output != null) {
            sender.sendMessage(output);
        }
    }

    /**
     * Updates the locally cached reflection class
     *
     * @param typeIn class type to cache a reflection map for
     */
    protected void updateReflectionClass(Class<?> typeIn) {
        if (availableMethods.getMappedClass() != typeIn) {
            availableMethods = mapCache.getMethodMapFor(typeIn);
        }
    }

    protected void clearReflectionClass() {
        this.availableMethods = MethodMap.EMPTY;
    }

    /**
     * Convenience method to run objects past the TypeHandler
     *
     * @param object Object to get String output for
     * @return textual description of Object
     */
    protected @Nullable String getOutputStringFor(@Nullable Object object) {
        return debuggery.getTypeHandler().getOutputFor(object);
    }

    @Override
    public List<String> tabCompleteLogic(CommandSender sender, Command command, String alias, String[] args) {
        List<String> arguments = Arrays.asList(args);
        MethodMap reflectionMap = this.availableMethods;

        return CommandUtil.getReflectiveCompletions(arguments, reflectionMap, mapCache);
    }
}
