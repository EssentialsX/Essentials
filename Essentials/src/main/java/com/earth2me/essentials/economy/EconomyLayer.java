package com.earth2me.essentials.economy;

import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import java.math.BigDecimal;

public interface EconomyLayer {
    String getName();

    String getBackendName();

    void enable(Plugin plugin);

    boolean onServerLoad();

    void disable();

    String getPluginName();

    String getPluginVersion();

    boolean hasAccount(OfflinePlayer player);

    boolean createPlayerAccount(OfflinePlayer player);

    BigDecimal getBalance(OfflinePlayer player);

    boolean deposit(OfflinePlayer player, BigDecimal amount);

    boolean withdraw(OfflinePlayer player, BigDecimal amount);

    default boolean set(OfflinePlayer player, BigDecimal amount) {
        if (!withdraw(player, getBalance(player))) {
            return false;
        }
        return amount.equals(BigDecimal.ZERO) || deposit(player, amount);
    }
}
