package net.essentialsx.api.v2.services.discord;

/**
 * Represents a role of an interaction member.
 */
public interface InteractionRole {
    /**
     * Gets the name of this role.
     * @return this role's name.
     */
    String getName();

    /**
     * Gets the mention of this role.
     * @return this role's mention.
     */
    String getAsMention();

    /**
     * Whether this role is managed by an external integration.
     * @return true if the role is managed.
     */
    boolean isManaged();

    /**
     * Whether this role is the default role given to all users (@everyone).
     * @return true if this is the default role.
     */
    boolean isPublicRole();

    /**
     * Gets the raw RGB color value of this role.
     * @return this role's color value.
     */
    int getColorRaw();

    /**
     * Whether this role's color is the default one (has no color).
     * @return true if the role has no color.
     */
    boolean isDefaultColor();

    /**
     * Whether this role can be given to other members by the current logged in bot.
     * @return true if this role can be interacted with by the current bot user.
     */
    boolean canInteract();

    /**
     * Gets the ID of this role.
     * @return this role's ID.
     */
    String getId();
}
