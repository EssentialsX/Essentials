package com.earth2me.essentials.economy.layers;

import com.earth2me.essentials.economy.EconomyLayer;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import java.math.BigDecimal;

public class VaultLayer implements EconomyLayer {
    private Plugin plugin;
    private Economy adapter;

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return adapter.hasAccount(player);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        return adapter.createPlayerAccount(player);
    }

    @Override
    public BigDecimal getBalance(OfflinePlayer player) {
        return BigDecimal.valueOf(adapter.getBalance(player));
    }

    @Override
    public boolean deposit(OfflinePlayer player, BigDecimal amount) {
        return adapter.depositPlayer(player, amount.doubleValue()).transactionSuccess();
    }

    @Override
    public boolean withdraw(OfflinePlayer player, BigDecimal amount) {
        return false;
    }

    @Override
    public String getName() {
        return "Vault Compatibility Layer";
    }

    @Override
    public String getBackendName() {
        return adapter.getName();
    }

    @Override
    public void enable(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onServerLoad() {
        this.adapter = Bukkit.getServicesManager().load(Economy.class);
        return adapter != null && !adapter.getName().equals("Essentials Economy") && !adapter.getName().equals("EssentialsX Economy");
    }

    @Override
    public void disable() {
        this.plugin = null;
        this.adapter = null;
    }

    @Override
    public String getPluginName() {
        return "Vault";
    }

    @Override
    public String getPluginVersion() {
        return plugin == null ? null : plugin.getDescription().getVersion();
    }
}
