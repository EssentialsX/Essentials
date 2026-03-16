package com.earth2me.essentials.adventure;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface AdventureFacet {
    void close();

    ComponentHolder deserializeMiniMessage(String message);

    void send(CommandSender sender, ComponentHolder component);

    void send(Player player, ComponentHolder component);

    /**
     * Converts a section sign legacy string to a MiniMessage string.
     */
    String legacyToMini(String message);

    /**
     * Converts a section sign legacy string to a MiniMessage string.
     *
     * @param useCustomTags true if gold and red colors should use primary and secondary tags instead.
     */
    String legacyToMini(String message, boolean useCustomTags);

    /**
     * Convenience method for submodules to escape MiniMessage tags.
     */
    String escapeTags(String input);

    /**
     * Strips all MiniMessage tags from the given input, returning plain text.
     */
    String stripTags(String input);

    /**
     * Converts a section sign legacy string to an adventure component.
     */
    ComponentHolder legacyToAdventure(String message);

    /**
     * Converts a plain text string to an adventure component.
     */
    ComponentHolder text(String message);

    /**
     * Converts a MiniMessage string to a section sign legacy string.
     */
    String miniToLegacy(String message);

    /**
     * Converts an adventure component to a section sign legacy string.
     */
    String adventureToLegacy(ComponentHolder component);

    ComponentHolder append(ComponentHolder base, ComponentHolder... addition);
}
