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

package io.zachbr.debuggery.reflection.chain;

import io.zachbr.debuggery.DebuggeryBase;
import io.zachbr.debuggery.Logger;
import io.zachbr.debuggery.reflection.*;
import io.zachbr.debuggery.reflection.types.InputException;
import io.zachbr.debuggery.reflection.types.TypeHandler;
import io.zachbr.debuggery.reflection.types.handlers.base.platform.PlatformSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Represents a chain of reflection operations
 */
class ReflectionChain {
    private final MethodMapProvider methodMapProvider;
    private final TypeHandler typeHandler;
    private final Logger logger;

    private final List<String> input;
    private final Object initialInstance;
    private final @Nullable PlatformSender<?> sender;
    private ReflectionResult result;

    ReflectionChain(ReflectionChainFactory factory, @NotNull String[] args,
                    @NotNull Object initialInstance, @Nullable PlatformSender<?> sender) {
        this.methodMapProvider = factory.methodMapProvider;
        this.typeHandler = factory.typeHandler;
        this.logger = factory.logger;

        this.input = Arrays.asList(args);
        this.initialInstance = initialInstance;
        this.sender = sender;
    }

    /**
     * Performs a series of reflective operations as specified at creation
     */
    void runChain() {
        MethodMap reflectionMap;
        Object currentInstance = initialInstance;
        ReflectionResult result = null;

        Object priorInstance;
        Method currentMethod;
        Object[] methodParameters;
        int argsToSkip = 0;

        for (int i = 0; i < input.size(); i++) {
            String currentArg = input.get(i);
            if (argsToSkip > 0) {
                argsToSkip--;
                continue;
            }

            Objects.requireNonNull(currentInstance);
            reflectionMap = methodMapProvider.getMethodMapFor(currentInstance.getClass());

            currentMethod = reflectionMap.getById(currentArg);
            if (currentMethod == null) {
                result = new ReflectionResult(ReflectionResult.Type.UNKNOWN_REFERENCE, null,
                        "Unknown or unavailable method");
                break;
            }

            List<String> stringMethodArgs = ReflectionUtil.getArgsForMethod(this.input.subList(i + 1, input.size()), currentMethod);
            argsToSkip = stringMethodArgs.size();

            // break early if there's an issue with the method arg count
            if (stringMethodArgs.size() != currentMethod.getParameterCount()) {
                result = new ReflectionResult(ReflectionResult.Type.ARG_MISMATCH, null,
                        ReflectionUtil.getArgMismatchString(currentMethod), null);
                break;
            }

            priorInstance = currentInstance;

            try {
                methodParameters = typeHandler.instantiateTypes(currentMethod.getParameterTypes(), stringMethodArgs, sender);
                currentInstance = reflect(currentInstance, currentMethod, methodParameters);
            } catch (Throwable ex) {
                ReflectionResult.Type type = ex instanceof InputException ? ReflectionResult.Type.INPUT_ERROR : ReflectionResult.Type.UNHANDLED_EXCEPTION;
                Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                String reason = cause.getLocalizedMessage() != null ? cause.getLocalizedMessage() : cause.toString();

                result = new ReflectionResult(type, null, reason, cause);
                break;
            }

            if (DebuggeryBase.isDebugMode()) {
                List<String> remainingArgs = input.subList(i, input.size());
                logDebug(i, reflectionMap, priorInstance, currentInstance, currentMethod, argsToSkip, methodParameters, remainingArgs);
            }

            if (currentMethod.getReturnType() != Void.TYPE && currentInstance == null) {
                result = new ReflectionResult(ReflectionResult.Type.NULL_REFERENCE, null,
                        ReflectionUtil.getFormattedMethodSignature(currentMethod) + " returned null!");
                break;
            }

            if (currentMethod.getReturnType() == Void.TYPE && currentInstance == null && i + argsToSkip < input.size() - 1) {
                result = new ReflectionResult(ReflectionResult.Type.ARG_MISMATCH, null,
                        "You provided extra args after a void return type!\n" + ReflectionUtil.getArgMismatchString(currentMethod));
                break;
            }

            if (currentMethod.getReturnType() == Void.TYPE && currentInstance == null) {
                result = new ReflectionResult(ReflectionResult.Type.SUCCESS, Void.TYPE, "Method returned void.");
            }
        }

        if (result == null) {
            // if we've made it this far without any result, we should assume that the operation was a
            // success and create a new successful result object with the currentInstance as the result
            result = new ReflectionResult(ReflectionResult.Type.SUCCESS, currentInstance);
        }

        Objects.requireNonNull(result);
        this.result = result;
    }

    /**
     * Gets the results of the reflective operation
     *
     * @return results
     * @throws IllegalStateException if called before the chain is run
     */
    @NotNull ReflectionResult getResult() {
        if (this.result == null) {
            throw new IllegalStateException("Cannot return a result before the chain is run!");
        }

        return this.result;
    }

    /**
     * Performs an individual reflective operation
     *
     * @param instance what to operate on
     * @param method   what to call
     * @param args     parameters to pass to operation
     * @return resulting object instance or null if that's the correct result
     * @throws InvocationTargetException see {@link Method#invoke(Object, Object...)}
     * @throws IllegalAccessException    see {@link Method#invoke(Object, Object...)}
     */
    private @Nullable Object reflect(@NotNull Object instance, @NotNull Method method, @NotNull Object[] args) throws InvocationTargetException, IllegalAccessException {
        if (args.length != method.getParameterCount()) {
            throw new IllegalArgumentException("Given argument count: " + args.length + " does not match required parameter count: " + method.getParameterCount());
        }

        method.trySetAccessible();
        return method.invoke(instance, args);
    }

    private void logDebug(int index, MethodMap currentMap, Object priorInstance, Object postInstance,
                          Method currentMethod, int argsToSkip, Object[] methodParams, List<String> remainingArgs) {
        logger.debug("========= CHAIN LOOP START  =========");
        logger.debug("index: " + index);
        logger.debug("MethodMap: " + currentMap);
        logger.debug("Prior-Instance: " + priorInstance);
        logger.debug("Method: " + currentMethod);
        logger.debug("Method Params: " + Arrays.toString(methodParams));
        logger.debug("Post-Instance: " + postInstance);
        logger.debug("Skip Args: " + argsToSkip);
        logger.debug("Remaining Args: " + remainingArgs.toString());
        logger.debug("========= CHAIN LOOP END =========");
    }
}
