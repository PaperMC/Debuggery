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

package io.zachbr.debuggery.reflection.types.handlers.bukkit.input;

import io.zachbr.debuggery.reflection.types.handlers.base.InputHandler;
import io.zachbr.debuggery.reflection.types.handlers.base.platform.PlatformSender;
import io.zachbr.debuggery.util.StringUtil;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.NotNull;

public class EulerAngleInputHandler implements InputHandler<EulerAngle> {

    @Override
    public @NotNull EulerAngle instantiateInstance(String input, Class<? extends EulerAngle> clazz, PlatformSender<?> sender) {
        double[] parsed = StringUtil.parseDoublesFromString(input, 3);

        return new EulerAngle(parsed[0], parsed[1], parsed[2]);
    }

    @Override
    public @NotNull Class<EulerAngle> getRelevantClass() {
        return EulerAngle.class;
    }
}
