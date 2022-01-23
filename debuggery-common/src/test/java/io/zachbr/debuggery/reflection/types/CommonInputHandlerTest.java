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

package io.zachbr.debuggery.reflection.types;

import io.zachbr.debuggery.TestLoggerImpl;
import io.zachbr.debuggery.reflection.types.implementations.AnEnum;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.*;

public class CommonInputHandlerTest {
    private final TypeHandler typeHandler = new TypeHandler(new TestLoggerImpl());

    @SuppressWarnings("RedundantCast")
    @Test
    public void testPrimitives() throws InputException {
        Class[] inputTypes = {
                byte.class,
                short.class,
                int.class,
                long.class,
                float.class,
                double.class,
                boolean.class,
                char.class
        };

        String[] input = {
                "127",
                "15",
                "11612",
                "5512512",
                ".0451",
                "2.254",
                "true",
                "§"
        };

        Object[] output = typeHandler.instantiateTypes(inputTypes, Arrays.asList(input));

        // First let's make sure we didn't lose anything, or get anything
        assertEquals(inputTypes.length, output.length);

        // Next let's make sure everything is the right type
        // Java will autobox away our primitives, test against wrappers
        assertTrue(output[0] instanceof Byte);
        assertTrue(output[1] instanceof Short);
        assertTrue(output[2] instanceof Integer);
        assertTrue(output[3] instanceof Long);
        assertTrue(output[4] instanceof Float);
        assertTrue(output[5] instanceof Double);
        assertTrue(output[6] instanceof Boolean);
        assertTrue(output[7] instanceof Character);

        // Finally, let's make sure the values are correct
        // Yes, I am aware some of these casts are redundant, deal with it
        assertEquals(output[0], ((byte) 127));
        assertEquals(output[1], ((short) 15));
        assertEquals(output[2], ((int) 11612));
        assertEquals(output[3], ((long) 5512512));
        assertEquals(output[4], ((float) .0451));
        assertEquals(output[5], ((double) 2.254));
        assertEquals(output[6], ((boolean) true));
        assertEquals(output[7], ((char) '§'));
    }

    @Test
    public void testValueFromEnum() throws InputException {
        Class[] inputTypes = {
                AnEnum.class,
                AnEnum.class,
                AnEnum.class,
                AnEnum.class
        };

        String[] input = {
                "val_1",
                "vaL_2",
                "VAL_4",
                "VaL_5"
        };

        Object[] output = typeHandler.instantiateTypes(inputTypes, Arrays.asList(input));

        // First let's make sure we didn't lose anything, or get anything
        assertEquals(inputTypes.length, output.length);

        // Next let's make sure everything is the right type
        assertTrue(output[0] instanceof AnEnum);
        assertTrue(output[1] instanceof AnEnum);
        assertTrue(output[2] instanceof AnEnum);
        assertTrue(output[3] instanceof AnEnum);

        // Finally, let's make sure the values are correct
        assertSame(AnEnum.VAL_1, output[0]);
        assertSame(AnEnum.VAL_2, output[1]);
        assertSame(AnEnum.VAL_4, output[2]);
        assertSame(AnEnum.VAL_5, output[3]);
    }

    @Test
    public void testCollection() throws InputException {
        Class[] inputTypes = {List.class, Set.class, Collection.class, Queue.class};
        String[] input = {"java.lang.String:Grass,Stone,Dirt", "java.lang.String:Wither,Zombie,Creeper", "java.lang.String:Nether,Ender",
                "java.lang.String:Redwood,RED_MUSHROOM,dark_oak"};

        Object[] output = typeHandler.instantiateTypes(inputTypes, Arrays.asList(input), null);

        // verify we got the types we requested
        assertTrue(output[0] instanceof List);
        assertTrue(output[1] instanceof Set);
        assertTrue(output[2] instanceof Collection);
        assertTrue(output[3] instanceof Queue);

        BiFunction<Collection, Object[], Boolean> testAllPresent = (collection, testers) -> {
            boolean passes = true;
            for (Object tester : testers) {
                if (!collection.contains(tester)) {
                    System.out.println("CANNOT FIND REQUESTED TYPE: " + tester);
                    passes = false;
                }
            }

            return passes;
        };

        // verify list contents
        List list = (List) output[0];
        String[] expectedMats = {"Grass", "Stone", "Dirt"};
        assertSame(expectedMats.length, list.size());
        assertTrue(testAllPresent.apply(list, expectedMats));

        // verify set contents
        Set set = (Set) output[1];
        String[] expectedSkulls = {"Wither", "Zombie", "Creeper"};
        assertSame(expectedSkulls.length, set.size());
        assertTrue(testAllPresent.apply(set, expectedSkulls));

        // verify collection contents
        Collection collection = (Collection) output[2];
        String[] expectedPortTypes = {"Nether", "Ender"};
        assertSame(expectedPortTypes.length, collection.size());
        assertTrue(testAllPresent.apply(collection, expectedPortTypes));

        // verify queue contents
        Queue queue = (Queue) output[3];
        String[] expectedClasses = {"Redwood", "RED_MUSHROOM", "dark_oak"};
        assertSame(expectedClasses.length, queue.size());
        assertTrue(testAllPresent.apply(queue, expectedClasses));
    }
}
