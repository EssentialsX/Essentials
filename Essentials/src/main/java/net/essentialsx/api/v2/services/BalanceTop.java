package net.essentialsx.api.v2.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * A class which provides numerous methods to interact with Essentials' balance top calculations.
 * <p>
 * Note: Implementations of this class should be thread-safe and thus do not need to be called from the server thread.
 */
public interface BalanceTop {

    /**
     * Re-calculates the balance top cache asynchronously.
     *
     * This method will return a {@link CompletableFuture CompletableFuture&lt;Void&gt;} which
     * will be completed upon the recalculation of the balance top map.
     * After which you should run {@link BalanceTop#getBalanceTopCache()}
     * to get the newly updated cache
     *
     * @return A future which completes after the balance top cache has been calculated.
     */
    CompletableFuture<Void> calculateBalanceTopMapAsync();

    /**
     * Gets the balance top cache or an empty list if one has not been calculated yet. The balance top cache is a list
     * of {@link Map.Entry} objects which map a user's display name to their balance. The returned list is sorted by
     * greatest to least wealth.
     *
     * There is no guarantee the returned cache is up to date. The balancetop command is directly responsible for updating
     * this cache and does so every two minutes (if executed). See {@link BalanceTop#calculateBalanceTopMapAsync()} to
     * manually update this cache yourself.
     *
     * @see BalanceTop#calculateBalanceTopMapAsync()
     * @return The balance top cache.
     */
    List<Map.Entry<String, BigDecimal>> getBalanceTopCache();

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
     * @see BalanceTop#getCacheAge() to find last baltop cache calculation
     * @return The total amount of money in the economy or zero.
     */
    BigDecimal getBalanceTopTotal();

    /**
     * Checks to see if {@link BalanceTop#calculateBalanceTopMapAsync()} is still in the process of calculating the map.
     *
     * @return true if the balance top cache is still in the process of being calculated, otherwise false.
     */
    boolean isCacheLocked();
}
