package net.essentialsx.api.v2.services.discord;

/**
 * A class which provides numerous methods to interact with discord slash commands.
 */
public interface InteractionController {
    /**
     * Gets the command with the given name or null if no command by that name exists.
     * @param name The name of the command.
     * @return The {@link InteractionCommand command} by the given name, or null.
     */
    InteractionCommand getCommand(String name);

    /**
     * Registers the given slash command with discord.
     * @param command The slash command to be registered.
     * @throws InteractionException if a command with that name was already registered or if the given command was already registered.
     */
    void registerCommand(InteractionCommand command) throws InteractionException;

    /**
     * Edits the response an interaction with the given token to the given message. Should only be used internally.
     * @param interactionToken The token of the interaction
     * @param message          The new message of the interaction.
     */
    void editInteractionResponse(String interactionToken, String message);
}
