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
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class PotionEffectTypeInputHandler implements InputHandler<PotionEffectType> {
    private static final Map<String, PotionEffectType> TYPES = new HashMap<>();

    public PotionEffectTypeInputHandler() {
        for (Field field : PotionEffectType.class.getDeclaredFields()) {
            if (field.getType() == PotionEffectType.class) {
                PotionEffectType value = null;

                try {
                    value = (PotionEffectType) field.get(null);
                } catch (IllegalAccessException ignored) {
                }

                if (value == null) {
                    continue;
                }

                TYPES.put(field.getName().toLowerCase(), value);
            }
        }
    }

    static @NotNull PotionEffectType getPotionEffectType(String name) throws NoSuchFieldException {
        PotionEffectType type = TYPES.get(name.toLowerCase());
        if (type == null) {
            throw new NoSuchFieldException("No PotionEffectType with the name: " + name);
        } else {
            return type;
        }
    }

    @Override
    public @NotNull PotionEffectType instantiateInstance(String input, Class<? extends PotionEffectType> clazz, @Nullable PlatformSender<?> sender) throws Exception {
        return getPotionEffectType(input);
    }

    @Override
    public @NotNull Class<PotionEffectType> getRelevantClass() {
        return PotionEffectType.class;
    }
}
