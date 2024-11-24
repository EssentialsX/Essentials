package com.earth2me.essentials.utils;

import net.ess3.api.IUser;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FormatUtilTest {

    @Test
    public void testFormatCase() {
        checkFormatPerms("&aT&Aest", "&aT&Aest");
        checkFormatPerms("§aT§Aest", "Test");

        checkFormatPerms("&aT&Aest", "§aT§Aest", "color");
        checkFormatPerms("§aT§Aest", "§aT§Aest", "color");
    }

    @Test
    public void testFormatHexColors() {
        checkFormatPerms("&#ffffff", "&#ffffff");
        checkFormatPerms("&#ffffff", "§x§f§f§f§f§f§f", "color", "rgb");
        checkFormatPerms("&#ff0000test", "§x§f§f§0§0§0§0test", "rgb");
        checkFormatPerms("&#ff0000test", "§x§f§f§0§0§0§0test", "rgb", "color");
        checkFormatPerms("&#ff0000test", "§x§f§f§0§0§0§0test", "rgb", "white");
        checkFormatPerms("&#ff0000test", "§x§f§f§0§0§0§0test", "rgb", "black");
        checkFormatPerms("&#ff0000test", "§x§f§f§0§0§0§0test", "rgb", "black", "white");
    }

    @Test
    public void testFormatCategoryPerms() {
        checkFormatPerms("Test", "Test");
        checkFormatPerms("Test", "Test", "color", "format");

        checkFormatPerms("&1C&2o&3l&4o&5r&6m&7a&8t&9i&ac", "&1C&2o&3l&4o&5r&6m&7a&8t&9i&ac"); // Unchanged
        checkFormatPerms("§1C§2o§3l§4o§5r§6m§7a§8t§9i§ac", "Colormatic"); // Removed
        checkFormatPerms("&1C&2o&3l&4o&5r&6m&7a&8t&9i&ac", "§1C§2o§3l§4o§5r§6m§7a§8t§9i§ac", "color"); // Converted
        checkFormatPerms("§1C§2o§3l§4o§5r§6m§7a§8t§9i§ac", "§1C§2o§3l§4o§5r§6m§7a§8t§9i§ac", "color"); // Unchanged

        checkFormatPerms("&kFUNKY LOL", "§kFUNKY LOL", "magic"); // Converted
        checkFormatPerms("§kFUNKY LOL", "§kFUNKY LOL", "magic"); // Unchanged

        // Magic isn't included in the format group
        checkFormatPerms("&kFUNKY LOL", "&kFUNKY LOL"); // Unchanged
        checkFormatPerms("§kFUNKY LOL", "FUNKY LOL"); // Removed
        checkFormatPerms("&kFUNKY LOL", "&kFUNKY LOL", "format"); // Unchanged
        checkFormatPerms("§kFUNKY LOL", "FUNKY LOL", "format"); // Removed

        checkFormatPerms("&f&ltest", "&f&ltest");
        checkFormatPerms("§f§ltest", "test");
        checkFormatPerms("&f&ltest", "§f&ltest", "color");
        checkFormatPerms("§f§ltest", "§ftest", "color");
        checkFormatPerms("&f&ltest", "&f§ltest", "format");
        checkFormatPerms("§f§ltest", "§ltest", "format");
        checkFormatPerms("&f&ltest", "§f§ltest", "color", "format");
        checkFormatPerms("§f§ltest", "§f§ltest", "color", "format");
    }

    @Test
    public void testFormatCodePerms() {
        checkFormatPerms("&1Te&2st", "&1Te&2st");
        checkFormatPerms("§1Te§2st", "Test");

        checkFormatPerms("&1Te&2st", "§1Te&2st", "dark_blue");
        checkFormatPerms("§1Te§2st", "§1Test", "dark_blue");

        checkFormatPerms("&1Te&2st", "&1Te§2st", "dark_green");
        checkFormatPerms("§1Te§2st", "Te§2st", "dark_green");

        checkFormatPerms("&1Te&2st", "§1Te§2st", "dark_blue", "dark_green");
        checkFormatPerms("§1Te§2st", "§1Te§2st", "dark_blue", "dark_green");

        // Obfuscated behaves the same as magic
        checkFormatPerms("&kFUNKY LOL", "§kFUNKY LOL", "obfuscated");
        checkFormatPerms("§kFUNKY LOL", "§kFUNKY LOL", "obfuscated");
    }

    @Test
    public void testFormatAddRemovePerms() {
        checkFormatPerms("&1Te&2st&ling", "&1Te§2st&ling", "color", "-dark_blue");
        checkFormatPerms("§1Te§2st§ling", "Te§2sting", "color", "-dark_blue");

        // Nothing happens when negated without being previously present
        checkFormatPerms("&1Te&2st&ling", "&1Te§2st&ling", "color", "-dark_blue", "-bold");
        checkFormatPerms("§1Te§2st§ling", "Te§2sting", "color", "-dark_blue", "-bold");
    }

    @Test
    public void testFormatEscaping() {
        // Don't do anything to non-format codes
        checkFormatPerms("True & false", "True & false");
        checkFormatPerms("True && false", "True && false");

        // Formats are only unescaped if you have the right perms
        checkFormatPerms("This is &&a message", "This is &&a message");
        checkFormatPerms("This is &&a message", "This is &a message", "color");

        // Can't put an & before a non-escaped format
        checkFormatPerms("This is &&&a message", "This is &&&a message");
        checkFormatPerms("This is &&&a message", "This is &&a message", "color");
    }

    @Test
    public void testUnformat() {
        // Unformatting should only unformat codes which you have perms for
        checkUnformatPerms("§bMessage", "Message");
        checkUnformatPerms("§bMessage", "&bMessage", "color");

        // It should work for rgb color codes too
        checkUnformatPerms("§x§b§3§4§2§f§5This is a message", "This is a message");
        checkUnformatPerms("§x§b§3§4§2§f§5This is a message", "&#b342f5This is a message", "rgb");

        checkUnformatPerms("§x§b§3§4§2§f§5Th§eis is §aa §dmessag§5e", "This is a message");
        checkUnformatPerms("§x§b§3§4§2§f§5Th§eis is §aa §dmessag§5e", "&#b342f5This is a message", "rgb");
        checkUnformatPerms("§x§b§3§4§2§f§5Th§eis is §aa §dmessag§5e", "&#b342f5Th&eis is a message", "rgb", "yellow");
    }

    private IUser getMockUser(final String... perms) {
        final IUser user = mock(IUser.class);
        for (String perm : perms) {
            if (perm.startsWith("-")) {
                // Negated perms
                perm = perm.substring(1);
                when(user.isAuthorized("essentials.chat." + perm)).thenReturn(false);
            } else {
                when(user.isAuthorized("essentials.chat." + perm)).thenReturn(true);
            }

            when(user.isPermissionSet("essentials.chat." + perm)).thenReturn(true);
        }
        return user;
    }

    private void checkFormatPerms(final String input, final String expectedOutput, final String... perms) {
        assertEquals(expectedOutput, FormatUtil.formatString(getMockUser(perms), "essentials.chat", input));
    }

    private void checkUnformatPerms(final String input, final String expectedOutput, final String... perms) {
        assertEquals(expectedOutput, FormatUtil.unformatString(getMockUser(perms), "essentials.chat", input));
    }
}
