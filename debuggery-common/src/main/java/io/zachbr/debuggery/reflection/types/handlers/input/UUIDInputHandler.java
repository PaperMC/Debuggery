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

package io.zachbr.debuggery.reflection.types.handlers.input;

import io.zachbr.debuggery.reflection.types.handlers.base.InputHandler;
import io.zachbr.debuggery.reflection.types.handlers.base.platform.PlatformSender;
import io.zachbr.debuggery.reflection.types.handlers.base.platform.PlatformSpecific;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class UUIDInputHandler extends PlatformSpecific<UUID> implements InputHandler<UUID> {

    @Override
    public @NotNull UUID instantiateInstance(String input, Class<? extends UUID> clazz, @Nullable PlatformSender<?> sender) {
        UUID uuid = fromExtensions(input, clazz, sender);
        return uuid != null ? uuid : UUID.fromString(input);
    }

    @Override
    public @NotNull Class<UUID> getRelevantClass() {
        return UUID.class;
    }
}
