package net.essentialsx.api.v2.services.discord;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * Represents a command to be registered with the discord client.
 */
public interface InteractionCommand {
    /**
     * Whether or not the command has been disabled and should not be registered at the request of the user.
     * @return true if the command has been disabled.
     */
    boolean isDisabled();

    /**
     * Whether or not the command is ephemeral and if its usage/replies should be private for the user on in discord client.
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
     * @return the list of arguments.
     */
    List<InteractionCommandArgument> getArguments();

    /**
     * Gets the list of discord 'snowflakes' (user/role ids) granted 'elevated' privilege by the implementation.
     * @return the list of admin snowflakes.
     */
    List<String> getAdminSnowflakes();

    /**
     * Registers an argument with this command.
     * <p>
     * Note: Arguments can only be registered before the command itself is registered, others will be ignored.
     * @param argument The argument to register.
     */
    void addArgument(InteractionCommandArgument argument);

    /**
     * Called when an interaction command is received from discord.
     * @param event The event which caused this command to be executed.
     */
    void onCommand(InteractionEvent event);

    /**
     * Serializes this command into a JSON representation to be sent to Discord.
     * @return the json representation of this command.
     */
    default String serialize() {
        final JsonObject cmdObject = new JsonObject();
        cmdObject.addProperty("name", getName());
        cmdObject.addProperty("description", getDescription());
        final JsonArray optionsArray = new JsonArray();
        for (InteractionCommandArgument argument : getArguments()) {
            optionsArray.add(argument.serialize());
        }
        cmdObject.add("options", optionsArray);
        return cmdObject.toString();
    }
}
