package net.essentialsx.api.v2.services.discordlink;

import net.essentialsx.api.v2.services.discord.InteractionMember;

import java.util.UUID;

/**
 * A class which provides numerous methods to interact with the link module for EssentialsX Discord.
 */
public interface DiscordLinkService {
    /**
     * Gets the Discord ID linked to the given {@link UUID} or {@code null} if none is present.
     * @param uuid the {@link UUID} of the player to lookup.
     * @return the Discord ID or {@code null}.
     */
    String getDiscordId(final UUID uuid);

    /**
     * Checks if there is a Discord account linked to the given {@link UUID}.
     * @param uuid the {@link UUID} to check.
     * @return true if there is a Discord account linked to the given {@link UUID}.
     */
    default boolean isLinked(final UUID uuid) {
        return getDiscordId(uuid) != null;
    }

    /**
     * Gets the {@link UUID} linked to the given Discord ID or {@code null} if none is present.
     * @param discordId The Discord ID to lookup.
     * @return the {@link UUID} or {@code null}.
     */
    UUID getUUID(final String discordId);

    /**
     * Checks if there is a Minecraft account linked to the given Discord ID.
     * @param discordId the Discord ID to check.
     * @return true if there is a Minecraft account linked to the given Discord ID.
     */
    default boolean isLinked(final String discordId) {
        return getUUID(discordId) != null;
    }

    /**
     * Links the given {@link UUID} to the given {@link InteractionMember}.
     * <p>
     * This will automatically trigger role sync (if configured) for the given
     * player if this method returns {@code true}.
     * <p>
     * This method will return true if the accounts are successfully linked, or
     * false if either the provided {@link UUID} or {@link InteractionMember} are
     * already linked to another account.
     * @param uuid   The {@link UUID} of the target player.
     * @param member The {@link InteractionMember} to link to the target player.
     * @see net.essentialsx.api.v2.services.discord.DiscordService#getMemberById(String) to get an
     * {@link InteractionMember} by their ID.
     * @see #isLinked(UUID) to ensure the given {@link UUID} isn't already linked to an account.
     * @see #isLinked(String) to ensure the given {@link InteractionMember} isn't already linked to an account.
     * @throws IllegalArgumentException if either of the {@link UUID} or {@link InteractionMember} are null.
     * @return true if the accounts were linked successfully, otherwise false.
     */
    boolean linkAccount(final UUID uuid, final InteractionMember member);

    /**
     * Unlinks the given {@link UUID} with its associated Discord account (if present).
     * <p>
     * This will automatically trigger role unsync (if configured) for the given player if this method
     * returns {@code true}.
     * @param uuid The {@link UUID} of the player to unlink.
     * @throws IllegalArgumentException if the provided {@link UUID} is null.
     * @return true if there was an account associated with the given {@link UUID}, otherwise false.
     */
    boolean unlinkAccount(final UUID uuid);

    /**
     * Unlinks the given {@link InteractionMember} with its associated Minecraft account (if present).
     * <p>
     * This will automatically trigger role unsync (if configured) for the given {@link InteractionMember}
     * if this method returns {@code true}.
     * @param member The {@link InteractionMember} to unlink.
     * @throws IllegalArgumentException if the provided {@link InteractionMember} is null.
     * @return true if there was a linked Minecraft account associated with the given
     * {@link InteractionMember}, otherwise false.
     */
    boolean unlinkAccount(final InteractionMember member);
}
