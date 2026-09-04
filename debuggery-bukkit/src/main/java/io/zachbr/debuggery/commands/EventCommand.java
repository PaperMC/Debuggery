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

package io.zachbr.debuggery.commands;

import io.zachbr.debuggery.DebuggeryBukkit;
import io.zachbr.debuggery.commands.base.BukkitCommandReflection;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class EventCommand extends BukkitCommandReflection {
    private static final MethodHandle EVENT_TYPES_HANDLE;
    private final DebuggeryBukkit plugin;

    static {
        MethodHandle handle = null;

        try {
            final Field eventTypesField = HandlerList.class.getDeclaredField("EVENT_TYPES");
            eventTypesField.setAccessible(true);
            handle = MethodHandles.lookup().unreflectGetter(eventTypesField);
        } catch (final ReflectiveOperationException ignored) {}

        EVENT_TYPES_HANDLE = handle;
    }

    public EventCommand(DebuggeryBukkit debuggery) {
        super("devent", "debuggery.devent", true, true, Entity.class, debuggery);
        this.plugin = debuggery;
    }

    @Override
    public boolean commandLogic(Audience sender, String[] args) {
        if (args.length == 0) {
            return true;
        }

        String clazz = args[0];
        final Class<?> event = findEventClass(clazz);
        if (event == null) {
            sender.sendMessage(Component.text("Unknown class name %s!".formatted(clazz), NamedTextColor.RED));
            return true;
        }

        try {
            getCommandReflection().updateReflectionClass(event);
            if (!Event.class.isAssignableFrom(event)) {
                sender.sendMessage(Component.text("Provided class is not an event.", NamedTextColor.RED));
                return true;
            }

            String[] offsetArgs = Arrays.copyOfRange(args, 1, args.length);
            sender.sendMessage(Component.text("Added event debugger!", NamedTextColor.GREEN));
            this.plugin.getEventDebugger().addDebugger(event, (eventInstance) ->
                    this.getCommandReflection().doReflectionLookups(sender, offsetArgs, eventInstance)
            );
        } catch (Exception e) {
            sender.sendMessage(Component.text("Unknown class name %s!".formatted(clazz), NamedTextColor.RED));
            return true;
        }

        return true;
    }

    @Override
    public List<String> tabCompleteLogic(Audience sender, String[] args) {
        if (args.length == 0 || args.length == 1) {
            return knownEventClasses().stream().filter(className -> args.length == 0 || className.toLowerCase(Locale.ROOT).contains(args[0].toLowerCase(Locale.ROOT))).toList();
        }
        Class<?> event = findEventClass(args[0]);
        if (event == null) {
            return List.of();
        }

        getCommandReflection().updateReflectionClass(event);
        String[] trimmed = Arrays.copyOfRange(args, 1, args.length);
        if (trimmed.length == 0) {
            return List.of();
        }

        return super.tabCompleteLogic(sender, trimmed);
    }

    private Set<String> knownEventClasses() {
        if (EVENT_TYPES_HANDLE == null) {
            return Set.of();
        }

        try {
            return (Set<String>) EVENT_TYPES_HANDLE.invokeExact();
        } catch (Throwable e) {
            return Set.of();
        }
    }

    private @Nullable Class<?> findEventClass(final String className) {
        try {
            return Class.forName(className, true, this.getClass().getClassLoader());
        } catch (ClassNotFoundException e) {
            // could be a plugin event, check plugin classloaders
            for (final Plugin plugin : getPlugin().getServer().getPluginManager().getPlugins()) {
                try {
                    return Class.forName(className, true, plugin.getClass().getClassLoader());
                } catch (ClassNotFoundException ignored) {}
            }
        }

        return null;
    }
}
