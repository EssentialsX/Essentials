package com.earth2me.essentials;

import com.earth2me.essentials.craftbukkit.FakeWorld;
import org.bukkit.*;
import org.bukkit.Warning.WarningState;
import org.bukkit.World.Environment;
import org.bukkit.advancement.Advancement;
import org.bukkit.block.data.BlockData;
import org.bukkit.boss.*;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.help.HelpMap;
import org.bukkit.inventory.*;
import org.bukkit.loot.LootTable;
import org.bukkit.map.MapView;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.*;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitWorker;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.util.CachedServerIcon;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.logging.Logger;


public class FakeServer implements Server {
    private List<Player> players = new ArrayList<>();
    private final List<World> worlds = new ArrayList<>();
    private PluginManager pluginManager = new FakePluginManager();

    FakeServer() {
        if (Bukkit.getServer() == null) {
            Bukkit.setServer(this);
        }
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

    public void setOnlinePlayers(List<Player> players) {
        this.players = players;
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
    public int broadcastMessage(String string) {
        int i = 0;
        for (Player player : players) {
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
    public Player getPlayer(String string) {
        for (Player player : players) {
            if (player.getName().equalsIgnoreCase(string)) {
                return player;
            }
        }
        return null;
    }

    @Override
    public List<Player> matchPlayer(String string) {
        List<Player> matches = new ArrayList<>();
        for (Player player : players) {
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
            public int scheduleSyncDelayedTask(Plugin plugin, Runnable r, long l) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public int scheduleSyncDelayedTask(Plugin plugin, BukkitRunnable bukkitRunnable, long l) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public int scheduleSyncDelayedTask(Plugin plugin, Runnable r) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public int scheduleSyncDelayedTask(Plugin plugin, BukkitRunnable bukkitRunnable) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public int scheduleSyncRepeatingTask(Plugin plugin, Runnable r, long l, long l1) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public int scheduleSyncRepeatingTask(Plugin plugin, BukkitRunnable bukkitRunnable, long l, long l1) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public int scheduleAsyncRepeatingTask(Plugin plugin, Runnable r, long l, long l1) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public <T> Future<T> callSyncMethod(Plugin plugin, Callable<T> clbl) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void cancelTask(int i) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void cancelTasks(Plugin plugin) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public boolean isCurrentlyRunning(int i) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public boolean isQueued(int i) {
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
            public BukkitTask runTask(Plugin plugin, Runnable r) throws IllegalArgumentException {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void runTask(Plugin plugin, Consumer<BukkitTask> task) throws IllegalArgumentException {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public BukkitTask runTask(Plugin plugin, BukkitRunnable bukkitRunnable) throws IllegalArgumentException {
                return null;
            }

            @Override
            public BukkitTask runTaskAsynchronously(Plugin plugin, Runnable r) throws IllegalArgumentException {
                r.run();
                return null;
            }

            @Override
            public void runTaskAsynchronously(Plugin plugin, Consumer<BukkitTask> task) throws IllegalArgumentException {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public BukkitTask runTaskAsynchronously(Plugin plugin, BukkitRunnable bukkitRunnable) throws IllegalArgumentException {
                return null;
            }

            @Override
            public BukkitTask runTaskLater(Plugin plugin, Runnable r, long l) throws IllegalArgumentException {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void runTaskLater(Plugin plugin, Consumer<BukkitTask> task, long delay) throws IllegalArgumentException {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public BukkitTask runTaskLater(Plugin plugin, BukkitRunnable bukkitRunnable, long l) throws IllegalArgumentException {
                return null;
            }

            @Override
            public BukkitTask runTaskLaterAsynchronously(Plugin plugin, Runnable r, long l) throws IllegalArgumentException {
                r.run();
                return null;
            }

            @Override
            public void runTaskLaterAsynchronously(Plugin plugin, Consumer<BukkitTask> task, long delay) throws IllegalArgumentException {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public BukkitTask runTaskLaterAsynchronously(Plugin plugin, BukkitRunnable bukkitRunnable, long l) throws IllegalArgumentException {
                return null;
            }

            @Override
            public BukkitTask runTaskTimer(Plugin plugin, Runnable r, long l, long l1) throws IllegalArgumentException {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void runTaskTimer(Plugin plugin, Consumer<BukkitTask> task, long delay, long period) throws IllegalArgumentException {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public BukkitTask runTaskTimer(Plugin plugin, BukkitRunnable bukkitRunnable, long l, long l1) throws IllegalArgumentException {
                return null;
            }

            @Override
            public BukkitTask runTaskTimerAsynchronously(Plugin plugin, Runnable r, long l, long l1) throws IllegalArgumentException {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void runTaskTimerAsynchronously(Plugin plugin, Consumer<BukkitTask> task, long delay, long period) throws IllegalArgumentException {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public BukkitTask runTaskTimerAsynchronously(Plugin plugin, BukkitRunnable bukkitRunnable, long l, long l1) throws IllegalArgumentException {
                return null;
            }

            @Override
            public int scheduleAsyncDelayedTask(Plugin plugin, Runnable r, long l) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public int scheduleAsyncDelayedTask(Plugin plugin, Runnable r) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }

    @Override
    public ServicesManager getServicesManager() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<World> getWorlds() {
        return worlds;
    }

    public World createWorld(String string, Environment e) {
        World w = new FakeWorld(string, e);
        worlds.add(w);
        return w;
    }

    public World createWorld(String string, Environment e, long l) {
        World w = new FakeWorld(string, e);
        worlds.add(w);
        return w;
    }

    @Override
    public World getWorld(String string) {
        for (World world : worlds) {
            if (world.getName().equalsIgnoreCase(string)) {
                return world;
            }
        }
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
    public PluginCommand getPluginCommand(String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void savePlayers() {
    }

    @Override
    public boolean dispatchCommand(CommandSender cs, String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean addRecipe(Recipe recipe) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    void addPlayer(Player base1) {
        players.add(base1);
        pluginManager.callEvent(new PlayerJoinEvent(base1, null));
    }

    OfflinePlayer createPlayer(String name) {
        OfflinePlayer player = new OfflinePlayer(name, this);
        player.setLocation(new Location(worlds.get(0), 0, 0, 0, 0, 0));
        return player;
    }

    @Override
    public World createWorld(WorldCreator creator) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean unloadWorld(String string, boolean bln) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean unloadWorld(World world, boolean bln) {
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
    public void setSpawnRadius(int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean getOnlineMode() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public World getWorld(long l) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public World getWorld(UUID uuid) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public MapView getMap(int id) {
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
    public MapView createMap(World world) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ItemStack createExplorerMap(World world, Location location, StructureType structureType) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ItemStack createExplorerMap(World world, Location location, StructureType structureType, int radius, boolean findUnexplored) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean getAllowFlight() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setWhitelist(boolean bln) {
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
    public Player getPlayerExact(String string) {
        for (Player player : players) {
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
    public int broadcast(String string, String string1) {
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
            public void setWhitelisted(boolean bln) {
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
            public void setOp(boolean bln) {
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
                        return null;
                }
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void incrementStatistic(Statistic statistic) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void decrementStatistic(Statistic statistic) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public int getStatistic(Statistic statistic) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void incrementStatistic(Statistic statistic, int amount) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void decrementStatistic(Statistic statistic, int amount) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void setStatistic(Statistic statistic, int newValue) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void incrementStatistic(Statistic statistic, Material material) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void decrementStatistic(Statistic statistic, Material material) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public int getStatistic(Statistic statistic, Material material) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void incrementStatistic(Statistic statistic, Material material, int amount) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void decrementStatistic(Statistic statistic, Material material, int amount) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void setStatistic(Statistic statistic, Material material, int newValue) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void incrementStatistic(Statistic statistic, EntityType entityType) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void decrementStatistic(Statistic statistic, EntityType entityType) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public int getStatistic(Statistic statistic, EntityType entityType) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void incrementStatistic(Statistic statistic, EntityType entityType, int amount) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void decrementStatistic(Statistic statistic, EntityType entityType, int amount) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void setStatistic(Statistic statistic, EntityType entityType, int newValue) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }

    @Override
    public Set<String> getIPBans() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void banIP(String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void unbanIP(String string) {
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
    public void setDefaultGameMode(GameMode gamemode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ConsoleCommandSender getConsoleSender() {
        return new ConsoleCommandSender() {
            @Override
            public void sendMessage(String message) {
                System.out.println("Console message: " + message);
            }

            @Override
            public void sendMessage(String[] messages) {
                for (String message : messages) {
                    System.out.println("Console message: " + message);
                }
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
            public boolean isPermissionSet(String name) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public boolean isPermissionSet(Permission perm) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public boolean hasPermission(String name) {
                return true;
            }

            @Override
            public boolean hasPermission(Permission perm) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public PermissionAttachment addAttachment(Plugin plugin) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void removeAttachment(PermissionAttachment attachment) {
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
            public void setOp(boolean value) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public boolean isConversing() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void acceptConversationInput(String input) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public boolean beginConversation(Conversation conversation) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void abandonConversation(Conversation conversation) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void abandonConversation(Conversation conversation, ConversationAbandonedEvent details) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void sendRawMessage(String message) {
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
    public OfflinePlayer[] getOfflinePlayers() {
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
    public void sendPluginMessage(Plugin plugin, String string, byte[] bytes) {
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
    public int getTicksPerWaterSpawns() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getTicksPerAmbientSpawns() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Recipe> getRecipesFor(ItemStack is) {
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
    public boolean removeRecipe(NamespacedKey key) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public HelpMap getHelpMap() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Inventory createInventory(InventoryHolder ih, InventoryType it) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Inventory createInventory(InventoryHolder ih, int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Inventory createInventory(InventoryHolder ih, int i, String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Merchant createMerchant(String s) {
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
    public CachedServerIcon loadServerIcon(File file) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public CachedServerIcon loadServerIcon(BufferedImage bufferedImage) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setIdleTimeout(int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getIdleTimeout() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ChunkGenerator.ChunkData createChunkData(World world) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public BossBar createBossBar(String s, BarColor barColor, BarStyle barStyle, BarFlag... barFlags) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public KeyedBossBar createBossBar(NamespacedKey key, String title, BarColor color, BarStyle style, BarFlag... flags) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Iterator<KeyedBossBar> getBossBars() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public KeyedBossBar getBossBar(NamespacedKey key) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean removeBossBar(NamespacedKey key) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    @SuppressWarnings("deprecation")
    public UnsafeValues getUnsafe() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public BanList getBanList(BanList.Type arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Player getPlayer(UUID arg0) {
        for (Player player : players) {
            if (player.getUniqueId().equals(arg0)) {
                return player;
            }
        }
        return null;
    }

    @Override
    public org.bukkit.OfflinePlayer getOfflinePlayer(UUID arg0) {
        if (arg0.toString().equalsIgnoreCase("3c9ebe1a-9098-43fd-bc0c-a369b76817ba")) {
            return createOPlayer("testPlayer1");
        }
        if (arg0.toString().equalsIgnoreCase("f4a37409-5c40-3b2c-9cd6-57d3c5abdc76")) {
            return createOPlayer("npc1");
        }
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Inventory createInventory(InventoryHolder arg0, InventoryType arg1, String arg2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void reloadData() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Entity getEntity(UUID uuid) {
        return getPlayer(uuid);
    }

    @Override
    public Advancement getAdvancement(NamespacedKey key) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Iterator<Advancement> advancementIterator() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public BlockData createBlockData(Material material) {
        return null;
    }

    @Override
    public BlockData createBlockData(Material material, Consumer<BlockData> consumer) {
        return null;
    }

    @Override
    public BlockData createBlockData(String data) throws IllegalArgumentException {
        return null;
    }

    @Override
    public BlockData createBlockData(Material material, String data) throws IllegalArgumentException {
        return null;
    }

    @Override
    public <T extends Keyed> Tag<T> getTag(String s, NamespacedKey namespacedKey, Class<T> aClass) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public <T extends Keyed> Iterable<Tag<T>> getTags(String registry, Class<T> clazz) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    class FakePluginManager implements PluginManager {
        ArrayList<RegisteredListener> listeners = new ArrayList<>();

        @Override
        public void registerInterface(Class<? extends PluginLoader> loader) throws IllegalArgumentException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Plugin getPlugin(String name) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Plugin[] getPlugins() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean isPluginEnabled(String name) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean isPluginEnabled(Plugin plugin) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Plugin loadPlugin(File file) throws UnknownDependencyException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Plugin[] loadPlugins(File directory) {
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
        public void callEvent(Event event) throws IllegalStateException {
            Logger.getLogger("Minecraft").info("Called event " + event.getEventName());
            if (event instanceof PlayerJoinEvent) {
                for (RegisteredListener listener : listeners) {
                    if (listener.getListener() instanceof EssentialsPlayerListener) {
                        PlayerJoinEvent jEvent = (PlayerJoinEvent) event;
                        EssentialsPlayerListener epl = (EssentialsPlayerListener) listener.getListener();
                        epl.onPlayerJoin(jEvent);
                        Essentials ess = (Essentials) listener.getPlugin();
                        ess.getLogger().info("Sending join event to Essentials");
                        ess.getUser(jEvent.getPlayer());
                    }
                }
            }
        }

        @Override
        public void registerEvents(Listener listener, Plugin plugin) {
            listeners.add(new RegisteredListener(listener, null, null, plugin, false));
        }

        @Override
        public void registerEvent(Class<? extends Event> event, Listener listener, EventPriority priority, EventExecutor executor, Plugin plugin) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void registerEvent(Class<? extends Event> event, Listener listener, EventPriority priority, EventExecutor executor, Plugin plugin, boolean ignoreCancelled) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void enablePlugin(Plugin plugin) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void disablePlugin(Plugin plugin) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Permission getPermission(String name) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void addPermission(Permission perm) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void removePermission(Permission perm) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void removePermission(String name) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Set<Permission> getDefaultPermissions(boolean op) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void recalculatePermissionDefaults(Permission perm) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void subscribeToPermission(String permission, Permissible permissible) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void unsubscribeFromPermission(String permission, Permissible permissible) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Set<Permissible> getPermissionSubscriptions(String permission) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void subscribeToDefaultPerms(boolean op, Permissible permissible) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void unsubscribeFromDefaultPerms(boolean op, Permissible permissible) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Set<Permissible> getDefaultPermSubscriptions(boolean op) {
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

    @Override
	public LootTable getLootTable(NamespacedKey arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Entity> selectEntities(CommandSender sender, String selector) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
