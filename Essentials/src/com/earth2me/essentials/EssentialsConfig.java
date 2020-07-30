package com.earth2me.essentials;

import com.earth2me.essentials.configuration.Configuration;
import com.earth2me.essentials.configuration.ConfigurationComment;
import com.earth2me.essentials.configuration.CustomPath;
import com.earth2me.essentials.configuration.ExampleKeyValue;
import com.earth2me.essentials.configuration.ExampleValues;
import com.earth2me.essentials.configuration.Header;
import com.earth2me.essentials.configuration.HiddenValue;
import com.earth2me.essentials.configuration.KeyValueParser;
import com.earth2me.essentials.configuration.Kleenean;
import com.earth2me.essentials.configuration.NoSeparator;
import com.earth2me.essentials.configuration.Parser;
import com.earth2me.essentials.configuration.SectionComment;
import com.earth2me.essentials.signs.EssentialsSign;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static com.earth2me.essentials.ISettings.KeepInvPolicy;

@Header({
        "###########################################################",
        " +------------------------------------------------------+ #",
        " |                       Notes                          | #",
        " +------------------------------------------------------+ #",
        "###########################################################",
        "",
        "This is the config file for EssentialsX.",
        "",
        "If you want to use special characters in this document, such as accented letters, you MUST save the file as UTF-8, not ANSI.",
        "If you receive an error when Essentials loads, ensure that:",
        "  - No tabs are present: YAML only allows spaces",
        "  - Indents are correct: YAML hierarchy is based entirely on indentation",
        "  - You have \"escaped\" all apostrophes in your text: If you want to write \"don't\", for example, write \"don''t\" instead (note the doubled apostrophe)",
        "  - Text with symbols is enclosed in single or double quotation marks",
        "",
        "If you need help, you can join the EssentialsX community: https://essentialsx.net/community.html",
        "",
        "###########################################################",
        " +------------------------------------------------------+ #",
        " |                       Notes                          | #",
        " +------------------------------------------------------+ #",
        "###########################################################",
})
public class EssentialsConfig extends Configuration {

    public EssentialsConfig(IEssentials ess) {
        super(new File("newconfig.yml"), ess);
    }

    @ConfigurationComment({"A color code between 0-9 or a-f. Set to 'none' to disable.",
            "In 1.16+ you can use hex color codes here as well. (For example, #613e1d is brown)."})
    @Parser("color")
    public static String opsNameColor = ChatColor.DARK_RED.toString();
    @ConfigurationComment("The character(s) to prefix all nicknames, so that you know they are not true usernames.")
    public static String nicknamePrefix = "~";
    @ConfigurationComment("The maximum length allowed in nicknames. The nickname prefix is included in this.")
    public static int maxNickLength = 15;
    @ConfigurationComment({"A list of phrases that cannot be used in nicknames. You can include regular expressions here.",
    "Users with essentials.nick.blacklist.bypass will be able to bypass this filter."})
    @ExampleValues({"Notch", "^Dinnerbone"})
    public static List<String> nickBlacklist = new ArrayList<>();
    @ConfigurationComment({"When this option is enabled, nickname length checking will exclude color codes in player names.",
    "ie: \"&6Notch\" has 7 characters (2 are part of a color code), a length of 5 is used when this option is set to true"})
    public static boolean ignoreColorsInMaxNickLength = false;
    @ConfigurationComment({"When this option is enabled, display names for hidden users will not be shown.",
    "This prevents players from being able to see that they are online while vanished."})
    public static boolean hideDisplaynameInVanish = true;
    @ConfigurationComment("Disable this if you have any other plugin that modifies the displayname of a user.")
    public static boolean changeDisplayname = true;
    @ConfigurationComment({"When this option is enabled, the (tab) player list will be updated with the displayname.",
    "The value of change-displayname (above) has to be true for this option to work."})
    @Kleenean(true)
    public static Boolean changePlayerlist = null;
    @ConfigurationComment({"When EssentialsChat.jar isn't used, force essentials to add the prefix and suffix from permission plugins to displayname.",
    "This setting is ignored if EssentialsChat.jar is used, and defaults to 'true'.",
    "The value of change-displayname (above) has to be true.",
    "Do not edit this setting unless you know what you are doing!"})
    @Kleenean(false)
    public static Boolean addPrefixSuffix = null;
    @ConfigurationComment({"When this option is enabled, player prefixes will be shown in the playerlist.",
    "This feature only works for Minecraft version 1.8 and higher.",
    "This value of change-playerlist has to be true."})
    @Kleenean(true)
    public static Boolean addPrefixInPlayerlist = null;
    @ConfigurationComment({"When this option is enabled, player suffixes will be shown in the playerlist.",
    "This feature only works for Minecraft version 1.8 and higher.",
    "This value of change-playerlist has to be true."})
    @Kleenean(true)
    public static Boolean addSuffixInPlayerlist = null;

