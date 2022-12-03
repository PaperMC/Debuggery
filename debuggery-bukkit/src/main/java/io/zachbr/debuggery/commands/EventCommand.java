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
import io.zachbr.debuggery.commands.base.CommandReflection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

import java.util.Arrays;
import java.util.List;

public class EventCommand extends CommandReflection {


    public EventCommand(DebuggeryBukkit debuggery) {
        super("devent", "debuggery.devent", true, Entity.class, debuggery);
    }

    @Override
    protected boolean commandLogic(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return true;
        }

        String clazz = args[0];

        try {
            Class<?> event = Class.forName(clazz, true, this.getClass().getClassLoader());
            updateReflectionClass(event);
            if (!Event.class.isAssignableFrom(event)) {
                sender.sendMessage(Component.text("Provided class is not an event.", NamedTextColor.RED));
                return true;
            }

            String[] offsetArgs = Arrays.copyOfRange(args, 1, args.length);
            sender.sendMessage(Component.text("Added event debugger!", NamedTextColor.GREEN));
            this.debuggery.getEventDebugger().addDebugger(event, (eventInstance) -> {
                this.doReflectionLookups(sender, offsetArgs, eventInstance);
            });
        } catch (Exception e) {
            sender.sendMessage(Component.text("Unknown class name %s!".formatted(clazz), NamedTextColor.RED));
            return true;
        }

        return true;
    }

    @Override
    public List<String> tabCompleteLogic(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 0) {
            return List.of();
        }
        try {
            Class<?> event = Class.forName(args[0], true, this.getClass().getClassLoader());
            updateReflectionClass(event);
        } catch (ClassNotFoundException e) {
            return List.of();
        }

        String[] trimmed = Arrays.copyOfRange(args, 1, args.length);
        if (trimmed.length == 0) {
            return List.of();
        }

        return super.tabCompleteLogic(sender, command, alias, trimmed);
    }
}
