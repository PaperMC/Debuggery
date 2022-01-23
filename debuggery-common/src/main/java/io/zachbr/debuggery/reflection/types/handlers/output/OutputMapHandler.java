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
import io.zachbr.debuggery.reflection.types.handlers.base.OutputHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class OutputMapHandler implements OutputHandler<Map> {
    private final TypeHandler typeHandler;

    public OutputMapHandler(TypeHandler handler) {
        this.typeHandler = handler;
    }

    @Override
    public @Nullable String getFormattedOutput(Map object) {
        final Map<?,?> map = (Map<?,?>) object;
        StringBuilder out = new StringBuilder("{");

        for (var iterator = map.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<?,?> entry = iterator.next();

            out.append('[').append(typeHandler.getOutputFor(entry.getKey())).append(", ");
            out.append(typeHandler.getOutputFor(entry.getValue())).append(']');

            if (iterator.hasNext()) {
                out.append('\n');
            }
        }

        return out.append('}').toString();
    }

    @Override
    public @NotNull Class<Map> getRelevantClass() {
        return Map.class;
    }
}
