package net.essentialsx.api.v2.services.discord;

/**
 * A class which provides numerous methods to interact with EssentialsX Discord.
 */
public interface EssentialsDiscordAPI {
    /**
     * Gets the {@link InteractionController} instance.
     * @return the {@link InteractionController} instance.
     */
    InteractionController getInteractionController();

    /**
     * Gets unstable API that is subject to change at any time.
     * @return {@link Unsafe the unsafe} instance.
     * @see Unsafe
     */
    Unsafe getUnsafe();
}
