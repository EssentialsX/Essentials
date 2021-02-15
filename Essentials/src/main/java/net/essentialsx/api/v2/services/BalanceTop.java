package net.essentialsx.api.v2.services;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * A class which provides numerous methods to interact with Essentials' balance top calculations.
 * <p>
 * Note: Implementations of this class should be thread-safe and thus do not need to be called from the server thread.
 */
public interface BalanceTop {
    /**
     * Re-calculates the balance top cache asynchronously.
     * <p>
     * This method will return a {@link CompletableFuture CompletableFuture&lt;Void&gt;} which
     * will be completed upon the recalculation of the balance top map.
     * After which you should run {@link BalanceTop#getBalanceTopCache()}
     * to get the newly updated cache
     *
     * @return A future which completes after the balance top cache has been calculated.
     */
    CompletableFuture<Void> calculateBalanceTopMapAsync();

    /**
     * Gets the balance top cache or an empty list if one has not been calculated yet. The balance top cache is a {@link Map}
     * which maps the UUID of the player to a {@link BalanceTop.Entry} object which stores the user's display name and balance.
     * The returned map is sorted by greatest to least wealth.
     * <p>
     * There is no guarantee the returned cache is up to date. The balancetop command is directly responsible for updating
     * this cache and does so every two minutes (if executed). See {@link BalanceTop#calculateBalanceTopMapAsync()} to
     * manually update this cache yourself.
     *
     * @return The balance top cache.
     * @see BalanceTop#calculateBalanceTopMapAsync()
     */
    Map<UUID, Entry> getBalanceTopCache();

    /**
     * Gets the epoch time (in mills.) that the baltop cache was last updated at. A value of zero indicates the cache
     * has not been calculated yet at all.
     *
     * @return The epoch time (in mills.) since last cache update or zero.
     */
    long getCacheAge();

    /**
     * Gets the total amount of money in the economy at the point of the last balance top cache calculation or returns zero
     * if no baltop calculation has been made as of yet.
     *
     * @return The total amount of money in the economy or zero.
     * @see BalanceTop#getCacheAge() to find last baltop cache calculation
     */
    BigDecimal getBalanceTopTotal();

    /**
     * Checks to see if {@link BalanceTop#calculateBalanceTopMapAsync()} is still in the process of calculating the map.
     *
     * @return true if the balance top cache is still in the process of being calculated, otherwise false.
     */
    boolean isCacheLocked();

    /**
     * This class represents a user's name/balance in the balancetop cache.
     */
    class Entry {
        private final UUID uuid;
        private final String displayName;
        private final BigDecimal balance;

        public Entry(UUID uuid, String displayName, BigDecimal balance) {
            this.uuid = uuid;
            this.displayName = displayName;
            this.balance = balance;
        }

        /**
         * Gets the UUID of the user.
         * @return The uuid of this user.
         */
        public UUID getUuid() {
            return uuid;
        }

        /**
         * Gets the display name of the user at the time of cache population.
         * @return The display name of this user.
         */
        public String getDisplayName() {
            return displayName;
        }

        /**
         * Gets the balance of the user at the time of cache population.
         * @return The balance of this user.
         */
        public BigDecimal getBalance() {
            return balance;
        }
    }
}
