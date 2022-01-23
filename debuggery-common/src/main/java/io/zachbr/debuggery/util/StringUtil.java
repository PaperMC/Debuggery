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

package io.zachbr.debuggery.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public final class StringUtil {
    /**
     * Parses an array of doubles from a given comma separated string
     *
     * @param input        string to parse
     * @param expectedSize count expected
     * @return new array of doubles
     */
    public static double @NotNull [] parseDoublesFromString(String input, int expectedSize) {
        String[] split = input.split(",", expectedSize);
        if (split.length != expectedSize) {
            throw new IllegalArgumentException("Could not find exactly " + expectedSize + " comma separated values to parse!");
        }

        double[] parsed = new double[expectedSize];
        for (int i = 0; i < split.length; i++) {
            parsed[i] = Double.parseDouble(split[i]);
        }

        return parsed;
    }

    /**
     * Generic function to attempt to parse a magic value from an integer.
     * <p>
     * It will first attempt to return a value from the primary parsing function. If the primary parsing function
     * returns a null value or the given value cannot be interpreted as an integer, this function will use the fallback
     * function which is required to return a non-nullable value.
     *
     * @param input          Input string to parse.
     * @param primaryParser  Function that returns a nullable value.
     * @param fallbackParser Function that returns a non-nullable value (or throws).
     * @param <T>            Expected return type.
     * @return Parsed object based on the input from either the primary parser or the fallback parser.
     */
    public static @NotNull <T> T parseAsIntOrFallback(@NotNull String input, Function<Integer, @Nullable T> primaryParser,
                                                      Function<String, @NotNull T> fallbackParser) {
        try {
            int value = Integer.parseInt(input);
            T primaryAttempt = primaryParser.apply(value);
            if (primaryAttempt != null) {
                return primaryAttempt;
            }
        } catch (NumberFormatException ignored) {
        }

        return fallbackParser.apply(input);
    }
}
