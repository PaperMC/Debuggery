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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PotionEffectInputHandler implements InputHandler<PotionEffect> {

    @Override
    public @NotNull PotionEffect instantiateInstance(String input, Class<? extends PotionEffect> clazz, @Nullable PlatformSender<?> sender) throws Exception {
        String[] values = input.split(",");

        PotionEffectType type = PotionEffectTypeInputHandler.getPotionEffectType(values[0]);
        int duration = 120 * 20; // value is in ticks
        int amplifier = 1;

        if (values.length >= 2) {
            duration = Integer.parseInt(values[1]);
        }

        if (values.length >= 3) {
            amplifier = Integer.parseInt(values[2]);
        }

        return new PotionEffect(type, duration, amplifier);
    }

    @Override
    public @NotNull Class<PotionEffect> getRelevantClass() {
        return PotionEffect.class;
    }
}
