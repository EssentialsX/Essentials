package com.earth2me.essentials;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.UnsafeValues;
import org.bukkit.Warning.WarningState;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.advancement.Advancement;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.help.HelpMap;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.Recipe;
import org.bukkit.map.MapView;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.UnknownDependencyException;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitWorker;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.util.CachedServerIcon;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.logging.Logger;

@SuppressWarnings({"NullableProblems"})
public final class FakeServer implements Server {
    private final List<World> worlds = new ArrayList<>();
    private final PluginManager pluginManager = new FakePluginManager();
    private final List<Player> players = new ArrayList<>();

    private FakeServer() {
        createWorld("testWorld", Environment.NORMAL);
    }

    public static FakeServer getServer() {
        if (Bukkit.getServer() == null) {
            Bukkit.setServer(new FakeServer());
        }
        return (FakeServer) Bukkit.getServer();
    }

    @Override
    public String getName() {
        return "Essentials Fake Server";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public Collection<? extends Player> getOnlinePlayers() {
        return players;
    }

    @Override
    public int getMaxPlayers() {
        return 100;
    }

    @Override
    public int getPort() {
        return 25565;
    }

    @Override
    public String getIp() {
        return "127.0.0.1";
    }

    @Override
    public String getServerName() {
        return getName();
    }

    @Override
    public String getServerId() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int broadcastMessage(final String string) {
        int i = 0;
        for (final Player player : players) {
            player.sendMessage(string);
            i++;
        }
        return i;
    }

    @Override
    public String getUpdateFolder() {
        return "update";
    }

    @Override
    public File getUpdateFolderFile() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isHardcore() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Player getPlayer(final String string) {
        for (final Player player : players) {
            if (player.getName().equalsIgnoreCase(string)) {
                return player;
            }
        }
        return null;
    }

    @Override
    public List<Player> matchPlayer(final String string) {
        final List<Player> matches = new ArrayList<>();
        for (final Player player : players) {
            if (player.getName().substring(0, Math.min(player.getName().length(), string.length())).equalsIgnoreCase(string)) {
                matches.add(player);
            }
        }
        return matches;
    }

    @Override
    public PluginManager getPluginManager() {
        return pluginManager;
    }

    @Override
    public BukkitScheduler getScheduler() {
        return new BukkitScheduler() {
            @Override
            public int scheduleSyncDelayedTask(final Plugin plugin, final Runnable r, final long l) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public int scheduleSyncDelayedTask(final Plugin plugin, final BukkitRunnable bukkitRunnable, final long l) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public int scheduleSyncDelayedTask(final Plugin plugin, final Runnable r) {
                return -1;
            }

            @Override
            public int scheduleSyncDelayedTask(final Plugin plugin, final BukkitRunnable bukkitRunnable) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public int scheduleSyncRepeatingTask(final Plugin plugin, final Runnable r, final long l, final long l1) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public int scheduleSyncRepeatingTask(final Plugin plugin, final BukkitRunnable bukkitRunnable, final long l, final long l1) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public int scheduleAsyncRepeatingTask(final Plugin plugin, final Runnable r, final long l, final long l1) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public <T> Future<T> callSyncMethod(final Plugin plugin, final Callable<T> clbl) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void cancelTask(final int i) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void cancelTasks(final Plugin plugin) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void cancelAllTasks() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public boolean isCurrentlyRunning(final int i) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public boolean isQueued(final int i) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public List<BukkitWorker> getActiveWorkers() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public List<BukkitTask> getPendingTasks() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public BukkitTask runTask(final Plugin plugin, final Runnable r) throws IllegalArgumentException {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public BukkitTask runTask(final Plugin plugin, final BukkitRunnable bukkitRunnable) throws IllegalArgumentException {
                return null;
            }

            @Override
            public BukkitTask runTaskAsynchronously(final Plugin plugin, final Runnable r) throws IllegalArgumentException {
                r.run();
                return null;
            }

            @Override
            public BukkitTask runTaskAsynchronously(final Plugin plugin, final BukkitRunnable bukkitRunnable) throws IllegalArgumentException {
                return null;
            }

            @Override
            public BukkitTask runTaskLater(final Plugin plugin, final Runnable r, final long l) throws IllegalArgumentException {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public BukkitTask runTaskLater(final Plugin plugin, final BukkitRunnable bukkitRunnable, final long l) throws IllegalArgumentException {
                return null;
            }

            @Override
            public BukkitTask runTaskLaterAsynchronously(final Plugin plugin, final Runnable r, final long l) throws IllegalArgumentException {
                r.run();
                return null;
            }

            @Override
            public BukkitTask runTaskLaterAsynchronously(final Plugin plugin, final BukkitRunnable bukkitRunnable, final long l) throws IllegalArgumentException {
                return null;
            }

            @Override
            public BukkitTask runTaskTimer(final Plugin plugin, final Runnable r, final long l, final long l1) throws IllegalArgumentException {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public BukkitTask runTaskTimer(final Plugin plugin, final BukkitRunnable bukkitRunnable, final long l, final long l1) throws IllegalArgumentException {
                return null;
            }

            @Override
            public BukkitTask runTaskTimerAsynchronously(final Plugin plugin, final Runnable r, final long l, final long l1) throws IllegalArgumentException {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public BukkitTask runTaskTimerAsynchronously(final Plugin plugin, final BukkitRunnable bukkitRunnable, final long l, final long l1) throws IllegalArgumentException {
                return null;
            }

            @Override
            public int scheduleAsyncDelayedTask(final Plugin plugin, final Runnable r, final long l) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public int scheduleAsyncDelayedTask(final Plugin plugin, final Runnable r) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }

    @Override
    public ServicesManager getServicesManager() {
        return new ServicesManager() {
            @Override
            public <T> void register(Class<T> service, T provider, Plugin plugin, ServicePriority priority) {

            }

            @Override
            public void unregisterAll(Plugin plugin) {

            }

            @Override
            public void unregister(Class<?> service, Object provider) {

            }

            @Override
            public void unregister(Object provider) {

            }

            @Override
            public <T> T load(Class<T> service) {
                return null;
            }

            @Override
            public <T> RegisteredServiceProvider<T> getRegistration(Class<T> service) {
                return null;
            }

            @Override
            public List<RegisteredServiceProvider<?>> getRegistrations(Plugin plugin) {
                return null;
            }

            @Override
            public <T> Collection<RegisteredServiceProvider<T>> getRegistrations(Class<T> service) {
                return null;
            }

            @Override
            public Collection<Class<?>> getKnownServices() {
                return null;
            }

            @Override
            public <T> boolean isProvidedFor(Class<T> service) {
                return false;
            }
        };
    }

    @Override
    public List<World> getWorlds() {
        return worlds;
    }

    public World createWorld(final String string, final Environment e) {
        final World w = new FakeWorld(string, e);
        worlds.add(w);
        return w;
    }

    @Override
    public World getWorld(final String string) {
        for (final World world : worlds) {
            if (world.getName().equalsIgnoreCase(string)) {
                return world;
            }
        }
        return null;
    }

    @Override
    public World getWorld(final UUID uuid) {
        for (final World world : worlds) {
            if (world.getUID().equals(uuid)) {
                return world;
            }
        }
        return null;
    }

    @Override
    public MapView getMap(short id) {
        return null;
    }

    @Override
    public void reload() {
    }

    @Override
    public Logger getLogger() {
        return Logger.getLogger("Minecraft");
    }

    @Override
    public PluginCommand getPluginCommand(final String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void savePlayers() {
    }

    @Override
    public boolean dispatchCommand(final CommandSender cs, final String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean addRecipe(final Recipe recipe) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    void addPlayer(final Player base1) {
        players.add(base1);
        pluginManager.callEvent(new PlayerJoinEvent(base1, null));
    }

    OfflinePlayerStub createPlayer(final String name) {
        final OfflinePlayerStub player = new OfflinePlayerStub(name, this);
        player.setLocation(new Location(worlds.get(0), 0, 0, 0, 0, 0));
        return player;
    }

    @Override
    public World createWorld(final WorldCreator creator) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean unloadWorld(final String string, final boolean bln) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean unloadWorld(final World world, final boolean bln) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<String, String[]> getCommandAliases() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getSpawnRadius() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setSpawnRadius(final int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean getOnlineMode() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public MapView getMap(final int id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getViewDistance() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean getAllowNether() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean hasWhitelist() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MapView createMap(final World world) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean getAllowFlight() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setWhitelist(final boolean bln) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<org.bukkit.OfflinePlayer> getWhitelistedPlayers() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void reloadWhitelist() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Player getPlayerExact(final String string) {
        for (final Player player : players) {
            if (player.getName().equals(string)) {
                return player;
            }
        }
        return null;
    }

    @Override
    public void shutdown() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int broadcast(final String string, final String string1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public org.bukkit.OfflinePlayer getOfflinePlayer(final String string) {
        return createOPlayer(string);
    }

    private org.bukkit.OfflinePlayer createOPlayer(final String string) {
        return new org.bukkit.OfflinePlayer() {
            @Override
            public boolean isOnline() {
                return false;
            }

            @Override
            public String getName() {
                return string;
            }

            @Override
            public boolean isBanned() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public boolean isWhitelisted() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void setWhitelisted(final boolean bln) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public Player getPlayer() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public boolean isOp() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public Map<String, Object> serialize() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public long getFirstPlayed() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void setOp(final boolean bln) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public long getLastPlayed() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public boolean hasPlayedBefore() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public Location getBedSpawnLocation() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public UUID getUniqueId() {
                switch (string) {
                    case "testPlayer1":
                        return UUID.fromString("3c9ebe1a-9098-43fd-bc0c-a369b76817ba");
                    case "testPlayer2":
                        return UUID.fromString("2c9ebe1a-9098-43fd-bc0c-a369b76817ba");
                    case "npc1":
                        return UUID.fromString("f4a37409-5c40-3b2c-9cd6-57d3c5abdc76");
                }
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }

    @Override
    public Set<String> getIPBans() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void banIP(final String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void unbanIP(final String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<org.bukkit.OfflinePlayer> getBannedPlayers() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public GameMode getDefaultGameMode() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setDefaultGameMode(final GameMode gamemode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ConsoleCommandSender getConsoleSender() {
        return new ConsoleCommandSender() {
            @Override
            public void sendMessage(final String message) {
                System.out.println("Console message: " + message);
            }

            @Override
            public void sendMessage(final String[] messages) {
                for (final String message : messages) {
                    System.out.println("Console message: " + message);
                }
            }

            public void sendMessage(final UUID uuid, final String message) {
                sendMessage(message);
            }

            public void sendMessage(final UUID uuid, final String[] messages) {
                sendMessage(messages);
            }

            @Override
            public Server getServer() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public String getName() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public boolean isPermissionSet(final String name) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public boolean isPermissionSet(final Permission perm) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public boolean hasPermission(final String name) {
                return true;
            }

            @Override
            public boolean hasPermission(final Permission perm) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public PermissionAttachment addAttachment(final Plugin plugin, final String name, final boolean value) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public PermissionAttachment addAttachment(final Plugin plugin) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public PermissionAttachment addAttachment(final Plugin plugin, final String name, final boolean value, final int ticks) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public PermissionAttachment addAttachment(final Plugin plugin, final int ticks) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void removeAttachment(final PermissionAttachment attachment) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void recalculatePermissions() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public Set<PermissionAttachmentInfo> getEffectivePermissions() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public boolean isOp() {
                return true;
            }

            @Override
            public boolean isConversing() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void acceptConversationInput(final String input) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void setOp(final boolean value) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public boolean beginConversation(final Conversation conversation) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void abandonConversation(final Conversation conversation) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void abandonConversation(final Conversation conversation, final ConversationAbandonedEvent details) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void sendRawMessage(final String message) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }

    @Override
    public Set<org.bukkit.OfflinePlayer> getOperators() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getBukkitVersion() {
        return "Essentials Fake-Server";
    }

    @Override
    public File getWorldContainer() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public OfflinePlayerStub[] getOfflinePlayers() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean getAllowEnd() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Messenger getMessenger() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void sendPluginMessage(final Plugin plugin, final String string, final byte[] bytes) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<String> getListeningPluginChannels() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getTicksPerAnimalSpawns() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getTicksPerMonsterSpawns() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Recipe> getRecipesFor(final ItemStack is) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Iterator<Recipe> recipeIterator() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void clearRecipes() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void resetRecipes() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public HelpMap getHelpMap() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Inventory createInventory(final InventoryHolder ih, final InventoryType it) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Inventory createInventory(final InventoryHolder ih, final int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Inventory createInventory(final InventoryHolder ih, final int i, final String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Merchant createMerchant(final String s) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getWorldType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean getGenerateStructures() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long getConnectionThrottle() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getMonsterSpawnLimit() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getAnimalSpawnLimit() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getWaterAnimalSpawnLimit() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isPrimaryThread() {
        return true; // Can be set to true or false, just needs to return for AFK status test to pass.
    }

    @Override
    public String getMotd() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public WarningState getWarningState() {
        return WarningState.DEFAULT;
    }

    @Override
    public int getAmbientSpawnLimit() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getShutdownMessage() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ItemFactory getItemFactory() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ScoreboardManager getScoreboardManager() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public CachedServerIcon getServerIcon() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public CachedServerIcon loadServerIcon(final File file) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public CachedServerIcon loadServerIcon(final BufferedImage bufferedImage) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getIdleTimeout() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setIdleTimeout(final int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ChunkGenerator.ChunkData createChunkData(final World world) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public BossBar createBossBar(final String s, final BarColor barColor, final BarStyle barStyle, final BarFlag... barFlags) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    @SuppressWarnings("deprecation")
    public UnsafeValues getUnsafe() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public BanList getBanList(final BanList.Type arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Player getPlayer(final UUID arg0) {
        for (final Player player : players) {
            if (player.getUniqueId().equals(arg0)) {
                return player;
            }
        }
        return null;
    }

    @Override
    public org.bukkit.OfflinePlayer getOfflinePlayer(final UUID arg0) {
        if (arg0.toString().equalsIgnoreCase("3c9ebe1a-9098-43fd-bc0c-a369b76817ba")) {
            return createOPlayer("testPlayer1");
        }
        if (arg0.toString().equalsIgnoreCase("f4a37409-5c40-3b2c-9cd6-57d3c5abdc76")) {
            return createOPlayer("npc1");
        }
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Inventory createInventory(final InventoryHolder arg0, final InventoryType arg1, final String arg2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void reloadData() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Entity getEntity(final UUID uuid) {
        return getPlayer(uuid);
    }

    @Override
    public Advancement getAdvancement(final NamespacedKey key) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Iterator<Advancement> advancementIterator() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    static class FakePluginManager implements PluginManager {
        final ArrayList<RegisteredListener> listeners = new ArrayList<>();

        @Override
        public void registerInterface(final Class<? extends PluginLoader> loader) throws IllegalArgumentException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Plugin getPlugin(final String name) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Plugin[] getPlugins() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean isPluginEnabled(final String name) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean isPluginEnabled(final Plugin plugin) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Plugin loadPlugin(final File file) throws UnknownDependencyException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Plugin[] loadPlugins(final File directory) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void disablePlugins() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void clearPlugins() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void callEvent(final Event event) throws IllegalStateException {
            Logger.getLogger("Minecraft").info("Called event " + event.getEventName());
            if (event instanceof PlayerJoinEvent) {
                for (final RegisteredListener listener : listeners) {
                    if (listener.getListener() instanceof FakeAccessor) {
                        final PlayerJoinEvent jEvent = (PlayerJoinEvent) event;
                        final FakeAccessor epl = (FakeAccessor) listener.getListener();
                        epl.onPlayerJoin(jEvent);
                        Logger.getLogger("Essentials").info("Sending join event to Essentials");
                        epl.getUser(jEvent.getPlayer());
                    }
                }
            }
        }

        @Override
        public void registerEvents(final Listener listener, final Plugin plugin) {
            listeners.add(new RegisteredListener(listener, null, null, plugin, false));
        }

        @Override
        public void registerEvent(final Class<? extends Event> event, final Listener listener, final EventPriority priority, final EventExecutor executor, final Plugin plugin) {
            listeners.add(new RegisteredListener(listener, executor, priority, plugin, false));
        }

        @Override
        public void registerEvent(final Class<? extends Event> event, final Listener listener, final EventPriority priority, final EventExecutor executor, final Plugin plugin, final boolean ignoreCancelled) {
            listeners.add(new RegisteredListener(listener, executor, priority, plugin, false));
        }

        @Override
        public void enablePlugin(final Plugin plugin) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void disablePlugin(final Plugin plugin) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Permission getPermission(final String name) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void addPermission(final Permission perm) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void removePermission(final Permission perm) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void removePermission(final String name) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Set<Permission> getDefaultPermissions(final boolean op) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void recalculatePermissionDefaults(final Permission perm) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void subscribeToPermission(final String permission, final Permissible permissible) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void unsubscribeFromPermission(final String permission, final Permissible permissible) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Set<Permissible> getPermissionSubscriptions(final String permission) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void subscribeToDefaultPerms(final boolean op, final Permissible permissible) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void unsubscribeFromDefaultPerms(final boolean op, final Permissible permissible) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Set<Permissible> getDefaultPermSubscriptions(final boolean op) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Set<Permission> getPermissions() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean useTimings() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
