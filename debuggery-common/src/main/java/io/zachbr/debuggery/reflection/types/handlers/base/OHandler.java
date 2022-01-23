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

import org.jetbrains.annotations.Nullable;

/**
 * Responsible for taking an instance of an object and converting it
 * into a human readable string
 */
public interface OHandler extends Handler {

    /**
     * Formatted string containing information from the given object instance
     *
     * @param object instance to get information from
     * @return formatted string
     */
    @Nullable String getFormattedOutput(Object object); //TODO generics
}
