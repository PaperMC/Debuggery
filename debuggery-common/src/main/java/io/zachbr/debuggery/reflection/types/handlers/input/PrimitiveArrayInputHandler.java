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
import io.zachbr.debuggery.reflection.types.handlers.base.InputHandler;
import io.zachbr.debuggery.reflection.types.handlers.base.platform.PlatformSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.List;

public final class PrimitiveArrayInputHandler {

    public static void addHandlers(List<Handler> registration) {
        // loop through all supported classes and register them to the handler
        Class<?>[] supportedClasses = {byte[].class, short[].class, int[].class, long[].class, float[].class, double[].class, boolean[].class, char[].class};
        for (Class<?> clazz : supportedClasses) {
            registration.add(handler(clazz));
        }
    }

    private static <T> @NotNull InputHandler<T> handler(@NotNull final Class<T> clazz) {
        return new InputHandler<>() {
            @Override
            public @NotNull T instantiateInstance(String input, Class<? extends T> clazz, @Nullable PlatformSender<?> sender) {
                String[] elements = input.split(",");
                Class<?> arrayType = clazz.getComponentType();
                T typedArray = (T) Array.newInstance(arrayType, elements.length);

                for (int i = 0; i < elements.length; i++) {
                    Object value = PrimitivesInputHandler.getPrimitive(elements[i], arrayType);
                    Array.set(typedArray, i, value);
                }

                return typedArray;
            }

            @Override
            public @NotNull Class<T> getRelevantClass() {
                return clazz;
            }
        };
    }
}
