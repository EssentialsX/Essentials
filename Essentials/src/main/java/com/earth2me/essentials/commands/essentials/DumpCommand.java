package com.earth2me.essentials.commands.essentials;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.commands.EssentialsTreeNode;
import com.earth2me.essentials.economy.EconomyLayer;
import com.earth2me.essentials.economy.EconomyLayers;
import com.earth2me.essentials.utils.CommandMapUtil;
import com.earth2me.essentials.utils.DateUtil;
import com.earth2me.essentials.utils.PasteUtil;
import com.earth2me.essentials.utils.VersionUtil;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.ess3.provider.KnownCommandsProvider;
import net.ess3.provider.OnlineModeProvider;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.earth2me.essentials.I18n.tlLiteral;

public class DumpCommand extends EssentialsTreeNode {
    public DumpCommand() {
        super("dump");
    }

    @Override
    protected void run(CommandSource sender, String commandLabel, String[] args) throws Exception {
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
        serverData.addProperty("online-mode", ess.provider(OnlineModeProvider.class).getOnlineModeString());
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
            pluginData.addProperty("official", plugin == ess || VersionUtil.officialPlugins.contains(name));
            pluginData.addProperty("unsupported", VersionUtil.warnPlugins.contains(name));

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

        final Map<String, Command> knownCommandsCopy = new HashMap<>(ess.provider(KnownCommandsProvider.class).getKnownCommands());
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
                    final String dumpUrl = "https://essentialsx.net/dump?bytebin=" + result.getPasteId();
                    sender.sendTl("dumpUrl", dumpUrl);
                    // pastes.dev doesn't support deletion keys
                    //sender.sendTl("dumpDeleteKey", result.getDeletionKey());
                    if (sender.isPlayer()) {
                        ess.getLogger().info(ess.getAdventureFacet().miniToLegacy(tlLiteral("dumpConsoleUrl", dumpUrl)));
                        // pastes.dev doesn't support deletion keys
                        //ess.getLogger().info(AdventureUtil.miniToLegacy(tlLiteral("dumpDeleteKey", result.getDeletionKey())));
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

    @Override
    protected List<String> tabComplete(CommandSource sender, String commandLabel, String[] args) {
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
}
