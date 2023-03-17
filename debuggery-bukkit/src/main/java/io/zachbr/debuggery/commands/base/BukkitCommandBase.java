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

import io.zachbr.debuggery.commands.CommandBase;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.permission.PermissionChecker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * Class to handle all the stupid minutia involved with commands
 */
public abstract class BukkitCommandBase extends CommandBase implements CommandExecutor, TabCompleter {
    private static final Component NO_PERMS_MSG = Component.text("You do not have permission to do that!", NamedTextColor.RED) ;
    private static final Component PLAYER_USE_ONLY_MSG = Component.text("This command can only be used by players!", NamedTextColor.RED);

    protected BukkitCommandBase(String name, String permission, boolean requiresPlayer, boolean shouldShowInHelp) {
        super(name, permission, requiresPlayer, shouldShowInHelp);
    }

    /**
     * This is the normal Bukkit command function, intercepted here so that we don't have to deal the same
     * repetitive garbage over and over.
     */
    @Override
    public final boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission(this.getPermission())) {
            sender.sendMessage(NO_PERMS_MSG);
            return true;
        }

        if (this.isRequiresPlayer() && !(sender instanceof Player)) {
            sender.sendMessage(PLAYER_USE_ONLY_MSG);
            return true;
        }

        return this.commandLogic(sender, args);
    }

    /**
     * Shows help text for this command
     *
     * @param sender {@link CommandSender} responsible for sending the message
     * @param args   arguments for the given command
     * @return whether the command was successfully handled
     */
    public final boolean showHelpText(Audience sender, String[] args) {
        if (!sender.get(PermissionChecker.POINTER).map(checker -> checker.test(this.getPermission())).orElse(false)) {
            sender.sendMessage(NO_PERMS_MSG);
            return true;
        }

        sender.sendMessage(
                Component.text("==== ")
                        .append(Component.text(this.getName(), NamedTextColor.GOLD))
                        .append(Component.text(" ===="))
        );
        return this.helpLogic(sender, args);
    }

    @Override
    public final List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!sender.hasPermission(this.getPermission())) {
            sender.sendMessage(NO_PERMS_MSG);
            return Collections.emptyList();
        }

        if (this.isRequiresPlayer() && !(sender instanceof Player)) {
            sender.sendMessage(PLAYER_USE_ONLY_MSG);
            return Collections.emptyList();
        }

        return this.tabCompleteLogic(sender, args);
    }
}
