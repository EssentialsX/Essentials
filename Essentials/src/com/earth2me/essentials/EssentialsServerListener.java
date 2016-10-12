package com.earth2me.essentials;

import net.ess3.api.IEssentials;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import java.util.Iterator;


public class EssentialsServerListener implements Listener {
    private final transient IEssentials ess;
    private boolean errorLogged = false;

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
            if (!errorLogged) {
                ess.getLogger().warning("Current server implementation does not support "
                    + "hiding players from server list ping. Update or contact the maintainers.");
                errorLogged = true;
            }
        }
    }
}
