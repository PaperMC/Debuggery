package io.zachbr.debuggery.reflection.types.handlers.bukkit.input;

import io.zachbr.debuggery.reflection.types.handlers.base.InputHandler;
import io.zachbr.debuggery.reflection.types.handlers.base.platform.PlatformSender;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockInputHandler implements InputHandler<Block> {
    @Override
    public @NotNull Block instantiateInstance(String input, Class<? extends Block> clazz, @Nullable PlatformSender<?> sender) throws Exception {
        return LocationInputHandler.getLocation(input, sender).getBlock();
    }

    @Override
    public @NotNull Class<Block> getRelevantClass() {
        return Block.class;
    }
}
