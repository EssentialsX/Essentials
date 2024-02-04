package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.EssentialsUpgrade;
import com.earth2me.essentials.User;
import com.earth2me.essentials.craftbukkit.Inventories;
import com.earth2me.essentials.economy.EconomyLayer;
import com.earth2me.essentials.economy.EconomyLayers;
import com.earth2me.essentials.userstorage.ModernUserMap;
import com.earth2me.essentials.utils.AdventureUtil;
import com.earth2me.essentials.utils.CommandMapUtil;
import com.earth2me.essentials.utils.CommonPlaceholders;
import com.earth2me.essentials.utils.DateUtil;
import com.earth2me.essentials.utils.EnumUtil;
import com.earth2me.essentials.utils.FloatUtil;
import com.earth2me.essentials.utils.NumberUtil;
import com.earth2me.essentials.utils.PasteUtil;
import com.earth2me.essentials.utils.VersionUtil;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.ess3.api.IUser;
import net.ess3.api.TranslatableException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static com.earth2me.essentials.I18n.tlLiteral;

// This command has 4 undocumented behaviours #EasterEgg
public class Commandessentials extends EssentialsCommand {

    private static final Sound NOTE_HARP = EnumUtil.valueOf(Sound.class, "BLOCK_NOTE_BLOCK_HARP", "BLOCK_NOTE_HARP", "NOTE_PIANO");
    private static final Sound MOO_SOUND = EnumUtil.valueOf(Sound.class, "COW_IDLE", "ENTITY_COW_MILK");

    private static final String HOMES_USAGE = "/<command> homes (fix | delete [world])";

    private static final String NYAN_TUNE = "1D#,1E,2F#,,2A#,1E,1D#,1E,2F#,2B,2D#,2E,2D#,2A#,2B,,2F#,,1D#,1E,2F#,2B,2C#,2A#,2B,2C#,2E,2D#,2E,2C#,,2F#,,2G#,,1D,1D#,,1C#,1D,1C#,1B,,1B,,1C#,,1D,,1D,1C#,1B,1C#,1D#,2F#,2G#,1D#,2F#,1C#,1D#,1B,1C#,1B,1D#,,2F#,,2G#,1D#,2F#,1C#,1D#,1B,1D,1D#,1D,1C#,1B,1C#,1D,,1B,1C#,1D#,2F#,1C#,1D,1C#,1B,1C#,,1B,,1C#,,2F#,,2G#,,1D,1D#,,1C#,1D,1C#,1B,,1B,,1C#,,1D,,1D,1C#,1B,1C#,1D#,2F#,2G#,1D#,2F#,1C#,1D#,1B,1C#,1B,1D#,,2F#,,2G#,1D#,2F#,1C#,1D#,1B,1D,1D#,1D,1C#,1B,1C#,1D,,1B,1C#,1D#,2F#,1C#,1D,1C#,1B,1C#,,1B,,1B,,1B,,1F#,1G#,1B,,1F#,1G#,1B,1C#,1D#,1B,1E,1D#,1E,2F#,1B,,1B,,1F#,1G#,1B,1E,1D#,1C#,1B,,,,1F#,1B,,1F#,1G#,1B,,1F#,1G#,1B,1B,1C#,1D#,1B,1F#,1G#,1F#,1B,,1B,1A#,1B,1F#,1G#,1B,1E,1D#,1E,2F#,1B,,1A#,,1B,,1F#,1G#,1B,,1F#,1G#,1B,1C#,1D#,1B,1E,1D#,1E,2F#,1B,,1B,,1F#,1G#,1B,1F#,1E,1D#,1C#,1B,,,,1F#,1B,,1F#,1G#,1B,,1F#,1G#,1B,1B,1C#,1D#,1B,1F#,1G#,1F#,1B,,1B,1A#,1B,1F#,1G#,1B,1E,1D#,1E,2F#,1B,,1A#,,1B,,1F#,1G#,1B,,1F#,1G#,1B,1C#,1D#,1B,1E,1D#,1E,2F#,1B,,1B,,1F#,1G#,1B,1F#,1E,1D#,1C#,1B,,,,1F#,1B,,1F#,1G#,1B,,1F#,1G#,1B,1B,1C#,1D#,1B,1F#,1G#,1F#,1B,,1B,1A#,1B,1F#,1G#,1B,1E,1D#,1E,2F#,1B,,1A#,,1B,,1F#,1G#,1B,,1F#,1G#,1B,1C#,1D#,1B,1E,1D#,1E,2F#,1B,,1B,,1F#,1G#,1B,1F#,1E,1D#,1C#,1B,,,,1F#,1B,,1F#,1G#,1B,,1F#,1G#,1B,1B,1C#,1D#,1B,1F#,1G#,1F#,1B,,1B,1A#,1B,1F#,1G#,1B,1E,1D#,1E,2F#,1B,,1A#,,1B,,1F#,1G#,1B,,1F#,1G#,1B,1C#,1D#,1B,1E,1D#,1E,2F#,1B,,1B,,1F#,1G#,1B,1F#,1E,1D#,1C#,1B,,,,1F#,1B,,1F#,1G#,1B,,1F#,1G#,1B,1B,1C#,1D#,1B,1F#,1G#,1F#,1B,,1B,1A#,1B,1F#,1G#,1B,1E,1D#,1E,2F#,1B,,1B,,";
    private static final String[] CONSOLE_MOO = new String[] {"         (__)", "         (oo)", "   /------\\/", "  / |    ||", " *  /\\---/\\", "    ~~   ~~", "....\"Have you mooed today?\"..."};
    private static final String[] PLAYER_MOO = new String[] {"            (__)", "            (oo)", "   /------\\/", "  /  |      | |", " *  /\\---/\\", "    ~~    ~~", "....\"Have you mooed today?\"..."};
    private static final List<String> versionPlugins = Arrays.asList(
        "Vault", // API
        "Reserve", // API
        "PlaceholderAPI", // API
        "CMI", // potential for issues
        "Towny", // past issues; admins should ensure latest
        "ChestShop", // past issues; admins should ensure latest
        "Citizens", // fires player events
        "LuckPerms", // permissions (recommended)
        "UltraPermissions",
        "PermissionsEx", // permissions (unsupported)
        "GroupManager", // permissions (unsupported)
        "bPermissions", // permissions (unsupported)
        "DiscordSRV", // potential for issues if EssentialsXDiscord is installed

        // Chat signing bypass plugins that can potentially break EssentialsChat
        "AntiPopup",
        "NoChatReports",
        "NoEncryption"
    );
    private static final List<String> officialPlugins = Arrays.asList(
        "EssentialsAntiBuild",
        "EssentialsChat",
        "EssentialsDiscord",
        "EssentialsDiscordLink",
        "EssentialsGeoIP",
        "EssentialsProtect",
        "EssentialsSpawn",
        "EssentialsXMPP"
    );
    private static final List<String> warnPlugins = Arrays.asList(
        "PermissionsEx",
        "GroupManager",
        "bPermissions",

        // Brain-dead chat signing bypass that break EssentialsChat
        "NoChatReports",
        "NoEncryption"
    );
    private transient TuneRunnable currentTune = null;

