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

import io.zachbr.debuggery.reflection.types.handlers.base.InputPolymorphicHandler;
import io.zachbr.debuggery.reflection.types.handlers.base.platform.PlatformSender;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockDataInputHandler implements InputPolymorphicHandler<BlockData> {

    @Override
    public @NotNull BlockData instantiateInstance(String input, Class<? extends BlockData> clazz, @Nullable PlatformSender<?> sender) {
        // first try from straight material name
        Material material;
        try {
            material = MaterialInputHandler.getMaterial(input);
            return Bukkit.createBlockData(material);
        } catch (NullPointerException ignored) {
        }

        // next try using vanilla data strings
        return Bukkit.createBlockData(input); // will throw illegalargumentexception
    }

    @Override
    public @NotNull Class<BlockData> getRelevantClass() {
        return BlockData.class;
    }
}
