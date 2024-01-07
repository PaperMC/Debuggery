package io.zachbr.debuggery.commands;

import io.zachbr.debuggery.DebuggeryBase;
import io.zachbr.debuggery.reflection.MethodMap;
import io.zachbr.debuggery.reflection.MethodMapProvider;
import io.zachbr.debuggery.reflection.chain.ReflectionResult;
import io.zachbr.debuggery.reflection.types.InputException;
import io.zachbr.debuggery.reflection.types.handlers.base.platform.PlatformSender;
import io.zachbr.debuggery.util.FancyExceptionWrapper;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class CommandReflection {
    private final DebuggeryBase debuggery;
    private final MethodMapProvider mapCache;
    private MethodMap availableMethods = MethodMap.EMPTY;

    public CommandReflection(Class<?> clazz, DebuggeryBase plugin) {
        this.debuggery = plugin;
        this.mapCache = plugin.getMethodMapProvider();
        updateReflectionClass(clazz);
    }

    /**
     * Handles all the reflection based command logic
     *
     * @param sender   sender to send information to
     * @param args     command arguments
     * @param instance instance of the class type
     * @return true if handled successfully
     */
    public boolean doReflectionLookups(@NotNull Audience sender, @NotNull String[] args, Object instance) {
        // 0 args just return info on object itself
        if (args.length == 0) {
            String result = getOutputStringFor(instance);
            if (result != null) {
                sender.sendMessage(Component.text(result));
            }
            return true;
        }

        // Combine quoted elements
        args = parse(String.join(" ", args));

        // more than 0 args, start chains
        Class<?> activeClass = availableMethods.getMappedClass();
        if (!activeClass.isInstance(instance)) {
            throw new IllegalArgumentException("Instance is of type: " + instance.getClass().getSimpleName() + "but was expecting: " + activeClass.getSimpleName());
        }

        final String inputMethod = args[0];
        if (!availableMethods.containsId(inputMethod)) {
            sender.sendMessage(Component.text("Unknown or unavailable method").color(NamedTextColor.RED));
            return true;
        }

        PlatformSender<?> platformSender = new PlatformSender<>(sender);
        ReflectionResult chainResult = debuggery.runReflectionChain(args, instance, platformSender);
        switch (chainResult.getType()) {
            case SUCCESS -> notifySenderOfSuccess(sender, chainResult);
            case INPUT_ERROR, UNHANDLED_EXCEPTION -> notifySenderOfException(sender, chainResult);
            case NULL_REFERENCE, UNKNOWN_REFERENCE, ARG_MISMATCH -> notifySenderOfResultReason(sender, chainResult);
            default -> throw new IllegalArgumentException("Unhandled switch case for result of type: " + chainResult.getType());
        }

        return true;
    }

    private void notifySenderOfException(@NotNull Audience sender, @NotNull ReflectionResult chainResult) {
        Throwable ex = chainResult.getException();
        Objects.requireNonNull(ex);

        String errorMessage = ex instanceof InputException ? "Exception deducing proper types from your input!" : "Exception invoking method - See console for more details!";
        Throwable cause = ex.getCause() == null ? ex : ex.getCause();

        FancyExceptionWrapper.sendFancyChatException(sender, errorMessage, cause);

        cause.printStackTrace();
    }

    private void notifySenderOfResultReason(Audience sender, ReflectionResult chainResult) {
        Objects.requireNonNull(chainResult.getReason());
        sender.sendMessage(Component.text(chainResult.getReason()));
    }

    private void notifySenderOfSuccess(Audience sender, ReflectionResult chainResult) {
        String output = getOutputStringFor(chainResult.getEndingInstance());
        if (output != null) {
            sender.sendMessage(Component.text(output));
        }
    }

    /**
     * Updates the locally cached reflection class
     *
     * @param typeIn class type to cache a reflection map for
     */
    public void updateReflectionClass(Class<?> typeIn) {
        if (availableMethods.getMappedClass() != typeIn) {
            availableMethods = mapCache.getMethodMapFor(typeIn);
        }
    }

    /**
     * Convenience method to run objects past the TypeHandler
     *
     * @param object Object to get String output for
     * @return textual description of Object
     */
    public @Nullable String getOutputStringFor(@Nullable Object object) {
        return debuggery.getTypeHandler().getOutputFor(object);
    }

    private static String[] parse(String input) {
        List<String> arguments = new ArrayList<>();
        StringBuilder currentArgument = new StringBuilder();
        boolean inQuote = false;

        for (int i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i);

            if (currentChar == '"') {
                inQuote = !inQuote;
            } else if (currentChar == ' ' && !inQuote) {
                arguments.add(currentArgument.toString());
                currentArgument.setLength(0);
            } else {
                currentArgument.append(currentChar);
            }
        }

        if (currentArgument.length() > 0) {
            arguments.add(currentArgument.toString());
        }

        return arguments.toArray(new String[0]);
    }

    public MethodMap getAvailableMethods() {
        return availableMethods;
    }

    public MethodMapProvider getMapCache() {
        return mapCache;
    }

    public void clearReflectionClass() {
        this.availableMethods = MethodMap.EMPTY;
    }
}