    @ConfigurationComment({"If the teleport destination is unsafe, should players be teleported to the nearest safe location?",
    "If this is set to true, Essentials will attempt to teleport players close to the intended destination.",
    "If this is set to false, attempted teleports to unsafe locations will be cancelled with a warning."})
    public static boolean teleportSafety = true;

    @ConfigurationComment({"This forcefully disables teleport safety checks without a warning if attempting to teleport to unsafe locations.",
    "teleport-safety and this option need to be set to true to force teleportation to dangerous locations."})
    public static boolean forceDisableTeleportSafety = false;
    @ConfigurationComment({"If a player is teleporting to an unsafe location in creative, adventure, or god mode; they will not be teleported to a",
    "safe location. If you'd like players to be teleported to a safe location all of the time, set this option to true."})
    public static boolean forceSafeTeleportLocation = false;
    @ConfigurationComment({"If a player has any passengers, the teleport will fail. Should their passengers be dismounted before they are teleported?",
    "If this is set to true, Essentials will dismount the player's passengers before teleporting.",
    "If this is set to false, attempted teleports will be canceled with a warning."})
    public static boolean teleportPassengerDismount = true;
    @ConfigurationComment("The delay, in seconds, required between /home, /tp, etc.")
    public static int teleportCooldown = 0;
    @ConfigurationComment("The delay, in seconds, before a user actually teleports. If the user moves or gets attacked in this timeframe, the teleport is cancelled.")
    public static int teleportDelay = 0;
    @ConfigurationComment({"The delay, in seconds, a player can't be attacked by other players after they have been teleported by a command.",
    "This will also prevent the player attacking other players."})
    public static int teleportInvulnerability = 4;
    @ConfigurationComment("Whether to make all teleportations go to the center of the block; where the x and z coordinates decimal become .5")
    public static boolean teleportToCenter = true;

    @ConfigurationComment("The delay, in seconds, required between /heal or /feed attempts.")
    public static int healCooldown = 60;
    @ConfigurationComment("Do you want to remove potion effects when healing a player?")
    public static boolean removeEffectsOnHeal = true;

    @ConfigurationComment({"The default radius with /near",
    "Used to use chat radius but we are going to make it separate."})
    public static int nearRadius = 200;

//    @ConfigurationComment({"What to prevent from /item and /give.",
//    "e.g item-spawn-blacklist: dirt,grass,stone"})
//    @Parser("csv:material")
//    public static Collection<Material> itemSpawnBlacklist = new ArrayList<>();
    @ConfigurationComment({"Set this to true if you want permission based item spawn rules.",
    "Note: The blacklist above will be ignored then.",
    "Example permissions (these go in your permissions manager):",
    " - essentials.itemspawn.item-all",
    " - essentials.itemspawn.item-[itemname]",
    " - essentials.itemspawn.item-[itemid]",
    " - essentials.give.item-all",
    " - essentials.give.item-[itemname]",
    " - essentials.give.item-[itemid]",
    " - essentials.unlimited.item-all",
    " - essentials.unlimited.item-[itemname]",
    " - essentials.unlimited.item-[itemid]",
    " - essentials.unlimited.item-bucket # Unlimited liquid placing",
    "",
    "For more information, visit http://wiki.ess3.net/wiki/Command_Reference/ICheat#Item.2FGive"})
    public static boolean permissionBasedItemSpawn = false;

    @ConfigurationComment("Mob limit on the /spawnmob command per execution.")
    public static int spawnmobLimit = 10;

