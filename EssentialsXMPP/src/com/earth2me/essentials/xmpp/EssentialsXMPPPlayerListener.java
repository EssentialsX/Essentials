package com.earth2me.essentials.xmpp;

import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.User;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;


class EssentialsXMPPPlayerListener implements Listener {
    private final transient IEssentials ess;

    EssentialsXMPPPlayerListener(final IEssentials ess) {
        super();
        this.ess = ess;
    }

    /**
     * <p>onPlayerJoin.</p>
     *
     * @param event a {@link org.bukkit.event.player.PlayerJoinEvent} object.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final User user = ess.getUser(event.getPlayer());

        Bukkit.getScheduler().scheduleSyncDelayedTask(ess, EssentialsXMPP::updatePresence);

        sendMessageToSpyUsers("Player " + user.getDisplayName() + " joined the game");
    }

    /**
     * <p>onPlayerChat.</p>
     *
     * @param event a {@link org.bukkit.event.player.AsyncPlayerChatEvent} object.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChat(final AsyncPlayerChatEvent event) {
        final User user = ess.getUser(event.getPlayer());
        sendMessageToSpyUsers(String.format(event.getFormat(), user.getDisplayName(), event.getMessage()));
    }

    /**
     * <p>onPlayerQuit.</p>
     *
     * @param event a {@link org.bukkit.event.player.PlayerQuitEvent} object.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final User user = ess.getUser(event.getPlayer());

        Bukkit.getScheduler().scheduleSyncDelayedTask(ess, EssentialsXMPP::updatePresence);


        sendMessageToSpyUsers("Player " + user.getDisplayName() + " left the game");
    }

    private void sendMessageToSpyUsers(final String message) {
        try {
            List<String> users = EssentialsXMPP.getInstance().getSpyUsers();
            synchronized (users) {
                for (final String address : users) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(ess, () -> EssentialsXMPP.getInstance().sendMessage(address, message));
                }
            }
        } catch (Exception ignored) {}
    }
}
