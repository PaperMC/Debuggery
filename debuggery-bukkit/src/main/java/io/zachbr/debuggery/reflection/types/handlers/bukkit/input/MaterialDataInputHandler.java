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
import io.zachbr.debuggery.reflection.types.handlers.input.PrimitivesInputHandler;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MaterialDataInputHandler implements InputHandler<MaterialData> {

    @Override
    public @NotNull MaterialData instantiateInstance(String input, Class<? extends MaterialData> clazz, @Nullable PlatformSender<?> sender) {
        String[] contents = input.split(":", 2);
        Material material = MaterialInputHandler.getMaterial(contents[0]);
        byte data = (byte) PrimitivesInputHandler.getPrimitive(contents[1], byte.class);

        return new MaterialData(material, data);
    }

    @Override
    public @NotNull Class<MaterialData> getRelevantClass() {
        return MaterialData.class;
    }
}
