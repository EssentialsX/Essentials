package net.ess3.provider;

import org.bukkit.entity.Player;

public interface PlayerLocaleProvider extends Provider {
    String getLocale(Player player);
}
