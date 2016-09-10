package com.earth2me.essentials;

import net.ess3.api.IEssentials;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import java.util.Iterator;
import java.util.logging.Level;


public class EssentialsServerListener implements Listener {
    private final transient IEssentials ess;

    public EssentialsServerListener(final IEssentials ess) {
        this.ess = ess;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onServerListPing(final ServerListPingEvent event) {
        try {
            Iterator<Player> iterator = event.iterator();
            while (iterator.hasNext()) {
                Player player = iterator.next();
                if (ess.getUser(player).isVanished()) {
                    iterator.remove();
                }
            }
        } catch (UnsupportedOperationException e) {
            if (ess.getServer().getName().equalsIgnoreCase("Cauldron")) {
                Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e);
                Bukkit.getLogger().log(Level.SEVERE, "Please update your server or report this to the developers of your server implementation!");
            }
        }
    }
}
