package net.essentialsx.api.v2.services.discord;

/**
 * A class which provides numerous methods to interact with Discord slash commands.
 */
public interface InteractionController {
    /**
     * Gets the command with the given name or null if no command by that name exists.
     * @param name The name of the command.
     * @return The {@link InteractionCommand command} by the given name, or null.
     */
    InteractionCommand getCommand(String name);

    /**
     * Registers the given slash command with Discord.
     * @param command The slash command to be registered.
     * @throws InteractionException if a command with that name was already registered or if the given command was already registered.
     */
    void registerCommand(InteractionCommand command) throws InteractionException;
}
