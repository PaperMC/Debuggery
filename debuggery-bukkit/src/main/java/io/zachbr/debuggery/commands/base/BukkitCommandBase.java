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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * Class to handle all the stupid minutia involved with commands
 */
public abstract class BukkitCommandBase extends Command implements CommandBase, PluginIdentifiableCommand {
    private static final Component PLAYER_USE_ONLY_MSG = Component.text("This command can only be used by players!", NamedTextColor.RED);

    private final boolean requiresPlayer;
    private final boolean shouldShowInHelp;
    private final JavaPlugin plugin;

    protected BukkitCommandBase(String name, String permission, boolean requiresPlayer, boolean shouldShowInHelp, JavaPlugin plugin) {
        super(name);
        this.setPermission(permission);
        this.requiresPlayer = requiresPlayer;
        this.shouldShowInHelp = shouldShowInHelp;
        this.plugin = plugin;
    }

    /**
     * This is the normal Bukkit command function, intercepted here so that we don't have to deal the same
     * repetitive garbage over and over.
     */
    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
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
    public final boolean showHelpText(final Audience sender, final String[] args) {
        sender.sendMessage(
                Component.text("==== ")
                        .append(Component.text(this.getName(), NamedTextColor.GOLD))
                        .append(Component.text(" ===="))
        );
        return this.helpLogic(sender, args);
    }



    @Override
    public final @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args, @Nullable Location location) {
        if (this.isRequiresPlayer() && !(sender instanceof Player)) {
            sender.sendMessage(PLAYER_USE_ONLY_MSG);
            return Collections.emptyList();
        }

        return this.tabCompleteLogic(sender, args);
    }

    @Override
    public boolean isRequiresPlayer() {
        return this.requiresPlayer;
    }

    @Override
    public boolean shouldShowInHelp() {
        return this.shouldShowInHelp;
    }

    @Override
    public @NotNull Plugin getPlugin() {
        return this.plugin;
    }
}
