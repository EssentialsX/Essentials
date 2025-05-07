package net.ess3.provider.providers;

import net.ess3.provider.PlayerLocaleProvider;
import net.essentialsx.providers.ProviderData;
import org.bukkit.entity.Player;

@ProviderData(description = "Legacy Player Locale Provider")
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
}
