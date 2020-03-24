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
import io.zachbr.debuggery.reflection.types.handlers.base.*;
import io.zachbr.debuggery.reflection.types.handlers.base.platform.PlatformSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Manages all type handling
 * <p>
 * All conversions from string to object and vice-versa are implemented as individual
 * {@link Handler} instances and accessed through this managing class
 */
public final class TypeHandler {
    static final String NULL_INSTANCE_KEYWORD = "\\null\\";
    private final Logger logger;
    // input handlers
    // keep these two in sync on additions and removals
    private final Map<Class<?>, IHandler> inputHandlers = new HashMap<>();
    private final Set<IPolymorphicHandler> polymorphicHandlers = new LinkedHashSet<>();
    // output handlers
    private final Set<OHandler> outputHandlers = new LinkedHashSet<>();

    public TypeHandler(Logger logger) {
        this.logger = logger;
        BootstrapHandlers.init(this, logger);
    }

    /**
     * Gets the formatted "friendly" {@link String} output for the given instance of
     * an object.
     *
     * @param object instance to get output for
     * @return String output or null
     */
    public @Nullable String getOutputFor(@Nullable Object object) {
        // if the object is null, just return null
        // indicating that we shouldn't send any input at all
        if (object == null) {
            return null;
        }

        OHandler handler = getOHandlerForClass(object.getClass());
        if (handler != null) {
            return handler.getFormattedOutput(object);
        } else {
            // failing that, just give the generic toString
            return String.valueOf(object);
        }
    }

    /**
     * Creates new instances of the requested class types using the provided input
     *
     * @param classes {@link Class} types to be instantiated
     * @param input   The input to be used in the instantiation of the new instances
     * @return {@link Object} array of the requested types, instantiated and ready for use
     * @throws InputException when there's an issue instantiating the requested types
     */
    public @NotNull Object[] instantiateTypes(Class<?>[] classes, List<String> input) throws InputException {
        return instantiateTypes(classes, input, null);
    }

    /**
     * Creates new instances of the requested class types using the provided input
     *
     * @param classes {@link Class} types to be instantiated
     * @param input   The input to be used in the instantiation of the new instances
     * @param sender  The sender instance to be passed along to input handlers
     * @return {@link Object} array of the requested types, instantiated and ready for use
     * @throws InputException when there's an issue instantiating the requested types
     */
    public @NotNull Object[] instantiateTypes(Class<?>[] classes, List<String> input, @Nullable PlatformSender<?> sender) throws InputException {
        Objects.requireNonNull(classes);
        Objects.requireNonNull(input);

        List<Object> outputObjects = new ArrayList<>();

        for (int i = 0; i < input.size(); i++) {
            outputObjects.add(instantiateObjectFor(classes[i], input.get(i), sender));
        }

        return outputObjects.toArray();
    }

    /**
     * Creates new instances of the requested class types using the provided input
     *
     * @param clazz  {@link Class} type to be instantiated
     * @param input  The input to be used in the instantiation of the new instances
     * @return An instance of the requested class or null
     * @throws InputException when there's an issue instantiating the requested type
     */
    private @Nullable Object instantiateObjectFor(Class<?> clazz, String input, @Nullable PlatformSender<?> sender) throws InputException {
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(input);

        Object out = null;

        // if the user has explicitly requested null (via '\null\') then just give them null
        if (input.equals(NULL_INSTANCE_KEYWORD)) {
            return null;
        }

        // otherwise, lookup a handler and use it to instantiate an object
        IHandler handler = getIHandlerForClass(clazz);
        if (handler != null) {
            try {
                out = handler.instantiateInstance(input, clazz, sender);
            } catch (Exception ex) {
                // re-wrap exception and toss up the stack
                throw InputException.of(ex);
            }
        } else {
            throw InputException.of(new HandlerNotImplementedException(clazz));
        }

        return out;
    }

