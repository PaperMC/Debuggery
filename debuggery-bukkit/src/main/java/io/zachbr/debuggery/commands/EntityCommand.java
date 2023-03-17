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
import io.zachbr.debuggery.util.PlatformUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EntityCommand extends BukkitCommandReflection {
    private final DebuggeryBukkit plugin;
    public EntityCommand(DebuggeryBukkit debuggery) {
        super("dentity", "debuggery.entity", true, true, Entity.class, debuggery);
        this.plugin = debuggery;
    }

    @Override
    protected boolean commandLogic(Audience sender, String[] args) {
        Player player = (Player) sender;
        Entity target = this.getTarget(player);
        if (target == null) {
            sender.sendMessage(Component.text("Couldn't detect the entity you were looking at!", NamedTextColor.RED));
            return true;
        }

        return getCommandReflection().doReflectionLookups(sender, args, target);
    }

    @Override
    public List<String> tabCompleteLogic(Audience sender, String[] args) {
        Entity target = this.getTarget((Player) sender);
        if (target == null) {
            getCommandReflection().clearReflectionClass();
            return List.of("NOT FOUND");
        } else {
            getCommandReflection().updateReflectionClass(target.getClass());
        }

        return super.tabCompleteLogic(sender, args);
    }

    @Nullable
    private Entity getTarget(Player player) {
        Entity entity = null;
        if (this.plugin.getTargetedEntity() != null) {
            entity = Bukkit.getEntity(this.plugin.getTargetedEntity());
            if (entity == null) {
                this.plugin.setTargetedEntity(null);
            }
        }

        if (entity == null) {
            entity = PlatformUtil.getEntityPlayerLookingAt(player, 25, 1.5D);
        }
        return entity;
    }
}
