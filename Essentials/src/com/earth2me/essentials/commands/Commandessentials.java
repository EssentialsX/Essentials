package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.EssentialsUpgrade;
import com.earth2me.essentials.User;
import com.earth2me.essentials.UserMap;
import com.earth2me.essentials.utils.*;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.function.Supplier;

import static com.earth2me.essentials.I18n.tl;

// This command has 4 undocumented behaviours #EasterEgg
public class Commandessentials extends EssentialsCommand {

    private static final Sound NOTE_HARP = EnumUtil.valueOf(Sound.class, "BLOCK_NOTE_BLOCK_HARP", "BLOCK_NOTE_HARP", "NOTE_PIANO");
    private static final Sound MOO_SOUND = EnumUtil.valueOf(Sound.class, "COW_IDLE", "ENTITY_COW_MILK");

    private static final String NYAN_TUNE = "1D#,1E,2F#,,2A#,1E,1D#,1E,2F#,2B,2D#,2E,2D#,2A#,2B,,2F#,,1D#,1E,2F#,2B,2C#,2A#,2B,2C#,2E,2D#,2E,2C#,,2F#,,2G#,,1D,1D#,,1C#,1D,1C#,1B,,1B,,1C#,,1D,,1D,1C#,1B,1C#,1D#,2F#,2G#,1D#,2F#,1C#,1D#,1B,1C#,1B,1D#,,2F#,,2G#,1D#,2F#,1C#,1D#,1B,1D,1D#,1D,1C#,1B,1C#,1D,,1B,1C#,1D#,2F#,1C#,1D,1C#,1B,1C#,,1B,,1C#,,2F#,,2G#,,1D,1D#,,1C#,1D,1C#,1B,,1B,,1C#,,1D,,1D,1C#,1B,1C#,1D#,2F#,2G#,1D#,2F#,1C#,1D#,1B,1C#,1B,1D#,,2F#,,2G#,1D#,2F#,1C#,1D#,1B,1D,1D#,1D,1C#,1B,1C#,1D,,1B,1C#,1D#,2F#,1C#,1D,1C#,1B,1C#,,1B,,1B,,1B,,1F#,1G#,1B,,1F#,1G#,1B,1C#,1D#,1B,1E,1D#,1E,2F#,1B,,1B,,1F#,1G#,1B,1E,1D#,1C#,1B,,,,1F#,1B,,1F#,1G#,1B,,1F#,1G#,1B,1B,1C#,1D#,1B,1F#,1G#,1F#,1B,,1B,1A#,1B,1F#,1G#,1B,1E,1D#,1E,2F#,1B,,1A#,,1B,,1F#,1G#,1B,,1F#,1G#,1B,1C#,1D#,1B,1E,1D#,1E,2F#,1B,,1B,,1F#,1G#,1B,1F#,1E,1D#,1C#,1B,,,,1F#,1B,,1F#,1G#,1B,,1F#,1G#,1B,1B,1C#,1D#,1B,1F#,1G#,1F#,1B,,1B,1A#,1B,1F#,1G#,1B,1E,1D#,1E,2F#,1B,,1A#,,1B,,1F#,1G#,1B,,1F#,1G#,1B,1C#,1D#,1B,1E,1D#,1E,2F#,1B,,1B,,1F#,1G#,1B,1F#,1E,1D#,1C#,1B,,,,1F#,1B,,1F#,1G#,1B,,1F#,1G#,1B,1B,1C#,1D#,1B,1F#,1G#,1F#,1B,,1B,1A#,1B,1F#,1G#,1B,1E,1D#,1E,2F#,1B,,1A#,,1B,,1F#,1G#,1B,,1F#,1G#,1B,1C#,1D#,1B,1E,1D#,1E,2F#,1B,,1B,,1F#,1G#,1B,1F#,1E,1D#,1C#,1B,,,,1F#,1B,,1F#,1G#,1B,,1F#,1G#,1B,1B,1C#,1D#,1B,1F#,1G#,1F#,1B,,1B,1A#,1B,1F#,1G#,1B,1E,1D#,1E,2F#,1B,,1A#,,1B,,1F#,1G#,1B,,1F#,1G#,1B,1C#,1D#,1B,1E,1D#,1E,2F#,1B,,1B,,1F#,1G#,1B,1F#,1E,1D#,1C#,1B,,,,1F#,1B,,1F#,1G#,1B,,1F#,1G#,1B,1B,1C#,1D#,1B,1F#,1G#,1F#,1B,,1B,1A#,1B,1F#,1G#,1B,1E,1D#,1E,2F#,1B,,1B,,";
    private static final String[] CONSOLE_MOO = new String[]{"         (__)", "         (oo)", "   /------\\/", "  / |    ||", " *  /\\---/\\", "    ~~   ~~", "....\"Have you mooed today?\"..."};
    private static final String[] PLAYER_MOO = new String[]{"            (__)", "            (oo)", "   /------\\/", "  /  |      | |", " *  /\\---/\\", "    ~~    ~~", "....\"Have you mooed today?\"..."};

