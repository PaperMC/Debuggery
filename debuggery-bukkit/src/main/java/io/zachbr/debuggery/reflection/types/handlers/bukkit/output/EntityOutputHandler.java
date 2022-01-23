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

package io.zachbr.debuggery.reflection.types.handlers.bukkit.output;

import io.zachbr.debuggery.reflection.types.handlers.base.OutputHandler;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EntityOutputHandler implements OutputHandler<Entity> {

    @Override
    public @Nullable String getFormattedOutput(Entity entity) {
        String out = entity.toString();
        final boolean hasTags = out.endsWith("}");
        final String idStr = "id=" + entity.getEntityId();

        if (hasTags) {
            // strip off ending brace and append
            out = out.substring(0, out.length() - 1);
            out += ", " + idStr + "}";
        } else {
            // just append
            out += "{" + idStr + "}";
        }

        return out;
    }

    @Override
    public @NotNull Class<Entity> getRelevantClass() {
        return Entity.class;
    }
}
