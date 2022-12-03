package io.zachbr.debuggery.util;

import io.zachbr.debuggery.DebuggeryBukkit;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class EventDebugger {

    private final DebuggeryBukkit debuggeryBukkit;
    private final Map<Class<?>, Consumer<Event>> eventDebuggers = new ConcurrentHashMap<>();
    private final Set<Class<?>> hasRegisteredDebuggers = new HashSet<>();

    public EventDebugger(DebuggeryBukkit debuggeryBukkit) {
        this.debuggeryBukkit = debuggeryBukkit;
    }


    public void clearAll() {
        this.eventDebuggers.clear();
    }

    public boolean clear(Class<?> clazz) {
        return this.eventDebuggers.remove(clazz) != null;
    }

    public Collection<Class<?>> getAll() {
        return this.eventDebuggers.keySet();
    }

    @SuppressWarnings("unchecked")
    public void addDebugger(Class<?> event, Consumer<Event> eventConsumer) {
        if (!this.hasRegisteredDebuggers.contains(event)) {
            Bukkit.getPluginManager().registerEvent((Class<? extends Event>) event, new Listener() {
            }, EventPriority.NORMAL, (listener, eventInstance) -> {
                Consumer<Event> consumer = EventDebugger.this.eventDebuggers.get(eventInstance.getClass());
                if (consumer != null) {
                    consumer.accept(eventInstance);
                }
            }, this.debuggeryBukkit.getJavaPlugin());
            this.hasRegisteredDebuggers.add(event);
        }

        this.eventDebuggers.put(event, eventConsumer);
    }
}
