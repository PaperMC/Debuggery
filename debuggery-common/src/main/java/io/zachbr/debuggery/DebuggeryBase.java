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

package io.zachbr.debuggery;

import io.zachbr.debuggery.reflection.MethodMapProvider;
import io.zachbr.debuggery.reflection.chain.ReflectionChainFactory;
import io.zachbr.debuggery.reflection.chain.ReflectionResult;
import io.zachbr.debuggery.reflection.types.TypeHandler;
import io.zachbr.debuggery.reflection.types.handlers.base.platform.PlatformSender;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class DebuggeryBase {
    public static final String VERSION = "$VERSION";
    private static final boolean DEBUG_MODE = Boolean.getBoolean("debuggery.debug");
    private final MethodMapProvider methodMapProvider;
    private final ReflectionChainFactory chainFactory;
    private final TypeHandler typeHandler;
    private final Logger logger;

    DebuggeryBase(Logger logger) {
        this.logger = logger;
        this.methodMapProvider = new MethodMapProvider();
        this.typeHandler = new TypeHandler(getLogger());
        this.chainFactory = new ReflectionChainFactory(typeHandler, methodMapProvider, getLogger());
    }

    public final Logger getLogger() {
        return this.logger;
    }

    abstract String getPluginVersion();

    abstract String getPlatformName();

    abstract String getPlatformVersion();

    public static boolean isDebugMode() {
        return DEBUG_MODE;
    }

    /**
     * Gets system information as an array of lines
     *
     * @return system information
     */
    public @NotNull String[] getSystemInfo() {
        return new String[] {
            "Debuggery Ver: " + getPluginVersion(),
            "Server Impl: " + getPlatformName(),
            "Server Ver: " + getPlatformVersion(),
            "Java Runtime: " + System.getProperty("java.vendor") + " " + System.getProperty("java.runtime.version"),
            "Operating System: " + System.getProperty("os.name") + " "
                    + System.getProperty("os.version") + " "
                    + "(" + System.getProperty("os.arch") + ")"
        };
    }

    public final void printSystemInfo() {
        for (String line : getSystemInfo()) {
            logger.debug(line);
        }
    }

    public final MethodMapProvider getMethodMapProvider() {
        return this.methodMapProvider;
    }

    public final TypeHandler getTypeHandler() {
        return this.typeHandler;
    }

    // todo - better solutions elsewhere?
    public ReflectionResult runReflectionChain(String[] inputArgs, Object initialInstance, PlatformSender<?> sender) {
        Objects.requireNonNull(inputArgs);
        Objects.requireNonNull(initialInstance);

        return chainFactory.runChain(inputArgs, initialInstance, sender);
    }
}
