package io.zachbr.debuggery.reflection.types.handlers.bukkit.input;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.zachbr.debuggery.reflection.types.handlers.base.InputHandler;
import io.zachbr.debuggery.reflection.types.handlers.base.platform.PlatformSender;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.Locale;

public class RegistryElementInputHandler<T extends Keyed> implements InputHandler<T> {
    private final RegistryKey<T> registryKey;
    private final Class<T> clazz;

    public RegistryElementInputHandler(RegistryKey<T> registryKey, Class<T> clazz) {
        this.registryKey = registryKey;
        this.clazz = clazz;
    }

    @Override
    public @NonNull T instantiateInstance(String input, Class<? extends T> clazz, @Nullable PlatformSender<?> sender) throws Exception {
        final Registry<T> registry = RegistryAccess.registryAccess().getRegistry(registryKey);
        final NamespacedKey key = NamespacedKey.fromString(input.toLowerCase(Locale.ROOT));
        if (key == null) {
            throw new IllegalArgumentException(input + " is not a valid namespaced key!");
        }

        return registry.getOrThrow(key);
    }

    @Override
    public @NotNull Class<T> getRelevantClass() {
        return this.clazz;
    }
}
