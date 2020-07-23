package com.earth2me.essentials;

import com.earth2me.essentials.configuration.Configuration;
import com.earth2me.essentials.configuration.ConfigurationComment;
import com.earth2me.essentials.configuration.ExampleValues;
import com.earth2me.essentials.configuration.Header;
import com.earth2me.essentials.configuration.Parser;

import java.io.File;
import java.util.ArrayList;
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

    public EssentialsConfig() {
        super(new File("newconfig.yml"));
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
    " The value of change-displayname (above) has to be true for this option to work."})
    public static boolean changePlayerlist = false;
}
