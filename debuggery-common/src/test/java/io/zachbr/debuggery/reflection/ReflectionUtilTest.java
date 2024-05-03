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

package io.zachbr.debuggery.reflection;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReflectionUtilTest {

    @Test
    public void ensureArgsForMethodAccurate() throws NoSuchMethodException {
        // Given a method with only one param, pick out that param without the others
        List<String> inputStr = Arrays.asList("1", "nextMethod", "param", "param2");
        Method testClass1234 = ReflTestClass.ReflSubClass.class.getMethod("get1234", int.class);
        List<String> out = ReflectionUtil.getArgsForMethod(inputStr, testClass1234);

        assertEquals(1, out.size());
        assertEquals("1", out.getFirst());

        // Given a method that requires multiple params, but not provided enough, ensure all are passed through
        inputStr = Arrays.asList("1", "2", "3");
        Method lotsOfParams = ReflTestClass.class.getMethod("methodWithLotsOfParams", int.class, int.class, int.class, int.class, int.class, int.class, int.class);
        out = ReflectionUtil.getArgsForMethod(inputStr, lotsOfParams);

        assertEquals(inputStr.size(), out.size());
        for (int i = 0; i < inputStr.size(); i++) {
            assertEquals(inputStr.get(i), out.get(i));
        }

        // if no params are required, and no input is given, ensure we always get an empty list
        inputStr = new ArrayList<>();
        Method noParams = ReflTestClass.class.getMethod("getSomeNumbers");
        out = ReflectionUtil.getArgsForMethod(inputStr, noParams);

        assertEquals(0, out.size());
    }

    @Test
    public void ensureNoWhitespaceInMethodIds() throws NoSuchMethodException {
        Method method = ReflTestClass.class.getMethod("getSomeNumbers");
        String id = ReflectionUtil.getMethodId(method);

        long whitespaceCount = id.chars()
                .filter(Character::isWhitespace)
                .count();

        if (whitespaceCount != 0) {
            System.out.println("Illegal whitespace chars found in id: " + id);
        }

        assertEquals(0, whitespaceCount);
    }

    @Test
    public void validateArgsMismatchContent() throws NoSuchMethodException {
        BiConsumer<Method, String> validateBasics = (method, s) -> {
            s = s.toLowerCase();
            assertTrue(s.contains(method.getName().toLowerCase()));
            assertTrue(s.contains(method.getReturnType().getSimpleName().toLowerCase()));
            assertTrue(s.contains(String.valueOf(method.getParameterCount())));
        };

        Method voidReturn = ReflTestClass.class.getMethod("methodWithLotsOfParams", int.class, int.class, int.class, int.class, int.class, int.class, int.class);
        String voidReturnDescriptor = ReflectionUtil.getArgMismatchString(voidReturn);
        validateBasics.accept(voidReturn, voidReturnDescriptor);
        // ensure we point out its void
        assertTrue(voidReturnDescriptor.toLowerCase().contains("void"));

        Method getSubClass = ReflTestClass.class.getMethod("getSubClass");
        String getterDescriptor = ReflectionUtil.getArgMismatchString(getSubClass);
        validateBasics.accept(getSubClass, getterDescriptor);
        // check our a, an, grammar predicate while we're here
        assertTrue(getterDescriptor.toLowerCase().contains(" a "));

        Method getSomeNumbers = ReflTestClass.class.getMethod("getSomeNumbers");
        String someNumbersDescriptor = ReflectionUtil.getArgMismatchString(getSomeNumbers);
        validateBasics.accept(getSomeNumbers, someNumbersDescriptor);
        // check our a, an, grammar predicate while we're here
        assertTrue(someNumbersDescriptor.toLowerCase().contains(" an "));
    }
}
