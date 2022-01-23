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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BukkitClassInputHandler implements InputHandler<Class> {

    static @NotNull Class<?> getClass(String input) throws ClassNotFoundException {
        // This is only used for entities right now, so we can save some drama and just search those packages
        // todo above assumption no longer true, figure out what to do here
        final String[] searchPackages = {"org.bukkit", "org.bukkit.entity", "org.bukkit.entity.minecart"};

        String normalized = input;
        if (normalized.endsWith(".class")) {
            normalized = normalized.substring(0, normalized.length() - 6);
        }

        Class<?> clazz = null;
        for (String packageName : searchPackages) {
            try {
                clazz = Class.forName(packageName + "." + normalized);
            } catch (ClassNotFoundException ignored) {
            }

            if (clazz != null) {
                return clazz;
            }
        }

        throw new ClassNotFoundException(normalized + " not present in Bukkit entity namespace");
    }

    @Override
    public @NotNull Class<?> instantiateInstance(String input, Class<? extends Class> clazz, @Nullable PlatformSender<?> sender) throws ClassNotFoundException {
        return getClass(input); // separate method so that IBukkitClassesHandler can get to it
    }

    @Override
    public @NotNull Class<Class> getRelevantClass() {
        return Class.class; // This is only used for entities right now, so we can save some drama and just search those packages
    }
}
