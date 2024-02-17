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

import io.papermc.paper.math.Position;
import io.zachbr.debuggery.reflection.types.handlers.base.InputHandler;
import io.zachbr.debuggery.reflection.types.handlers.base.platform.PlatformSender;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PositionInputHandler implements InputHandler<Position> {

    static @NotNull Position getLocation(String input, @Nullable PlatformSender<?> sender) {
        if (sender != null && sender.getRawSender() instanceof final Player player) {
            if (input.equalsIgnoreCase("here")) {
                return player.getLocation();
            } else if (input.equalsIgnoreCase("there")) {
                return player.getTargetBlock(null, 50).getLocation();
            }
        }

        String[] contents = input.split(",", 3);

        return Position.block(Integer.parseInt(contents[0]), Integer.parseInt(contents[1]), Integer.parseInt(contents[2]));
    }

    @Override
    public @NotNull Position instantiateInstance(String input, Class<? extends Position> clazz, @Nullable PlatformSender<?> sender) {
        return getLocation(input, sender); // separate method so that related commands can get to it
    }

    @Override
    public @NotNull Class<Position> getRelevantClass() {
        return Position.class;
    }
}
