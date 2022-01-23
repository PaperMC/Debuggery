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
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LocationInputHandler implements InputHandler<Location> {

    static @NotNull Location getLocation(String input, @Nullable PlatformSender<?> sender) {
        if (sender != null && sender.getRawSender() instanceof Player) {
            final Player player = (Player) sender.getRawSender();
            if (input.equalsIgnoreCase("here")) {
                return player.getLocation();
            } else if (input.equalsIgnoreCase("there")) {
                return player.getTargetBlock(null, 50).getLocation();
            }
        }

        String[] contents = input.split(",", 4);
        double[] xyz = new double[3];

        World world = Bukkit.getWorld(contents[0]);
        if (world == null) {
            throw new NullPointerException("No world by that name could be found");
        }

        for (int i = 0; i < contents.length - 1; i++) {
            xyz[i] = Double.parseDouble(contents[i + 1]); // offset by 1 because of world at index 0
        }

        return new Location(world, xyz[0], xyz[1], xyz[2]);
    }

    @Override
    public @NotNull Location instantiateInstance(String input, Class<? extends Location> clazz, @Nullable PlatformSender<?> sender) {
        return getLocation(input, sender); // separate method so that related commands can get to it
    }

    @Override
    public @NotNull Class<Location> getRelevantClass() {
        return Location.class;
    }
}
