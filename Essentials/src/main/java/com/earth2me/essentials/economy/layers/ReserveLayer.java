package com.earth2me.essentials.economy.layers;

import com.earth2me.essentials.economy.EconomyLayer;
import net.tnemc.core.Reserve;
import net.tnemc.core.economy.EconomyAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import java.math.BigDecimal;

public class ReserveLayer implements EconomyLayer {
    private Plugin plugin;
    private EconomyAPI adapter;

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return adapter.hasAccount(player.getUniqueId());
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        return adapter.createAccount(player.getUniqueId());
    }

    @Override
    public BigDecimal getBalance(OfflinePlayer player) {
        return adapter.getHoldings(player.getUniqueId());
    }

    @Override
    public boolean deposit(OfflinePlayer player, BigDecimal amount) {
        return adapter.addHoldings(player.getUniqueId(), amount);
    }

    @Override
    public boolean withdraw(OfflinePlayer player, BigDecimal amount) {
        return adapter.removeHoldings(player.getUniqueId(), amount);
    }

    @Override
    public String getName() {
        return "Reserve Compatibility Layer";
    }

    @Override
    public String getBackendName() {
        return adapter.name();
    }

    @Override
    public void enable(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onServerLoad() {
        if(Bukkit.getPluginManager().isPluginEnabled("Reserve")) {
            this.adapter = Reserve.instance().economy();
        }
        return adapter != null && !adapter.name().equals("EssentialsX");
    }

    @Override
    public void disable() {
        this.plugin = null;
        this.adapter = null;
    }

    @Override
    public String getPluginName() {
        return "Reserve";
    }

    @Override
    public String getPluginVersion() {
        return plugin == null ? null : plugin.getDescription().getVersion();
    }
}