    /**
     * Registers a new handler to the system
     *
     * @param handler type handler
     * @return true if successfully registered
     */
    public boolean registerHandler(Handler handler) {
        Objects.requireNonNull(handler);

        logger.debug("-- Attempting to register handler: " + handler + " for class: " + handler.getRelevantClass().getName());

        if (handler instanceof IHandler) {
            return registerInputHandler((IHandler) handler);
        } else {
            return registerOutputHandler((OHandler) handler);
        }
    }

    /**
     * Registers an input handler to the system
     *
     * @param handler type handler to register
     * @return true if successfully registered
     */
    private boolean registerInputHandler(IHandler handler) {
        final Class<?> handlerRelevantClass = handler.getRelevantClass();

        // first, make sure there isn't an existing handler already registered to this type
        // do NOT factor polymorphic handlers into this lookup, allow them to be overridden with specific implementations
        final IHandler existingHandler = getIHandlerForClass(handlerRelevantClass, false);
        if (existingHandler != null) {
            logger.debug("!! Cannot register " + handler + ", conflicts with " + existingHandler);
            return false;
        } else {
            inputHandlers.put(handlerRelevantClass, handler);
            logger.debug("Added handler " + handler + " to Input Handlers");

            // if this handler is polymorphic, add it to that collection as well
            // we MUST keep these in sync with one another
            if (handler instanceof IPolymorphicHandler poly) {
                polymorphicHandlers.add(poly);
                logger.debug("-- -- Handler " + handler + " registered as polymorphic");
            }

            return true;
        }
    }

    /**
     * Registers an output handler to the system
     *
     * @param handler type handler to register
     * @return true if successfully added
     */
    private boolean registerOutputHandler(OHandler handler) {
        // first, make sure this handler isn't already registered
        OHandler existingHandler = getOHandlerForClass(handler.getRelevantClass());
        if (existingHandler != null) {
            logger.debug("!! Cannot register " + handler + ", conflicts with " + existingHandler);
            return false;
        } else {
            outputHandlers.add(handler);
            logger.debug("Added handler " + handler + " to Output Handlers");

            return true;
        }
    }

    /**
     * Removes a handler from the system
     *
     * @param handler type handler
     * @return true if successfully removed
     */
    boolean removeHandler(@NotNull Handler handler) {
        Objects.requireNonNull(handler);

        logger.debug("Attempting to remove handler: " + handler);

        if (handler instanceof IHandler) {
            return removeInputHandler((IHandler) handler);
        } else {
            return removeOutputHandler((OHandler) handler);
        }
    }

    /**
     * Removes a {@link IHandler} from the input system based on its class type
     *
     * @param clazz the {@link Class} type associated with the handler
     * @return true if successfully removed
     */
    boolean removeInputHandlerFor(@NotNull Class<?> clazz) {
        Objects.requireNonNull(clazz);
        logger.debug("Attempting to remove handler for class: " + clazz + " from Input Handlers.");

        IHandler handler = getIHandlerForClass(clazz);
        if (handler != null) {
            return removeHandler(handler);
        } else {
            logger.debug(" Cannot remove handler for class we cannot find: " + clazz);
            return false;
        }
    }

    /**
     * Removes a {@link OHandler} from the output system based on its class type
     *
     * @param clazz the {@link Class} type associated with the handler
     * @return true if successfully removed
     */
    boolean removeOutputHandlerFor(@NotNull Class<?> clazz) {
        Objects.requireNonNull(clazz);
        logger.debug("Attempting to remove handler for class: " + clazz + " from Output Handlers.");

        OHandler handler = getOHandlerForClass(clazz);
        if (handler != null) {
            return removeHandler(handler);
        } else {
            logger.debug(" Cannot remove handler for class we cannot find: " + clazz);
            return false;
        }
    }

