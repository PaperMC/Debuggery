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
import io.zachbr.debuggery.reflection.types.handlers.input.EnumInputHandler;
import io.zachbr.debuggery.util.StringUtil;
import org.bukkit.GameMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GameModeInputHandler implements InputHandler<GameMode> {

    @Override
    public @NotNull GameMode instantiateInstance(String input, Class<? extends GameMode> clazz, @Nullable PlatformSender<?> sender) {
        return StringUtil.parseAsIntOrFallback(input, GameMode::getByValue, s -> EnumInputHandler.getEnumValue(s, GameMode.class));
    }

    @Override
    public @NotNull Class<GameMode> getRelevantClass() {
        return GameMode.class;
    }
}
