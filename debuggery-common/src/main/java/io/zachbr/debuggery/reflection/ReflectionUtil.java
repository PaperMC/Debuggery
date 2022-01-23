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

package io.zachbr.debuggery.reflection;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ReflectionUtil {

    /**
     * Attempts to parse a string for method parameters
     * <p>
     * Doesn't do any actual type detection or object instantiation, that's
     * not what this is for.
     *
     * @param args   A list of all arguments to search
     * @param method method to check against
     * @return a best-effort list of method params from the given list
     */
    public static @NotNull List<String> getArgsForMethod(@NotNull List<String> args, @NotNull Method method) {
        if (args.isEmpty() && method.getParameterCount() == 0) {
            return Collections.emptyList();
        }

        List<String> argsOut = new ArrayList<>();

        if (args.size() < method.getParameterCount()) {
            argsOut.addAll(args);
        } else {
            for (int i = 0; i < method.getParameterCount(); i++) {
                argsOut.add(args.get(i));
            }
        }

        return argsOut;
    }

    /**
     * Gets a complete and formatted method signature
     * <p>
     * The signature is exact and should tell user's what to expect
     * It will look something like this "method(ParamType, ParamType)"
     * <p>
     * Each param will result in an entry between the parentheses,
     * a method with no params will result in nothing between the parentheses
     *
     * @param method which method to get a formatted name for
     * @return a formatted name
     */
    public static @NotNull String getFormattedMethodSignature(Method method) {
        StringBuilder builder = new StringBuilder(method.getName());

        builder.append('(');
        boolean first = true;
        for (Class<?> type : method.getParameterTypes()) {
            if (first) {
                builder.append(type.getSimpleName());
                first = false;
            } else {
                builder.append(", ").append(type.getSimpleName());
            }
        }
        builder.append(')');

        return builder.toString();
    }

    public static @NotNull String getMethodId(Method method) {
        return getFormattedMethodSignature(method).replace(" ", "");
    }

    /**
     * Gets the error message we should send when the input string is missing arguments
     *
     * @param method method with missing arguments
     * @return error message
     */
    public static @NotNull String getArgMismatchString(Method method) {
        final String methodName = method.getName();
        final Class<?> returnType = method.getReturnType();
        final String returnTypeName = returnType.getSimpleName();
        String returnInfo;

        if (returnType == Void.TYPE) {
            returnInfo = "returns void.";
        } else if (startsWithVowel(returnTypeName)) {
            returnInfo = "returns an " + returnTypeName;
        } else {
            returnInfo = "returns a " + returnTypeName;
        }

        return "Method " + methodName + " requires " + method.getParameterCount() + " args and " + returnInfo + "\n"
                + ReflectionUtil.getFormattedMethodSignature(method);
    }

    /**
     * Returns whether the given string starts with a vowel
     *
     * @return whether the given string starts with a vowel
     */
    private static boolean startsWithVowel(@NotNull final String s) {
        if (s.isEmpty()) {
            return false;
        }

        final char c = Character.toLowerCase(s.charAt(0));
        return c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u';
    }
}
