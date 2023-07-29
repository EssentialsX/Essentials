package com.earth2me.essentials.userstorage;

import com.earth2me.essentials.OfflinePlayerStub;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.NumberUtil;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.ess3.api.IEssentials;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;

public class ModernUserMap extends CacheLoader<UUID, User> implements IUserMap {
    private final transient IEssentials ess;
    private final transient ModernUUIDCache uuidCache;
    private final transient LoadingCache<UUID, User> userCache;

    private final boolean debugPrintStackWithWarn;
    private final long debugMaxWarnsPerType;
    private final boolean debugLogCache;
    private final ConcurrentMap<String, AtomicLong> debugNonPlayerWarnCounts;

    public ModernUserMap(final IEssentials ess) {
        this.ess = ess;
        this.uuidCache = new ModernUUIDCache(ess);
        this.userCache = CacheBuilder.newBuilder()
                .maximumSize(ess.getSettings().getMaxUserCacheCount())
                .expireAfterAccess(ess.getSettings().getMaxUserCacheValueExpiry(), TimeUnit.SECONDS)
                .softValues()
                .build(this);

        // -Dnet.essentialsx.usermap.print-stack=true
        final String printStackProperty = System.getProperty("net.essentialsx.usermap.print-stack", "false");
        // -Dnet.essentialsx.usermap.max-warns=20
        final String maxWarnProperty = System.getProperty("net.essentialsx.usermap.max-warns", "100");
        // -Dnet.essentialsx.usermap.log-cache=true
        final String logCacheProperty = System.getProperty("net.essentialsx.usermap.log-cache", "false");

        this.debugMaxWarnsPerType = NumberUtil.isLong(maxWarnProperty) ? Long.parseLong(maxWarnProperty) : -1;
        this.debugPrintStackWithWarn = Boolean.parseBoolean(printStackProperty);
        this.debugLogCache = Boolean.parseBoolean(logCacheProperty);
        this.debugNonPlayerWarnCounts = new ConcurrentHashMap<>();
    }

    @Override
    public Set<UUID> getAllUserUUIDs() {
        return uuidCache.getCachedUUIDs();
    }

    @Override
    public long getCachedCount() {
        return userCache.size();
    }

    @Override
    public int getUserCount() {
        return uuidCache.getCacheSize();
    }

    @Override
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

    @Override
    public User getUser(final Player base) {
        final User user = loadUncachedUser(base);
        userCache.put(user.getUUID(), user);
        debugLogCache(user);
        return user;
    }

    @Override
    public User getUser(final String name) {
        if (name == null) {
            return null;
        }

        final User user = getUser(uuidCache.getCachedUUID(name));
        if (user != null && user.getBase() instanceof OfflinePlayerStub) {
            if (user.getLastAccountName() != null) {
                ((OfflinePlayerStub) user.getBase()).setName(user.getLastAccountName());
            } else {
                ((OfflinePlayerStub) user.getBase()).setName(name);
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

    @SuppressWarnings("NullableProblems")
    @Override
    public User load(final UUID uuid) throws Exception {
        final User user = loadUncachedUser(uuid);
        if (user != null) {
            debugLogCache(user);
            return user;
        }

        throw new Exception("User not found!");
    }

    @Override
    public User loadUncachedUser(final Player base) {
        if (base == null) {
            return null;
        }

        User user = getUser(base.getUniqueId());
        if (user == null) {
            debugLogUncachedNonPlayer(base);
            user = new User(base, ess);
        } else if (!base.equals(user.getBase())) {
            ess.getLogger().log(Level.INFO, "Essentials updated the underlying Player object for " + user.getUUID());
            user.update(base);
        }
        uuidCache.updateCache(user.getUUID(), user.getName());

        return user;
    }

    @Override
    public User loadUncachedUser(final UUID uuid) {
        User user = userCache.getIfPresent(uuid);
        if (user != null) {
            return user;
        }

        Player player = ess.getServer().getPlayer(uuid);
        if (player != null) {
            // This is a real player, cache their UUID.
            user = new User(player, ess);
            uuidCache.updateCache(uuid, player.getName());
            return user;
        }

        final File userFile = getUserFile(uuid);
        if (userFile.exists()) {
            player = new OfflinePlayerStub(uuid, ess.getServer());
            user = new User(player, ess);
            ((OfflinePlayerStub) player).setName(user.getLastAccountName());
            uuidCache.updateCache(uuid, null);
            return user;
        }

        return null;
    }

    public void addCachedUser(final User user) {
        userCache.put(user.getUUID(), user);
        debugLogCache(user);
    }

    @Override
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

    private void debugLogCache(final User user) {
        if (!debugLogCache) {
            return;
        }
        final Throwable throwable = new Throwable();
        ess.getLogger().log(Level.INFO, String.format("Caching user %s (%s)", user.getName(), user.getUUID()), throwable);
    }

    private void debugLogUncachedNonPlayer(final Player base) {
        final String typeName = base.getClass().getName();
        final long count = debugNonPlayerWarnCounts.computeIfAbsent(typeName, name -> new AtomicLong(0)).getAndIncrement();
        if (debugMaxWarnsPerType < 0 || count <= debugMaxWarnsPerType) {
            final Throwable throwable = debugPrintStackWithWarn ? new Throwable() : null;
            ess.getLogger().log(Level.INFO, "Created a User for " + base.getName() + " (" + base.getUniqueId() + ") for non Bukkit type: " + typeName, throwable);
            if (count == debugMaxWarnsPerType) {
                ess.getLogger().log(Level.WARNING, "Essentials will not log any more warnings for " + typeName + ". Please report this to the EssentialsX team.");
            }
        }
    }
}
