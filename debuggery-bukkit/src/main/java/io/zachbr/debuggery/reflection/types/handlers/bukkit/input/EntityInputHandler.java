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

package io.zachbr.debuggery.reflection.types.handlers.bukkit.input;

import io.zachbr.debuggery.reflection.types.handlers.base.InputHandler;
import io.zachbr.debuggery.reflection.types.handlers.base.platform.PlatformSender;
import io.zachbr.debuggery.util.PlatformUtil;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EntityInputHandler implements InputHandler<Entity> {

    static @NotNull Entity getEntity(String input, @Nullable PlatformSender<?> sender) {
        Entity target;

        // player specific commands to make things easier for them
        if (sender != null && sender.getRawSender() instanceof Player) {
            Player player = (Player) sender.getRawSender();
            if (input.equalsIgnoreCase("that")) {
                target = PlatformUtil.getEntityPlayerLookingAt(player, 25, 1.5D);

                if (target != null) {
                    return target;
                }
            } else if (input.equalsIgnoreCase("me")) {
                return (player);
            }
        }

        // otherwise fall back to just getting the closest entity to the given location
        Location loc = LocationInputHandler.getLocation(input, sender);
        Entity nearest = PlatformUtil.getEntityNearestTo(loc, 25, 1.5D);

        if (nearest != null) {
            return nearest;
        } else {
          throw new NullPointerException("Cannot find any entities near you!");
        }
    }

    @Override
    public @NotNull Entity instantiateInstance(String input, Class<? extends Entity> clazz, @Nullable PlatformSender<?> sender) {
        return getEntity(input, sender); // separate method so that other entity related commands can get to it
    }

    @Override
    public @NotNull Class<Entity> getRelevantClass() {
        return Entity.class;
    }
}
