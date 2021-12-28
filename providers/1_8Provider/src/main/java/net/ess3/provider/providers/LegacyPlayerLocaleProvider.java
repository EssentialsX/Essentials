package net.ess3.provider.providers;

import net.ess3.provider.PlayerLocaleProvider;
import org.bukkit.entity.Player;

public class LegacyPlayerLocaleProvider implements PlayerLocaleProvider {
    @Override
    public String getLocale(Player player) {
        return player.spigot().getLocale();
    }

    @Override
    public String getDescription() {
        return "Legacy Player Locale Provider";
    }
}
