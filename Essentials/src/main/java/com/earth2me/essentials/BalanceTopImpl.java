package com.earth2me.essentials;

import net.ess3.api.IEssentials;
import net.essentialsx.api.v2.services.BalanceTop;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class BalanceTopImpl implements BalanceTop {
    private final IEssentials ess;
    private List<Map.Entry<String, BigDecimal>> topCache = new ArrayList<>();
    private BigDecimal balanceTopTotal = BigDecimal.ZERO;
    private long cacheAge = 0;
    private CompletableFuture<Void> cacheLock;

    public BalanceTopImpl(IEssentials ess) {
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

    @Override
    public CompletableFuture<Void> calculateBalanceTopMapAsync() {
        if (cacheLock != null) {
            return cacheLock;
        }
        cacheLock = new CompletableFuture<>();
        ess.runTaskAsynchronously(this::calculateBalanceTopMap);
        return cacheLock;
    }

    @Override
    public List<Map.Entry<String, BigDecimal>> getBalanceTopCache() {
        return Collections.unmodifiableList(topCache);
    }

    @Override
    public long getCacheAge() {
        return cacheAge;
    }

    @Override
    public BigDecimal getBalanceTopTotal() {
        return balanceTopTotal;
    }

    public boolean isCacheLocked() {
        return cacheLock != null;
    }
}
