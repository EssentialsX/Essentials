package com.earth2me.essentials;

import net.ess3.api.IEssentials;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * A class which provides numerous methods to interact with Essentials' balance top calculations.
 *
 * Note: This class is thread safe.
 */
public class BalanceTop {
    private final IEssentials ess;
    private List<Map.Entry<String, BigDecimal>> topCache = new ArrayList<>();
    private BigDecimal balanceTopTotal = BigDecimal.ZERO;
    private long cacheAge = 0;
    private CompletableFuture<Void> cacheLock;

    public BalanceTop(IEssentials ess) {
        this.ess = ess;
    }

    private void calculateBalanceTopMap() {
        final Map<String, BigDecimal> map = new HashMap<>();
        BigDecimal newTotal = BigDecimal.ZERO;
        for (UUID u : ess.getUserMap().getAllUniqueUsers()) {
            final User user = ess.getUserMap().getUser(u);
            if (user != null) {
                if (!ess.getSettings().isNpcsInBalanceRanking() && user.isNPC()) {
                    // Don't list NPCs in output
                    continue;
                }
                if (!user.isAuthorized("essentials.balancetop.exclude")) {
                    final BigDecimal userMoney = user.getMoney();
                    user.updateMoneyCache(userMoney);
                    newTotal = newTotal.add(userMoney);
                    final String name = user.isHidden() ? user.getName() : user.getDisplayName();
                    map.put(name, userMoney);
                }
            }
        }
        final List<Map.Entry<String, BigDecimal>> newTopCache = new ArrayList<>(map.entrySet());
        newTopCache.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));
        topCache = newTopCache;
        balanceTopTotal = newTotal;
        cacheAge = System.currentTimeMillis();
        cacheLock.complete(null);
        cacheLock = null;
    }

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
    public CompletableFuture<Void> calculateBalanceTopMapAsync() {
        if (cacheLock != null) {
            return cacheLock;
        }
        cacheLock = new CompletableFuture<>();
        ess.runTaskAsynchronously(this::calculateBalanceTopMap);
        return cacheLock;
    }

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
    public List<Map.Entry<String, BigDecimal>> getBalanceTopCache() {
        return Collections.unmodifiableList(topCache);
    }

    /**
     * Gets the epoch time (in mills.) that the baltop cache was last updated at. A value of zero indicates the cache
     * has not been calculated yet at all.
     *
     * @return The epoch time (in mills.) since last cache update or zero.
     */
    public long getCacheAge() {
        return cacheAge;
    }

    /**
     * Gets the total amount of money in the economy at the point of the last balance top cache calculation or returns zero
     * if no baltop calculation has been made as of yet.
     *
     * @see BalanceTop#getCacheAge() to find last baltop cache calculation
     * @return The total amount of money in the economy or zero.
     */
    public BigDecimal getBalanceTopTotal() {
        return balanceTopTotal;
    }

    /**
     * Checks to see if {@link BalanceTop#calculateBalanceTopMapAsync()} is still in the process of calculating the map.
     *
     * @return true if the balance top cache is still in the process of being calculated, otherwise false.
     */
    public boolean isCacheLocked() {
        return cacheLock != null;
    }
}
