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
import io.zachbr.debuggery.commands.base.BukkitCommandBase;
import io.zachbr.debuggery.util.CommandUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static net.kyori.adventure.text.Component.text;

public class DebuggeryCommand extends BukkitCommandBase {
    private final DebuggeryBukkit debuggery;

    public DebuggeryCommand(DebuggeryBukkit debuggery) {
        super("debuggery", "debuggery.debuggery", false, false);
        this.debuggery = debuggery;
    }

    @Override
    protected boolean commandLogic(Audience sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(text("=== Debuggery v" + debuggery.getJavaPlugin().getDescription().getVersion() + " ===", NamedTextColor.GOLD));
            sender.sendMessage(text("Debuggery is designed to expose API values at runtime."));
            sender.sendMessage(text("To see what commands are available and any help associated with them, use tab completion on this command."));
            sender.sendMessage(text("Source code can be found here: ").append(text(debuggery.getJavaPlugin().getDescription().getWebsite(), NamedTextColor.BLUE)));
            return true;
        }

        if (args.length == 1) {
            String arg = args[0].toLowerCase(Locale.ENGLISH);
            if (!debuggery.getAllCommands().containsKey(arg)) {
                sender.sendMessage(text("Debuggery command not found", NamedTextColor.RED));
                return true;
            }

            BukkitCommandBase target = debuggery.getAllCommands().get(arg);
            return target.showHelpText(sender, args);
        }

        sender.sendMessage(text("Too many arguments", NamedTextColor.RED));
        return true;
    }

    @Override
    protected boolean helpLogic(Audience sender, String[] args) {
        sender.sendMessage(text("Displays general information about the plugin."));
        sender.sendMessage(text("Also shows more specific help for each command when entered"));
        sender.sendMessage(text("Try using tab completion to see all available subtopics."));
        return true;
    }

    @Override
    protected List<String> tabCompleteLogic(Audience sender, String[] args) {
        if (args.length > 1) {
            return Collections.emptyList();
        }

        List<String> commands = debuggery.getAllCommands().values().stream()
                .filter(BukkitCommandBase::shouldShowInHelp)
                .map(BukkitCommandBase::getName)
                .collect(Collectors.toList());

        return CommandUtil.getCompletionsMatching(args, commands);
    }
}