    @ConfigurationComment("Shall we notify users when using /lightning?")
    public static boolean warnOnSmite = true;

    @ConfigurationComment("Shall we drop items instead of adding to inventory if the target inventory is full?")
    public static boolean dropItemsIfFull = false;

    @ConfigurationComment("Should we notify players if they have no new mail?")
    public static boolean notifyNoNewMail = true;
    @ConfigurationComment({"Specifies the duration (in seconds) between each time a player is notified of mail they have.",
    "Useful for servers with a lot of mail traffic."})
    public static int notifyPlayerOfMailCooldown = 60;

    @ConfigurationComment({"When a command conflicts with another plugin, by default, Essentials will try to force the OTHER plugin to take priority.",
    "Commands in this list, will tell Essentials to 'not give up' the command to other plugins.",
    "In this state, which plugin 'wins' appears to be almost random.",
    "If you have two plugin with the same command and you wish to force Essentials to take over, you need an alias.",
    "To force essentials to take 'god' alias 'god' to 'egod'.",
    "See http://wiki.bukkit.org/Commands.yml#aliases for more information."})
    @ExampleValues({"god", "info"})
    public static List<String> overriddenCommands = new ArrayList<>();
    @ConfigurationComment({"Disabling commands here will prevent Essentials handling the command, this will not affect command conflicts.",
    "You should not have to disable commands used in other plugins, they will automatically get priority.",
    "See http://wiki.bukkit.org/Commands.yml#aliases to map commands to other plugins."})
    @ExampleValues({"nick", "clear"})
    public static List<String> disabledCommands = new ArrayList<>();
    @ConfigurationComment({"These commands will be shown to players with socialSpy enabled.",
    "You can add commands from other plugins you may want to track or",
    "remove commands that are used for something you dont want to spy on.",
    "Set - '*' in order to listen on all possible commands."})
    public static List<String> socialspyCommands = Arrays.asList("msg", "w", "r", "mail", "m", "t", "whisper", "emsg", "tell",
            "er", "reply", "ereply", "email", "action", "describe", "eme", "eaction", "edescribe", "etell", "ewhisper", "pm");
    @ConfigurationComment({"Whether the private and public messages from muted players should appear in the social spy.",
    "If so, they will be differentiated from those sent by normal players."})
    public static boolean socialspyListenMutedPlayers = true;

    @ConfigurationComment({"The following settings listen for when a player changes worlds.",
    "If you use another plugin to control speed and flight, you should change these to false.",
    "",
    "When a player changes world, should EssentialsX reset their flight?",
    "This will disable flight if the player does not have essentials.fly."})
    public static boolean worldChangeFlyReset = true;
    @ConfigurationComment({"When a player changes world, should we reset their speed according to their permissions?",
    "This resets the player's speed to the default if they don't have essentials.speed.",
    "If the player doesn't have essentials.speed.bypass, this resets their speed to the maximum specified above."})
    public static boolean worldChangeSpeedReset = true;

    @ConfigurationComment({"These commands will be disabled when a player is muted.",
    "Use '*' to disable every command.",
    "Essentials already disabled Essentials messaging commands by default.",
    "It only cares about the root command, not args after that (it sees /f chat the same as /f)"})
    public static List<String> muteCommands = Arrays.asList("f", "kittycannon");
    @ConfigurationComment({"If you do not wish to use a permission system, you can define a list of 'player perms' below.",
    "This list has no effect if you are using a supported permissions system.",
    "If you are using an unsupported permissions system, simply delete this section.",
    "Whitelist the commands and permissions you wish to give players by default (everything else is op only).",
    "These are the permissions without the \"essentials.\" part.",
    "",
    "To enable this feature, please set use-bukkit-permissions (below this) to false."})
    public static List<String> playerCommands = Arrays.asList("afk", "afk.auto", "back", "back.ondeath", "balance",
            "balance.others", "balancetop", "build", "chat.color", "chat.format", "chat.shout", "chat.question",
            "clearinventory", "compass", "depth", "delhome", "getpos", "geoip.show", "help", "helpop", "home",
            "home.others", "ignore", "info", "itemdb", "kit", "kits.tools", "list", "mail", "mail.send", "me", "motd",
            "msg", "msg.color", "nick", "near", "pay", "ping", "protect", "r", "rules", "realname", "seen", "sell",
            "sethome", "setxmpp", "signs.create.protection", "signs.create.trade", "signs.break.protection",
            "signs.break.trade", "signs.use.balance", "signs.use.buy", "signs.use.disposal", "signs.use.enchant",
            "signs.use.free", "signs.use.gamemode", "signs.use.heal", "signs.use.info", "signs.use.kit", "signs.use.mail",
            "signs.use.protection", "signs.use.repair", "signs.use.sell", "signs.use.time", "signs.use.trade",
            "signs.use.warp", "signs.use.weather", "spawn", "suicide", "time", "tpa", "tpaccept", "tpahere", "tpdeny",
            "warp", "warp.list", "world", "worth", "xmpp");
    @ConfigurationComment({"Use this option to force superperms-based permissions handler regardless of detected installed perms plugin.",
    "This is useful if you want superperms-based permissions (with wildcards) for custom permissions plugins.",
    "If you wish to use EssentialsX's built-in permissions using the `player-commands` section above, set this to false.",
    "Default is true."})
    public static boolean useBukkitPermissions = true;

