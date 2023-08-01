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
import org.bukkit.World;
import org.bukkit.entity.Player;

public class WorldCommand extends BukkitCommandReflection {

    public WorldCommand(DebuggeryBukkit debuggery) {
        super("dworld", "debuggery.world", true, true, World.class, debuggery);
    }

    @Override
    public boolean commandLogic(Audience sender, String[] args) {
        final Player player = (Player) sender;

        return getCommandReflection().doReflectionLookups(sender, args, player.getWorld());
    }
}
