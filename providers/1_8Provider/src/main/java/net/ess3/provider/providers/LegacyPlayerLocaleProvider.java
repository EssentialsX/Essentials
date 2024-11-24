package net.ess3.provider.providers;

import net.ess3.provider.PlayerLocaleProvider;
import org.bukkit.entity.Player;

public class LegacyPlayerLocaleProvider implements PlayerLocaleProvider {
    @Override
    public String getLocale(Player player) {
        try {
            return player.spigot().getLocale();
        } catch (final Throwable ignored) {
            // CraftBukkit "compatability"
            return null;
        }
    }

    @Override
    public String getDescription() {
        return "Legacy Player Locale Provider";
    }
}