    @ConfigurationComment({"When this option is enabled, one-time use kits (ie. delay < 0) will be",
    "removed from the /kit list when a player can no longer use it"})
    public static boolean skipUsedOneTimeKitsFromKitList = false;
    @ConfigurationComment({"Determines the functionality of the /createkit command.",
    "If this is true, /createkit will give the user a link with the kit code.",
    "If this is false, /createkit will add the kit to the kits.yml config file directly."})
    public static boolean pastebinCreatekit = false;

    @ConfigurationComment({"Essentials Sign Control",
    "See http://wiki.ess3.net/wiki/Sign_Tutorial for instructions on how to use these.",
    "To enable signs, remove # symbol. To disable all signs, comment/remove each sign.",
    "Essentials colored sign support will be enabled when any sign types are enabled.",
    "Color is not an actual sign, it's for enabling using color codes on signs, when the correct permissions are given."})
    @ExampleValues({"color", "balance", "buy", "sell", "trade", "free", "disposal", "warp", "kit", "mail", "enchant",
    "gamemode", "heal", "info", "spawnmob", "repair", "time", "weather"})
    @Parser("signs")
    @CustomPath("enabledSigns")
    public static List<EssentialsSign> enabledSigns = null;
    @ConfigurationComment({"How many times per second can Essentials signs be interacted with per player.",
    "Values should be between 1-20, 20 being virtually no lag protection.",
    "Lower numbers will reduce the possibility of lag, but may annoy players."})
    public int signUsePerSecond = 4;
    @ConfigurationComment({"Allow item IDs on pre-existing signs on 1.13 and above.",
    "You cannot use item IDs on new signs, but this will allow players to interact with signs that",
    "were placed before 1.13."})
    public boolean allowOldIdSigns = false;
    @ConfigurationComment({"List of sign names Essentials should not protect. This feature is especially useful when",
    "another plugin provides a sign that EssentialsX provides, but Essentials overrides.",
    "For example, if a plugin provides a [kit] sign, and you wish to use theirs instead of",
    "Essentials's, then simply add kit below and Essentials will not protect it.",
    "",
    "See https://github.com/EssentialsX/Essentials/pull/699 for more information."})
    @ExampleValues("kit")
    @Parser("signs")
    public static List<EssentialsSign> unprotectedSignNames = new ArrayList<>();

    @SectionComment("backup:Backup runs a batch/bash command while saving is disabled.")
    @ConfigurationComment("Interval in minutes.")
    public static int backup_interval = 30;
    @ConfigurationComment("If true, the backup task will run even if there are no players online.")
    public static boolean backup_alwaysRun = false;
    @ConfigurationComment({"Unless you add a valid backup command or script here, this feature will be useless.",
    "Use 'save-all' to simply force regular world saving without backup."})
    public static String backup_command = "";

    @ConfigurationComment("Set this true to enable permission per warp.")
    public static boolean perWarpPermission = false;

