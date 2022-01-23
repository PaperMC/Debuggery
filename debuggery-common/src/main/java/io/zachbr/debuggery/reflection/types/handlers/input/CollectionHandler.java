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

import io.zachbr.debuggery.reflection.types.HandlerNotImplementedException;
import io.zachbr.debuggery.reflection.types.TypeHandler;
import io.zachbr.debuggery.reflection.types.handlers.base.IPolymorphicHandler;
import io.zachbr.debuggery.reflection.types.handlers.base.platform.PlatformSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

public class CollectionHandler implements IPolymorphicHandler {
    private final TypeHandler typeHandler;

    public CollectionHandler(TypeHandler handler) {
        this.typeHandler = handler;
    }

    @Override
    public @NotNull Collection<?> instantiateInstance(String input, Class<?> clazz, @Nullable PlatformSender<?> sender) throws Exception {
        // type erasure really screws us here, so you have to specify the type of data you want to input
        // CLASSTYPE:ELEMENT,ELEMENT,ELEMENT
        // Material:stone,grass,dirt
        String[] requestedAndElements = input.split(":", 2);
        Class<?> collectionType = ClassLoader.getSystemClassLoader().loadClass(requestedAndElements[0]);

        // split up input and prep for sending to type handler
        List<String> elements = Arrays.asList(requestedAndElements[1].split(","));
        Class<?>[] classSetup = new Class[elements.size()];
        Arrays.fill(classSetup, collectionType);

        Object[] instantiatedTypes;

        instantiatedTypes = typeHandler.instantiateTypes(classSetup, elements, sender);

        // Now figure out output and get that all setup
        Collection<Object> instanceOut;
        if (clazz == Set.class) {
            instanceOut = new HashSet<>();
        } else if (clazz == List.class) {
            instanceOut = new ArrayList<>();
        } else if (clazz == Queue.class) {
            instanceOut = new PriorityQueue<>();
        } else if (clazz == Collection.class) {
            instanceOut = new HashSet<>();
        } else {
            throw new HandlerNotImplementedException(clazz);
        }

        Collections.addAll(instanceOut, instantiatedTypes);

        return instanceOut;
    }

    @Override
    public @NotNull Class<?> getRelevantClass() {
        return Collection.class;
    }
}
