package io.zachbr.debuggery.reflection.types.handlers.bukkit.input;

import io.zachbr.debuggery.reflection.types.handlers.base.InputHandler;
import io.zachbr.debuggery.reflection.types.handlers.base.platform.PlatformSender;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ComponentInputHandler implements InputHandler<Component> {
	@Override
	public @NotNull Component instantiateInstance(String input, Class<? extends Component> clazz, @Nullable PlatformSender<?> sender) throws Exception {
		return MiniMessage.miniMessage().deserialize(input);
	}

	@Override
	public @NotNull Class<Component> getRelevantClass() {
		return Component.class;
	}
}
