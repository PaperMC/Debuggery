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

import io.zachbr.debuggery.BukkitLogger;
import io.zachbr.debuggery.DebuggeryBukkit;
import io.zachbr.debuggery.commands.base.CommandBase;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static net.kyori.adventure.text.Component.text;

public class DebugCommand extends CommandBase {
    private final DebuggeryBukkit plugin;
    private final Set<CommandSender> debugListeners;

    public DebugCommand(DebuggeryBukkit debuggery) {
        super("ddebug", "debuggery.debug", false, false);

        this.plugin = debuggery;
        this.debugListeners = ((BukkitLogger) debuggery.getLogger()).getDebugListeners();
    }

    @Override
    protected boolean commandLogic(CommandSender sender, Command command, String label, String[] args) {
        // no debug mode, no debug command
        if (!DebuggeryBukkit.isDebugMode()) {
            sender.sendMessage(text("Debuggery Debug Mode is not enabled!", NamedTextColor.RED));
            sender.sendMessage(text("Enable it with the -Ddebuggery.debug=true system property", NamedTextColor.YELLOW));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(text("Specify a sub-command", NamedTextColor.RED));
            return true;
        }

        String subCommand = args[0].toLowerCase();
        // shift args forward
        String[] subArgs = new String[args.length - 1];
        if (args.length > 1) {
            System.arraycopy(args, 1, subArgs, 0, args.length - 1);
        }

        switch (subCommand) {
            case "subscribe":
                this.subOrUnsub(sender, subArgs, true);
                break;
            case "unsubscribe":
                this.subOrUnsub(sender, subArgs, false);
                break;
            case "info":
                this.sendSystemInfo(sender);
                break;
            default:
                sender.sendMessage(text("Unknown sub-command", NamedTextColor.RED));
        }
        return true;
    }

    private void subOrUnsub(CommandSender sender, String[] args, boolean add) {
        CommandSender target = null;

        if (args.length >= 1) {
            Player search = Bukkit.getPlayer(args[0]);

            if (search != null) {
                target = search;
            } else {
                sender.sendMessage(text("Cannot find player with name: " + args[0]));
                return;
            }
        }

        if (target == null && sender instanceof ConsoleCommandSender) {
            sender.sendMessage(text("Debug messages are always sent to the console", NamedTextColor.RED));
            return;
        }

        if (target == null) {
            target = sender;
        }

        final boolean contains = this.debugListeners.contains(target);

        if (add) {
            if (contains) {
                sender.sendMessage(text(target.getName() + " is already subscribed to debug messages"));
            } else {
                this.debugListeners.add(target);
                sender.sendMessage(text("Subscribed " + target.getName() + " to debug messages"));
            }
        } else {
            if (contains) {
                this.debugListeners.remove(target);
                sender.sendMessage(text("Unsubscribed " + target.getName() + " to debug messages"));
            } else {
                sender.sendMessage(text(target.getName() + " is not subscribed to debug messages"));
            }
        }
    }

    /**
     * Sends system information to the specified sender
     *
     * @param sender what to send to
     */
    private void sendSystemInfo(CommandSender sender) {
        for (String line : plugin.getSystemInfo()) {
            sender.sendMessage(text(line, NamedTextColor.GOLD));
        }
    }

    @Override
    protected boolean helpLogic(CommandSender sender, String[] args) {
        sender.sendMessage(text("Exposes the internal systems state of the Debuggery plugin"));
        return true;
    }

    @Override
    protected List<String> tabCompleteLogic(CommandSender sender, Command command, String alias, String[] args) {
        switch (args.length) {
            case 1:
                return List.of("subscribe", "unsubscribe", "info");
            case 2:
                switch (args[0].toLowerCase()) {
                    case "subscribe":
                    case "unsubscribe":
                        return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList());
                    default:
                        return Collections.emptyList();
                }
            default:
                return Collections.emptyList();
        }
    }
}
