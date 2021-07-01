package net.essentialsx.api.v2.services.discord;

import java.util.List;

/**
 * Represents a command to be registered with the Discord client.
 */
public interface InteractionCommand {
    /**
     * Whether or not the command has been disabled and should not be registered at the request of the user.
     * @return true if the command has been disabled.
     */
    boolean isDisabled();

    /**
     * Whether or not the command is ephemeral and if its usage/replies should be private for the user on in Discord client.
     * @return true if the command is ephemeral.
     */
    boolean isEphemeral();

    /**
     * Gets the name of this command as it appears in Discord.
     * @return the name of the command.
     */
    String getName();

    /**
     * Gets the brief description of the command as it appears in Discord.
     * @return the description of the command.
     */
    String getDescription();

    /**
     * Gets the list of arguments registered to this command.
     * <p>
     * Note: Arguments can only be registered before the command itself is registered, others will be ignored.
     * @return the list of arguments.
     */
    List<InteractionCommandArgument> getArguments();

    /**
     * Called when an interaction command is received from Discord.
     * @param event The {@link InteractionEvent} which caused this command to be executed.
     */
    void onCommand(InteractionEvent event);
}
