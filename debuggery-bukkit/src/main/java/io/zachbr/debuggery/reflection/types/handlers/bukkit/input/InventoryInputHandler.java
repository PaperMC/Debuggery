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
import org.bukkit.block.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InventoryInputHandler implements InputHandler<Inventory> {
    @Override
    public @NotNull Inventory instantiateInstance(String input, Class<? extends Inventory> clazz, @Nullable PlatformSender<?> sender) throws Exception {
        if (sender == null || !(sender.getRawSender() instanceof Player)) {
            throw new UnsupportedOperationException("Inventory input handler is only supported when used by a player!");
        }

        Player player = (Player) sender.getRawSender();
        switch (input.toLowerCase()) {
            case "player":
                return player.getInventory();
            case "ender":
                return player.getEnderChest();
            case "new":
                return Bukkit.createInventory(player, 27);
            case "block":
                Block block = player.getTargetBlock(25);
                BlockState state = block != null ? block.getState() : null;
                if (state instanceof Container) {
                    return ((Container) block.getState(false)).getInventory();
                }

                throw new IllegalArgumentException("Block does not exist or is not a container type!");
        }

        throw new IllegalArgumentException("Unknown inventory argument, allowed: \"player\", \"ender\", \"new\", \"block\"");
    }

    @Override
    public @NotNull Class<Inventory> getRelevantClass() {
        return Inventory.class;
    }
}
