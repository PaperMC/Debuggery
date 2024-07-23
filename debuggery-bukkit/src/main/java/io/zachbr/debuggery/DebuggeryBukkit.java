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

import io.zachbr.debuggery.commands.*;
import io.zachbr.debuggery.commands.base.BukkitCommandBase;
import io.zachbr.debuggery.reflection.types.handlers.bukkit.BukkitBootstrap;
import io.zachbr.debuggery.util.EventDebugger;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;

import java.util.*;

public class DebuggeryBukkit extends DebuggeryBase {

    private final EventDebugger eventDebugger;
    @Nullable
    private UUID targetedEntity;
    private final DebuggeryJavaPlugin javaPlugin;
    private final Map<String, BukkitCommandBase> commands = new HashMap<>();
    private final List<String> events;

    DebuggeryBukkit(DebuggeryJavaPlugin plugin, Logger logger) {
        super(logger);
        this.javaPlugin = plugin;
        this.eventDebugger = new EventDebugger(this);
        events = new Reflections().getSubTypesOf(Event.class).stream().map(Class::getCanonicalName).toList();
    }

    void onEnable() {
        printSystemInfo();
        new BukkitBootstrap(getTypeHandler(), getLogger());

        this.registerCommands();
    }

    void onDisable() {
        this.getMethodMapProvider().clearCache();
    }

    private void registerCommands() {
        this.registerCommand(new BlockCommand(this));
        this.registerCommand(new ChunkCommand(this));
        this.registerCommand(new DebugCommand(this));
        this.registerCommand(new DebuggeryCommand(this));
        this.registerCommand(new EntityCommand(this));
        this.registerCommand(new ItemCommand(this));
        this.registerCommand(new PlayerCommand(this));
        this.registerCommand(new ServerCommand(this));
        this.registerCommand(new WorldCommand(this));
        this.registerCommand(new SelectEntityCommand(this));
        this.registerCommand(new EventCommand(this));
        this.registerCommand(new EventRemoveCommand(this));

        for (BukkitCommandBase c : commands.values()) {
            this.getJavaPlugin().getServer().getCommandMap().register(c.getName(), c);
        }
    }

    private void registerCommand(final BukkitCommandBase command) {
        this.commands.put(command.getName(), command);
    }

    public Map<String, BukkitCommandBase> getAllCommands() {
        return Collections.unmodifiableMap(commands);
    }

    public JavaPlugin getJavaPlugin() {
        return javaPlugin;
    }

    @Nullable
    public UUID getTargetedEntity() {
        return targetedEntity;
    }

    public void setTargetedEntity(@Nullable UUID targetedEntity) {
        this.targetedEntity = targetedEntity;
    }

    public EventDebugger getEventDebugger() {
        return eventDebugger;
    }

    @Override
    String getPluginVersion() {
        return javaPlugin.getPluginMeta().getVersion();
    }

    @Override
    String getPlatformName() {
        return Bukkit.getName();
    }

    String getPlatformVersion() {
        return Bukkit.getVersion();
    }

    public List<String> getEvents() {
        return events;
    }
}
