package io.zachbr.debuggery.commands;

import net.kyori.adventure.audience.Audience;

import java.util.List;

public interface CommandBase {

    /**
     * Gets the name of this command
     * This is also used for registration
     *
     * @return command name
     */
    String getName();

    /**
     * Gets the permission required to execute this command
     *
     * @return permission
     */
    String getPermission();

    /**
     * Gets if this command requires a player
     *
     * @return if command requires a player
     */
    boolean isRequiresPlayer();

    /**
     * Gets if this command should show in the plugin help directory
     *
     * @return if command should show in help
     */
    boolean shouldShowInHelp();

    /**
     * Used by classes to implement their tab completion logic
     *
     * @param sender  {@link Audience} responsible for sending the message
     * @param args    arguments for the given command
     * @return whether the command was successfully handled
     */
    List<String> tabCompleteLogic(Audience sender, String [] args);

    /**
     * Used by classes to implement their primary command logic
     *
     * @param sender  {@link Audience} responsible for sending the message
     * @param args    arguments for the given command
     * @return whether the command was successfully handled
     */
    boolean commandLogic(Audience sender, String[] args);

    /**
     * Called whenever someone uses the /debuggery command with a specific command topic
     *
     * @param sender {@link Audience} responsible for sending the message
     * @param args   arguments for the given command
     * @return whether the command was successfully handled
     */
    boolean helpLogic(Audience sender, String[] args);
}
