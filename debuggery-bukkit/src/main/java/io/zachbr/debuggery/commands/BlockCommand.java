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
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class BlockCommand extends BukkitCommandReflection {

    public BlockCommand(DebuggeryBukkit debuggery) {
        super("dblock", "debuggery.block", true, true, Block.class, debuggery);
    }

    @Override
    protected boolean commandLogic(Audience sender, String[] args) {
        Player player = (Player) sender;
        Block block = player.getTargetBlock(null, 50);

        return getCommandReflection().doReflectionLookups(sender, args, block);
    }
}
