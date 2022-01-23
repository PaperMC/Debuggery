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
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NamespacedKeyInputHandler implements InputHandler<NamespacedKey> {

    @Override
    public @NotNull NamespacedKey instantiateInstance(String input, Class<? extends NamespacedKey> clazz, @Nullable PlatformSender<?> sender) {
        String namespace = "minecraft";
        String key = null;

        String[] args = input.split(":", 2);
        switch (args.length) {
            case 2:
                namespace = args[0];
                key = args[1];
                break;
            case 1:
                key = args[0];
                break;
            default:
                throw new IllegalArgumentException("Please provide 1 value or 2 values separated by a colon!");
        }

        return new NamespacedKey(namespace, key);
    }

    @Override
    public @NotNull Class<NamespacedKey> getRelevantClass() {
        return NamespacedKey.class;
    }
}
