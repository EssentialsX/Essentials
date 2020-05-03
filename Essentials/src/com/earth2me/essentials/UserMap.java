package com.earth2me.essentials;

import com.earth2me.essentials.utils.StringUtil;
import com.google.common.base.Charsets;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;
import net.ess3.api.IEssentials;
import org.bukkit.entity.Player;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;


public class UserMap extends CacheLoader<String, User> implements IConf {
    private final transient IEssentials ess;
    private final transient ConcurrentSkipListSet<UUID> keys = new ConcurrentSkipListSet<>();
    private final transient ConcurrentSkipListMap<String, UUID> names = new ConcurrentSkipListMap<>();
    private final transient ConcurrentSkipListMap<UUID, ArrayList<String>> history = new ConcurrentSkipListMap<>();
    private final UUIDMap uuidMap;

    private final transient Cache<String, User> users;
    private static boolean legacy = false;

    public UserMap(final IEssentials ess) {
        super();
        this.ess = ess;
        uuidMap = new UUIDMap(ess);
        //RemovalListener<UUID, User> remListener = new UserMapRemovalListener();
        //users = CacheBuilder.newBuilder().maximumSize(ess.getSettings().getMaxUserCacheCount()).softValues().removalListener(remListener).build(this);
        CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder();
        int maxCount = ess.getSettings().getMaxUserCacheCount();
        try {
            cacheBuilder.maximumSize(maxCount);
        } catch (NoSuchMethodError nsme) {
            legacy = true;
            legacyMaximumSize(cacheBuilder, maxCount);
        }
        cacheBuilder.softValues();
        if (!legacy) {
            users = cacheBuilder.build(this);
        } else {
            users = legacyBuild(cacheBuilder);
        }
    }

    private void loadAllUsersAsync(final IEssentials ess) {
        ess.runTaskAsynchronously(() -> {
            synchronized (users) {
                final File userdir = new File(ess.getDataFolder(), "userdata");
                if (!userdir.exists()) {
                    return;
                }
                keys.clear();
                users.invalidateAll();
                for (String string : userdir.list()) {
                    if (!string.endsWith(".yml")) {
                        continue;
                    }
                    final String name = string.substring(0, string.length() - 4);
                    try {
                        keys.add(UUID.fromString(name));
                    } catch (IllegalArgumentException ex) {
                        //Ignore these users till they rejoin.
                    }
                }
                uuidMap.loadAllUsers(names, history);
            }
        });
    }

    public boolean userExists(final UUID uuid) {
        return keys.contains(uuid);
    }

    public User getUser(final String name) {
        try {
            final String sanitizedName = StringUtil.safeString(name);
            if (names.containsKey(sanitizedName)) {
                final UUID uuid = names.get(sanitizedName);
                return getUser(uuid);
            }

            final File userFile = getUserFileFromString(sanitizedName);
            if (userFile.exists()) {
                ess.getLogger().info("Importing user " + name + " to usermap.");
                User user = new User(new OfflinePlayer(sanitizedName, ess.getServer()), ess);
                trackUUID(user.getBase().getUniqueId(), user.getName(), true);
                return user;
            }
            return null;
        } catch (UncheckedExecutionException ex) {
            return null;
        }
    }

    public User getUser(final UUID uuid) {
        try {
            if (!legacy) {
                return ((LoadingCache<String, User>) users).get(uuid.toString());
            } else {
                return legacyCacheGet(uuid);
            }
        } catch (ExecutionException | UncheckedExecutionException ex) {
            return null;
        }
    }

    public void trackUUID(final UUID uuid, final String name, boolean replace) {
        if (uuid != null) {
            keys.add(uuid);
            if (name != null && name.length() > 0) {
                final String keyName = ess.getSettings().isSafeUsermap() ? StringUtil.safeString(name) : name;
                if (!names.containsKey(keyName)) {
                    names.put(keyName, uuid);
                    uuidMap.writeUUIDMap();
                } else if (!names.get(keyName).equals(uuid)) {
                    if (replace) {
                        ess.getLogger().info("Found new UUID for " + name + ". Replacing " + names.get(keyName).toString() + " with " + uuid.toString());
                        names.put(keyName, uuid);
                        uuidMap.writeUUIDMap();
                    } else {
                        if (ess.getSettings().isDebug()) {
                            ess.getLogger().info("Found old UUID for " + name + " (" + uuid.toString() + "). Not adding to usermap.");
                        }
                    }
                }
            }
        }
    }

    @Override
    public User load(final String stringUUID) throws Exception {
        UUID uuid = UUID.fromString(stringUUID);
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
        UUID uuid = names.get(name);
        if (uuid != null) {
            keys.remove(uuid);
            users.invalidate(uuid.toString());
        }
        names.remove(name);
        names.remove(StringUtil.safeString(name));
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

    protected ConcurrentSkipListMap<UUID, ArrayList<String>> getHistory() {
        return history;
    }

    public List<String> getUserHistory(final UUID uuid) {
        return history.get(uuid);
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
//	class UserMapRemovalListener implements RemovalListener
//	{
//		@Override
//		public void onRemoval(final RemovalNotification notification)
//		{
//			Object value = notification.getValue();
//			if (value != null)
//			{
//				((User)value).cleanup();
//			}
//		}
//	}

    private final Pattern validUserPattern = Pattern.compile("^[a-zA-Z0-9_]{2,16}$");

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
        org.bukkit.OfflinePlayer offlinePlayer = ess.getServer().getOfflinePlayer(name);
        if (offlinePlayer == null) {
            return null;
        }
        UUID uuid;
        try {
            uuid = offlinePlayer.getUniqueId();
        } catch (UnsupportedOperationException | NullPointerException e) {
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

    private static Method getLegacy;

    private User legacyCacheGet(UUID uuid) {
        if (getLegacy == null) {
            Class<?> usersClass = users.getClass();
            for (Method m : usersClass.getDeclaredMethods()) {
                if (m.getName().equals("get")) {
                    getLegacy = m;
                    getLegacy.setAccessible(true);
                    break;
                }
            }
        }
        try {
            return (User) getLegacy.invoke(users, uuid.toString());
        } catch (IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }

    private void legacyMaximumSize(CacheBuilder builder, int maxCount) {
        try {
            Method maxSizeLegacy = builder.getClass().getDeclaredMethod("maximumSize", Integer.TYPE);
            maxSizeLegacy.setAccessible(true);
            maxSizeLegacy.invoke(builder, maxCount);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private Cache<String, User> legacyBuild(CacheBuilder builder) {
        Method build = null;
        for (Method method : builder.getClass().getDeclaredMethods()) {
            if (method.getName().equals("build")) {
                build = method;
                break;
            }
        }
        Cache<String, User> legacyUsers;
        try {
            assert build != null;
            build.setAccessible(true);
            legacyUsers = (Cache<String, User>) build.invoke(builder, this);
        } catch (IllegalAccessException | InvocationTargetException e) {
            legacyUsers = null;
        }
        return legacyUsers;
    }
}
