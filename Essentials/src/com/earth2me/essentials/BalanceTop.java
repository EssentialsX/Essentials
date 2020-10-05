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

public class BalanceTop {
    private final transient IEssentials ess;
    private List<Map.Entry<String, BigDecimal>> topCache = new ArrayList<>();
    private transient BigDecimal balanceTopTotal = BigDecimal.ZERO;

    public BalanceTop(IEssentials ess) {
        this.ess = ess;
        calculateBalanceTopMap();
    }

    protected void calculateBalanceTopMap() {
        final Map<String, BigDecimal> map = new HashMap<>();
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
                    balanceTopTotal = balanceTopTotal.add(userMoney);
                    final String name = user.isHidden() ? user.getName() : user.getDisplayName();
                    map.put(name, userMoney);
                }
            }
        }
        topCache = new ArrayList<>(map.entrySet());
        topCache.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));
    }

    /**
     * Returns a future which will be completed with the latest calculated balance top map.
     * @return A future with the latest balance top map.
     */
    public CompletableFuture<List<Map.Entry<String, BigDecimal>>> calculateBalanceTopMapAsync() {
        final CompletableFuture<List<Map.Entry<String, BigDecimal>>> future = new CompletableFuture<>();
        ess.runTaskAsynchronously(() -> {
            final Map<String, BigDecimal> map = new HashMap<>();
            final List<Map.Entry<String, BigDecimal>> returned;
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
                        final String name = user.isHidden() ? user.getName() : user.getDisplayName();
                        map.put(name, userMoney);
                    }
                }
            }
            returned = new ArrayList<>(map.entrySet());
            returned.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));
            future.complete(returned);
        });
        return future;
    }

    /**
     * There is no guarantee the returned cache is up to date. The balancetop command is directly responsible for updating
     * this cache and does so every two minutes (if executed). See {@link BalanceTop#calculateBalanceTopMapAsync()} if
     * you need an up-to-date balance map.
     *
     * @see BalanceTop#calculateBalanceTopMapAsync()
     * @return The balance top cache.
     */
    public List<Map.Entry<String, BigDecimal>> getBalanceTopCache() {
        return Collections.unmodifiableList(topCache);
    }

    public BigDecimal getBalanceTopTotal() {
        return balanceTopTotal;
    }
}