    public Commandessentials() {
        super("essentials");
    }

    private transient TuneRunnable currentTune = null;

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
        "bPermissions" // permissions (unsupported)
    );

    private static final List<String> officialPlugins = Arrays.asList(
        "EssentialsAntiBuild",
        "EssentialsChat",
        "EssentialsGeoIP",
        "EssentialsProtect",
        "EssentialsSpawn",
        "EssentialsXMPP"
    );

    private static final List<String> warnPlugins = Arrays.asList(
        "PermissionsEx",
        "GroupManager",
        "bPremissions"
    );

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length == 0) {
            showUsage(sender);
        }

        switch(args[0]) {
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
            case "uuidconvert":
                runUUIDConvert(server, sender, commandLabel, args);
                break;
            case "uuidtest":
                runUUIDTest(server, sender, commandLabel, args);
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
        }
    }

    // Displays the command's usage.
    private void showUsage(final CommandSource sender) throws Exception {
        throw new NotEnoughArgumentsException("/<command> <reload/debug/commands>");
    }

    // Lists commands that are being handed over to other plugins.
    private void runCommands(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        final StringBuilder disabledCommands = new StringBuilder();
        for (Map.Entry<String, String> entry : ess.getAlternativeCommandsHandler().disabledCommands().entrySet()) {
            if (disabledCommands.length() > 0) {
                disabledCommands.append("\n");
            }
            disabledCommands.append(entry.getKey()).append(" => ").append(entry.getValue());
        }
        if (disabledCommands.length() > 0) {
            sender.sendMessage(tl("blockList"));
            sender.sendMessage(disabledCommands.toString());
        } else {
            sender.sendMessage(tl("blockListEmpty"));
        }
    }

    // Resets the given player's user data.
    private void runReset(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 2) {
            throw new Exception("/<command> reset <player>");
        }
        final User user = getPlayer(server, args, 1, true, true);
        user.reset();
        sender.sendMessage("Reset Essentials userdata for player: " + user.getDisplayName());
    }

    // Toggles debug mode.
    private void runDebug(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        ess.getSettings().setDebug(!ess.getSettings().isDebug());
        sender.sendMessage("Essentials " + ess.getDescription().getVersion() + " debug mode " + (ess.getSettings().isDebug() ? "enabled" : "disabled"));
    }

    // Reloads all reloadable configs.
    private void runReload(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        ess.reload();
        sender.sendMessage(tl("essentialsReload", ess.getDescription().getVersion()));
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
            for (String s : CONSOLE_MOO) {
                logger.info(s);
            }
            for (Player player : ess.getOnlinePlayers()) {
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

        sender.sendMessage(tl("cleaning"));

        final long daysArg = Long.parseLong(args[1]);
        final double moneyArg = args.length >= 3 ? FloatUtil.parseDouble(args[2].replaceAll("[^0-9\\.]", "")) : 0;
        final int homesArg = args.length >= 4 && NumberUtil.isInt(args[3]) ? Integer.parseInt(args[3]) : 0;
        final UserMap userMap = ess.getUserMap();

        ess.runTaskAsynchronously(() -> {
            long currTime = System.currentTimeMillis();
            for (UUID u : userMap.getAllUniqueUsers()) {
                final User user = ess.getUserMap().getUser(u);
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

                long timeDiff = currTime - lastLog;
                long milliDays = daysArg * 24L * 60L * 60L * 1000L;
                int homeCount = user.getHomes().size();
                double moneyCount = user.getMoney().doubleValue();

                if ((lastLog == 0) || (timeDiff < milliDays) || (homeCount > homesArg) || (moneyCount > moneyArg)) {
                    continue;
                }

                if (ess.getSettings().isDebug()) {
                    ess.getLogger().info("Deleting user: " + user.getName() + " Money: " + moneyCount + " Homes: " + homeCount + " Last seen: " + DateUtil.formatDateDiff(lastLog));
                }

                user.reset();
            }
            sender.sendMessage(tl("cleaned"));
        });

    }

    // Forces a rerun of userdata UUID conversion.
    private void runUUIDConvert(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        sender.sendMessage("Starting Essentials UUID userdata conversion; this may lag the server.");

        Boolean ignoreUFCache = (args.length > 2 && args[1].toLowerCase(Locale.ENGLISH).contains("ignore"));
        EssentialsUpgrade.uuidFileConvert(ess, ignoreUFCache);

        sender.sendMessage("UUID conversion complete. Check your server log for more information.");
    }

    // Looks up various UUIDs for a user.
    private void runUUIDTest(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 2) {
            throw new Exception("/<command> uuidtest <name>");
        }
        String name = args[1];
        sender.sendMessage("Looking up UUID for " + name);

        UUID onlineUUID = null;

        for (Player player : ess.getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(name)) {
                onlineUUID = player.getUniqueId();
                break;
            }
        }

        UUID essUUID = ess.getUserMap().getUser(name).getConfigUUID();

        org.bukkit.OfflinePlayer player = ess.getServer().getOfflinePlayer(name);
        UUID bukkituuid = player.getUniqueId();
        sender.sendMessage("Bukkit Lookup: " + bukkituuid.toString());

        if (onlineUUID != null && onlineUUID != bukkituuid) {
            sender.sendMessage("Online player: " + onlineUUID.toString());
        }

        if (essUUID != null && essUUID != bukkituuid) {
            sender.sendMessage("Essentials config: " + essUUID.toString());
        }

        UUID npcuuid = UUID.nameUUIDFromBytes(("NPC:" + name).getBytes(Charsets.UTF_8));
        sender.sendMessage("NPC UUID: " + npcuuid.toString());

        UUID offlineuuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(Charsets.UTF_8));
        sender.sendMessage("Offline Mode UUID: " + offlineuuid.toString());
    }

    // Displays versions of EssentialsX and related plugins.
    private void runVersion(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (sender.isPlayer() && !ess.getUser(sender.getPlayer()).isAuthorized("essentials.version")) return;

        boolean isMismatched = false;
        boolean isVaultInstalled = false;
        boolean isUnsupported = false;
        final boolean isServerSupported = VersionUtil.isServerSupported();
        final PluginManager pm = server.getPluginManager();
        final String essVer = pm.getPlugin("Essentials").getDescription().getVersion();

        sender.sendMessage(tl(isServerSupported ? "versionOutputFine" : "versionOutputWarn", "Server", server.getBukkitVersion() + " " + server.getVersion()));
        sender.sendMessage(tl("versionOutputFine", "EssentialsX", essVer));

        for (Plugin plugin : pm.getPlugins()) {
            final PluginDescriptionFile desc = plugin.getDescription();
            String name = desc.getName();
            String version = desc.getVersion();

            if (name.startsWith("Essentials") && !name.equalsIgnoreCase("Essentials")) {
                if (officialPlugins.contains(name)) {
                    name = name.replace("Essentials", "EssentialsX");

                    if (!version.equalsIgnoreCase(essVer)) {
                        isMismatched = true;
                        sender.sendMessage(tl("versionOutputWarn", name, version));
                    } else {
                        sender.sendMessage(tl("versionOutputFine", name, version));
                    }
                } else {
                    sender.sendMessage(tl("versionOutputUnsupported", name, version));
                    isUnsupported = true;
                }
            }

            if (versionPlugins.contains(name)) {
                if (warnPlugins.contains(name)) {
                    sender.sendMessage(tl("versionOutputUnsupported", name, version));
                    isUnsupported = true;
                } else {
                    sender.sendMessage(tl("versionOutputFine", name, version));
                }
            }

            if (name.equals("Vault")) isVaultInstalled = true;
        }

        if (isMismatched) {
            sender.sendMessage(tl("versionMismatchAll"));
        }

        if (!isVaultInstalled) {
            sender.sendMessage(tl("versionOutputVaultMissing"));
        }

        if (isUnsupported) {
            sender.sendMessage(tl("versionOutputUnsupportedPlugins"));
        }

        if (!VersionUtil.isServerSupported()) {
            sender.sendMessage(tl("serverUnsupported"));
        }
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            List<String> options = Lists.newArrayList();
            options.add("debug");
            options.add("commands");
            options.add("version");
            options.add("reload");
            options.add("reset");
            options.add("cleanup");
            //options.add("uuidconvert");
            //options.add("uuidtest");
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
            case "uuidtest":
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
            case "uuidconvert":
                if (args.length == 2) {
                    return Lists.newArrayList("ignoreUFCache");
                }
                break;
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

            for (Player onlinePlayer : players.get()) {
                onlinePlayer.playSound(onlinePlayer.getLocation(), sound, 1, noteMap.get(note));
            }
        }
    }
}
