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
import io.zachbr.debuggery.commands.base.CommandBase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class EventRemoveCommand extends CommandBase {

    private final DebuggeryBukkit debuggery;

    public EventRemoveCommand(DebuggeryBukkit debuggery) {
        super("deventremove", "debuggery.devent.remove", true);
        this.debuggery = debuggery;
    }

    @Override
    protected boolean commandLogic(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && args[0].equals("*")) {
            this.debuggery.getEventDebugger().clearAll();
            sender.sendMessage(Component.text("Cleared all event debuggers!", NamedTextColor.GREEN));
            return true;
        }

        try {
            Class<?> event = Class.forName(args[0], false, this.getClass().getClassLoader());
            if (this.debuggery.getEventDebugger().clear(event)) {
                sender.sendMessage(Component.text("Cleared event debugger for " + event, NamedTextColor.GREEN));
            } else {
                sender.sendMessage(Component.text("No debugger for that event found!", NamedTextColor.RED));
            }
        } catch (Exception e) {
            sender.sendMessage(Component.text("Class not found!", NamedTextColor.RED));
        }

        return true;
    }

    @Override
    protected boolean helpLogic(CommandSender sender, String[] args) {
        sender.sendMessage(Component.text("Clears the event debugger for the provided event."));
        return true;
    }

    @Override
    protected List<String> tabCompleteLogic(CommandSender sender, Command command, String alias, String[] args) {
        List<String> names = new ArrayList<>();
        this.debuggery.getEventDebugger().getAll().forEach((clazz) -> names.add(clazz.getName()));
        names.add("*");

        return names;
    }
}
