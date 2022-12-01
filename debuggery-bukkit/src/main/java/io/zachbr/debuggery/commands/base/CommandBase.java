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
public abstract class CommandBase implements CommandExecutor, TabCompleter {
    private static final Component NO_PERMS_MSG = Component.text("You do not have permission to do that!", NamedTextColor.RED) ;
    private static final Component PLAYER_USE_ONLY_MSG = Component.text("This command can only be used by players!", NamedTextColor.RED);

    private final String name;
    private final String permission;
    private final boolean requiresPlayer;
    private final boolean shouldShowInHelp;

    protected CommandBase(String name, String permission, boolean requiresPlayer) {
        this(name, permission, requiresPlayer, true);
    }

    protected CommandBase(String name, String permission, boolean requiresPlayer, boolean shouldShowInHelp) {
        this.name = name;
        this.permission = permission;
        this.requiresPlayer = requiresPlayer;
        this.shouldShowInHelp = shouldShowInHelp;
    }

    /**
     * This is the normal Bukkit command function, intercepted here so that we don't have to deal the same
     * repetitive garbage over and over.
     */
    @Override
    public final boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission(this.permission)) {
            sender.sendMessage(NO_PERMS_MSG);
            return true;
        }

        if (this.requiresPlayer && !(sender instanceof Player)) {
            sender.sendMessage(PLAYER_USE_ONLY_MSG);
            return true;
        }

        return this.commandLogic(sender, command, label, args);
    }

    /**
     * Shows help text for this command
     *
     * @param sender {@link CommandSender} responsible for sending the message
     * @param args   arguments for the given command
     * @return whether the command was successfully handled
     */
    public final boolean showHelpText(CommandSender sender, String[] args) {
        if (!sender.hasPermission(this.permission)) {
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
        if (!sender.hasPermission(this.permission)) {
            sender.sendMessage(NO_PERMS_MSG);
            return Collections.emptyList();
        }

        if (this.requiresPlayer && !(sender instanceof Player)) {
            sender.sendMessage(PLAYER_USE_ONLY_MSG);
            return Collections.emptyList();
        }

        return this.tabCompleteLogic(sender, command, alias, args);
    }

    /**
     * Used by classes to implement their primary command logic
     *
     * @param sender  {@link CommandSender} responsible for sending the message
     * @param command Relevant {@link Command} being used
     * @param label   Command label in use
     * @param args    arguments for the given command
     * @return whether the command was successfully handled
     */
    protected abstract boolean commandLogic(CommandSender sender, Command command, String label, String[] args);

    /**
     * Called whenever someone uses the /debuggery command with a specific command topic
     *
     * @param sender {@link CommandSender} responsible for sending the message
     * @param args   arguments for the given command
     * @return whether the command was successfully handled
     */
    protected abstract boolean helpLogic(CommandSender sender, String[] args);

    /**
     * Used by classes to implement their tab completion logic
     *
     * @param sender  {@link CommandSender} responsible for sending the message
     * @param command Relevant {@link Command} being used
     * @param alias   Command alias in use
     * @param args    arguments for the given command
     * @return whether the command was successfully handled
     */
    protected abstract List<String> tabCompleteLogic(CommandSender sender, Command command, String alias, String[] args);

    /**
     * Gets the name of this command
     * This is also used for registration
     *
     * @return command name
     */
    public final String getName() {
        return this.name;
    }

    /**
     * Gets the permission required to execute this command
     *
     * @return permission
     */
    public final String getPermission() {
        return this.permission;
    }

    /**
     * Gets if this command requires a player
     *
     * @return if command requires a player
     */
    public final boolean getRequiresPlayer() {
        return this.requiresPlayer;
    }

    /**
     * Gets if this command should show in the plugin help directory
     *
     * @return if command should show in help
     */
    public final boolean shouldShowInHelp() {
        return this.shouldShowInHelp;
    }
}
