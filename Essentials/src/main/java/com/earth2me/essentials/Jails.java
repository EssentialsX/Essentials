package com.earth2me.essentials;

import com.earth2me.essentials.config.ConfigurateUtil;
import com.earth2me.essentials.config.EssentialsConfiguration;
import com.earth2me.essentials.config.entities.LazyLocation;
import net.ess3.api.IEssentials;
import net.ess3.api.IUser;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.plugin.PluginManager;
import org.spongepowered.configurate.CommentedConfigurationNode;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.earth2me.essentials.I18n.tl;

public class Jails implements net.ess3.api.IJails {
    private static final transient Logger LOGGER = Logger.getLogger("Essentials");
    private static transient boolean enabled = false;
    private final IEssentials ess;
    private final EssentialsConfiguration config;
    private final Map<String, LazyLocation> jails = new HashMap<>();

    public Jails(final IEssentials ess) {
        this.ess = ess;
        this.config = new EssentialsConfiguration(new File(ess.getDataFolder(), "jail.yml"));
        reloadConfig();
    }

    @Override
    public void reloadConfig() {
        synchronized (jails) {
            config.load();
            jails.clear();
            final CommentedConfigurationNode jailsNode = config.getSection("jails");
            for (final Map.Entry<String, CommentedConfigurationNode> entry : ConfigurateUtil.getMap(jailsNode).entrySet()) {
                final CommentedConfigurationNode jailNode = entry.getValue();
                final String worldId = jailNode.node("world").getString();
                if (worldId == null || worldId.isEmpty()) {
                    continue;
                }
                jails.put(entry.getKey().toLowerCase(Locale.ENGLISH), new LazyLocation(worldId, jailNode.node("world-name").getString(""), jailNode.node("x").getDouble(), jailNode.node("y").getDouble(),
                        jailNode.node("z").getDouble(), jailNode.node("yaw").getFloat(), jailNode.node("pitch").getFloat()));
            }
            checkRegister();
        }
    }

    private void registerListeners() {
        enabled = true;
        final PluginManager pluginManager = ess.getServer().getPluginManager();
        final JailListener blockListener = new JailListener();
        pluginManager.registerEvents(blockListener, ess);
        if (ess.getSettings().isDebug()) {
            LOGGER.log(Level.INFO, "Registering Jail listener");
        }
    }

    public void resetListener() {
        enabled = false;
        checkRegister();
    }

    private void checkRegister() {
        if (!enabled && getCount() > 0) {
            registerListeners();
        }
    }

    @Override
    public void startTransaction() {
        config.startTransaction();
    }

    @Override
    public void stopTransaction(final boolean blocking) {
        config.stopTransaction(blocking);
    }

    @Override
    public Location getJail(String jailName) throws Exception {
        if (jailName == null) {
            throw new Exception(tl("jailNotExist"));
        }

        jailName = jailName.toLowerCase(Locale.ENGLISH);
        synchronized (jails) {
            if (!jails.containsKey(jailName)) {
                throw new Exception(tl("jailNotExist"));
            }
            final Location location = jails.get(jailName).location();
            if (location == null) {
                throw new Exception(tl("jailWorldNotExist"));
            }
            return location;
        }
    }

    @Override
    public Collection<String> getList() throws Exception {
        synchronized (jails) {
            return new ArrayList<>(jails.keySet());
        }
    }

    @Override
    public void removeJail(String jail) throws Exception {
        if (jail == null) {
            return;
        }

        jail = jail.toLowerCase(Locale.ENGLISH);
        synchronized (jails) {
            if (jails.remove(jail) != null) {
                config.getSection("jails").node(jail).set(null);
                config.save();
            }
        }
    }

    /**
     * @deprecated This method does not use asynchronous teleportation. Use {@link Jails#sendToJail(IUser, String, CompletableFuture)}
     */
    @SuppressWarnings("deprecation")
    @Override
    @Deprecated
    public void sendToJail(final IUser user, String jail) throws Exception {
        if (jail == null || jail.isEmpty()) {
            return;
        }

        jail = jail.toLowerCase(Locale.ENGLISH);
        synchronized (jails) {
            if (jails.containsKey(jail)) {
                if (user.getBase().isOnline()) {
                    user.getTeleport().now(getJail(jail), false, TeleportCause.COMMAND);
                }
                user.setJail(jail);
            }
        }
    }

    @Override
    public void sendToJail(final IUser user, final String jailName, final CompletableFuture<Boolean> future) throws Exception {
        if (jailName == null || jailName.isEmpty()) {
            return;
        }

        final String jail = jailName.toLowerCase(Locale.ENGLISH);
        synchronized (jails) {
            if (jails.containsKey(jail)) {
                if (user.getBase().isOnline()) {
                    user.getAsyncTeleport().now(getJail(jail), false, TeleportCause.COMMAND, future);
                    future.thenAccept(success -> user.setJail(jail));
                    return;
                }
                user.setJail(jail);
            }
        }
    }

