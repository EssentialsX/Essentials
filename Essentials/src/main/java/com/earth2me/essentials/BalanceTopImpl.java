package com.earth2me.essentials;

import net.ess3.api.IEssentials;
import net.essentialsx.api.v2.services.BalanceTop;
import org.bukkit.plugin.ServicePriority;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class BalanceTopImpl implements BalanceTop {
    private final IEssentials ess;
    private LinkedHashMap<UUID, BalanceTop.Entry> topCache = new LinkedHashMap<>();
    private BigDecimal balanceTopTotal = BigDecimal.ZERO;
    private long cacheAge = 0;
    private CompletableFuture<Void> cacheLock;

    public BalanceTopImpl(IEssentials ess) {
        this.ess = ess;
        ess.getServer().getServicesManager().register(BalanceTop.class, this, ess, ServicePriority.Normal);
    }

    private void calculateBalanceTopMap() {
        final List<Entry> entries = new LinkedList<>();
        BigDecimal newTotal = BigDecimal.ZERO;
        for (UUID u : ess.getUsers().getAllUserUUIDs()) {
            final User user = ess.getUsers().loadUncachedUser(u);
            if (user != null) {
                if (!ess.getSettings().isNpcsInBalanceRanking() && user.isNPC()) {
                    // Don't list NPCs in output
                    continue;
                }
                if (!user.isBaltopExempt()) {
                    final BigDecimal userMoney = user.getMoney();
                    user.updateMoneyCache(userMoney);
                    newTotal = newTotal.add(userMoney);
                    final String name;
                    if (user.getBase() instanceof OfflinePlayerStub) {
                        name = user.getLastAccountName();
                    } else if (user.isHidden()) {
                        name = user.getName();
                    } else {
                        name = user.getDisplayName();
                    }
                    final String nameColorLimiter = "§f";
                    entries.add(new BalanceTop.Entry(user.getUUID(), name + nameColorLimiter, userMoney));
                }
            }
        }
        final LinkedHashMap<UUID, Entry> sortedMap = new LinkedHashMap<>();
        entries.sort((entry1, entry2) -> entry2.getBalance().compareTo(entry1.getBalance()));
        for (Entry entry : entries) {
            sortedMap.put(entry.getUuid(), entry);
        }
        topCache = sortedMap;
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
    public Map<UUID, Entry> getBalanceTopCache() {
        return Collections.unmodifiableMap(topCache);
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
