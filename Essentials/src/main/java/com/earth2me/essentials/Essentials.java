/*
 * Essentials - a bukkit plugin
 * Copyright (C) 2011  Essentials Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.earth2me.essentials;

import com.earth2me.essentials.commands.EssentialsCommand;
import com.earth2me.essentials.commands.IEssentialsCommand;
import com.earth2me.essentials.commands.NoChargeException;
import com.earth2me.essentials.commands.NotEnoughArgumentsException;
import com.earth2me.essentials.commands.QuietAbortException;
import com.earth2me.essentials.items.AbstractItemDb;
import com.earth2me.essentials.items.CustomItemResolver;
import com.earth2me.essentials.items.FlatItemDb;
import com.earth2me.essentials.items.LegacyItemDb;
import com.earth2me.essentials.metrics.MetricsWrapper;
import com.earth2me.essentials.perm.PermissionsDefaults;
import com.earth2me.essentials.perm.PermissionsHandler;
import com.earth2me.essentials.register.payment.Methods;
import com.earth2me.essentials.signs.SignBlockListener;
import com.earth2me.essentials.signs.SignEntityListener;
import com.earth2me.essentials.signs.SignPlayerListener;
import com.earth2me.essentials.textreader.IText;
import com.earth2me.essentials.textreader.KeywordReplacer;
import com.earth2me.essentials.textreader.SimpleTextInput;
import com.earth2me.essentials.utils.DateUtil;
import com.earth2me.essentials.utils.VersionUtil;
import io.papermc.lib.PaperLib;
import net.ess3.api.Economy;
import net.ess3.api.IEssentials;
import net.ess3.api.IItemDb;
import net.ess3.api.IJails;
import net.ess3.api.ISettings;
import net.ess3.nms.refl.providers.ReflServerStateProvider;
import net.ess3.nms.refl.providers.ReflSpawnEggProvider;
import net.ess3.nms.refl.providers.ReflSpawnerBlockProvider;
import net.ess3.nms.refl.providers.ReflKnownCommandsProvider;
import net.ess3.provider.ContainerProvider;
import net.ess3.provider.KnownCommandsProvider;
import net.ess3.provider.PotionMetaProvider;
import net.ess3.provider.ProviderListener;
import net.ess3.provider.ServerStateProvider;
import net.ess3.provider.SpawnEggProvider;
import net.ess3.provider.SpawnerBlockProvider;
import net.ess3.provider.SpawnerItemProvider;
import net.ess3.provider.providers.BasePotionDataProvider;
import net.ess3.provider.providers.BlockMetaSpawnerItemProvider;
import net.ess3.provider.providers.BukkitSpawnerBlockProvider;
import net.ess3.provider.providers.FlatSpawnEggProvider;
import net.ess3.provider.providers.LegacyPotionMetaProvider;
import net.ess3.provider.providers.LegacySpawnEggProvider;
import net.ess3.provider.providers.PaperContainerProvider;
import net.ess3.provider.providers.PaperKnownCommandsProvider;
import net.ess3.provider.providers.PaperRecipeBookListener;
import net.ess3.provider.providers.PaperServerStateProvider;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.earth2me.essentials.I18n.tl;

public class Essentials extends JavaPlugin implements net.ess3.api.IEssentials {
    private static final Logger LOGGER = Logger.getLogger("Essentials");
    private final transient TNTExplodeListener tntListener = new TNTExplodeListener(this);
    private final transient Set<String> vanishedPlayers = new LinkedHashSet<>();
    private transient ISettings settings;
    private transient Jails jails;
    private transient Warps warps;
    private transient Worth worth;
    private transient List<IConf> confList;
    private transient Backup backup;
    private transient AbstractItemDb itemDb;
    private transient CustomItemResolver customItemResolver;
    private transient PermissionsHandler permissionsHandler;
    private transient AlternativeCommandsHandler alternativeCommandsHandler;
    private transient UserMap userMap;
    private transient ExecuteTimer execTimer;
    private transient I18n i18n;
    private transient MetricsWrapper metrics;
    private transient EssentialsTimer timer;
    private transient SpawnerItemProvider spawnerItemProvider;
    private transient SpawnerBlockProvider spawnerBlockProvider;
    private transient SpawnEggProvider spawnEggProvider;
    private transient PotionMetaProvider potionMetaProvider;
    private transient ServerStateProvider serverStateProvider;
    private transient ContainerProvider containerProvider;
    private transient KnownCommandsProvider knownCommandsProvider;
    private transient ProviderListener recipeBookEventProvider;
    private transient Kits kits;
    private transient RandomTeleport randomTeleport;

    static {
        // TODO: improve legacy code
        Methods.init();
    }

    public Essentials() {
    }

    protected Essentials(final JavaPluginLoader loader, final PluginDescriptionFile description, final File dataFolder, final File file) {
        super(loader, description, dataFolder, file);
    }

    public Essentials(final Server server) {
        super(new JavaPluginLoader(server), new PluginDescriptionFile("Essentials", "", "com.earth2me.essentials.Essentials"), null, null);
    }

    @Override
    public ISettings getSettings() {
        return settings;
    }

    public void setupForTesting(final Server server) throws IOException, InvalidDescriptionException {
        final File dataFolder = File.createTempFile("essentialstest", "");
        if (!dataFolder.delete()) {
            throw new IOException();
        }
        if (!dataFolder.mkdir()) {
            throw new IOException();
        }
        i18n = new I18n(this);
        i18n.onEnable();
        i18n.updateLocale("en");
        Console.setInstance(this);

        LOGGER.log(Level.INFO, tl("usingTempFolderForTesting"));
        LOGGER.log(Level.INFO, dataFolder.toString());
        settings = new Settings(this);
        userMap = new UserMap(this);
        permissionsHandler = new PermissionsHandler(this, false);
        Economy.setEss(this);
        confList = new ArrayList<>();
        jails = new Jails(this);
        registerListeners(server.getPluginManager());
        kits = new Kits(this);
    }

    @Override
    public void onEnable() {
        try {
            if (LOGGER != this.getLogger()) {
                LOGGER.setParent(this.getLogger());
            }
            execTimer = new ExecuteTimer();
            execTimer.start();
            i18n = new I18n(this);
            i18n.onEnable();
            execTimer.mark("I18n1");

            Console.setInstance(this);

            switch (VersionUtil.getServerSupportStatus()) {
                case UNSTABLE:
                    getLogger().severe(tl("serverUnsupportedMods"));
                    break;
                case OUTDATED:
                    getLogger().severe(tl("serverUnsupported"));
                    break;
                case LIMITED:
                    getLogger().info(tl("serverUnsupportedLimitedApi"));
                    break;
            }

            final PluginManager pm = getServer().getPluginManager();
            for (final Plugin plugin : pm.getPlugins()) {
                if (plugin.getDescription().getName().startsWith("Essentials") && !plugin.getDescription().getVersion().equals(this.getDescription().getVersion()) && !plugin.getDescription().getName().equals("EssentialsAntiCheat")) {
                    getLogger().warning(tl("versionMismatch", plugin.getDescription().getName()));
                }
            }

            try {
                final EssentialsUpgrade upgrade = new EssentialsUpgrade(this);
                upgrade.beforeSettings();
                execTimer.mark("Upgrade");

                confList = new ArrayList<>();
                settings = new Settings(this);
                confList.add(settings);
                execTimer.mark("Settings");

                userMap = new UserMap(this);
                confList.add(userMap);
                execTimer.mark("Init(Usermap)");

                kits = new Kits(this);
                confList.add(kits);
                upgrade.convertKits();
                execTimer.mark("Kits");

                upgrade.afterSettings();
                execTimer.mark("Upgrade2");

                warps = new Warps(getServer(), this.getDataFolder());
                confList.add(warps);
                execTimer.mark("Init(Warp)");

                worth = new Worth(this.getDataFolder());
                confList.add(worth);
                execTimer.mark("Init(Worth)");

                itemDb = getItemDbFromConfig();
                confList.add(itemDb);
                execTimer.mark("Init(ItemDB)");

                randomTeleport = new RandomTeleport(this);
                if (randomTeleport.getPreCache()) {
                    randomTeleport.cacheRandomLocations(randomTeleport.getCenter(), randomTeleport.getMinRange(), randomTeleport.getMaxRange());
                }
                confList.add(randomTeleport);
                execTimer.mark("Init(RandomTeleport)");

                customItemResolver = new CustomItemResolver(this);
                try {
                    itemDb.registerResolver(this, "custom_items", customItemResolver);
                    confList.add(customItemResolver);
                } catch (final Exception e) {
                    e.printStackTrace();
                    customItemResolver = null;
                }
                execTimer.mark("Init(CustomItemResolver)");

                jails = new Jails(this);
                confList.add(jails);
                execTimer.mark("Init(Jails)");

                //Spawner item provider only uses one but it's here for legacy...
                spawnerItemProvider = new BlockMetaSpawnerItemProvider();

                //Spawner block providers
                if (VersionUtil.getServerBukkitVersion().isLowerThan(VersionUtil.v1_12_0_R01)) {
                    spawnerBlockProvider = new ReflSpawnerBlockProvider();
                } else {
                    spawnerBlockProvider = new BukkitSpawnerBlockProvider();
                }

                //Spawn Egg Providers
                if (VersionUtil.getServerBukkitVersion().isLowerThanOrEqualTo(VersionUtil.v1_8_8_R01)) {
                    spawnEggProvider = new LegacySpawnEggProvider();
                } else if (VersionUtil.getServerBukkitVersion().isLowerThanOrEqualTo(VersionUtil.v1_12_2_R01)) {
                    spawnEggProvider = new ReflSpawnEggProvider();
                } else {
                    spawnEggProvider = new FlatSpawnEggProvider();
                }

                //Potion Meta Provider
                if (VersionUtil.getServerBukkitVersion().isLowerThanOrEqualTo(VersionUtil.v1_8_8_R01)) {
                    potionMetaProvider = new LegacyPotionMetaProvider();
                } else {
                    potionMetaProvider = new BasePotionDataProvider();
                }

                //Server State Provider
                //Container Provider
                if (PaperLib.isPaper() && VersionUtil.getServerBukkitVersion().isHigherThanOrEqualTo(VersionUtil.v1_15_2_R01)) {
                    serverStateProvider = new PaperServerStateProvider();
                    containerProvider = new PaperContainerProvider();
                } else {
                    serverStateProvider = new ReflServerStateProvider(getLogger());
                }

                //Event Providers
                if (PaperLib.isPaper()) {
                    try {
                        Class.forName("com.destroystokyo.paper.event.player.PlayerRecipeBookClickEvent");
                        recipeBookEventProvider = new PaperRecipeBookListener(event -> {
                            if (this.getUser(((PlayerEvent) event).getPlayer()).isRecipeSee()) {
                                ((Cancellable) event).setCancelled(true);
                            }
                        });
                    } catch (final ClassNotFoundException ignored) {
                    }
                }

                //Known Commands Provider
                if (PaperLib.isPaper() && VersionUtil.getServerBukkitVersion().isHigherThanOrEqualTo(VersionUtil.v1_11_2_R01)) {
                    knownCommandsProvider = new PaperKnownCommandsProvider();
                } else {
                    knownCommandsProvider = new ReflKnownCommandsProvider();
                }

                execTimer.mark("Init(Providers)");
                reload();

                // The item spawn blacklist is loaded with all other settings, before the item
                // DB, but it depends on the item DB, so we need to reload it again here:
                ((Settings) settings)._lateLoadItemSpawnBlacklist();
            } catch (final YAMLException exception) {
                if (pm.getPlugin("EssentialsUpdate") != null) {
                    LOGGER.log(Level.SEVERE, tl("essentialsHelp2"));
                } else {
                    LOGGER.log(Level.SEVERE, tl("essentialsHelp1"));
                }
                handleCrash(exception);
                return;
            }
            backup = new Backup(this);
            permissionsHandler = new PermissionsHandler(this, settings.useBukkitPermissions());
            alternativeCommandsHandler = new AlternativeCommandsHandler(this);

            timer = new EssentialsTimer(this);
            scheduleSyncRepeatingTask(timer, 1000, 50);

            Economy.setEss(this);
            execTimer.mark("RegHandler");

            // Register /hat and /back default permissions
            PermissionsDefaults.registerAllBackDefaults();
            PermissionsDefaults.registerAllHatDefaults();

            metrics = new MetricsWrapper(this, 858, true);

            final String timeroutput = execTimer.end();
            if (getSettings().isDebug()) {
                LOGGER.log(Level.INFO, "Essentials load {0}", timeroutput);
            }
        } catch (final NumberFormatException ex) {
            handleCrash(ex);
        } catch (final Error ex) {
            handleCrash(ex);
            throw ex;
        }
        getBackup().setPendingShutdown(false);
    }

    @Override
    public void saveConfig() {
        // We don't use any of the bukkit config writing, as this breaks our config file formatting.
    }

    private void registerListeners(final PluginManager pm) {
        HandlerList.unregisterAll(this);

        if (getSettings().isDebug()) {
            LOGGER.log(Level.INFO, "Registering Listeners");
        }

        final EssentialsPluginListener pluginListener = new EssentialsPluginListener(this);
        pm.registerEvents(pluginListener, this);
        confList.add(pluginListener);

        final EssentialsPlayerListener playerListener = new EssentialsPlayerListener(this);
        playerListener.registerEvents();

        final EssentialsBlockListener blockListener = new EssentialsBlockListener(this);
        pm.registerEvents(blockListener, this);

        final SignBlockListener signBlockListener = new SignBlockListener(this);
        pm.registerEvents(signBlockListener, this);

        final SignPlayerListener signPlayerListener = new SignPlayerListener(this);
        pm.registerEvents(signPlayerListener, this);

        final SignEntityListener signEntityListener = new SignEntityListener(this);
        pm.registerEvents(signEntityListener, this);

        final EssentialsEntityListener entityListener = new EssentialsEntityListener(this);
        pm.registerEvents(entityListener, this);

        final EssentialsWorldListener worldListener = new EssentialsWorldListener(this);
        pm.registerEvents(worldListener, this);

        final EssentialsServerListener serverListener = new EssentialsServerListener(this);
        pm.registerEvents(serverListener, this);

        pm.registerEvents(tntListener, this);

        if (recipeBookEventProvider != null) {
            pm.registerEvents(recipeBookEventProvider, this);
        }

        jails.resetListener();
    }

    @Override
    public void onDisable() {
        final boolean stopping = getServerStateProvider().isStopping();
        if (!stopping) {
            LOGGER.log(Level.SEVERE, tl("serverReloading"));
        }
        getBackup().setPendingShutdown(true);
        for (final User user : getOnlineUsers()) {
            if (user.isVanished()) {
                user.setVanished(false);
                user.sendMessage(tl("unvanishedReload"));
            }
            if (stopping) {
                user.setLastLocation();
                if (!user.isHidden()) {
                    user.setLastLogout(System.currentTimeMillis());
                }
                user.cleanup();
            } else {
                user.stopTransaction();
            }
        }
        cleanupOpenInventories();
        if (getBackup().getTaskLock() != null && !getBackup().getTaskLock().isDone()) {
            LOGGER.log(Level.SEVERE, tl("backupInProgress"));
            getBackup().getTaskLock().join();
        }
        if (i18n != null) {
            i18n.onDisable();
        }
        if (backup != null) {
            backup.stopTask();
        }

        this.getPermissionsHandler().unregisterContexts();

        Economy.setEss(null);
        Trade.closeLog();
        getUserMap().getUUIDMap().shutdown();

        HandlerList.unregisterAll(this);
    }

    @Override
    public void reload() {
        Trade.closeLog();

        for (final IConf iConf : confList) {
            iConf.reloadConfig();
            execTimer.mark("Reload(" + iConf.getClass().getSimpleName() + ")");
        }

        i18n.updateLocale(settings.getLocale());
        for (final String commandName : this.getDescription().getCommands().keySet()) {
            final Command command = this.getCommand(commandName);
            if (command != null) {
                command.setDescription(tl(commandName + "CommandDescription"));
                command.setUsage(tl(commandName + "CommandUsage"));
            }
        }

        final PluginManager pm = getServer().getPluginManager();
        registerListeners(pm);
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String commandLabel, final String[] args) {
        return onTabCompleteEssentials(sender, command, commandLabel, args, Essentials.class.getClassLoader(),
            "com.earth2me.essentials.commands.Command", "essentials.", null);
    }

    @Override
    public List<String> onTabCompleteEssentials(final CommandSender cSender, final Command command, final String commandLabel, final String[] args,
                                                final ClassLoader classLoader, final String commandPath, final String permissionPrefix,
                                                final IEssentialsModule module) {
        if (!getSettings().isCommandOverridden(command.getName()) && (!commandLabel.startsWith("e") || commandLabel.equalsIgnoreCase(command.getName()))) {
            final PluginCommand pc = alternativeCommandsHandler.getAlternative(commandLabel);
            if (pc != null) {
                try {
                    final TabCompleter completer = pc.getTabCompleter();
                    if (completer != null) {
                        return completer.onTabComplete(cSender, command, commandLabel, args);
                    }
                } catch (final Exception ex) {
                    Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
        }

        try {
            // Note: The tab completer is always a player, even when tab-completing in a command block
            User user = null;
            if (cSender instanceof Player) {
                user = getUser((Player) cSender);
            }

            final CommandSource sender = new CommandSource(cSender);

            // Check for disabled commands
            if (getSettings().isCommandDisabled(commandLabel)) {
                return Collections.emptyList();
            }

            final IEssentialsCommand cmd;
            try {
                cmd = (IEssentialsCommand) classLoader.loadClass(commandPath + command.getName()).newInstance();
                cmd.setEssentials(this);
                cmd.setEssentialsModule(module);
            } catch (final Exception ex) {
                sender.sendMessage(tl("commandNotLoaded", commandLabel));
                LOGGER.log(Level.SEVERE, tl("commandNotLoaded", commandLabel), ex);
                return Collections.emptyList();
            }

            // Check authorization
            if (user != null && !user.isAuthorized(cmd, permissionPrefix)) {
                return Collections.emptyList();
            }

            if (user != null && user.isJailed() && !user.isAuthorized(cmd, "essentials.jail.allow.")) {
                return Collections.emptyList();
            }

            // Run the command
            try {
                if (user == null) {
                    return cmd.tabComplete(getServer(), sender, commandLabel, command, args);
                } else {
                    return cmd.tabComplete(getServer(), user, commandLabel, command, args);
                }
            } catch (final Exception ex) {
                showError(sender, ex, commandLabel);
                // Tab completion shouldn't fail
                LOGGER.log(Level.SEVERE, tl("commandFailed", commandLabel), ex);
                return Collections.emptyList();
            }
        } catch (final Throwable ex) {
            LOGGER.log(Level.SEVERE, tl("commandFailed", commandLabel), ex);
            return Collections.emptyList();
        }
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String commandLabel, final String[] args) {
        metrics.markCommand(command.getName(), true);
        return onCommandEssentials(sender, command, commandLabel, args, Essentials.class.getClassLoader(), "com.earth2me.essentials.commands.Command", "essentials.", null);
    }

    @Override
    public boolean onCommandEssentials(final CommandSender cSender, final Command command, final String commandLabel, final String[] args, final ClassLoader classLoader, final String commandPath, final String permissionPrefix, final IEssentialsModule module) {
        // Allow plugins to override the command via onCommand
        if (!getSettings().isCommandOverridden(command.getName()) && (!commandLabel.startsWith("e") || commandLabel.equalsIgnoreCase(command.getName()))) {
            final PluginCommand pc = alternativeCommandsHandler.getAlternative(commandLabel);
            if (pc != null) {
                alternativeCommandsHandler.executed(commandLabel, pc);
                try {
                    return pc.execute(cSender, commandLabel, args);
                } catch (final Exception ex) {
                    Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
                    cSender.sendMessage(tl("internalError"));
                    return true;
                }
            }
        }

        try {

            User user = null;
            Block bSenderBlock = null;
            if (cSender instanceof Player) {
                user = getUser((Player) cSender);
            } else if (cSender instanceof BlockCommandSender) {
                final BlockCommandSender bsender = (BlockCommandSender) cSender;
                bSenderBlock = bsender.getBlock();
            }

            if (bSenderBlock != null) {
                if (getSettings().logCommandBlockCommands()) {
                    Bukkit.getLogger().log(Level.INFO, "CommandBlock at {0},{1},{2} issued server command: /{3} {4}", new Object[] {bSenderBlock.getX(), bSenderBlock.getY(), bSenderBlock.getZ(), commandLabel, EssentialsCommand.getFinalArg(args, 0)});
                }
            } else if (user == null) {
                Bukkit.getLogger().log(Level.INFO, "{0} issued server command: /{1} {2}", new Object[] {cSender.getName(), commandLabel, EssentialsCommand.getFinalArg(args, 0)});
            }

            final CommandSource sender = new CommandSource(cSender);

            // New mail notification
            if (user != null && !getSettings().isCommandDisabled("mail") && !command.getName().equals("mail") && user.isAuthorized("essentials.mail")) {
                user.notifyOfMail();
            }

            //Print version even if admin command is not available #easteregg
            if (commandLabel.equalsIgnoreCase("essversion")) {
                sender.sendMessage("This server is running Essentials " + getDescription().getVersion());
                return true;
            }

            // Check for disabled commands
            if (getSettings().isCommandDisabled(commandLabel)) {
                sender.sendMessage(tl("commandDisabled", commandLabel));
                return true;
            }

            final IEssentialsCommand cmd;
            try {
                cmd = (IEssentialsCommand) classLoader.loadClass(commandPath + command.getName()).newInstance();
                cmd.setEssentials(this);
                cmd.setEssentialsModule(module);
            } catch (final Exception ex) {
                sender.sendMessage(tl("commandNotLoaded", commandLabel));
                LOGGER.log(Level.SEVERE, tl("commandNotLoaded", commandLabel), ex);
                return true;
            }

            // Check authorization
            if (user != null && !user.isAuthorized(cmd, permissionPrefix)) {
                LOGGER.log(Level.INFO, tl("deniedAccessCommand", user.getName()));
                user.sendMessage(tl("noAccessCommand"));
                return true;
            }

            if (user != null && user.isJailed() && !user.isAuthorized(cmd, "essentials.jail.allow.")) {
                if (user.getJailTimeout() > 0) {
                    user.sendMessage(tl("playerJailedFor", user.getName(), DateUtil.formatDateDiff(user.getJailTimeout())));
                } else {
                    user.sendMessage(tl("jailMessage"));
                }
                return true;
            }

            // Run the command
            try {
                if (user == null) {
                    cmd.run(getServer(), sender, commandLabel, command, args);
                } else {
                    cmd.run(getServer(), user, commandLabel, command, args);
                }
                return true;
            } catch (final NoChargeException | QuietAbortException ex) {
                return true;
            } catch (final NotEnoughArgumentsException ex) {
                sender.sendMessage(command.getDescription());
                sender.sendMessage(command.getUsage().replaceAll("<command>", commandLabel));
                if (!ex.getMessage().isEmpty()) {
                    sender.sendMessage(ex.getMessage());
                }
                if (ex.getCause() != null && settings.isDebug()) {
                    ex.getCause().printStackTrace();
                }
                return true;
            } catch (final Exception ex) {
                showError(sender, ex, commandLabel);
                if (settings.isDebug()) {
                    ex.printStackTrace();
                }
                return true;
            }
        } catch (final Throwable ex) {
            LOGGER.log(Level.SEVERE, tl("commandFailed", commandLabel), ex);
            return true;
        }
    }

    public void cleanupOpenInventories() {
        for (final User user : getOnlineUsers()) {
            if (user.isRecipeSee()) {
                user.getBase().getOpenInventory().getTopInventory().clear();
                user.getBase().getOpenInventory().close();
                user.setRecipeSee(false);
            }
            if (user.isInvSee() || user.isEnderSee()) {
                user.getBase().getOpenInventory().close();
                user.setInvSee(false);
                user.setEnderSee(false);
            }
        }
    }

    @Override
    public void showError(final CommandSource sender, final Throwable exception, final String commandLabel) {
        sender.sendMessage(tl("errorWithMessage", exception.getMessage()));
        if (getSettings().isDebug()) {
            LOGGER.log(Level.INFO, tl("errorCallingCommand", commandLabel), exception);
        }
    }

    @Override
    public BukkitScheduler getScheduler() {
        return this.getServer().getScheduler();
    }

    @Override
    public IJails getJails() {
        return jails;
    }

    @Override
    public Warps getWarps() {
        return warps;
    }

    @Override
    public Worth getWorth() {
        return worth;
    }

    @Override
    public Backup getBackup() {
        return backup;
    }

    @Override
    public Kits getKits() {
        return kits;
    }

    @Override
    public RandomTeleport getRandomTeleport() {
        return randomTeleport;
    }

    @Deprecated
    @Override
    public User getUser(final Object base) {
        if (base instanceof Player) {
            return getUser((Player) base);
        }
        if (base instanceof org.bukkit.OfflinePlayer) {
            return getUser(((org.bukkit.OfflinePlayer) base).getUniqueId());
        }
        if (base instanceof UUID) {
            return getUser((UUID) base);
        }
        if (base instanceof String) {
            return getOfflineUser((String) base);
        }
        return null;
    }

    //This will return null if there is not a match.
    @Override
    public User getUser(final String base) {
        return getOfflineUser(base);
    }

    //This will return null if there is not a match.
    @Override
    public User getUser(final UUID base) {
        return userMap.getUser(base);
    }

    //This will return null if there is not a match.
    @Override
    public User getOfflineUser(final String name) {
        final User user = userMap.getUser(name);
        if (user != null && user.getBase() instanceof OfflinePlayer) {
            //This code should attempt to use the last known name of a user, if Bukkit returns name as null.
            final String lastName = user.getLastAccountName();
            if (lastName != null) {
                ((OfflinePlayer) user.getBase()).setName(lastName);
            } else {
                ((OfflinePlayer) user.getBase()).setName(name);
            }
        }
        return user;
    }

    //This will create a new user if there is not a match.
    @Override
    public User getUser(final Player base) {
        if (base == null) {
            return null;
        }

        if (userMap == null) {
            LOGGER.log(Level.WARNING, "Essentials userMap not initialized");
            return null;
        }

        User user = userMap.getUser(base.getUniqueId());

        if (user == null) {
            if (getSettings().isDebug()) {
                LOGGER.log(Level.INFO, "Constructing new userfile from base player {0}", base.getName());
            }
            user = new User(base, this);
        } else {
            user.update(base);
        }
        return user;
    }

    private void handleCrash(final Throwable exception) {
        final PluginManager pm = getServer().getPluginManager();
        LOGGER.log(Level.SEVERE, exception.toString());
        exception.printStackTrace();
        pm.registerEvents(new Listener() {
            @EventHandler(priority = EventPriority.LOW)
            public void onPlayerJoin(final PlayerJoinEvent event) {
                event.getPlayer().sendMessage("Essentials failed to load, read the log file.");
            }
        }, this);
        for (final Player player : getOnlinePlayers()) {
            player.sendMessage("Essentials failed to load, read the log file.");
        }
        this.setEnabled(false);
    }

    @Override
    public World getWorld(final String name) {
        if (name.matches("[0-9]+")) {
            final int worldId = Integer.parseInt(name);
            if (worldId < getServer().getWorlds().size()) {
                return getServer().getWorlds().get(worldId);
            }
        }
        return getServer().getWorld(name);
    }

    @Override
    public void addReloadListener(final IConf listener) {
        confList.add(listener);
    }

    @Override
    public int broadcastMessage(final String message) {
        return broadcastMessage(null, null, message, true, u -> false);
    }

    @Override
    public int broadcastMessage(final IUser sender, final String message) {
        return broadcastMessage(sender, null, message, false, u -> false);
    }

    @Override
    public int broadcastMessage(final IUser sender, final String message, final Predicate<IUser> shouldExclude) {
        return broadcastMessage(sender, null, message, false, shouldExclude);
    }

    @Override
    public int broadcastMessage(final String permission, final String message) {
        return broadcastMessage(null, permission, message, false, u -> false);
    }

    private int broadcastMessage(final IUser sender, final String permission, final String message, final boolean keywords, final Predicate<IUser> shouldExclude) {
        if (sender != null && sender.isHidden()) {
            return 0;
        }

        IText broadcast = new SimpleTextInput(message);

        final Collection<Player> players = getOnlinePlayers();
        for (final Player player : players) {
            final User user = getUser(player);
            if ((permission == null && (sender == null || !user.isIgnoredPlayer(sender))) || (permission != null && user.isAuthorized(permission))) {
                if (shouldExclude.test(user)) {
                    continue;
                }
                if (keywords) {
                    broadcast = new KeywordReplacer(broadcast, new CommandSource(player), this, false);
                }
                for (final String messageText : broadcast.getLines()) {
                    user.sendMessage(messageText);
                }
            }
        }

        return players.size();
    }

    @Override
    public BukkitTask runTaskAsynchronously(final Runnable run) {
        return this.getScheduler().runTaskAsynchronously(this, run);
    }

    @Override
    public BukkitTask runTaskLaterAsynchronously(final Runnable run, final long delay) {
        return this.getScheduler().runTaskLaterAsynchronously(this, run, delay);
    }

    @Override
    public BukkitTask runTaskTimerAsynchronously(final Runnable run, final long delay, final long period) {
        return this.getScheduler().runTaskTimerAsynchronously(this, run, delay, period);
    }

    @Override
    public int scheduleSyncDelayedTask(final Runnable run) {
        return this.getScheduler().scheduleSyncDelayedTask(this, run);
    }

    @Override
    public int scheduleSyncDelayedTask(final Runnable run, final long delay) {
        return this.getScheduler().scheduleSyncDelayedTask(this, run, delay);
    }

    @Override
    public int scheduleSyncRepeatingTask(final Runnable run, final long delay, final long period) {
        return this.getScheduler().scheduleSyncRepeatingTask(this, run, delay, period);
    }

    @Override
    public TNTExplodeListener getTNTListener() {
        return tntListener;
    }

    @Override
    public PermissionsHandler getPermissionsHandler() {
        return permissionsHandler;
    }

    @Override
    public AlternativeCommandsHandler getAlternativeCommandsHandler() {
        return alternativeCommandsHandler;
    }

    @Override
    public IItemDb getItemDb() {
        return itemDb;
    }

    @Override
    public UserMap getUserMap() {
        return userMap;
    }

    @Override
    public I18n getI18n() {
        return i18n;
    }

    @Override
    public EssentialsTimer getTimer() {
        return timer;
    }

    @Override
    public List<String> getVanishedPlayers() {
        return Collections.unmodifiableList(new ArrayList<>(vanishedPlayers));
    }

    @Override
    public Collection<String> getVanishedPlayersNew() {
        return vanishedPlayers;
    }

    @Override
    public Collection<Player> getOnlinePlayers() {
        return (Collection<Player>) getServer().getOnlinePlayers();
    }

    @Override
    public Iterable<User> getOnlineUsers() {
        final List<User> onlineUsers = new ArrayList<>();
        for (final Player player : getOnlinePlayers()) {
            onlineUsers.add(getUser(player));
        }
        return onlineUsers;
    }

    @Override
    public SpawnerItemProvider getSpawnerItemProvider() {
        return spawnerItemProvider;
    }

    @Override
    public SpawnerBlockProvider getSpawnerBlockProvider() {
        return spawnerBlockProvider;
    }

    @Override
    public SpawnEggProvider getSpawnEggProvider() {
        return spawnEggProvider;
    }

    @Override
    public PotionMetaProvider getPotionMetaProvider() {
        return potionMetaProvider;
    }

    @Override
    public CustomItemResolver getCustomItemResolver() {
        return customItemResolver;
    }

    @Override
    public ServerStateProvider getServerStateProvider() {
        return serverStateProvider;
    }

    @Override
    public ContainerProvider getContainerProvider() {
        return containerProvider;
    }

    @Override
    public KnownCommandsProvider getKnownCommandsProvider() {
        return knownCommandsProvider;
    }

    private AbstractItemDb getItemDbFromConfig() {
        final String setting = settings.getItemDbType();

        if (setting.equalsIgnoreCase("json")) {
            return new FlatItemDb(this);
        } else if (setting.equalsIgnoreCase("csv")) {
            return new LegacyItemDb(this);
        } else {
            final VersionUtil.BukkitVersion version = VersionUtil.getServerBukkitVersion();

            if (version.isHigherThanOrEqualTo(VersionUtil.v1_13_0_R01)) {
                return new FlatItemDb(this);
            } else {
                return new LegacyItemDb(this);
            }
        }
    }

    private static class EssentialsWorldListener implements Listener, Runnable {
        private transient final IEssentials ess;

        EssentialsWorldListener(final IEssentials ess) {
            this.ess = ess;
        }

        @EventHandler(priority = EventPriority.LOW)
        public void onWorldLoad(final WorldLoadEvent event) {
            PermissionsDefaults.registerBackDefaultFor(event.getWorld());

            ess.getJails().onReload();
            ess.getWarps().reloadConfig();
            for (final IConf iConf : ((Essentials) ess).confList) {
                if (iConf instanceof IEssentialsModule) {
                    iConf.reloadConfig();
                }
            }
        }

        @EventHandler(priority = EventPriority.LOW)
        public void onWorldUnload(final WorldUnloadEvent event) {
            ess.getJails().onReload();
            ess.getWarps().reloadConfig();
            for (final IConf iConf : ((Essentials) ess).confList) {
                if (iConf instanceof IEssentialsModule) {
                    iConf.reloadConfig();
                }
            }
        }

        @Override
        public void run() {
            ess.reload();
        }
    }
}
