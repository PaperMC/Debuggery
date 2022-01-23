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

package io.zachbr.debuggery.reflection.types.handlers.bukkit;

import io.zachbr.debuggery.Logger;
import io.zachbr.debuggery.reflection.types.TypeHandler;
import io.zachbr.debuggery.reflection.types.handlers.base.Handler;
import io.zachbr.debuggery.reflection.types.handlers.base.InputHandler;
import io.zachbr.debuggery.reflection.types.handlers.base.platform.PlatformExtension;
import io.zachbr.debuggery.reflection.types.handlers.base.platform.PlatformSpecific;
import io.zachbr.debuggery.reflection.types.handlers.bukkit.input.*;
import io.zachbr.debuggery.reflection.types.handlers.bukkit.output.*;
import org.bukkit.entity.Player;

import java.util.*;

public class BukkitBootstrap {
    private final TypeHandler typeHandler;

    public BukkitBootstrap(TypeHandler typeHandler, Logger logger) {
        this.typeHandler = typeHandler;

        logger.debug("Begin Bukkit TypeHandler bootstrap");
        List<Handler> bukkitHandlers = new ArrayList<>();

        //
        // Input Handlers
        //

        // order can matter here
        bukkitHandlers.add(new BukkitClassInputHandler());
        bukkitHandlers.add(new DifficultyInputHandler());
        bukkitHandlers.add(new EntityInputHandler());
        bukkitHandlers.add(new EulerAngleInputHandler());
        bukkitHandlers.add(new GameModeInputHandler());
        bukkitHandlers.add(new InventoryInputHandler());
        bukkitHandlers.add(new ItemStackInputHandler());
        bukkitHandlers.add(new LocationInputHandler());
        bukkitHandlers.add(new MaterialDataInputHandler());
        bukkitHandlers.add(new MaterialInputHandler());
        bukkitHandlers.add(new NamespacedKeyInputHandler());
        bukkitHandlers.add(new PermissionInputHandler());
        bukkitHandlers.add(new PlayerInputHandler());
        bukkitHandlers.add(new PotionEffectInputHandler());
        bukkitHandlers.add(new PotionEffectTypeInputHandler());
        bukkitHandlers.add(new VectorInputHandler());
        // register polymorphics last
        bukkitHandlers.add(new BlockDataInputHandler());

        //
        // Output Handlers
        //

        // order can matter here
        bukkitHandlers.add(new OutputBlockStateHandler());
        bukkitHandlers.add(new EntityOutputHandler()); // above CommandSender
        bukkitHandlers.add(new CommandSenderOutputHandler());
        bukkitHandlers.add(new EulerAngleOutputHandler());
        bukkitHandlers.add(new HelpMapOutputHandler());
        bukkitHandlers.add(new InventoryOutputHandler(this.typeHandler));
        bukkitHandlers.add(new MessengerOutputHandler(this.typeHandler));
        bukkitHandlers.add(new OfflinePlayerOutputHandler());
        bukkitHandlers.add(new PermissionAttachmentInfoOutputHandler());
        bukkitHandlers.add(new WorldBorderOutputHandler());

        //
        // Register
        //

        for (Handler handler : bukkitHandlers) {
            if (!this.typeHandler.registerHandler(handler)) {
                throw new IllegalArgumentException("Unable to register " + handler);
            }
        }

        //
        // Platform Extensions to common classes
        //

        addExtensionFor(UUID.class, (args, clazz, sender) -> {
            if (sender.getRawSender() instanceof Player && args.equalsIgnoreCase("me")) {
                return ((Player) sender.getRawSender()).getUniqueId();
            } else {
                return null;
            }
        });

        logger.debug("End Bukkit TypeHandler bootstrap");
    }

    private <T> void addExtensionFor(Class<T> clazz, PlatformExtension<T> extension) {
        InputHandler<?> handler = typeHandler.getIHandlerForClass(clazz);
        if (handler == null) {
            throw new IllegalArgumentException("Cannot get input handler for type: " + clazz.getCanonicalName());
        }

        if (!(handler instanceof PlatformSpecific<?>)) {
            throw new IllegalArgumentException("Handler for " + clazz.getCanonicalName() + " is not platform specific!");
        }

        PlatformSpecific<T> psHandler = (PlatformSpecific<T>) handler;
        psHandler.add(extension);
    }
}
