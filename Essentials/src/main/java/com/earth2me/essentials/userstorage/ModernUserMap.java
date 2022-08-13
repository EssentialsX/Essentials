package com.earth2me.essentials.userstorage;

import com.earth2me.essentials.OfflinePlayer;
import com.earth2me.essentials.User;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.ess3.api.IEssentials;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public class ModernUserMap extends CacheLoader<UUID, User> {
    private final transient IEssentials ess;
    private final transient ModernUUIDCache uuidCache;
    private final transient LoadingCache<UUID, User> userCache;

    public ModernUserMap(final IEssentials ess) {
        this.ess = ess;
        this.uuidCache = new ModernUUIDCache(ess);
        this.userCache = CacheBuilder.newBuilder()
                .maximumSize(ess.getSettings().getMaxUserCacheCount())
                .softValues()
                .build(this);
    }

    public Set<UUID> getAllUserUUIDs() {
        return uuidCache.getCachedUUIDs();
    }

    public long getCachedCount() {
        return userCache.size();
    }

    public int getUserCount() {
        return uuidCache.getCacheSize();
    }

    public User getUser(final UUID uuid) {
        if (uuid == null) {
            return null;
        }

        try {
            return userCache.get(uuid);
        } catch (ExecutionException e) {
            if (ess.getSettings().isDebug()) {
                ess.getLogger().log(Level.WARNING, "Exception while getting user for " + uuid, e);
            }
            return null;
        }
    }

    public User getUser(final Player base) {
        final User user = loadUncachedUser(base);
        userCache.put(user.getUUID(), user);
        return user;
    }

    public User getUser(final String name) {
        if (name == null) {
            return null;
        }

        final User user = getUser(uuidCache.getCachedUUID(name));
        if (user != null && user.getBase() instanceof OfflinePlayer) {
            if (user.getLastAccountName() != null) {
                ((OfflinePlayer) user.getBase()).setName(user.getLastAccountName());
            } else {
                ((OfflinePlayer) user.getBase()).setName(name);
            }
        }
        return user;
    }

    public void addCachedNpcName(final UUID uuid, final String name) {
        if (uuid == null || name == null) {
            return;
        }

        uuidCache.updateCache(uuid, name);
    }

    @Override
    public User load(final UUID uuid) throws Exception {
        final User user = loadUncachedUser(uuid);
        if (user != null) {
            return user;
        }

        throw new Exception("User not found!");
    }

    public User loadUncachedUser(final Player base) {
        if (base == null) {
            return null;
        }

        User user = getUser(base.getUniqueId());
        if (user == null) {
            ess.getLogger().log(Level.INFO, "Essentials created a User for " + base.getName() + " (" + base.getUniqueId() + ") for non Bukkit type: " + base.getClass().getName());
            user = new User(base, ess);
        } else if (!base.equals(user.getBase())) {
            ess.getLogger().log(Level.INFO, "Essentials updated the underlying Player object for " + user.getUUID());
            user.update(base);
        }
        uuidCache.updateCache(user.getUUID(), user.getName());

        return user;
    }

    /**
     * Gets a User by the given UUID in the cache, if present, otherwise loads the user without placing them in the cache.
     * Ideally to be used when running operations on all stored users.
     */
    public User loadUncachedUser(final UUID uuid) {
        User user = userCache.getIfPresent(uuid);
        if (user != null) {
            return user;
        }

        Player player = ess.getServer().getPlayer(uuid);
        if (player != null) {
            user = new User(player, ess);
            uuidCache.updateCache(uuid, player.getName());
            return user;
        }

        final File userFile = getUserFile(uuid);
        if (userFile.exists()) {
            player = new OfflinePlayer(uuid, ess.getServer());
            user = new User(player, ess);
            ((OfflinePlayer) player).setName(user.getLastAccountName());
            uuidCache.updateCache(uuid, null);
            return user;
        }

        return null;
    }

    public Map<String, UUID> getNameCache() {
        return uuidCache.getNameCache();
    }

    public String getSanitizedName(final String name) {
        return uuidCache.getSanitizedName(name);
    }

    public void blockingSave() {
        uuidCache.blockingSave();
    }

    public void invalidate(final UUID uuid) {
        userCache.invalidate(uuid);
        uuidCache.removeCache(uuid);
    }

    private File getUserFile(final UUID uuid) {
        return new File(new File(ess.getDataFolder(), "userdata"), uuid.toString() + ".yml");
    }

    public void shutdown() {
        uuidCache.shutdown();
    }
}
