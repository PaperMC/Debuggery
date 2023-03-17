package io.zachbr.debuggery.commands;

import net.kyori.adventure.audience.Audience;

import java.util.List;

public abstract class CommandBase {
    private final String name;
    private final String permission;
    private final boolean requiresPlayer;
    private final boolean shouldShowInHelp;
    protected CommandBase(String name, String permission, boolean requiresPlayer, boolean shouldShowInHelp) {
        this.name = name;
        this.permission = permission;
        this.requiresPlayer = requiresPlayer;
        this.shouldShowInHelp = shouldShowInHelp;
    }

    /**
     * Gets the name of this command
     * This is also used for registration
     *
     * @return command name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the permission required to execute this command
     *
     * @return permission
     */
    public String getPermission() {
        return permission;
    }

    /**
     * Gets if this command requires a player
     *
     * @return if command requires a player
     */
    public boolean isRequiresPlayer() {
        return requiresPlayer;
    }

    /**
     * Gets if this command should show in the plugin help directory
     *
     * @return if command should show in help
     */
    public boolean shouldShowInHelp() {
        return shouldShowInHelp;
    }

    /**
     * Used by classes to implement their tab completion logic
     *
     * @param sender  {@link Audience} responsible for sending the message
     * @param args    arguments for the given command
     * @return whether the command was successfully handled
     */
    protected abstract List<String> tabCompleteLogic(Audience sender, String [] args);

    /**
     * Used by classes to implement their primary command logic
     *
     * @param sender  {@link Audience} responsible for sending the message
     * @param args    arguments for the given command
     * @return whether the command was successfully handled
     */
    protected abstract boolean commandLogic(Audience sender, String[] args);

    /**
     * Called whenever someone uses the /debuggery command with a specific command topic
     *
     * @param sender {@link Audience} responsible for sending the message
     * @param args   arguments for the given command
     * @return whether the command was successfully handled
     */
    protected abstract boolean helpLogic(Audience sender, String[] args);
}