    @HiddenValue
    public static boolean sortListByGroups = false;
    @ConfigurationComment({"Sort output of /list command by groups.",
    "You can hide and merge the groups displayed in /list by defining the desired behaviour here.",
    "Detailed instructions and examples can be found on the wiki: http://wiki.ess3.net/wiki/List"})
    @CustomPath("list")
    @Parser("list")
    public static Map<String, Object> listGroupConfig = Collections.singletonMap("Admins", "owner admin");
    @ConfigurationComment("Displays real names in /list next to players who are using a nickname.")
    public static boolean realNamesOnList = false;

    @ConfigurationComment("More output to the console")
    public static boolean debug = false;
    @ConfigurationComment({"Set the locale for all messages.",
    "If you don't set this, the default locale of the server will be used.",
    "For example, to set language to English, set locale to en, to use the file \"messages_en.properties\".",
    "Don't forget to remove the # in front of the line.",
    "For more information, visit http://wiki.ess3.net/wiki/Locale"})
    public static String locale = "";

    @ConfigurationComment("Turn off god mode when people leave the server.")
    public static boolean removeGodOnDisconnect = false;

    @ConfigurationComment({"Auto-AFK",
    "After this timeout in seconds, the user will be set as AFK.",
    "This feature requires the player to have essentials.afk.auto node.",
    "Set to -1 for no timeout."})
    public static int autoAfk = 300;
    @ConfigurationComment({"After this timeout in seconds, the user will be kicked from the server.",
    "essentials.afk.kickexempt node overrides this feature.",
    "Set to -1 for no timeout."})
    public static int autoAfkKick = -1;
    @ConfigurationComment({"Set this to true, if you want to freeze the player, if the player is AFK.",
    "Other players or monsters can't push the player out of AFK mode then.",
    "This will also enable temporary god mode for the AFK player.",
    "The player has to use the command /afk to leave the AFK mode."})
    public static boolean freezeAfkPlayers = false;
    @ConfigurationComment({"When the player is AFK, should he be able to pickup items?",
    "Enable this, when you don't want people idling in mob traps."})
    public static boolean disableItemPickupWhileAfk = false;
    @ConfigurationComment({"This setting controls if a player is marked as active on interaction.",
    "When this setting is false, the player would need to manually un-AFK using the /afk command."})
    public static boolean cancelAfkOnInteract = true;
    @ConfigurationComment({"Should we automatically remove afk status when a player moves?",
    "Player will be removed from AFK on chat/command regardless of this setting.",
    "Disable this to reduce server lag."})
    public static boolean cancelAfkOnMove = true;
    @ConfigurationComment({"Should AFK players be ignored when other players are trying to sleep?",
    "When this setting is false, players won't be able to skip the night if some players are AFK.",
    "Users with the permission node essentials.sleepingignored will always be ignored."})
    public static boolean sleepIgnoresAfkPlayers = true;
    @ConfigurationComment({"Set the player's list name when they are AFK. This is none by default which specifies that Essentials",
    "should not interfere with the AFK player's list name.",
    "You may use color codes, use {USERNAME} the player's name or {PLAYER} for the player's displayname."})
    public static String afkListName = "none";
    @ConfigurationComment({"When a player enters or exits AFK mode, should the AFK notification be broadcast",
    "to the entire server, or just to the player?",
    "When this setting is false, only the player will be notified upon changing their AFK state."})
    public static boolean broadcastAfkMessage = true;

    @ConfigurationComment("You can disable the death messages of Minecraft here.")
    public static boolean deathMessages = true;
    @ConfigurationComment("When players die, should they receive the coordinates they died at?")
    public static boolean sendInfoAfterDeath = false;

    @ConfigurationComment({"How should essentials handle players with the essentials.keepinv permission who have items with",
    "curse of vanishing when they die?",
    "You can set this to \"keep\" (to keep the item), \"drop\" (to drop the item), or \"delete\" (to delete the item).",
    "Defaults to \"keep\""})
    public static KeepInvPolicy vanishingItemsPolicy = KeepInvPolicy.KEEP;
    @ConfigurationComment({"How should essentials handle players with the essentials.keepinv permission who have items with",
            "curse of binding when they die?",
            "You can set this to \"keep\" (to keep the item), \"drop\" (to drop the item), or \"delete\" (to delete the item).",
            "Defaults to \"keep\""})
    public static KeepInvPolicy bindingItemsPolicy = KeepInvPolicy.KEEP;

