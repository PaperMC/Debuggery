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
import org.bukkit.help.HelpMap;
import org.bukkit.help.HelpTopic;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HelpMapOutputHandler implements OutputHandler<HelpMap> {

    @Override
    public @Nullable String getFormattedOutput(HelpMap helpMap) {
        StringBuilder returnString = new StringBuilder("[");

        for (HelpTopic topic : helpMap.getHelpTopics()) {
            returnString
                    .append('{')
                    .append(topic.getName())
                    .append(", ")
                    .append(topic.getShortText())
                    .append("}\n");
        }

        return returnString.append(']').toString();
    }

    @Override
    public @NotNull Class<HelpMap> getRelevantClass() {
        return HelpMap.class;
    }
}
