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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PlayerInputHandler implements InputHandler<Player> {

    @Override
    public @NotNull Player instantiateInstance(String input, Class<? extends Player> clazz, @Nullable PlatformSender<?> sender) {
        Player target = null;

        // try to get a player matching by name
        Player byName = Bukkit.getPlayer(input);
        if (byName != null) {
            target = byName;
        }

        // shortcut getting themselves if possible
        if (target == null && sender != null && sender.getRawSender() instanceof Player) {
            if (input.equals("me")) {
                target = (Player) sender;
            }
        }

        // in case they're insane and input an entire UUID, who are we to say no
        if (target == null) {
            UUID asUuid = null;

            try {
                asUuid = UUID.fromString(input);
            } catch (IllegalArgumentException ignored) {
            }

            if (asUuid != null) {
                Player byUuid = Bukkit.getPlayer(asUuid);
                if (byUuid != null) {
                    target = byUuid;
                }
            }
        }

        if (target != null) {
            return target;
        } else {
            throw new IllegalArgumentException("Cannot find any players based on your input!");
        }
    }

    @Override
    public @NotNull Class<Player> getRelevantClass() {
        return Player.class;
    }
}