    @Override
    public void setJail(String jailName, final Location loc) throws Exception {
        jailName = jailName.toLowerCase(Locale.ENGLISH);
        synchronized (jails) {
            jails.put(jailName, LazyLocation.fromLocation(loc));
            config.setProperty("jails." + jailName, loc);
            config.save();
        }
    }

    @Override
    public int getCount() {
        try {
            return getList().size();
        } catch (final Exception ex) {
            return 0;
        }
    }

    private class JailListener implements Listener {
        @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
        public void onJailBlockBreak(final BlockBreakEvent event) {
            final User user = ess.getUser(event.getPlayer());
            if (user.isJailed() && !user.isAuthorized("essentials.jail.allow-break")) {
                event.setCancelled(true);
            }
        }

        @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
        public void onJailBlockPlace(final BlockPlaceEvent event) {
            final User user = ess.getUser(event.getPlayer());
            if (user.isJailed() && !user.isAuthorized("essentials.jail.allow-place")) {
                event.setCancelled(true);
            }
        }

        @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
        public void onJailBlockDamage(final BlockDamageEvent event) {
            final User user = ess.getUser(event.getPlayer());
            if (user.isJailed() && !user.isAuthorized("essentials.jail.allow-block-damage")) {
                event.setCancelled(true);
            }
        }

        @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
        public void onJailEntityDamageByEntity(final EntityDamageByEntityEvent event) {
            if (event.getCause() != DamageCause.ENTITY_ATTACK || event.getEntity().getType() != EntityType.PLAYER) {
                return;
            }
            final Entity damager = event.getDamager();
            if (damager.getType() == EntityType.PLAYER) {
                final User user = ess.getUser((Player) damager);
                if (user != null && user.isJailed()) {
                    event.setCancelled(true);
                }
            }
        }

        @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
        public void onJailPlayerInteract(final PlayerInteractEvent event) {
            final User user = ess.getUser(event.getPlayer());
            if (user.isJailed() && !user.isAuthorized("essentials.jail.allow-interact")) {
                event.setCancelled(true);
            }
        }

        @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
        public void onJailPlayerGameModeChange(final PlayerGameModeChangeEvent event) {
            final User user = ess.getUser(event.getPlayer());
            if (user.isJailed()) {
                event.setCancelled(true);
            }
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onJailPlayerRespawn(final PlayerRespawnEvent event) {
            final User user = ess.getUser(event.getPlayer());
            if (!user.isJailed() || user.getJail() == null || user.getJail().isEmpty()) {
                return;
            }

            try {
                event.setRespawnLocation(getJail(user.getJail()));
            } catch (final Exception ex) {
                if (ess.getSettings().isDebug()) {
                    LOGGER.log(Level.INFO, tl("returnPlayerToJailError", user.getName(), ex.getLocalizedMessage()), ex);
                } else {
                    LOGGER.log(Level.INFO, tl("returnPlayerToJailError", user.getName(), ex.getLocalizedMessage()));
                }
            }
        }

        @EventHandler(priority = EventPriority.HIGH)
        public void onJailPlayerTeleport(final PlayerTeleportEvent event) {
            final User user = ess.getUser(event.getPlayer());
            if (!user.isJailed() || user.getJail() == null || user.getJail().isEmpty()) {
                return;
            }

            try {
                event.setTo(getJail(user.getJail()));
            } catch (final Exception ex) {
                if (ess.getSettings().isDebug()) {
                    LOGGER.log(Level.INFO, tl("returnPlayerToJailError", user.getName(), ex.getLocalizedMessage()), ex);
                } else {
                    LOGGER.log(Level.INFO, tl("returnPlayerToJailError", user.getName(), ex.getLocalizedMessage()));
                }
            }
            user.sendMessage(tl("jailMessage"));
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onJailPlayerJoin(final PlayerJoinEvent event) {
            final User user = ess.getUser(event.getPlayer());
            final long currentTime = System.currentTimeMillis();
            user.checkJailTimeout(currentTime);
            if (!user.isJailed() || user.getJail() == null || user.getJail().isEmpty()) {
                return;
            }

            final CompletableFuture<Boolean> future = new CompletableFuture<>();
            future.exceptionally(ex -> {
                if (ess.getSettings().isDebug()) {
                    LOGGER.log(Level.INFO, tl("returnPlayerToJailError", user.getName(), ex.getLocalizedMessage()), ex);
                } else {
                    LOGGER.log(Level.INFO, tl("returnPlayerToJailError", user.getName(), ex.getLocalizedMessage()));
                }
                return false;
            });
            future.thenAccept(success -> user.sendMessage(tl("jailMessage")));

            try {
                sendToJail(user, user.getJail(), future);
            } catch (final Exception ex) {
                future.completeExceptionally(ex);
            }
        }
    }
}
