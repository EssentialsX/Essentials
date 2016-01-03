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

    public EssentialsSpawnPlayerListener(IEssentials ess, SpawnStorage spawns) {
        this.ess = ess;
        this.spawns = spawns;
    }

    public void onPlayerJoin(PlayerJoinEvent event) {
        ess.runTaskAsynchronously(new DelayedJoin(event.getPlayer()));
    }

    private class DelayedJoin implements Runnable {

        final Player player;

        public DelayedJoin(Player player) {
            this.player = player;
        }

        @Override
        public void run() {
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

            final boolean announce = ess.getSettings().getAnnounceNewPlayers();

            ess.scheduleSyncDelayedTask(new Runnable() {
                @Override
                public void run() {
                    if (!user.getBase().isOnline()) {
                        return;
                    }

                    //This method allows for multiple line player announce messages using multiline yaml syntax #EasterEgg
                    if (announce) {
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
    }

    public void onPlayerRespawn(PlayerRespawnEvent event) {
        User user = ess.getUser(event.getPlayer());

        if (user.isJailed() && user.getJail() != null && !user.getJail().isEmpty()) {
            return;
        }

        Location loc = getRespawnLocation(user);
        if (loc != null) {
            event.setRespawnLocation(loc);
        }
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

        public NewPlayerTeleport(User user) {
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
                LOGGER.log(Level.WARNING, tl("teleportNewPlayerError"), ex);
            }
        }
    }
}
