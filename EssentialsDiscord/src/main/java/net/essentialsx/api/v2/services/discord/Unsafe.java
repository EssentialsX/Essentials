package net.essentialsx.api.v2.services.discord;

import net.dv8tion.jda.api.JDA;

/**
 * Unstable methods that may vary with our implementation.
 * These methods have no guarantee of remaining consistent and may change at any time.
 */
public interface Unsafe {
    /**
     * Gets the JDA instance associated with this EssentialsX Discord instance, if available.
     * @return the {@link JDA} instance or null if not ready.
     */
    JDA getJDAInstance();
}
