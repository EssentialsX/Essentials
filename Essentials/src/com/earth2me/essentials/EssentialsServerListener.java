package com.earth2me.essentials;

import net.ess3.api.IEssentials;
import net.ess3.nms.refl.ReflUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;


public class EssentialsServerListener implements Listener {
    private final transient IEssentials ess;
    private boolean errorLogged = false;
    private Boolean isPaperSample;
    private Method setSampleText;
    private Method getSampleText;

    public EssentialsServerListener(final IEssentials ess) {
        this.ess = ess;
        setSampleText = ReflUtil.getMethodCached(ServerListPingEvent.class, "setSampleText", List.class);
        getSampleText = ReflUtil.getMethodCached(ServerListPingEvent.class, "getSampleText");
        if (setSampleText != null && getSampleText != null) {
            ess.getLogger().info("Using Paper 1.12+ ServerListPingEvent methods");
            isPaperSample = true;
        } else {
            ess.getLogger().info("Using Spigot 1.7.10+ ServerListPingEvent iterator");
            isPaperSample = false;
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onServerListPing(final ServerListPingEvent event) {
        if (isPaperSample) {
            // ess.getLogger().info("Paper sample");
            try {
                List<String> playerNames = (List<String>) getSampleText.invoke(event, null);
                Iterator<String> iterator = playerNames.iterator();
                while (iterator.hasNext()) {
                    String player = iterator.next();
                    if (ess.getUser(player).isVanished()) {
                        iterator.remove();
                    }
                }
                setSampleText.invoke(event, playerNames);
            } catch (IllegalAccessException | InvocationTargetException | ClassCastException e) {
                if (!errorLogged) {
                    ess.getLogger().log(Level.WARNING, "Unable to hide players from server list ping using Paper 1.12+ method!", e);
                    errorLogged = true;
                }
            }
        } else {
            // ess.getLogger().info("Spigot iterator");
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
}
