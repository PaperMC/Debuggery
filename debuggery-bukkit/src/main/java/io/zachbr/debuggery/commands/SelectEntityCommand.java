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
import io.zachbr.debuggery.util.PlatformUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class SelectEntityCommand extends BukkitCommandBase {

    private final DebuggeryBukkit debuggery;

    public SelectEntityCommand(DebuggeryBukkit debuggery) {
        super("dentityselect", "debuggery.entity.select", true, true, debuggery.getJavaPlugin());
        this.debuggery = debuggery;
    }

    @Override
    public boolean commandLogic(Audience sender, String[] args) {
        if (args.length > 0 && args[0].equals("unselect")) {
            this.debuggery.setTargetedEntity(null);
            sender.sendMessage(Component.text("Unselected entity!", NamedTextColor.GREEN));
            return true;
        }

        Player player = (Player) sender;
        Entity target = PlatformUtil.getEntityPlayerLookingAt(player, 100, 0.2);
        if (target == null) {
            player.sendMessage(Component.text("No entity found!", NamedTextColor.RED));
            return true;
        } else {
            this.debuggery.setTargetedEntity(target.getUniqueId());
            sender.sendMessage(Component.text("Selected entity!", NamedTextColor.GREEN));
            target.setGlowing(true);
            new BukkitRunnable(){

                @Override
                public void run() {
                    target.setGlowing(false);
                }
            }.runTaskLater(this.debuggery.getJavaPlugin(), 20);
        }

        return true;
    }

    @Override
    public boolean helpLogic(Audience sender, String[] args) {
        sender.sendMessage(Component.text("Look at an entity to select that entity for further /dentity actions."));
        return true;
    }

    @Override
    public List<String> tabCompleteLogic(Audience sender, String[] args) {
        return List.of("unselect");
    }
}
