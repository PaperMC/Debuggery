package io.zachbr.debuggery.reflection.types.handlers.bukkit.output;

import io.zachbr.debuggery.reflection.types.handlers.base.OutputHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ComponentOutputHandler implements OutputHandler<Component> {
	@Override
	public @Nullable String getFormattedOutput(Component component) {
		return MiniMessage.miniMessage().serialize(component);
	}

	@Override
	public @NotNull Class<Component> getRelevantClass() {
		return Component.class;
	}
}
