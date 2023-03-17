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

import io.zachbr.debuggery.reflection.*;

import java.lang.reflect.Method;
import java.util.*;

public final class CommandUtil {

    private static final String[] STRING_ARRAY = new String[0];

    /**
     * Gets all possible word completions based on the input
     *
     * @param input       Everything sent so far
     * @param completions Possible completions
     * @return List of possible completions based on the input
     */
    public static List<String> getCompletionsMatching(String[] input, Collection<String> completions) {
        String latestArg = input.length == 0 ? "" : input[input.length - 1];
        List<String> matches = new ArrayList<>();

        if (!completions.isEmpty()) {
            for (String possibleCompletion : completions) {
                if (doesStartWith(latestArg.toLowerCase(), possibleCompletion.toLowerCase())) {
                    matches.add(possibleCompletion);
                }
            }
        }

        return matches;
    }

    /**
     * Checks that the input matches the potential output
     *
     * @param target String to search for
     * @param base   Base string to check against
     * @return does the base string start with the target string
     */
    private static boolean doesStartWith(String target, String base) {
        return base.regionMatches(0, target, 0, target.length());
    }

    /**
     * Get reflective completions using the given input arguments checking against the initial {@link MethodMap}
     * <p>
     * This does not execute the methods nor does it ever instantiate any type objects as part of that process.
     *
     * @param args command input
     * @param initialMethods the initial method map used to start the loop
     * @param provider the method map provider we should use to get new maps as we need them
     * @return a list of matching completions
     */
    // todo - find some way to use a no-op reflectionchain with this rather than reimplement that loop
    public static List<String> getReflectiveCompletions(List<String> args, MethodMap initialMethods, MethodMapProvider provider) {
        MethodMap reflectionMap = initialMethods;
        Method lastMethod = null;
        Class<?> returnType = initialMethods.getMappedClass();

        int argsToSkip = 0;

        for (int i = 0; i < args.size(); i++) {
            String currentArg = args.get(i);
            if (argsToSkip > 0) {
                argsToSkip--;
                reflectionMap = null;

                continue;
            }

            reflectionMap = provider.getMethodMapFor(returnType);

            if (reflectionMap.getById(currentArg) != null) {
                lastMethod = reflectionMap.getById(currentArg);
                Objects.requireNonNull(lastMethod); // throw asap
                List<String> stringMethodArgs = ReflectionUtil.getArgsForMethod(args.subList(i + 1, args.size()), lastMethod);
                argsToSkip = stringMethodArgs.size();

                returnType = lastMethod.getReturnType();
            }
        }

        return reflectionMap == null
                ? Collections.emptyList()
                : getCompletionsMatching(args.toArray(STRING_ARRAY), reflectionMap.getAllIds());
    }
}
