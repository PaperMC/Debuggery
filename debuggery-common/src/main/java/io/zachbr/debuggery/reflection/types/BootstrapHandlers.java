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
import io.zachbr.debuggery.reflection.types.handlers.base.Handler;
import io.zachbr.debuggery.reflection.types.handlers.input.*;
import io.zachbr.debuggery.reflection.types.handlers.output.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Bootstraps common handler classes into the TypeHandler system
 */
class BootstrapHandlers {

    /**
     * Registers all handler classes
     *
     * @param typeHandler {@link TypeHandler} instance to bootstrap
     */
    static void init(TypeHandler typeHandler, Logger logger) {
        logger.debug("Begin Common TypeHandler bootstrap");
        List<Handler> registration = new ArrayList<>();

        //
        // Input Handlers
        //

        PrimitivesInputHandler.addHandlers(registration); // Special cased for multi-registration
        PrimitiveArrayInputHandler.addHandlers(registration); // multi-registration

        // order can matter here
        registration.add(new StringInputHandler());
        registration.add(new UUIDInputHandler());
        // register polymorphics last
        registration.add(new CollectionInputHandler(typeHandler));
        registration.add(new EnumInputHandler());
        registration.add(new ObjectArrayInputHandler(typeHandler));

        //
        // Output Handlers
        //

        new OutputArrayHandler(registration, typeHandler); // Special cased for multi-registration

        // order can matter here
        registration.add(new OutputCollectionHandler(typeHandler));
        registration.add(new OutputMapHandler(typeHandler));
        registration.add(new OutputStringHandler());

        for (Handler handler : registration) {
            if (!typeHandler.registerHandler(handler)) {
                throw new IllegalArgumentException("Unable to register " + handler);
            }
        }

        logger.debug("End Common TypeHandler bootstrap");
    }
}
