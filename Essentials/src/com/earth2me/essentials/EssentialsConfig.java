package com.earth2me.essentials;

import com.earth2me.essentials.configuration.CheckRegex;
import com.earth2me.essentials.configuration.Configuration;
import com.earth2me.essentials.configuration.ConfigurationComment;
import com.earth2me.essentials.configuration.CustomPath;
import com.earth2me.essentials.configuration.ExampleKeyValue;
import com.earth2me.essentials.configuration.ExampleValues;
import com.earth2me.essentials.configuration.Header;
import com.earth2me.essentials.configuration.HiddenValue;
import com.earth2me.essentials.configuration.IgnoreWhenNull;
import com.earth2me.essentials.configuration.KeyValueParser;
import com.earth2me.essentials.configuration.Kleenean;
import com.earth2me.essentials.configuration.NoSeparator;
import com.earth2me.essentials.configuration.Parser;
import com.earth2me.essentials.configuration.SectionComment;
import com.earth2me.essentials.configuration.ValueParser;
import com.earth2me.essentials.signs.EssentialsSign;
import com.google.common.collect.HashBiMap;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static com.earth2me.essentials.ISettings.EssEventPriority;
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
    @IgnoreWhenNull
    public static KeepInvPolicy vanishingItemsPolicy = KeepInvPolicy.KEEP;
    @ConfigurationComment({"How should essentials handle players with the essentials.keepinv permission who have items with",
            "curse of binding when they die?",
            "You can set this to \"keep\" (to keep the item), \"drop\" (to drop the item), or \"delete\" (to delete the item).",
            "Defaults to \"keep\""})
    @IgnoreWhenNull
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
    @ConfigurationComment("Whether command cooldowns should be persistent past server shutdowns")
    public static boolean commandCooldownPersistence = true;

    @ConfigurationComment({"Whether NPC balances should be listed in balance ranking features such as /balancetop.",
    "NPC balances can include features like factions from FactionsUUID plugin."})
    public static boolean npcsInBalanceRanking = false;

    @ConfigurationComment({"Allow bulk buying and selling signs when the player is sneaking.",
    "This is useful when a sign sells or buys one item at a time and the player wants to sell a bunch at once."})
    public static boolean allowBulkBuySell = true;
    @ConfigurationComment({"Allow selling of items with custom names with the /sell command.",
    "This may be useful to prevent players accidentally selling named items."})
    public static boolean allowSellingNamedItems = false;

    @ConfigurationComment({"Delay for the MOTD display for players on join, in milliseconds.",
    "This has no effect if the MOTD command or permission are disabled."})
    public static int delayMotd = 0;

    @ConfigurationComment({"A list of commands that should have their complementary confirm commands enabled by default.",
    "This is empty by default, for the latest list of valid commands see the latest source config.yml."})
    @ExampleValues({"pay", "clearinventory"})
    public static List<String> defaultEnabledConfirmCommands = new ArrayList<>();

    @ConfigurationComment("Whether or not to teleport a player back to their previous position after they have been freed from jail.")
    public static boolean teleportBackWhenFreedFromJail = true;
    @ConfigurationComment({"Set the timeout, in seconds for players to accept a tpa before the request is cancelled.",
    "Set to 0 for no timeout."})
    public static int tpaAcceptCancellation = 120;

    @ConfigurationComment("Allow players to set hats by clicking on their helmet slot.")
    public static boolean allowDirectHat = true;

    @ConfigurationComment({"Allow in-game players to specify a world when running /broadcastworld.",
    "If false, running /broadcastworld in-game will always send a message to the player's current world.",
    "This doesn't affect running the command from the console, where a world is always required."})
    public static boolean allowWorldInBroadcastWorld = true;

    @ConfigurationComment({"Consider water blocks as \"safe,\" therefore allowing players to teleport",
    "using commands such as /home or /spawn to a location that is occupied",
    "by water blocks"})
    public static boolean isWaterSafe = false;

    @ConfigurationComment({"Should the usermap try to sanitise usernames before saving them?",
    "You should only change this to false if you use Minecraft China."})
    public static boolean safeUsermapNames = true;

    @ConfigurationComment({"Should Essentials output logs when a command block executes a command?",
    "Example: CommandBlock at <x>,<y>,<z> issued server command: /<command>"})
    public static boolean logCommandBlockCommands = true;

    @ConfigurationComment("Set the maximum speed for projectiles spawned with /fireball.")
    public static int maxProjectileSpeed = 8;

    @ConfigurationComment({"###########################################################",
    " +------------------------------------------------------+ #",
    " |                        Homes                         | #",
    " +------------------------------------------------------+ #",
    "###########################################################",
    "",
    "Allows people to set their bed during the day.",
    "This setting has no effect in Minecraft 1.15+, as Minecraft will always allow the player to set their bed location during the day."})
    public static boolean updateBedAtDaytime = true;
    @ConfigurationComment({"Set to true to enable per-world permissions for using homes to teleport between worlds.",
    "This applies to the /home command only.",
    "Give someone permission to teleport to a world with essentials.worlds.<worldname>"})
    public static boolean worldHomePermissions = false;
    @ConfigurationComment({"Allow players to have multiple homes.",
    "Players need essentials.sethome.multiple before they can have more than 1 home.",
    "You can set the default number of multiple homes using the 'default' rank below.",
    "To remove the home limit entirely, give people 'essentials.sethome.multiple.unlimited'.",
    "To grant different home amounts to different people, you need to define a 'home-rank' below.",
    "Create the 'home-rank' below, and give the matching permission: essentials.sethome.multiple.<home-rank>",
    "For more information, visit http://wiki.ess3.net/wiki/Multihome"})
    @Parser("special:map")
    public static Map<String, Integer> sethomeMultiple = new HashMap<String, Integer>() {{
        put("default", 3);
        put("vip", 5);
        put("staff", 10);
    }};
    @ConfigurationComment({"In this example someone with 'essentials.sethome.multiple' and 'essentials.sethome.multiple.vip' will have 5 homes.",
    "Remember, they MUST have both permission nodes in order to be able to set multiple homes.",
    "",
    "Controls whether players need the permission \"essentials.home.compass\" in order to point",
    "the player's compass at their first home.",
    "#",
    "Leaving this as false will retain Essentials' original behaviour, which is to always",
    "change the compass' direction to point towards their first home."})
    public static boolean compassTowardsHomePerm = false;
    @ConfigurationComment({"If no home is set, would you like to send the player to spawn?",
    "If set to false, players will not be teleported when they run /home without setting a home first."})
    public static boolean spawnIfNoHome = true;
    @ConfigurationComment("Should players be asked to provide confirmation for homes which they attempt to overwrite?")
    public static boolean confirmHomeOverwrite = false;

    @ConfigurationComment({"###########################################################",
    " +------------------------------------------------------+ #",
    " |                       Economy                        | #",
    " +------------------------------------------------------+ #",
    "###########################################################",
    "For more information, visit http://wiki.ess3.net/wiki/Essentials_Economy",
    "You can control the values of items that are sold to the server by using the /setworth command.",
    "",
    "Defines the balance with which new players begin. Defaults to 0."})
    public static BigDecimal startingBalance = BigDecimal.ZERO;
    @ConfigurationComment({"Defines the cost to use the given commands PER USE.",
    "Some commands like /repair have sub-costs, check the wiki for more information."})
    @ExampleKeyValue({"example: 1000 # /example costs $1000 PER USE", "kit-tools: 1500 # /kit tools costs $1500 PER USE"})
    @Parser("special:map")
    public static Map<String, BigDecimal> commandCosts = new HashMap<>();
    @ConfigurationComment({"Set this to a currency symbol you want to use.",
    "Remember, if you want to use special characters in this document,",
    "such as accented letters, you MUST save the file as UTF-8, not ANSI."})
    @CheckRegex(regex = "\\A[^0-9]\\Z", defaultValue = "$")
    public static String currencySymbol = "$";
    @ConfigurationComment({"Enable this to make the currency symbol appear at the end of the amount rather than at the start.",
    "For example, the euro symbol typically appears after the current amount."})
    public static boolean currencySymbolSuffix = false;
    @ConfigurationComment({"Set the maximum amount of money a player can have.",
    "The amount is always limited to 10 trillion because of the limitations of a java double."})
    public static BigDecimal maxMoney = new BigDecimal("10000000000000");
    @ConfigurationComment({"Set the minimum amount of money a player can have (must be above the negative of max-money).",
    "Setting this to 0, will disable overdrafts/loans completely.  Users need 'essentials.eco.loan' perm to go below 0."})
    public static BigDecimal minMoney = new BigDecimal("-10000");
    @ConfigurationComment("Enable this to log all interactions with trade/buy/sell signs and sell command.")
    public static boolean economyLogEnabled = false;
    @ConfigurationComment({"Enable this to also log all transactions from other plugins through Vault.",
    "This can cause the economy log to fill up quickly so should only be enabled for testing purposes!"})
    public static boolean economyLogUpdateEnabled = false;
    @ConfigurationComment("Minimum acceptable amount to be used in /pay.")
    public static BigDecimal minimumPayAmount = new BigDecimal("0.001");
    @ConfigurationComment("Enable this to block users who try to /pay another user which ignore them.")
    public static boolean payExcludesIgnoreList = false;

    @ConfigurationComment({"###########################################################",
    " +------------------------------------------------------+ #",
    " |                         Help                         | #",
    " +------------------------------------------------------+ #",
    "###########################################################",
    "",
    "Show other plugins commands in help."})
    public static boolean nonEssInHelp = true;
    @ConfigurationComment({"Hide plugins which do not give a permission.",
    "You can override a true value here for a single plugin by adding a permission to a user/group.",
    "The individual permission is: essentials.help.<plugin>, anyone with essentials.* or '*' will see all help regardless.",
    "You can use negative permissions to remove access to just a single plugins help if the following is enabled."})
    public static boolean hidePermissionlessHelp = true;

    @SectionComment({"chat:###########################################################",
    "chat: +------------------------------------------------------+ #",
    "chat: |                   EssentialsX Chat                   | #",
    "chat: +------------------------------------------------------+ #",
    "chat:###########################################################",
    "chat: ",
    "chat:You need to install EssentialsX Chat for this section to work.",
    "chat:See https://essentialsx.net/wiki/Module-Breakdown.html for more information."})
    @ConfigurationComment({"If EssentialsX Chat is installed, this will define how far a player's voice travels, in blocks. Set to 0 to make all chat global.",
    "Note that users with the \"essentials.chat.spy\" permission will hear everything, regardless of this setting.",
    "Users with essentials.chat.shout can override this by prefixing their message with an exclamation mark (!)",
    "Users with essentials.chat.question can override this by prefixing their message with a question mark (?)",
    "You can add command costs for shout/question by adding chat-shout and chat-question to the command costs section."})
    @CustomPath("chat.radius")
    public static int chatRadius = 0;
    @ConfigurationComment({"Chat formatting can be done in two ways, you can either define a standard format for all chat.",
    "Or you can give a group specific chat format, to give some extra variation.",
    "For more information of chat formatting, check out the wiki: http://wiki.ess3.net/wiki/Chat_Formatting"})
    @CustomPath("chat.format")
    public static String chatFormat = "<{DISPLAYNAME}> {MESSAGE}";
    @ConfigurationComment({"If you are using group formats make sure to remove the '#' to allow the setting to be read.",
    "Note: Group names are case-sensitive so you must match them up with your permission plugin."})
    @ExampleKeyValue({"\"default\": \"{WORLDNAME} {DISPLAYNAME}&7:&r {MESSAGE}\"", "\"admins\": \"{WORLDNAME} &c[{GROUP}]&r {DISPLAYNAME}&7:&c {MESSAGE}\""})
    @Parser("special:map")
    @CustomPath("chat.group-formats")
    public static HashBiMap<String, String> chatGroupFormats = HashBiMap.create();

    @SectionComment({"protect:###########################################################",
    "protect: +------------------------------------------------------+ #",
    "protect: |                 EssentialsX Protect                  | #",
    "protect: +------------------------------------------------------+ #",
    "protect:###########################################################",
    "protect:You need to install EssentialsX Protect for this section to work.",
    "protect:See https://essentialsx.net/wiki/Module-Breakdown.html for more information.",
    "prevent:General physics/behavior modifications. Set these to true to disable behaviours."})
    public static boolean protect_prevent_lavaFlow = false;
    public static boolean protect_prevent_waterFlow = false;
    public static boolean protect_prevent_waterBucketFlow = false;
    public static boolean protect_prevent_lavaFireSpread = true;
    public static boolean protect_prevent_lavaItemdamage = false;
    public static boolean protect_prevent_flintFire = false;
    public static boolean protect_prevent_lightningFireSpread = true;
    public static boolean protect_prevent_portalCreation = false;
    public static boolean protect_prevent_tntExplosion = false;
    public static boolean protect_prevent_tntPlayerdamage = false;
    public static boolean protect_prevent_tntItemdamage = false;
    public static boolean protect_prevent_tntMinecartExplosion = false;
    public static boolean protect_prevent_tntMinecartPlayerdamage = false;
    public static boolean protect_prevent_tntMinecartItemdamage = false;
    public static boolean protect_prevent_fireballExplosion = false;
    public static boolean protect_prevent_fireballFire = false;
    public static boolean protect_prevent_fireballPlayerdamage = false;
    public static boolean protect_prevent_fireballItemdamage = false;
    public static boolean protect_prevent_witherskullExplosion = false;
    public static boolean protect_prevent_witherskullPlayerdamage = false;
    public static boolean protect_prevent_witherskullItemdamage = false;
    public static boolean protect_prevent_witherSpawnexplosion = false;
    public static boolean protect_prevent_witherBlockreplace = false;
    public static boolean protect_prevent_creeperExplosion = false;
    public static boolean protect_prevent_creeperPlayerdamage = false;
    public static boolean protect_prevent_creeperItemdamage = false;
    public static boolean protect_prevent_creeperBlockdamage = false;
    public static boolean protect_prevent_enderCrystalExplosion = false;
    public static boolean protect_prevent_enderdragonBlockdamage = true;
    public static boolean protect_prevent_endermanPickup = false;
    public static boolean protect_prevent_villagerDeath = false;
    public static boolean protect_prevent_bedExplosion = false;
    public static boolean protect_prevent_respawnAnchorExplosion = false;
    @ConfigurationComment({"Monsters won't follow players.",
    "permission essentials.protect.entitytarget.bypass disables this."})
    public static boolean protect_prevent_entitytarget = false;
    @ConfigurationComment("Prevents zombies from breaking down doors")
    public static boolean protect_prevent_zombieDoorBreak = false;
    @ConfigurationComment("Prevents Ravagers from stealing blocks")
    public static boolean protect_prevent_ravagerThief = false;
    @ConfigurationComment("Prevents sheep from turning grass to dirt")
    public static boolean protect_prevent_sheepEatGrass = false;
    @SectionComment("transformation:Prevent certain transformations.")
    @ConfigurationComment("Prevent creepers becoming charged when struck by lightning.")
    public static boolean protect_prevent_transformation_chargedCreeper = false;
    @ConfigurationComment("Prevent villagers becoming zombie villagers.")
    public static boolean protect_prevent_transformation_zombieVillager = false;
    @ConfigurationComment("Prevent zombie villagers being cured.")
    public static boolean protect_prevent_transformation_villager = false;
    @ConfigurationComment("Prevent villagers becoming witches when struck by lightning.")
    public static boolean protect_prevent_transformation_witch = false;
    @ConfigurationComment("Prevent pigs becoming zombie pigmen when struck by lightning.")
    public static boolean protect_prevent_transformation_zombiePigman = false;
    @ConfigurationComment("Prevent zombies turning into drowneds, and husks turning into zombies.")
    public static boolean protect_prevent_transformation_drowned = false;
    @ConfigurationComment("Prevent mooshrooms changing colour when struck by lightning.")
    public static boolean protect_prevent_transformation_mooshroom = false;
    @SectionComment("spawn:Prevent the spawning of creatures. If a creature is missing, you can add it following the format below.")
    public static boolean protect_prevent_spawn_creeper = false;
    public static boolean protect_prevent_spawn_skeleton = false;
    public static boolean protect_prevent_spawn_spider = false;
    public static boolean protect_prevent_spawn_giant = false;
    public static boolean protect_prevent_spawn_zombie = false;
    public static boolean protect_prevent_spawn_slime = false;
    public static boolean protect_prevent_spawn_ghast = false;
    @CustomPath("protect.prevent.spawn.pig_zombie")
    public static boolean protect_prevent_spawn_pig_zombie = false;
    public static boolean protect_prevent_spawn_enderman = false;
    @CustomPath("protect.prevent.spawn.cave_spider")
    public static boolean protect_prevent_spawn_cave_spider = false;
    public static boolean protect_prevent_spawn_silverfish = false;
    public static boolean protect_prevent_spawn_blaze = false;
    @CustomPath("protect.prevent.spawn.magma_cube")
    public static boolean protect_prevent_spawn_magma_cube = false;
    @CustomPath("protect.prevent.spawn.ender_dragon")
    public static boolean protect_prevent_spawn_ender_dragon = false;
    public static boolean protect_prevent_spawn_pig = false;
    public static boolean protect_prevent_spawn_sheep = false;
    public static boolean protect_prevent_spawn_cow = false;
    public static boolean protect_prevent_spawn_chicken = false;
    public static boolean protect_prevent_spawn_squid = false;
    public static boolean protect_prevent_spawn_wolf = false;
    @CustomPath("protect.prevent.spawn.mushroom_cow")
    public static boolean protect_prevent_spawn_mushroom_cow = false;
    public static boolean protect_prevent_spawn_snowman = false;
    public static boolean protect_prevent_spawn_ocelot = false;
    @CustomPath("protect.prevent.spawn.iron_golem")
    public static boolean protect_prevent_spawn_iron_golem = false;
    public static boolean protect_prevent_spawn_villager = false;
    public static boolean protect_prevent_spawn_wither = false;
    public static boolean protect_prevent_spawn_bat = false;
    public static boolean protect_prevent_spawn_witch = false;
    public static boolean protect_prevent_spawn_horse = false;
    public static boolean protect_prevent_spawn_phantom = false;

    @SectionComment({"creeper:Maximum height the creeper should explode. -1 allows them to explode everywhere.",
    "creeper:Set prevent.creeper-explosion to true, if you want to disable creeper explosions."})
    public static int protect_prevent_creeper_maxHeight = -1;

    @SectionComment("disable:Disable various default physics and behaviors.")
    @ConfigurationComment("Should fall damage be disabled?")
    public static boolean protect_prevent_disable_fall = false;
    @ConfigurationComment({"Users with the essentials.protect.pvp permission will still be able to attack each other if this is set to true.",
    "They will be unable to attack users without that same permission node."})
    public static boolean protect_prevent_disable_pvp = false;
    @ConfigurationComment({"Should drowning damage be disabled?",
    "(Split into two behaviors; generally, you want both set to the same value.)"})
    public static boolean protect_prevent_disable_drown = false;
    public static boolean protect_prevent_disable_suffocate = false;
    @ConfigurationComment("Should damage via lava be disabled?  Items that fall into lava will still burn to a crisp. ;)")
    public static boolean protect_prevent_disable_lavadmg = false;
    @ConfigurationComment("Should arrow damage be disabled?")
    public static boolean protect_prevent_disable_projectiles = false;
    @ConfigurationComment("This will disable damage from touching cacti.")
    public static boolean protect_prevent_disable_contactdmg = false;
    @ConfigurationComment("Burn, baby, burn!  Should fire damage be disabled?")
    public static boolean protect_prevent_disable_firedmg = false;
    @ConfigurationComment("Should the damage after hit by a lightning be disabled?")
    public static boolean protect_prevent_disable_lightning = false;
    @ConfigurationComment("Should Wither damage be disabled?")
    public static boolean protect_prevent_disable_wither = false;
    @SectionComment("weather:Disable weather options?")
    public static boolean protect_prevent_disable_weather_storm = false;
    public static boolean protect_prevent_disable_weather_thunder = false;
    public static boolean protect_prevent_disable_weather_lightning = false;
    @SectionComment({"###########################################################",
    " +------------------------------------------------------+ #",
    " |                EssentialsX AntiBuild                 | #",
    " +------------------------------------------------------+ #",
    "###########################################################",
    "",
    "You need to install EssentialsX AntiBuild for this section to work.",
    "See https://essentialsx.net/wiki/Module-Breakdown.html and http://wiki.ess3.net/wiki/AntiBuild for more information.",
    "",
    "Should people without the essentials.build permission be allowed to build?",
    "Set true to disable building for those people.",
    "Setting to false means EssentialsAntiBuild will never prevent you from building."})
    public static boolean protect_prevent_disable_build = false;
    @ConfigurationComment({"Should people without the essentials.build permission be allowed to use items?",
    "Set true to disable using for those people.",
    "Setting to false means EssentialsAntiBuild will never prevent you from using items."})
    public static boolean protect_prevent_disable_use = true;
    @ConfigurationComment("Should we warn people when they are not allowed to build?")
    public static boolean protect_prevent_disable_warnOnBuildDisallow = true;
    @SectionComment({"alert:For which block types would you like to be alerted?",
    "alert:You can find a list of items at https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html."})
    @Parser("csv:materialenum")
    public static List<Material> protect_prevent_alert_onPlacement = Arrays.asList(Material.LAVA, Material.TNT, Material.LAVA_BUCKET);
    @Parser("csv:materialenum")
    public static List<Material> protect_prevent_alert_onUse = Collections.singletonList(Material.LAVA_BUCKET);
    @Parser("csv:materialenum")
    public static List<Material> protect_prevent_alert_onBreak = new ArrayList<>();
    @ConfigurationComment("Which blocks should people be prevented from placing?")
    @Parser("csv:materialenum")
    public static List<Material> protect_prevent_blacklist_placement = Arrays.asList(Material.LAVA, Material.TNT, Material.LAVA_BUCKET);
    @ConfigurationComment("Which items should people be prevented from using?")
    @Parser("csv:materialenum")
    public static List<Material> protect_prevent_blacklist_usage = Collections.singletonList(Material.LAVA_BUCKET);
    @ConfigurationComment("Which blocks should people be prevented from breaking?")
    @Parser("csv:materialenum")
    public static List<Material> protect_prevent_blacklist_break = new ArrayList<>();
    @ConfigurationComment("Which blocks should not be pushed by pistons?")
    @Parser("csv:materialenum")
    public static List<Material> protect_prevent_blacklist_piston = new ArrayList<>();
    @ConfigurationComment("Which blocks should not be dispensed by dispensers")
    @Parser("csv:materialenum")
    public static List<Material> protect_prevent_blacklist_dispenser = new ArrayList<>();

    @SectionComment({"###########################################################",
    " +------------------------------------------------------+ #",
    " |            EssentialsX Spawn + New Players           | #",
    " +------------------------------------------------------+ #",
    "###########################################################",
    "",
    "You need to install EssentialsX Spawn for this section to work.",
    "See https://essentialsx.net/wiki/Module-Breakdown.html for more information."})
    @ConfigurationComment({"Should we announce to the server when someone logs in for the first time?",
    "If so, use this format, replacing {DISPLAYNAME} with the player name.",
    "If not, set to ''"})
    public static String newbies_announceFormat = "&dWelcome {DISPLAYNAME}&d to the server!";
    @ConfigurationComment({"When we spawn for the first time, which spawnpoint do we use?",
    "Set to \"none\" if you want to use the spawn point of the world."})
    public static String newbies_spawnpoint = "newbies";
    @ConfigurationComment({"Do we want to give users anything on first join? Set to '' to disable",
    "This kit will be given regardless of cost and permissions, and will not trigger the kit delay."})
    public static String newbies_kit = "tools";

    @ConfigurationComment({"What priority should we use for handling respawns?",
    "Set this to none, if you want vanilla respawning behaviour.",
    "Set this to lowest, if you want Multiverse to handle the respawning.",
    "Set this to high, if you want EssentialsSpawn to handle the respawning.",
    "Set this to highest, if you want to force EssentialsSpawn to handle the respawning."})
    @IgnoreWhenNull
    public static EssEventPriority respawnListenerPriority = EssEventPriority.HIGH;
    @ConfigurationComment({"What priority should we use for handling spawning on joining the server?",
    "See respawn-listener-priority for possible values.",
    "Note: changing this may impact or break spawn-on-join functionality."})
    @IgnoreWhenNull
    public static EssEventPriority spawnJoinListenerPriority = EssEventPriority.HIGH;
    @ConfigurationComment("When users die, should they respawn at their first home or bed, instead of the spawnpoint?")
    public static boolean respawnAtHome = false;
    @ConfigurationComment("When users die, should EssentialsSpawn respect users' respawn anchors?")
    public static boolean respawnAtAnchor = false;
    @ConfigurationComment("Teleport all joining players to the spawnpoint")
    @IgnoreWhenNull
    @Parser("spawngroups")
    public static List<String> spawnOnJoin = new ArrayList<>();

    static {
        registerParser("spawngroups", new ValueParser() {
            @Override
            public <T> Object parseToJava(Class<T> type, Object object) {
                if (object == null) {
                    return null;
                }
                if (object instanceof List) {
                    List<String> groups = new ArrayList<>();
                    for (Object obj : (List<?>) object) {
                        groups.add(obj.toString());
                    }
                    return groups;
                } else if (object instanceof Boolean) {
                    return (Boolean) object ? Collections.singletonList("*") : null;
                }
                String val = (String) object;
                return !val.isEmpty() ? Collections.singletonList(val) : null;
            }

            @Override
            public String parseToYAML(Object object) {
                //noinspection unchecked
                List<String> obj = (List<String>) object;
                if (obj.isEmpty()) {
                    return "false";
                } else if (obj.get(0).equals("*")) {
                    return "true";
                } else {
                    return super.parseToYAML(obj);
                }
            }
        });
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
