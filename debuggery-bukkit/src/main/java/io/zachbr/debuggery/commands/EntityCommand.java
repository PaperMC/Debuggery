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
import io.zachbr.debuggery.commands.base.CommandReflection;
import io.zachbr.debuggery.util.PlatformUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class EntityCommand extends CommandReflection {
    public EntityCommand(DebuggeryBukkit debuggery) {
        super("dentity", "debuggery.entity", true, Entity.class, debuggery);
    }

    @Override
    protected boolean commandLogic(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        Entity entity = null;
        if (this.debuggery.getTargetedEntity() != null) {
            entity = Bukkit.getEntity(this.debuggery.getTargetedEntity());
            if (entity == null) {
                this.debuggery.setTargetedEntity(null);
            }
        }

        if (entity == null) {
            entity = PlatformUtil.getEntityPlayerLookingAt(player, 25, 1.5D);
        }

        if (entity == null) {
            sender.sendMessage(Component.text("Couldn't detect the entity you were looking at!", NamedTextColor.RED));
            return true;
        }

        updateReflectionClass(entity.getClass());
        return doReflectionLookups(sender, args, entity);
    }
}
