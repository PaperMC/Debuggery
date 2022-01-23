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

import io.zachbr.debuggery.Logger;
import io.zachbr.debuggery.TestLoggerImpl;
import io.zachbr.debuggery.reflection.ReflTestClass;
import io.zachbr.debuggery.reflection.types.handlers.base.Handler;
import io.zachbr.debuggery.reflection.types.handlers.base.InputHandler;
import io.zachbr.debuggery.reflection.types.handlers.base.OutputHandler;
import io.zachbr.debuggery.reflection.types.handlers.base.platform.PlatformSender;
import io.zachbr.debuggery.reflection.types.implementations.AnEnum;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class TypeHandlerTest {
    private static final Class<?> CASE_NEVER_WILL_BE_REGISTERED = Assertions.class;
    private final Logger logger = new TestLoggerImpl();
    private final TypeHandler typeHandler = new TypeHandler(logger);

    @Test
    public void validateAllHandlers() {
        List<Handler> allHandlers = new ArrayList<>();
        allHandlers.addAll(typeHandler.getAllInputHandlers());
        allHandlers.addAll(typeHandler.getAllOutputHandlers());

        // test to make sure all handlers have a class type associated with them
        // this is supposed to always be true but if you're just implementing classes really fast
        // your IDE or tool or whatever will tend to generate implementations with a null return and
        // you can miss it and this is cheap to test at compile time so what the hell why not

        @SuppressWarnings("ConstantConditions")
        Predicate<Handler> hasNoClassType = handler -> handler.getRelevantClass() == null;

        List<Handler> offending = allHandlers.stream()
                .filter(hasNoClassType)
                .peek(h -> System.out.println(h.getClass().getCanonicalName() + " has null associated class!"))
                .collect(Collectors.toList());

        assertEquals(0, offending.size());
    }

    @Test
    public void ensurePolymorphicInput() throws InputException {
        // ensures that polymorphic input handlers are functioning correctly
        // todo - in future, test additional polymorphic type handlers
        Class[] requestedTypes = {AnEnum.class, AnEnum.class}; // Test various enums to be handled by IEnumHandler
        String[] inputArgs = {"VAL_1", "VAL_5"};

        Object[] output = typeHandler.instantiateTypes(requestedTypes, Arrays.asList(inputArgs));

        assertTrue(output[0] instanceof AnEnum);
        assertTrue(output[1] instanceof AnEnum);
        Assertions.assertEquals(AnEnum.VAL_1, output[0]);
        Assertions.assertEquals(AnEnum.VAL_5, output[1]);
    }

    @Test
    public void ensureAddAndRemoveHandlers() {
        //
        // Handler additions
        //

        // attempt to add a string handler
        // this should clash and fail, returning false
        InputHandler<String> iStringHandler = new InputHandler<>() {
            @NotNull
            @Override
            public String instantiateInstance(String input, Class<? extends String> clazz, @Nullable PlatformSender<?> sender) {
                throw new HandlerNotImplementedException(clazz);
            }

            @NotNull
            @Override
            public Class<String> getRelevantClass() {
                return String.class;
            }
        };

        // same but with an output handler
        OutputHandler<Collection> oCollectionHandler = new OutputHandler<>() {
            @NotNull
            @Override
            public String getFormattedOutput(Collection object) {
                throw new HandlerNotImplementedException(object.getClass());
            }

            @NotNull
            @Override
            public Class<Collection> getRelevantClass() {
                return Collection.class;
            }
        };

        final boolean stringInputAddSuccess = typeHandler.registerHandler(iStringHandler);
        final boolean collectionOutputAddSuccess = typeHandler.registerHandler(oCollectionHandler);

        // now test that adding a random new class will work
        InputHandler<ReflTestClass> iLocalClassHandler = new InputHandler<>() {
            @NotNull
            @Override
            public ReflTestClass instantiateInstance(String input, Class<? extends ReflTestClass> clazz, @Nullable PlatformSender<?> sender) {
                throw new HandlerNotImplementedException(clazz);
            }

            @NotNull
            @Override
            public Class<ReflTestClass> getRelevantClass() {
                return ReflTestClass.class;
            }
        };

        OutputHandler<ReflTestClass> oLocalClassHandler = new OutputHandler<>() {
            @NotNull
            @Override
            public String getFormattedOutput(ReflTestClass object) {
                throw new HandlerNotImplementedException(object.getClass());
            }

            @NotNull
            @Override
            public Class<ReflTestClass> getRelevantClass() {
                return ReflTestClass.class;
            }
        };

        final boolean localClassInputAddSuccess = typeHandler.registerHandler(iLocalClassHandler);
        final boolean localClassOutputAddSuccess = typeHandler.registerHandler(oLocalClassHandler);

        //
        // Handler removals
        //

        // remove earlier additions
        final boolean iLocalClassRemoveByInstanceSuccess = typeHandler.removeHandler(iLocalClassHandler);
        final boolean oLocalClassRemoveByInstanceSucesss = typeHandler.removeHandler(oLocalClassHandler);

        // attempt removing default bootstrapped class handlers by class type
        final boolean iStringRemoveByClassSuccess = typeHandler.removeInputHandlerFor(String.class);
        final boolean oCollectionRemoveByClassSuccess = typeHandler.removeOutputHandlerFor(Collection.class);

        // attempt removing handlers for a class that doesn't exist
        InputHandler<?> iNotRegistered = new InputHandler() {
            @NotNull
            @Override
            public Object instantiateInstance(String input, Class clazz, @Nullable PlatformSender sender) {
                throw new HandlerNotImplementedException(clazz);
            }

            @NotNull
            @Override
            public Class<?> getRelevantClass() {
                return CASE_NEVER_WILL_BE_REGISTERED;
            }
        };

        OutputHandler<?> oNotRegistered = new OutputHandler() {
            @NotNull
            @Override
            public String getFormattedOutput(Object object) {
                throw new HandlerNotImplementedException(object.getClass());
            }

            @NotNull
            @Override
            public Class<?> getRelevantClass() {
                return CASE_NEVER_WILL_BE_REGISTERED;
            }
        };

        final boolean removedUnregisteredIHandler = typeHandler.removeHandler(iNotRegistered);
        final boolean removedUnregisteredOHandler = typeHandler.removeHandler(oNotRegistered);

        //
        // Validation
        //

        // addition
        assertFalse(stringInputAddSuccess, "String Handler addition did not clash!");
        assertFalse(collectionOutputAddSuccess, "Collection handler addition did not clash!");
        assertTrue(localClassInputAddSuccess, "Unable to register new input local class!");
        assertTrue(localClassOutputAddSuccess, "Unable to register new output local class!");

        // removal
        assertTrue(iLocalClassRemoveByInstanceSuccess, "Unable to remove input local class by instance!");
        assertTrue(oLocalClassRemoveByInstanceSucesss, "Unable to remove output local class by instance!");
        assertTrue(iStringRemoveByClassSuccess, "Unable to remove input string handler by class type!");
        assertTrue(oCollectionRemoveByClassSuccess, "Unable to remove output collection handler by class type!");
        assertFalse(removedUnregisteredIHandler, "Attempting to remove an unregistered IHandler didnt return false!");
        assertFalse(removedUnregisteredOHandler, "Attempting to remvoe an unregistered OHandler didnt return false!");

        //
        // Cleanup
        //

        try {
            Field field = this.getClass().getDeclaredField("typeHandler");
            field.setAccessible(true);
            field.set(this, new TypeHandler(logger));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            logger.err("Unable to cleanup after ensureAddAndRemoveHandlers test!");
            e.printStackTrace();
        }
    }

    @Test
    public void ensureThrowsOnNoSuchIHandler() {
        // verify that if we have an unknown type requested, it always throws
        Assertions.assertThrows(InputException.class, () -> {
            Class[] requestedType = {CASE_NEVER_WILL_BE_REGISTERED};
            typeHandler.instantiateTypes(requestedType, Collections.singletonList("blah"));
        });
    }

    @Test
    public void ensureNullAlwaysAvailable() throws InputException {
        Object[] array = typeHandler.instantiateTypes(new Class[]{Object.class}, Collections.singletonList(TypeHandler.NULL_INSTANCE_KEYWORD));
        Object instance = array[0];

        // verify we always have access to null

        assertNull(instance);
    }

    @Test
    public void ensureNullInputGivesNullOutput() {
        String output = typeHandler.getOutputFor(null);

        // verifies that the output handler never gives us anything for a null input object
        // this matters because it's how we know not to send any message at all, rather than an empty string or empty
        // brackets or something

        assertNull(output);
    }

    @Test
    public void ensureInputExceptionNeverWrapsItself() {
        // Make sure that InputExceptions never end up wrapping themselves
        // We only ever want to wrap the base cause

        Exception trueException = new IllegalArgumentException("hiya is not a valid argument!");
        InputException baseException = InputException.of(trueException);
        InputException level2 = InputException.of(baseException);

        assertSame(trueException, level2.getCause());

        InputException level3 = InputException.of(level2);
        InputException level4 = InputException.of(level3);

        assertSame(trueException, level4.getCause());
    }
}
