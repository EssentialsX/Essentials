package com.earth2me.essentials;

import com.earth2me.essentials.configuration.Configuration;
import com.earth2me.essentials.configuration.ConfigurationComment;
import com.earth2me.essentials.configuration.ExampleValues;
import com.earth2me.essentials.configuration.Header;
import com.earth2me.essentials.configuration.Kleenean;
import com.earth2me.essentials.configuration.Parser;
import org.bukkit.Material;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    public static String opsNameColor = "c";
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

    @ConfigurationComment({"What to prevent from /item and /give.",
    "e.g item-spawn-blacklist: dirt,grass,stone"})
    @Parser("csv:material")
    public static Collection<Material> itemSpawnBlacklist = new ArrayList<>();
}
