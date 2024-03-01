package net.ess3.provider.providers;

import net.ess3.provider.PlayerLocaleProvider;
import org.bukkit.entity.Player;

public class ModernPlayerLocaleProvider implements PlayerLocaleProvider {
    @Override
    public String getLocale(Player player) {
        return player.getLocale();
    }

    @Override
    public String getDescription() {
        return "1.12.2+ Player Locale Provider";
    }
}
