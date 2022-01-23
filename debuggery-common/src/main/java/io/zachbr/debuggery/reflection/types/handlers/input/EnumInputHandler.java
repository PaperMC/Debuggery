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

import io.zachbr.debuggery.reflection.types.handlers.base.InputPolymorphicHandler;
import io.zachbr.debuggery.reflection.types.handlers.base.platform.PlatformSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EnumInputHandler implements InputPolymorphicHandler<Enum> {

    public static <T extends Enum<T>> @NotNull T getEnumValue(String input, Class<T> clazz) {
        return Enum.valueOf(clazz, input.toUpperCase());
    }

    @Override
    public @NotNull Enum<?> instantiateInstance(String input, Class<? extends Enum> clazz, @Nullable PlatformSender<?> sender) {
        return getEnumValue(input, clazz);
    }

    @Override
    public @NotNull Class<Enum> getRelevantClass() {
        return Enum.class;
    }
}
