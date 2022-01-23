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

package io.zachbr.debuggery.reflection.types.handlers.base;

import io.zachbr.debuggery.reflection.types.handlers.base.platform.PlatformSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Responsible for instantiating a specific class from input
 */
public interface InputHandler<T> extends Handler<T> {

    /**
     * Attempts to instantiate a new instance of an object using the given input
     * <p>
     * Implementing classes are expected to throw an exception if they cannot instantiate
     * an object for any reason. This method should never return null.
     *
     * @param input  what information to use to instantiate the object
     * @param clazz  class type needed
     * @return instantiated object
     * @throws Exception when an object cannot be instantiated
     */
    @NotNull T instantiateInstance(String input, Class<? extends T> clazz, @Nullable PlatformSender<?> sender) throws Exception;
}
