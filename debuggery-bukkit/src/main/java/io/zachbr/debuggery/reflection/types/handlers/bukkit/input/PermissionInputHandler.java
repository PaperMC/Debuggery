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
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PermissionInputHandler implements InputHandler<Permission> {

    @Override
    public @NotNull Permission instantiateInstance(String input, Class<? extends Permission> clazz, @Nullable PlatformSender<?> sender) {
        String[] split = input.split(",");

        String name = null;
        String desc = null;
        PermissionDefault def = null;

        switch (split.length) {
            case 3:
                def = getDefault(split[2]);
            case 2:
                desc = split[1];
            case 1:
                name = split[0];
                break;
            default:
                throw new IllegalArgumentException("Unable to parse arguments!");
        }

        return new Permission(name, desc, def);
    }

    @Override
    public @NotNull Class<Permission> getRelevantClass() {
        return Permission.class;
    }

    private static @NotNull PermissionDefault getDefault(String name) {
        PermissionDefault def = PermissionDefault.getByName(name);
        if (def != null) {
            return def;
        } else {
            throw new IllegalArgumentException("No default permission mode by that name!");
        }
    }
}