    /**
     * Removes an Input Handler instance from the input handling system
     *
     * @param handler type handler to remove
     * @return true if successfully removed
     */
    private boolean removeInputHandler(IHandler handler) {
        final boolean removed = inputHandlers.remove(handler.getRelevantClass(), handler);

        if (!removed) {
            logger.debug("Input Handler doesn't appear to be registered, was not removed");
            return false;
        } else {
            logger.debug("Removed handler " + handler + " from Input Handlers");

            // if we removed earlier and this is polymorphic, remove it from that collection
            // we MUST keep these in sync with one another
            if (handler instanceof IPolymorphicHandler) {
                polymorphicHandlers.remove(handler);
                logger.debug("Removed handler " + handler + " from polymorphic map");
            }

            return true;
        }
    }

    /**
     * Removes an Output Handler instance from the output handling system
     *
     * @param handler type handler to remove
     * @return true if successfully removed
     */
    private boolean removeOutputHandler(OHandler handler) {
        final boolean removed = outputHandlers.remove(handler);

        if (!removed) {
            logger.debug("Handler doesn't appear to be registered, was not removed");
            return false;
        } else {
            logger.debug("Removed handler " + handler + " from Output Handlers");
            return true;
        }
    }

    /**
     * Searches all handlers looking for the relevant handler for the given {@link Class}.
     * <p>
     * This is obliged to check for explicit handlers first before then looking through polymorphic handlers.
     *
     * @param clazz {@link Class} type to look for a handler for
     * @return Relevant handler or null if none could be found
     */
    public @Nullable IHandler getIHandlerForClass(Class<?> clazz) {
        return getIHandlerForClass(clazz, true);
    }

    private @Nullable IHandler getIHandlerForClass(Class<?> clazz, boolean usePolymorphic) {
        Objects.requireNonNull(clazz);

        // first check for an explicit input handler to use for this type
        IHandler handler = inputHandlers.get(clazz);
        if (handler != null) {
            logger.debug("Found input handler " + handler + " for " + clazz);
        } else if (usePolymorphic) {
            // otherwise fall back to a polymorphic handler lookup
            logger.debug("Could not find any specific input handler for " + clazz + ", using polymorphic lookup...");
            handler = getGenericPolymorphicForFrom(clazz, polymorphicHandlers, "Input Handlers");
        }  else {
            logger.debug("Could not find any specific input handler for " + clazz + ", but not using polymorphic lookup.");
        }

        return handler;
    }

    /**
     * Gets the relevant {@link OHandler} for the given {@link Class}
     *
     * @param clazz {@link} Class to search with
     * @return relevant output handler or null if none could be found
     */
    private @Nullable OHandler getOHandlerForClass(Class<?> clazz) {
        Objects.requireNonNull(clazz);

        return getGenericPolymorphicForFrom(clazz, outputHandlers, "Output Handlers");
    }

    /**
     * Searches for a {@link Handler} in the given {@link Collection} capable of handling
     * the given {@link Class}.
     *
     * @param clazz     {@link Class} type to look for a handler for
     * @param toSearch  {@link Collection} to search through
     * @param debugName {@link String} collection name to use in debug messages
     * @param <T>       {@link Handler} type to get
     * @return relevant handler or null
     */
    private <T extends Handler> @Nullable T getGenericPolymorphicForFrom(Class<?> clazz, Collection<T> toSearch, @Nullable String debugName) {
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(toSearch);

        final String debugMsg = debugName == null ? toSearch.toString() : debugName;
        for (T handler : toSearch) {
            if (handler.getRelevantClass().isAssignableFrom(clazz)) {
                logger.debug("Found existing polymorphic handler " + handler + " for " + clazz + " in " + debugMsg);
                return handler;
            }
        }

        logger.debug("Unable to find existing polymorphic handler for " + clazz + " in " + debugMsg);
        return null;
    }

    /**
     * Gets an unmodifiable collection of all registered Input Handlers
     *
     * @return unmodifiable collection
     */
    public @NotNull Collection<IHandler> getAllInputHandlers() {
        return Collections.unmodifiableCollection(inputHandlers.values());
    }

    /**
     * Gets an unmodifiable collection all registered Output Handlers
     *
     * @return unmodifiable collection
     */
    public @NotNull Collection<OHandler> getAllOutputHandlers() {
        return Collections.unmodifiableCollection(outputHandlers);
    }
}