    @ConfigurationComment({"Should players with permissions be able to join and part silently?",
    "You can control this with essentials.silentjoin and essentials.silentquit permissions if it is enabled.",
    "In addition, people with essentials.silentjoin.vanish will be vanished on join."})
    public static boolean allowSilentJoinQuit = false;

    @ConfigurationComment({"You can set custom join and quit messages here. Set this to \"none\" to use the default Minecraft message,",
    "or set this to \"\" to hide the message entirely.",
    "You may use color codes, {USERNAME} for the player's name, and {PLAYER} for the player's displayname."})
    @NoSeparator
    public static String customJoinMessage = "none";
    public static String customQuitMessage = "none";

    @ConfigurationComment({"You can disable join and quit messages when the player count reaches a certain limit.",
    "When the player count is below this number, join/quit messages will always be shown.",
    "Set this to -1 to always show join and quit messages regardless of player count."})
    public static int hideJoinQuitMessagesAbove = -1;

    @ConfigurationComment("Add worlds to this list, if you want to automatically disable god mode there.")
    @ExampleValues("world_nether")
    public static List<String> noGodInWorlds = new ArrayList<>();
    @ConfigurationComment({"Set to true to enable per-world permissions for teleporting between worlds with essentials commands.",
    "This applies to /world, /back, /tp[a|o][here|all], but not warps.",
    "Give someone permission to teleport to a world with essentials.worlds.<worldname>",
    "This does not affect the /home command, there is a separate toggle below for this."})
    public static boolean worldTeleportPermissions = false;

    @ConfigurationComment({"The number of items given if the quantity parameter is left out in /item or /give.",
    "If this number is below 1, the maximum stack size size is given. If over-sized stacks.",
    "are not enabled, any number higher than the maximum stack size results in more than one stack."})
    public static int defaultStackSize = -1;
    @ConfigurationComment({"Over-sized stacks are stacks that ignore the normal max stack size.",
    "They can be obtained using /give and /item, if the player has essentials.oversizedstacks permission.",
    "How many items should be in an over-sized stack?"})
    public static int oversizedStacksize = 64;

    @ConfigurationComment({"Allow repair of enchanted weapons and armor.",
    "If you set this to false, you can still allow it for certain players using the permission.",
    "essentials.repair.enchanted"})
    public static boolean repairEnchanted = true;
    @ConfigurationComment({"Allow 'unsafe' enchantments in kits and item spawning.",
    "Warning: Mixing and overleveling some enchantments can cause issues with clients, servers and plugins."})
    public static boolean unsafeEnchantments = false;

    @ConfigurationComment({"Do you want Essentials to keep track of previous location for /back in the teleport listener?",
    "If you set this to true any plugin that uses teleport will have the previous location registered."})
    public static boolean registerBackInListener = false;

    @ConfigurationComment("Delay to wait before people can cause attack damage after logging in.")
    public static int loginAttackDelay = 5;

    @ConfigurationComment("Set the max fly speed, values range from 0.1 to 1.0")
    public static double maxFlySpeed = 0.8;
    @ConfigurationComment("Set the max walk speed, values range from 0.1 to 1.0")
    public static double maxWalkSpeed = 0.8;

    @ConfigurationComment("Set the maximum amount of mail that can be sent within a minute.")
    public static int mailsPerMinute = 1000;

    @ConfigurationComment({"Set the maximum time /mute can be used for in seconds.",
    "Set to -1 to disable, and essentials.mute.unlimited can be used to override."})
    public static int maxMuteTime = -1;
    @ConfigurationComment({"Set the maximum time /tempban can be used for in seconds.",
    "Set to -1 to disable, and essentials.tempban.unlimited can be used to override."})
    public static int maxTempbanTime = -1;

    @ConfigurationComment({"Changes the default /reply functionality. This can be changed on a per-player basis using /rtoggle.",
    "If true, /r goes to the person you messaged last, otherwise the first person that messaged you.",
    "If false, /r goes to the last person that messaged you."})
    public static boolean lastMessageReplyRecipient = true;
    @ConfigurationComment({"If last-message-reply-recipient is enabled for a particular player,",
    "this specifies the duration, in seconds, that would need to elapse for the",
    "reply-recipient to update when receiving a message.",
    "Default is 180 (3 minutes)"})
    public static int lastMessageReplyRecipientTimeout = 180;

