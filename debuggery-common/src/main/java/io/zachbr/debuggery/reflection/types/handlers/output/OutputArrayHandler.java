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

package io.zachbr.debuggery.reflection.types.handlers.output;

import io.zachbr.debuggery.reflection.types.TypeHandler;
import io.zachbr.debuggery.reflection.types.handlers.base.Handler;
import io.zachbr.debuggery.reflection.types.handlers.base.OutputHandler;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.List;

public class OutputArrayHandler {
    private final TypeHandler typeHandler;

    public OutputArrayHandler(List<Handler> registration, TypeHandler handler) {
        this.typeHandler = handler;
        // loop through all supported classes and register them to the handler
        Class<?>[] supportedClasses = {Object[].class, byte[].class, short[].class, int[].class, long[].class, float[].class, double[].class, boolean[].class, char[].class};
        for (Class<?> clazz : supportedClasses) {
            registration.add(handler(clazz));
        }
    }

    private <T> @NotNull OutputHandler<T> handler(@NotNull final Class<T> clazz) {
        return new OutputHandler<>() {
            @Override
            public @NotNull String getFormattedOutput(T object) {
                return getFormattedArray(object);
            }

            @Override
            public @NotNull Class<T> getRelevantClass() {
                return clazz;
            }
        };
    }

    private @NotNull String getFormattedArray(Object object) {
        // Rather than handle every primitive type, just have java autobox the contents
        if (object.getClass().getComponentType().isPrimitive()) {
            int length = Array.getLength(object);
            Object[] newArray = new Object[length];

            for (int i = 0; i < length; i++) {
                newArray[i] = Array.get(object, i);
            }

            object = newArray;
        }

        StringBuilder out = new StringBuilder("{");

        Object[] array = (Object[]) object;
        for (int i = 0; i < array.length; i++) {
            out.append(typeHandler.getOutputFor(array[i]));

            if (i != array.length - 1) {
                out.append(", ");
            }
        }

        return out.append('}').toString();
    }
}
