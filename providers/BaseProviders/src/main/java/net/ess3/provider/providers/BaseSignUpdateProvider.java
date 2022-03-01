package net.ess3.provider.providers;

import net.ess3.provider.SignUpdateProvider;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

public class BaseSignUpdateProvider implements SignUpdateProvider {
    @Override
    public boolean updateSign(final Sign sign, final Player player, final String[] lines) {
        final SignChangeEvent event = new SignChangeEvent(sign.getBlock(), player, lines);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return false;
        }

        for (int i = 0; i < lines.length; i++) {
            sign.setLine(i, lines[i]);
        }

        sign.update();
        return true;
    }
}
