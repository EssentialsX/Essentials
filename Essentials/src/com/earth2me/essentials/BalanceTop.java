package com.earth2me.essentials;

import net.ess3.api.IEssentials;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BalanceTop {
    private final transient IEssentials ess;
    private List<Map.Entry<String, BigDecimal>> topCache = new ArrayList<>();
    private transient BigDecimal balanceTopTotal = BigDecimal.ZERO;

    public BalanceTop(IEssentials ess) {
        this.ess = ess;
    }

    protected void calculateBalanceTopMap() {
        HashMap<String, BigDecimal> map = new HashMap<>();
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

    public List<Map.Entry<String, BigDecimal>> getBalanceTopCache() {
        return Collections.unmodifiableList(topCache);
    }

    public BigDecimal getBalanceTopTotal() {
        return balanceTopTotal;
    }
}