    // why? :)
    @ConfigurationComment("Toggles whether or not left clicking mobs with a milk bucket turns them into a baby.")
    public static boolean milkBucketEasterEgg = true;

    @ConfigurationComment("Toggles whether or not the fly status message should be sent to players on join.")
    public static boolean sendFlyEnableOnJoin = true;

    @ConfigurationComment({"Set to true to enable per-world permissions for setting time for individual worlds with essentials commands.",
    "This applies to /time, /day, /eday, /night, /enight, /etime.",
    "Give someone permission to teleport to a world with essentials.time.world.<worldname>."})
    public static boolean worldTimePermissions = false;

    @ConfigurationComment({"Specify cooldown for both Essentials commands and external commands as well.",
    "All commands do not start with a Forward Slash (/). Instead of /msg, write msg",
    "#",
    "Wildcards are supported. E.g.",
    "- '*i*': 50",
    "adds a 50 second cooldown to all commands that include the letter i",
    "#",
    "EssentialsX supports regex by starting the command with a caret ^",
    "For example, to target commands starting with ban and not banip the following would be used:",
    " '^ban([^ip])( .*)?': 60 # 60 seconds /ban cooldown.",
    "Note: If you have a command that starts with ^, then you can escape it using backslash (\\). e.g. \\^command: 123"})
    @ExampleKeyValue({"feed: 100 # 100 second cooldown on /feed command", "'*': 5 # 5 Second cooldown on all commands"})
    @Parser("coolcmds")
    public static Map<Pattern, Long> commandCooldowns = new LinkedHashMap<>();

    static {
        registerParser("list", new KeyValueParser(new HashMap<String, Object>() {{
            if (sortListByGroups) {
                put("ListByGroup", "ListByGroup");
            } else {
                put("Players", "*");
            }
        }}));
        registerParser("coolcmds", new KeyValueParser() {
            private final Map<String, Long> yamlCooldowns = new LinkedHashMap<>();

            @Override
            public <T> Object parseToJava(Class<T> type, Object object) {
                yamlCooldowns.clear();
                if (!(object instanceof ConfigurationSection)) {
                    return new LinkedHashMap<>();
                }
                ConfigurationSection section = (ConfigurationSection) object;
                Map<Pattern, Long> result = new LinkedHashMap<>();
                for (String cmdEntry : section.getKeys(false)) {
                    Pattern pattern = null;
                    if (cmdEntry.startsWith("^")) {
                        try {
                            pattern = Pattern.compile(cmdEntry.substring(1));
                        } catch (PatternSyntaxException e) {
                            logger.warning("Command cooldown error: " + e.getMessage());
                        }
                    } else {
                        // Escape above Regex
                        if (cmdEntry.startsWith("\\^")) {
                            cmdEntry = cmdEntry.substring(1);
                        }
                        String cmd = cmdEntry
                                .replaceAll("\\*", ".*"); // Wildcards are accepted as asterisk * as known universally.
                        pattern = Pattern.compile(cmd + "( .*)?"); // This matches arguments, if present, to "ignore" them from the feature.
                    }

                    Object value = section.get(cmdEntry);
                    if (value instanceof String) {
                        try {
                            value = Double.parseDouble(value.toString());
                        } catch (NumberFormatException ignored) {
                        }
                    }
                    if (!(value instanceof Number)) {
                        logger.warning("Command cooldown error: '" + value + "' is not a valid cooldown");
                        continue;
                    }
                    double cooldown = ((Number) value).doubleValue();
                    if (cooldown < 1) {
                        logger.warning("Command cooldown with very short " + cooldown + " cooldown.");
                    }

                    yamlCooldowns.put(cmdEntry, (long) cooldown);
                    result.put(pattern, (long) cooldown * 1000); // convert to milliseconds
                }
                return result;
            }

            @Override
            public String parseToYAML(Object object) {
                return super.parseToYAML(yamlCooldowns);
            }
        });
    }
}
