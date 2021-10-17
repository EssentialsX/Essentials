package com.earth2me.essentials;

import com.earth2me.essentials.api.UserDoesNotExistException;
import com.earth2me.essentials.utils.StringUtil;
import com.google.common.base.Charsets;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;
import net.ess3.api.IEssentials;
import net.ess3.api.MaxMoneyException;
import org.bukkit.entity.Player;

import java.io.File;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class UserMap extends CacheLoader<String, User> implements IConf {
    private final transient IEssentials ess;
    private final transient ConcurrentSkipListSet<UUID> keys = new ConcurrentSkipListSet<>();
    private final transient ConcurrentSkipListMap<String, UUID> names = new ConcurrentSkipListMap<>();
    private final UUIDMap uuidMap;
    private final transient Cache<String, User> users;
    private final Pattern validUserPattern = Pattern.compile("^[a-zA-Z0-9_]{2,16}$");

    private static final String WARN_UUID_NOT_REPLACE = "Found UUID {0} for player {1}, but player already has a UUID ({2}). Not replacing UUID in usermap.";

    public UserMap(final IEssentials ess) {
        super();
        this.ess = ess;
        uuidMap = new UUIDMap(ess);
        final CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder();
        final int maxCount = ess.getSettings().getMaxUserCacheCount();
        cacheBuilder.maximumSize(maxCount);
        cacheBuilder.softValues();
        users = cacheBuilder.build(this);
    }

    private void loadAllUsersAsync(final IEssentials ess) {
        ess.runTaskAsynchronously(() -> {
            synchronized (users) {
                final File userDir = new File(ess.getDataFolder(), "userdata");
                if (!userDir.exists()) {
                    return;
                }
                keys.clear();
                users.invalidateAll();
                for (final String string : userDir.list()) {
                    if (!string.endsWith(".yml")) {
                        continue;
                    }
                    final String name = string.substring(0, string.length() - 4);
                    try {
                        keys.add(UUID.fromString(name));
                    } catch (final IllegalArgumentException ex) {
                        //Ignore these users till they rejoin.
                    }
                }
                uuidMap.loadAllUsers(names);
            }
        });
    }

    public User getUser(final String name) {
        final String sanitizedName = StringUtil.safeString(name);
        try {
            if (ess.getSettings().isDebug()) {
                ess.getLogger().warning("Looking up username " + name + " (" + sanitizedName + ") ...");
            }

            if (names.containsKey(sanitizedName)) {
                final UUID uuid = names.get(sanitizedName);
                return getUser(uuid);
            }

            if (ess.getSettings().isDebug()) {
                ess.getLogger().warning(name + "(" + sanitizedName + ") has no known usermap entry");
            }

            final File userFile = getUserFileFromString(sanitizedName);
            if (userFile.exists()) {
                ess.getLogger().info("Importing user " + name + " to usermap.");
                final User user = new User(new OfflinePlayer(sanitizedName, ess.getServer()), ess);
                trackUUID(user.getBase().getUniqueId(), user.getName(), true);
                return user;
            }
            return null;
        } catch (final UncheckedExecutionException ex) {
            if (ess.getSettings().isDebug()) {
                ess.getLogger().log(Level.WARNING, ex, () -> String.format("Exception while getting user for %s (%s)", name, sanitizedName));
            }
            return null;
        }
    }

    public User getUser(final UUID uuid) {
        try {
            return ((LoadingCache<String, User>) users).get(uuid.toString());
        } catch (final ExecutionException | UncheckedExecutionException ex) {
            if (ess.getSettings().isDebug()) {
                ess.getLogger().log(Level.WARNING, ex, () -> "Exception while getting user for " + uuid);
            }
            return null;
        }
    }

    public void trackUUID(final UUID uuid, final String name, final boolean replace) {
        if (uuid != null) {
            keys.add(uuid);
            if (name != null && name.length() > 0) {
                final String keyName = ess.getSettings().isSafeUsermap() ? StringUtil.safeString(name) : name;
                if (!names.containsKey(keyName)) {
                    names.put(keyName, uuid);
                    uuidMap.writeUUIDMap();
                } else if (!isUUIDMatch(uuid, keyName)) {
                    if (replace) {
                        ess.getLogger().info("Found new UUID for " + name + ". Replacing " + names.get(keyName).toString() + " with " + uuid.toString());
                        names.put(keyName, uuid);
                        uuidMap.writeUUIDMap();
                    } else {
                        ess.getLogger().log(Level.INFO, MessageFormat.format(WARN_UUID_NOT_REPLACE, uuid.toString(), name, names.get(keyName).toString()), new RuntimeException());
                    }
                }
            }
        }
    }

    public boolean isUUIDMatch(final UUID uuid, final String name) {
        return names.containsKey(name) && names.get(name).equals(uuid);
    }

    @Override
    public User load(final String stringUUID) throws Exception {
        final UUID uuid = UUID.fromString(stringUUID);
        Player player = ess.getServer().getPlayer(uuid);
        if (player != null) {
            final User user = new User(player, ess);
            trackUUID(uuid, user.getName(), true);
            return user;
        }

        final File userFile = getUserFileFromID(uuid);

        if (userFile.exists()) {
            player = new OfflinePlayer(uuid, ess.getServer());
            final User user = new User(player, ess);
            ((OfflinePlayer) player).setName(user.getLastAccountName());
            trackUUID(uuid, user.getName(), false);
            return user;
        }

        throw new Exception("User not found!");
    }

    public User load(final org.bukkit.OfflinePlayer player) throws UserDoesNotExistException {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null!");
        }

        if (player instanceof Player) {
            if (ess.getSettings().isDebug()) {
                ess.getLogger().info("Loading online OfflinePlayer into user map...");
            }
            final User user = new User((Player) player, ess);
            trackUUID(player.getUniqueId(), player.getName(), true);
            return user;
        }

        final File userFile = getUserFileFromID(player.getUniqueId());
        if (ess.getSettings().isDebug()) {
            ess.getLogger().info("Loading OfflinePlayer into user map. Has data: " + userFile.exists() + " for " + player);
        }

        final OfflinePlayer essPlayer = new OfflinePlayer(player.getUniqueId(), ess.getServer());
        final User user = new User(essPlayer, ess);
        if (userFile.exists()) {
            essPlayer.setName(user.getLastAccountName());
        } else {
            if (ess.getSettings().isDebug()) {
                ess.getLogger().info("OfflinePlayer usermap load saving user data for " + player);
            }

            // this code makes me sad
            user.startTransaction();
            try {
                user.setMoney(ess.getSettings().getStartingBalance());
            } catch (MaxMoneyException e) {
                // Shouldn't happen as it would be an illegal configuration state
                throw new RuntimeException(e);
            }
            user.setLastAccountName(user.getName());
            user.stopTransaction();
        }

        trackUUID(player.getUniqueId(), user.getName(), false);
        return user;
    }

    @Override
    public void reloadConfig() {
        getUUIDMap().forceWriteUUIDMap();
        loadAllUsersAsync(ess);
    }

    public void invalidateAll() {
        users.invalidateAll();
    }

    public void removeUser(final String name) {
        if (names == null) {
            ess.getLogger().warning("Name collection is null, cannot remove user.");
            return;
        }
        final UUID uuid = names.get(name);
        if (uuid != null) {
            keys.remove(uuid);
            users.invalidate(uuid.toString());
        }
        names.remove(name);
        names.remove(StringUtil.safeString(name));
    }

    public void removeUserUUID(final String uuid) {
        users.invalidate(uuid);
    }

    public Set<UUID> getAllUniqueUsers() {
        return Collections.unmodifiableSet(keys);
    }

    public int getUniqueUsers() {
        return keys.size();
    }

    protected ConcurrentSkipListMap<String, UUID> getNames() {
        return names;
    }

    public UUIDMap getUUIDMap() {
        return uuidMap;
    }

    private File getUserFileFromID(final UUID uuid) {
        final File userFolder = new File(ess.getDataFolder(), "userdata");
        return new File(userFolder, uuid.toString() + ".yml");
    }

    public File getUserFileFromString(final String name) {
        final File userFolder = new File(ess.getDataFolder(), "userdata");
        return new File(userFolder, StringUtil.sanitizeFileName(name) + ".yml");
    }

    @SuppressWarnings("deprecation")
    public User getUserFromBukkit(String name) {
        name = StringUtil.safeString(name);
        if (ess.getSettings().isDebug()) {
            ess.getLogger().warning("Using potentially blocking Bukkit UUID lookup for: " + name);
        }
        // Don't attempt to look up entirely invalid usernames
        if (name == null || !validUserPattern.matcher(name).matches()) {
            return null;
        }
        final org.bukkit.OfflinePlayer offlinePlayer = ess.getServer().getOfflinePlayer(name);

        final UUID uuid;
        try {
            uuid = offlinePlayer.getUniqueId();
        } catch (final UnsupportedOperationException | NullPointerException e) {
            return null;
        }
        // This is how Bukkit generates fake UUIDs
        if (UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(Charsets.UTF_8)).equals(uuid)) {
            return null;
        } else {
            names.put(name, uuid);
            return getUser(uuid);
        }
    }
}
