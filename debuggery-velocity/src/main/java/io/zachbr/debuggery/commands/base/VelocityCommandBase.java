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

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import io.zachbr.debuggery.commands.CommandBase;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class VelocityCommandBase extends CommandBase implements SimpleCommand {

    protected VelocityCommandBase(String name, String permNode, boolean requiresPlayer) {
        super(name, permNode, requiresPlayer, true);
    }

    @Override
    public void execute(final Invocation invocation) {
        commandLogic(invocation.source(), invocation.arguments());
    }

    @Override
    public List<String> suggest(final Invocation invocation) {
        return tabCompleteLogic(invocation.source(), invocation.arguments());
    }

    @Override
    protected boolean helpLogic(Audience source, @NotNull String[] args) {
        source.sendMessage(Component.text("==== /" + this.getName() + " ===="));
        return true;
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        if (this.isRequiresPlayer() && !(invocation.source() instanceof Player)) {
            return false;
        }
        return invocation.source().hasPermission(this.getPermission());
    }
}
