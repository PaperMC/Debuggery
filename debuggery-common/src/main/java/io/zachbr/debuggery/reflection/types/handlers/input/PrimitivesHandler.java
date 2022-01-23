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

package io.zachbr.debuggery.reflection.types.handlers.input;

import io.zachbr.debuggery.reflection.types.handlers.base.Handler;
import io.zachbr.debuggery.reflection.types.handlers.base.IHandler;
import io.zachbr.debuggery.reflection.types.handlers.base.platform.PlatformSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class PrimitivesHandler {

    public static void addHandlers(List<Handler> registration) {
        // loop through all supported classes and register them to the handler
        Class<?>[] supportedClasses = {byte.class, short.class, int.class, long.class, float.class, double.class, boolean.class, char.class};
        for (Class<?> clazz : supportedClasses) {
            IHandler handler = new IHandler() {
                @Override
                public @NotNull Object instantiateInstance(String input, Class<?> clazz, @Nullable PlatformSender<?> sender) {
                    return getPrimitive(input, clazz);
                }

                @Override
                public @NotNull Class<?> getRelevantClass() {
                    return clazz;
                }
            };

            registration.add(handler);
        }
    }

    public static @NotNull Object getPrimitive(String input, Class<?> clazz) {
        if (input == null) {
            throw new NullPointerException("Cannot get any value from null input!");
        }

        if (clazz.equals(byte.class)) {
            return Byte.parseByte(input);
        } else if (clazz.equals(short.class)) {
            return Short.parseShort(input);
        } else if (clazz.equals(int.class)) {
            return Integer.parseInt(input);
        } else if (clazz.equals(long.class)) {
            return Long.parseLong(input);
        } else if (clazz.equals(float.class)) {
            return Float.parseFloat(input);
        } else if (clazz.equals(double.class)) {
            return Double.parseDouble(input);
        } else if (clazz.equals(boolean.class)) {
            return Boolean.parseBoolean(input);
        } else if (clazz.equals(char.class)) {
            return input.charAt(0);
        }

        throw new AssertionError("Java added another primitive type!");
    }
}
