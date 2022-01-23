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

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import io.zachbr.debuggery.DebuggeryVelocity;
import io.zachbr.debuggery.commands.base.CommandReflection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

public class ServerConnectionCommand extends CommandReflection {

    public ServerConnectionCommand(DebuggeryVelocity plugin) {
        super("vconnection", "debuggery.vconnection", true, ServerConnection.class, plugin);
    }

    @Override
    protected void commandLogic(@NotNull CommandSource source, @NotNull String[] args) {
        Player player = (Player) source;
        player.getCurrentServer().ifPresentOrElse(
                it -> doReflectionLookups(source, args, it),
                () -> source.sendMessage(Component.text("Not connected to a server!").color(NamedTextColor.RED)));
    }
}
