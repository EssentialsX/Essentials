package com.earth2me.essentials.spawn;

import com.earth2me.essentials.Kit;
import com.earth2me.essentials.OfflinePlayer;
import com.earth2me.essentials.User;
import com.earth2me.essentials.textreader.IText;
import com.earth2me.essentials.textreader.KeywordReplacer;
import com.earth2me.essentials.textreader.SimpleTextPager;
import net.ess3.api.IEssentials;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.earth2me.essentials.I18n.tl;


public class EssentialsSpawnPlayerListener implements Listener {
    private static final Logger LOGGER = Bukkit.getLogger();
    private final transient IEssentials ess;
    private final transient SpawnStorage spawns;

    public EssentialsSpawnPlayerListener(final IEssentials ess, final SpawnStorage spawns) {
        super();
        this.ess = ess;
        this.spawns = spawns;
    }

    public void onPlayerRespawn(final PlayerRespawnEvent event) {
        final User user = ess.getUser(event.getPlayer());

        if (user.isJailed() && user.getJail() != null && !user.getJail().isEmpty()) {
            return;
        }

        final Location spawn = getRespawnLocation(user);
        if (spawn != null) {
            event.setRespawnLocation(spawn);
        }
    }

    public void onPlayerJoin(final PlayerJoinEvent event) {
        ess.runTaskAsynchronously(new Runnable() {
            @Override
            public void run() {
                delayedJoin(event.getPlayer());
            }
        });
    }

    public void delayedJoin(Player player) {
        if (player.hasPlayedBefore()) {
            LOGGER.log(Level.FINE, "Old player join");
            if (ess.getSettings().isRespawnOnJoinEnabled()) {
                ess.scheduleSyncDelayedTask(new PlayerLoginTeleport(ess.getUser(player)), 1L);
            }
            return;
        }

        final User user = ess.getUser(player);

        if (!"none".equalsIgnoreCase(ess.getSettings().getNewbieSpawn())) {
            ess.scheduleSyncDelayedTask(new NewPlayerTeleport(user), 1L);
        }

        ess.scheduleSyncDelayedTask(new Runnable() {
            @Override
            public void run() {
                if (!user.getBase().isOnline()) {
                    return;
                }

                //This method allows for multiple line player announce messages using multiline yaml syntax #EasterEgg
                if (ess.getSettings().getAnnounceNewPlayers()) {
                    final IText output = new KeywordReplacer(ess.getSettings().getAnnounceNewPlayerFormat(), user.getSource(), ess);
                    final SimpleTextPager pager = new SimpleTextPager(output);

                    for (String line : pager.getLines()) {
                        ess.broadcastMessage(user, line);
                    }
                }

                final String kitName = ess.getSettings().getNewPlayerKit();
                if (!kitName.isEmpty()) {
                    try {
                        final Kit kit = new Kit(kitName.toLowerCase(Locale.ENGLISH), ess);
                        kit.expandItems(user);
                    } catch (Exception ex) {
                        LOGGER.log(Level.WARNING, ex.getMessage());
                    }
                }

                LOGGER.log(Level.FINE, "New player join");
            }
        }, 2L);
    }
    
    private Location getRespawnLocation(User user) {
        if (ess.getSettings().getRespawnAtHome()) {
            Location bed = user.getBase().getBedSpawnLocation();
            if (bed != null) {
                return bed;
            }
            Location home = user.getHome(user.getLocation());
            if (home != null) {
                return home;
            }
        }
        return spawns.getSpawn(user.getGroup());
    }

    private class PlayerLoginTeleport implements Runnable {

        private final User user;

        public PlayerLoginTeleport(User user) {
            this.user = user;
        }

        @Override
        public void run() {
            if (user.getBase() instanceof OfflinePlayer || !user.getBase().isOnline()) {
                return;
            }
            try {
                final Location spawn = getRespawnLocation(user);
                if (spawn != null) {
                    user.getTeleport().now(spawn, false, TeleportCause.PLUGIN);
                }
            } catch (Exception ex) {
                LOGGER.log(Level.WARNING, tl("teleportLoginPlayerError"), ex);
            }
        }
    }

    private class NewPlayerTeleport implements Runnable {
        private final transient User user;

        public NewPlayerTeleport(final User user) {
            this.user = user;
        }

        @Override
        public void run() {
            if (user.getBase() instanceof OfflinePlayer || !user.getBase().isOnline()) {
                return;
            }

            try {
                final Location spawn = spawns.getSpawn(ess.getSettings().getNewbieSpawn());
                if (spawn != null) {
                    user.getTeleport().now(spawn, false, TeleportCause.PLUGIN);
                }
            } catch (Exception ex) {
                Bukkit.getLogger().log(Level.WARNING, tl("teleportNewPlayerError"), ex);
            }
        }
    }
}