    public Commandessentials() {
        super("essentials");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length == 0) {
            showUsage(sender);
        }

        switch (args[0]) {
            // Info commands
            case "debug":
            case "verbose":
                runDebug(server, sender, commandLabel, args);
                break;
            case "ver":
            case "version":
                runVersion(server, sender, commandLabel, args);
                break;
            case "cmd":
            case "commands":
                runCommands(server, sender, commandLabel, args);
                break;
            case "dump":
                runDump(server, sender, commandLabel, args);
                break;

            // Data commands
            case "reload":
                runReload(server, sender, commandLabel, args);
                break;
            case "reset":
                runReset(server, sender, commandLabel, args);
                break;
            case "cleanup":
                runCleanup(server, sender, commandLabel, args);
                break;
            case "homes":
                runHomes(server, sender, commandLabel, args);
                break;
            case "usermap":
                runUserMap(sender, args);
                break;

            case "itemtest":
                runItemTest(server, sender, commandLabel, args);
                break;

            // "#EasterEgg"
            case "nya":
            case "nyan":
                runNya(server, sender, commandLabel, args);
                break;
            case "moo":
                runMoo(server, sender, commandLabel, args);
                break;
            default:
                showUsage(sender);
                break;
        }
    }

    public void runItemTest(Server server, CommandSource sender, String commandLabel, String[] args) {
        if (!sender.isAuthorized("essentials.itemtest") || args.length < 2 || !sender.isPlayer()) {
            return;
        }

        final Player player = sender.getPlayer();
        assert player != null;

        switch (args[1]) {
            case "slot": {
                if (args.length < 3) {
                    return;
                }
                player.getInventory().setItem(Integer.parseInt(args[2]), new ItemStack(Material.DIRT));
                break;
            }
            case "overfill": {
                sender.sendMessage(Inventories.addItem(player, 42, false, new ItemStack(Material.DIAMOND_SWORD, 1), new ItemStack(Material.DIRT, 32), new ItemStack(Material.DIRT, 32)).toString());
                break;
            }
            case "overfill2": {
                if (args.length < 4) {
                    return;
                }
                final boolean armor = Boolean.parseBoolean(args[2]);
                final boolean add = Boolean.parseBoolean(args[3]);
                final ItemStack[] items = new ItemStack[]{new ItemStack(Material.DIAMOND_SWORD, 1), new ItemStack(Material.DIRT, 32), new ItemStack(Material.DIRT, 32), new ItemStack(Material.DIAMOND_HELMET, 4), new ItemStack(Material.CHAINMAIL_LEGGINGS, 1)};
                if (Inventories.hasSpace(player, 0, armor, items)) {
                    if (add) {
                        sender.sendMessage(Inventories.addItem(player, 0, armor, items).toString());
                    }
                    sender.sendMessage("SO MUCH SPACE!");
                } else {
                    sender.sendMessage("No space!");
                }
                break;
            }
            case "remove": {
                if (args.length < 3) {
                    return;
                }
                Inventories.removeItemExact(player, new ItemStack(Material.PUMPKIN, 1), Boolean.parseBoolean(args[2]));
                break;
            }
            default: {
                break;
            }
        }
    }

    // Displays the command's usage.
    private void showUsage(final CommandSource sender) throws Exception {
        throw new NotEnoughArgumentsException();
    }

    // Lists commands that are being handed over to other plugins.
    private void runCommands(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        if (ess.getAlternativeCommandsHandler().disabledCommands().size() == 0) {
            sender.sendTl("blockListEmpty");
            return;
        }

        sender.sendTl("blockList");
        for (final Map.Entry<String, String> entry : ess.getAlternativeCommandsHandler().disabledCommands().entrySet()) {
            sender.sendMessage(entry.getKey() + " => " + entry.getValue());
        }
    }

    // Generates a paste of useful information
    private void runDump(Server server, CommandSource sender, String commandLabel, String[] args) {
        sender.sendTl("dumpCreating");

        final JsonObject dump = new JsonObject();

        final JsonObject meta = new JsonObject();
        meta.addProperty("timestamp", Instant.now().toEpochMilli());
        meta.addProperty("sender", sender.getPlayer() != null ? sender.getPlayer().getName() : null);
        meta.addProperty("senderUuid", sender.getPlayer() != null ? sender.getPlayer().getUniqueId().toString() : null);
        dump.add("meta", meta);

        final JsonObject serverData = new JsonObject();
        serverData.addProperty("bukkit-version", Bukkit.getBukkitVersion());
        serverData.addProperty("server-version", Bukkit.getVersion());
        serverData.addProperty("server-brand", Bukkit.getName());
        serverData.addProperty("online-mode", ess.getOnlineModeProvider().getOnlineModeString());
        final JsonObject supportStatus = new JsonObject();
        final VersionUtil.SupportStatus status = VersionUtil.getServerSupportStatus();
        supportStatus.addProperty("status", status.name());
        supportStatus.addProperty("supported", status.isSupported());
        supportStatus.addProperty("trigger", VersionUtil.getSupportStatusClass());
        serverData.add("support-status", supportStatus);
        dump.add("server-data", serverData);

        final JsonObject environment = new JsonObject();
        environment.addProperty("java-version", System.getProperty("java.version"));
        environment.addProperty("operating-system", System.getProperty("os.name"));
        environment.addProperty("uptime", DateUtil.formatDateDiff(ManagementFactory.getRuntimeMXBean().getStartTime()));
        environment.addProperty("allocated-memory", (Runtime.getRuntime().totalMemory() / 1024 / 1024) + "MB");
        dump.add("environment", environment);

        final JsonObject essData = new JsonObject();
        essData.addProperty("version", ess.getDescription().getVersion());
        final JsonObject updateData = new JsonObject();
        updateData.addProperty("id", ess.getUpdateChecker().getVersionIdentifier());
        updateData.addProperty("branch", ess.getUpdateChecker().getVersionBranch());
        updateData.addProperty("dev", ess.getUpdateChecker().isDevBuild());
        essData.add("update-data", updateData);
        final JsonObject econLayer = new JsonObject();
        econLayer.addProperty("enabled", !ess.getSettings().isEcoDisabled());
        econLayer.addProperty("selected-layer", EconomyLayers.isLayerSelected());
        final EconomyLayer layer = EconomyLayers.getSelectedLayer();
        econLayer.addProperty("name", layer == null ? "null" : layer.getName());
        econLayer.addProperty("layer-version", layer == null ? "null" : layer.getPluginVersion());
        econLayer.addProperty("backend-name", layer == null ? "null" : layer.getBackendName());
        essData.add("economy-layer", econLayer);
        final JsonArray addons = new JsonArray();
        final JsonArray plugins = new JsonArray();
        final ArrayList<Plugin> alphabetical = new ArrayList<>();
        Collections.addAll(alphabetical, Bukkit.getPluginManager().getPlugins());
        alphabetical.sort(Comparator.comparing(o -> o.getName().toUpperCase(Locale.ENGLISH)));
        for (final Plugin plugin : alphabetical) {
            final JsonObject pluginData = new JsonObject();
            final PluginDescriptionFile info = plugin.getDescription();
            final String name = info.getName();

            pluginData.addProperty("name", name);
            pluginData.addProperty("version", info.getVersion());
            pluginData.addProperty("description", info.getDescription());
            pluginData.addProperty("main", info.getMain());
            pluginData.addProperty("enabled", plugin.isEnabled());
            pluginData.addProperty("official", plugin == ess || officialPlugins.contains(name));
            pluginData.addProperty("unsupported", warnPlugins.contains(name));

            final JsonArray authors = new JsonArray();
            for (final String author : info.getAuthors()) {
                authors.add(author == null ? JsonNull.INSTANCE : new JsonPrimitive(author));
            }
            pluginData.add("authors", authors);

            if (name.startsWith("Essentials") && !name.equals("Essentials")) {
                addons.add(pluginData);
            }
            plugins.add(pluginData);
        }
        essData.add("addons", addons);
        dump.add("ess-data", essData);
        dump.add("plugins", plugins);

        final List<PasteUtil.PasteFile> files = new ArrayList<>();
        files.add(new PasteUtil.PasteFile("dump.json", dump.toString()));

        final Plugin essDiscord = Bukkit.getPluginManager().getPlugin("EssentialsDiscord");
        final Plugin essDiscordLink = Bukkit.getPluginManager().getPlugin("EssentialsDiscordLink");
        final Plugin essSpawn = Bukkit.getPluginManager().getPlugin("EssentialsSpawn");

        final Map<String, Command> knownCommandsCopy = new HashMap<>(ess.getKnownCommandsProvider().getKnownCommands());
        final Map<String, String> disabledCommandsCopy = new HashMap<>(ess.getAlternativeCommandsHandler().disabledCommands());

        // Further operations will be heavy IO
        ess.runTaskAsynchronously(() -> {
            boolean config = false;
            boolean discord = false;
            boolean kits = false;
            boolean log = false;
            boolean worth = false;
            boolean tpr = false;
            boolean spawns = false;
            boolean commands = false;
            for (final String arg : args) {
                if (arg.equals("*") || arg.equalsIgnoreCase("all")) {
                    config = true;
                    discord = true;
                    kits = true;
                    log = true;
                    worth = true;
                    tpr = true;
                    spawns = true;
                    commands = true;
                    break;
                } else if (arg.equalsIgnoreCase("config")) {
                    config = true;
                } else if (arg.equalsIgnoreCase("discord")) {
                    discord = true;
                } else if (arg.equalsIgnoreCase("kits")) {
                    kits = true;
                } else if (arg.equalsIgnoreCase("log")) {
                    log = true;
                } else if (arg.equalsIgnoreCase("worth")) {
                    worth = true;
                } else if (arg.equalsIgnoreCase("tpr")) {
                    tpr = true;
                } else if (arg.equalsIgnoreCase("spawns")) {
                    spawns = true;
                } else if (arg.equalsIgnoreCase("commands")) {
                    commands = true;
                }
            }

            if (config) {
                try {
                    files.add(new PasteUtil.PasteFile("config.yml", new String(Files.readAllBytes(ess.getSettings().getConfigFile().toPath()), StandardCharsets.UTF_8)));
                } catch (IOException e) {
                    sender.sendTl("dumpErrorUpload", "config.yml", e.getMessage());
                }
            }

            if (discord && essDiscord != null) {
                try {
                    files.add(new PasteUtil.PasteFile("discord-config.yml",
                            new String(Files.readAllBytes(essDiscord.getDataFolder().toPath().resolve("config.yml")), StandardCharsets.UTF_8)
                                    .replaceAll("[A-Za-z\\d]{24}\\.[\\w-]{6}\\.[\\w-]{27}", "<censored token>")));
                } catch (IOException e) {
                    sender.sendTl("dumpErrorUpload", "discord-config.yml", e.getMessage());
                }

                if (essDiscordLink != null) {
                    try {
                        files.add(new PasteUtil.PasteFile("discord-link-config.yml",
                                new String(Files.readAllBytes(essDiscordLink.getDataFolder().toPath().resolve("config.yml")), StandardCharsets.UTF_8)));
                    } catch (IOException e) {
                        sender.sendTl("dumpErrorUpload", "discord-link-config.yml", e.getMessage());
                    }
                }
            }

            if (kits) {
                try {
                    files.add(new PasteUtil.PasteFile("kits.yml", new String(Files.readAllBytes(ess.getKits().getFile().toPath()), StandardCharsets.UTF_8)));
                } catch (IOException e) {
                    sender.sendTl("dumpErrorUpload", "kits.yml", e.getMessage());
                }
            }

            if (log) {
                try {
                    files.add(new PasteUtil.PasteFile("latest.log", new String(Files.readAllBytes(Paths.get("logs", "latest.log")), StandardCharsets.UTF_8)
                            .replaceAll("(?m)^\\[\\d\\d:\\d\\d:\\d\\d] \\[.+/(?:DEBUG|TRACE)]: .+\\s(?:[A-Za-z.]+:.+\\s(?:\\t.+\\s)*)?\\s*(?:\"[A-Za-z]+\" : .+[\\s}\\]]+)*", "")
                            .replaceAll("(?:[0-9]{1,3}\\.){3}[0-9]{1,3}", "<censored ip address>")));
                } catch (IOException e) {
                    sender.sendTl("dumpErrorUpload", "latest.log", e.getMessage());
                }
            }

            if (worth) {
                try {
                    files.add(new PasteUtil.PasteFile("worth.yml", new String(Files.readAllBytes(ess.getWorth().getFile().toPath()), StandardCharsets.UTF_8)));
                } catch (IOException e) {
                    sender.sendTl("dumpErrorUpload", "worth.yml", e.getMessage());
                }
            }

            if (tpr) {
                try {
                    files.add(new PasteUtil.PasteFile("tpr.yml", new String(Files.readAllBytes(ess.getRandomTeleport().getFile().toPath()), StandardCharsets.UTF_8)));
                } catch (IOException e) {
                    sender.sendTl("dumpErrorUpload", "tpr.yml", e.getMessage());
                }
            }

            if (spawns && essSpawn != null) {
                try {
                    files.add(new PasteUtil.PasteFile("spawn.yml", new String(Files.readAllBytes(ess.getDataFolder().toPath().resolve("spawn.yml")), StandardCharsets.UTF_8)));
                } catch (IOException e) {
                    sender.sendTl("dumpErrorUpload", "spawn.yml", e.getMessage());
                }
            }

            if (commands) {
                try {
                    files.add(new PasteUtil.PasteFile("commands.yml", new String(Files.readAllBytes(Paths.get("commands.yml")), StandardCharsets.UTF_8)));
                    files.add(new PasteUtil.PasteFile("commandmap.json", CommandMapUtil.toJsonPretty(ess, knownCommandsCopy)));
                    files.add(new PasteUtil.PasteFile("commandoverride.json", disabledCommandsCopy.toString()));
                } catch (IOException e) {
                    sender.sendTl("dumpErrorUpload", "commands.yml", e.getMessage());
                }
            }

            final CompletableFuture<PasteUtil.PasteResult> future = PasteUtil.createPaste(files);
            future.thenAccept(result -> {
                if (result != null) {
                    final String dumpUrl = "https://essentialsx.net/dump.html?id=" + result.getPasteId();
                    sender.sendTl("dumpUrl", dumpUrl);
                    sender.sendTl("dumpDeleteKey", result.getDeletionKey());
                    if (sender.isPlayer()) {
                        ess.getLogger().info(AdventureUtil.miniToLegacy(tlLiteral("dumpConsoleUrl", dumpUrl)));
                        ess.getLogger().info(AdventureUtil.miniToLegacy(tlLiteral("dumpDeleteKey", result.getDeletionKey())));
                    }
                }
                files.clear();
            });
            future.exceptionally(throwable -> {
                sender.sendTl("dumpError", throwable.getMessage());
                return null;
            });
        });
    }

    // Resets the given player's user data.
    private void runReset(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 2) {
            throw new Exception("/<command> reset <player>");
        }
        final User user = getPlayer(server, args, 1, true, true);
        user.reset();
        sender.sendMessage("Reset Essentials userdata for player: " + CommonPlaceholders.displayName((IUser) user));
    }

    // Toggles debug mode.
    private void runDebug(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        ess.getSettings().setDebug(!ess.getSettings().isDebug());
        sender.sendMessage("Essentials " + ess.getDescription().getVersion() + " debug mode " + (ess.getSettings().isDebug() ? "enabled" : "disabled"));
    }

    // Reloads all reloadable configs.
    private void runReload(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        ess.reload();
        sender.sendTl("essentialsReload", ess.getDescription().getVersion());
    }

    // Pop tarts.
    private void runNya(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (currentTune != null) {
            currentTune.cancel();
        }

        currentTune = new TuneRunnable(NYAN_TUNE, NOTE_HARP, ess::getOnlinePlayers);
        currentTune.runTaskTimer(ess, 20, 2);
    }

    // Cow farts.
    private void runMoo(final Server server, final CommandSource sender, final String command, final String[] args) {
        if (args.length == 2 && args[1].equals("moo")) {
            for (final String s : CONSOLE_MOO) {
                ess.getLogger().info(s);
            }
            for (final Player player : ess.getOnlinePlayers()) {
                player.sendMessage(PLAYER_MOO);
                player.playSound(player.getLocation(), MOO_SOUND, 1, 1.0f);
            }
        } else {
            if (sender.isPlayer()) {
                sender.getSender().sendMessage(PLAYER_MOO);
                final Player player = sender.getPlayer();
                player.playSound(player.getLocation(), MOO_SOUND, 1, 1.0f);

            } else {
                sender.getSender().sendMessage(CONSOLE_MOO);
            }
        }
    }

    // Cleans up inactive users.
    private void runCleanup(final Server server, final CommandSource sender, final String command, final String[] args) throws Exception {
        if (args.length < 2 || !NumberUtil.isInt(args[1])) {
            sender.sendMessage("This sub-command will delete users who haven't logged in in the last <days> days.");
            sender.sendMessage("Optional parameters define the minimum amount required to prevent deletion.");
            sender.sendMessage("Unless you define larger default values, this command will ignore people who have more than 0 money/homes.");
            throw new Exception("/<command> cleanup <days> [money] [homes]");
        }

        sender.sendTl("cleaning");

        final long daysArg = Long.parseLong(args[1]);
        final double moneyArg = args.length >= 3 ? FloatUtil.parseDouble(args[2].replaceAll("[^0-9.]", "")) : 0;
        final int homesArg = args.length >= 4 && NumberUtil.isInt(args[3]) ? Integer.parseInt(args[3]) : 0;

        ess.runTaskAsynchronously(() -> {
            final long currTime = System.currentTimeMillis();
            for (final UUID u : ess.getUsers().getAllUserUUIDs()) {
                final User user = ess.getUsers().loadUncachedUser(u);
                if (user == null) {
                    continue;
                }

                long lastLog = user.getLastLogout();
                if (lastLog == 0) {
                    lastLog = user.getLastLogin();
                }
                if (lastLog == 0) {
                    user.setLastLogin(currTime);
                }

                if (user.isNPC()) {
                    continue;
                }

                final long timeDiff = currTime - lastLog;
                final long milliDays = daysArg * 24L * 60L * 60L * 1000L;
                final int homeCount = user.getHomes().size();
                final double moneyCount = user.getMoney().doubleValue();

                if ((lastLog == 0) || (timeDiff < milliDays) || (homeCount > homesArg) || (moneyCount > moneyArg)) {
                    continue;
                }

                if (ess.getSettings().isDebug()) {
                    ess.getLogger().info("Deleting user: " + user.getName() + " Money: " + moneyCount + " Homes: " + homeCount + " Last seen: " + DateUtil.formatDateDiff(lastLog));
                }

                user.reset();
            }
            sender.sendTl("cleaned");
        });
    }

    private void runHomes(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 2) {
            sender.sendMessage("This sub-command provides a utility to mass-delete homes based on user options:");
            sender.sendMessage("Use \"fix\" to delete all homes inside non-existent or unloaded worlds.");
            sender.sendMessage("Use \"delete\" to delete all existing homes.");
            sender.sendMessage("Use \"delete <worldname>\" to delete all homes inside a specific world.");
            throw new Exception(HOMES_USAGE);
        }

        switch (args[1]) {
            case "fix":
                sender.sendTl("fixingHomes");
                ess.runTaskAsynchronously(() -> {
                    for (final UUID u : ess.getUsers().getAllUserUUIDs()) {
                        final User user = ess.getUsers().loadUncachedUser(u);
                        if (user == null) {
                            continue;
                        }
                        for (String homeName : user.getHomes()) {
                            try {
                                if (user.getHome(homeName) == null) {
                                    user.delHome(homeName);
                                }
                            } catch (Exception e) {
                                ess.getLogger().info("Unable to delete home " + homeName + " for " + user.getName());
                            }
                        }
                    }
                    sender.sendTl("fixedHomes");
                });
                break;
            case "delete":
                final boolean filterByWorld = args.length >= 3;
                if (filterByWorld && server.getWorld(args[2]) == null) {
                    throw new TranslatableException("invalidWorld");
                }
                if (filterByWorld) {
                    sender.sendTl("deletingHomesWorld", args[2]);
                } else {
                    sender.sendTl("deletingHomes");
                }
                ess.runTaskAsynchronously(() -> {
                    for (final UUID u : ess.getUsers().getAllUserUUIDs()) {
                        final User user = ess.getUsers().loadUncachedUser(u);
                        if (user == null) {
                            continue;
                        }
                        for (String homeName : user.getHomes()) {
                            try {
                                final Location home = user.getHome(homeName);
                                if (!filterByWorld || (home != null && home.getWorld() != null && home.getWorld().getName().equals(args[2]))) {
                                    user.delHome(homeName);
                                }
                            } catch (Exception e) {
                                ess.getLogger().info("Unable to delete home " + homeName + " for " + user.getName());
                            }
                        }
                    }

                    if (filterByWorld) {
                        sender.sendTl("deletedHomesWorld", args[2]);
                    } else {
                        sender.sendTl("deletedHomes");
                    }
                });
                break;
            default:
                throw new Exception(HOMES_USAGE);
        }
    }

    // Gets information about cached users
    private void runUserMap(final CommandSource sender, final String[] args) {
        if (!sender.isAuthorized("essentials.usermap")) {
            return;
        }

        final ModernUserMap userMap = (ModernUserMap) ess.getUsers();
        sender.sendTl("usermapSize", userMap.getCachedCount(), userMap.getUserCount(), ess.getSettings().getMaxUserCacheCount());
        if (args.length > 1) {
            if (args[1].equals("full")) {
                for (final Map.Entry<String, UUID> entry : userMap.getNameCache().entrySet()) {
                    sender.sendTl("usermapEntry", entry.getKey(), entry.getValue().toString());
                }
            } else if (args[1].equals("purge")) {
                final boolean seppuku = args.length > 2 && args[2].equals("iknowwhatimdoing");

                sender.sendTl("usermapPurge", String.valueOf(seppuku));

                final Set<UUID> uuids = new HashSet<>(ess.getUsers().getAllUserUUIDs());
                ess.runTaskAsynchronously(() -> {
                    final File userdataFolder = new File(ess.getDataFolder(), "userdata");
                    final File backupFolder = new File(ess.getDataFolder(), "userdata-npc-backup-boogaloo-" + System.currentTimeMillis());

                    if (!userdataFolder.isDirectory()) {
                        ess.getLogger().warning("Missing userdata folder, aborting usermap purge.");
                        return;
                    }

                    if (seppuku && !backupFolder.mkdir()) {
                        ess.getLogger().warning("Unable to create backup folder, aborting usermap purge.");
                        return;
                    }

                    int total = 0;
                    final File[] files = userdataFolder.listFiles(EssentialsUpgrade.YML_FILTER);
                    if (files != null) {
                        for (final File file : files) {
                            try {
                                final String fileName = file.getName();
                                final UUID uuid = UUID.fromString(fileName.substring(0, fileName.length() - 4));
                                if (!uuids.contains(uuid)) {
                                    total++;
                                    ess.getLogger().warning("Found orphaned userdata file: " + file.getName());
                                    if (seppuku) {
                                        try {
                                            com.google.common.io.Files.move(file, new File(backupFolder, file.getName()));
                                        } catch (IOException e) {
                                            ess.getLogger().log(Level.WARNING, "Unable to move orphaned userdata file: " + file.getName(), e);
                                        }
                                    }
                                }
                            } catch (IllegalArgumentException ignored) {
                            }
                        }
                    }
                    ess.getLogger().info("Found " + total + " orphaned userdata files.");
                });
            } else if (args[1].equalsIgnoreCase("cache")) {
                sender.sendTl("usermapKnown", ess.getUsers().getAllUserUUIDs().size(), ess.getUsers().getNameCache().size());
            } else {
                try {
                    final UUID uuid = UUID.fromString(args[1]);
                    for (final Map.Entry<String, UUID> entry : userMap.getNameCache().entrySet()) {
                        if (entry.getValue().equals(uuid)) {
                            sender.sendTl("usermapEntry", entry.getKey(), args[1]);
                        }
                    }
                } catch (IllegalArgumentException ignored) {
                    final String sanitizedName = userMap.getSanitizedName(args[1]);
                    sender.sendTl("usermapEntry", sanitizedName, userMap.getNameCache().get(sanitizedName).toString());
                }
            }
        }
    }

    // Displays versions of EssentialsX and related plugins.
    private void runVersion(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (sender.isPlayer() && !ess.getUser(sender.getPlayer()).isAuthorized("essentials.version")) return;

        boolean isMismatched = false;
        boolean isVaultInstalled = false;
        boolean isUnsupported = false;
        final VersionUtil.SupportStatus supportStatus = VersionUtil.getServerSupportStatus();
        final PluginManager pm = server.getPluginManager();
        final String essVer = pm.getPlugin("Essentials").getDescription().getVersion();

        final String serverMessageKey;
        if (supportStatus.isSupported()) {
            serverMessageKey = "versionOutputFine";
        } else if (supportStatus == VersionUtil.SupportStatus.UNSTABLE) {
            serverMessageKey = "versionOutputUnsupported";
        } else {
            serverMessageKey = "versionOutputWarn";
        }

        sender.sendTl(serverMessageKey, "Server", server.getBukkitVersion() + " " + server.getVersion());
        sender.sendTl(serverMessageKey, "Brand", server.getName());
        sender.sendTl("versionOutputFine", "EssentialsX", essVer);

        for (final Plugin plugin : pm.getPlugins()) {
            final PluginDescriptionFile desc = plugin.getDescription();
            String name = desc.getName();
            final String version = desc.getVersion();

            if (name.startsWith("Essentials") && !name.equalsIgnoreCase("Essentials")) {
                if (officialPlugins.contains(name)) {
                    name = name.replace("Essentials", "EssentialsX");

                    if (!version.equalsIgnoreCase(essVer)) {
                        isMismatched = true;
                        sender.sendTl("versionOutputWarn", name, version);
                    } else {
                        sender.sendTl("versionOutputFine", name, version);
                    }
                } else {
                    sender.sendTl("versionOutputUnsupported", name, version);
                    isUnsupported = true;
                }
            }

            if (versionPlugins.contains(name)) {
                if (warnPlugins.contains(name)) {
                    sender.sendTl("versionOutputUnsupported", name, version);
                    isUnsupported = true;
                } else {
                    sender.sendTl("versionOutputFine", name, version);
                }
            }

            if (name.equals("Vault")) isVaultInstalled = true;
        }

        final String layer;
        if (ess.getSettings().isEcoDisabled()) {
            layer = "Disabled";
        } else if (EconomyLayers.isLayerSelected()) {
            final EconomyLayer economyLayer = EconomyLayers.getSelectedLayer();
            layer = economyLayer.getName() + " (" + economyLayer.getBackendName() + ")";
        } else {
            layer = "None";
        }
        sender.sendTl("versionOutputEconLayer", layer);

        if (isMismatched) {
            sender.sendTl("versionMismatchAll");
        }

        if (!isVaultInstalled) {
            sender.sendTl("versionOutputVaultMissing");
        }

        if (isUnsupported) {
            sender.sendTl("versionOutputUnsupportedPlugins");
        }

        switch (supportStatus) {
            case NMS_CLEANROOM:
                sender.sendComponent(sender.tlComponent("serverUnsupportedCleanroom").color(NamedTextColor.DARK_RED));
                break;
            case DANGEROUS_FORK:
                sender.sendComponent(sender.tlComponent("serverUnsupportedDangerous").color(NamedTextColor.DARK_RED));
                break;
            case STUPID_PLUGIN:
                sender.sendComponent(sender.tlComponent("serverUnsupportedDumbPlugins").color(NamedTextColor.DARK_RED));
                break;
            case UNSTABLE:
                sender.sendComponent(sender.tlComponent("serverUnsupportedMods").color(NamedTextColor.DARK_RED));
                break;
            case OUTDATED:
                sender.sendComponent(sender.tlComponent("serverUnsupported").color(NamedTextColor.RED));
                break;
            case LIMITED:
                sender.sendComponent(sender.tlComponent("serverUnsupportedLimitedApi").color(NamedTextColor.RED));
                break;
        }
        if (VersionUtil.getSupportStatusClass() != null) {
            sender.sendComponent(sender.tlComponent("serverUnsupportedClass").color(NamedTextColor.RED));
        }

        sender.sendTl("versionFetching");
        ess.runTaskAsynchronously(() -> {
            for (final Component component : ess.getUpdateChecker().getVersionMessages(true, true, sender)) {
                sender.sendComponent(component);
            }
        });
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            final List<String> options = Lists.newArrayList();
            options.add("reload");
            options.add("version");
            options.add("dump");
            options.add("commands");
            options.add("debug");
            options.add("reset");
            options.add("cleanup");
            options.add("homes");
            //options.add("uuidconvert");
            //options.add("nya");
            //options.add("moo");
            return options;
        }

        switch (args[0]) {
            case "moo":
                if (args.length == 2) {
                    return Lists.newArrayList("moo");
                }
                break;
            case "reset":
                if (args.length == 2) {
                    return getPlayers(server, sender);
                }
                break;
            case "cleanup":
                if (args.length == 2) {
                    return COMMON_DURATIONS;
                } else if (args.length == 3 || args.length == 4) {
                    return Lists.newArrayList("-1", "0");
                }
                break;
            case "homes":
                if (args.length == 2) {
                    return Lists.newArrayList("fix", "delete");
                } else if (args.length == 3 && args[1].equalsIgnoreCase("delete")) {
                    return server.getWorlds().stream().map(World::getName).collect(Collectors.toList());
                }
                break;
            case "dump":
                final List<String> list = Lists.newArrayList("config", "kits", "log", "discord", "worth", "tpr", "spawns", "commands", "all");
                for (String arg : args) {
                    if (arg.equals("*") || arg.equalsIgnoreCase("all")) {
                        list.clear();
                        return list;
                    }
                    list.remove(arg.toLowerCase(Locale.ENGLISH));
                }
                return list;
        }

        return Collections.emptyList();
    }

    private static class TuneRunnable extends BukkitRunnable {
        private static final Map<String, Float> noteMap = ImmutableMap.<String, Float>builder()
            .put("1F#", 0.5f)
            .put("1G", 0.53f)
            .put("1G#", 0.56f)
            .put("1A", 0.6f)
            .put("1A#", 0.63f)
            .put("1B", 0.67f)
            .put("1C", 0.7f)
            .put("1C#", 0.76f)
            .put("1D", 0.8f)
            .put("1D#", 0.84f)
            .put("1E", 0.9f)
            .put("1F", 0.94f)
            .put("2F#", 1.0f)
            .put("2G", 1.06f)
            .put("2G#", 1.12f)
            .put("2A", 1.18f)
            .put("2A#", 1.26f)
            .put("2B", 1.34f)
            .put("2C", 1.42f)
            .put("2C#", 1.5f)
            .put("2D", 1.6f)
            .put("2D#", 1.68f)
            .put("2E", 1.78f)
            .put("2F", 1.88f)
            .build();

        private final String[] tune;
        private final Sound sound;
        private final Supplier<Collection<Player>> players;
        private int i = 0;

        TuneRunnable(final String tuneStr, final Sound sound, final Supplier<Collection<Player>> players) {
            this.tune = tuneStr.split(",");
            this.sound = sound;
            this.players = players;
        }

        @Override
        public void run() {
            final String note = tune[i];
            i++;
            if (i >= tune.length) {
                cancel();
            }
            if (note == null || note.isEmpty()) {
                return;
            }

            for (final Player onlinePlayer : players.get()) {
                onlinePlayer.playSound(onlinePlayer.getLocation(), sound, 1, noteMap.get(note));
            }
        }
    }
}
