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

import java.util.*;

public class MethodMapProvider {
    private final Map<Class<?>, MethodMap> cachedMethodMaps = new HashMap<>();

    /**
     * Gets a method map for the specified class
     * <p>
     * Will check the local cache before generating a new one.
     *
     * @param classIn class to get a method map for
     * @return method map for class
     */
    public @NotNull MethodMap getMethodMapFor(@NotNull Class<?> classIn) {
        Objects.requireNonNull(classIn);

        MethodMap methodMap = cachedMethodMaps.get(classIn);
        if (methodMap == null) {
            methodMap = new MethodMap(classIn);
            cachedMethodMaps.put(classIn, methodMap);
        }

        return methodMap;
    }

    /**
     * Gets all classes that have associated {@link MethodMap}s
     *
     * @return mapped classes
     */
    public @NotNull Set<Class<?>> getAllMappedClasses() {
        return new HashSet<>(cachedMethodMaps.keySet());
    }

    /**
     * Gets all {@link MethodMap}s that have been generated
     *
     * @return all method maps
     */
    public @NotNull Set<MethodMap> getAllMethodMaps() {
        return new HashSet<>(cachedMethodMaps.values());
    }

    /**
     * Clears the global cache, forcing future maps to be regenerated
     */
    public void clearCache() {
        this.cachedMethodMaps.clear();
    }

}
