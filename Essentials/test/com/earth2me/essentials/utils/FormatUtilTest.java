package com.earth2me.essentials.utils;

import net.ess3.api.IUser;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class FormatUtilTest {

    @Test
    public void testFormatCase() {
        checkFormatPerms("&aT&Aest", "&aT&Aest");
        checkFormatPerms("§aT§Aest", "Test");

        checkFormatPerms("&aT&Aest", "§aT§Aest", "color");
        checkFormatPerms("§aT§Aest", "§aT§Aest", "color");
    }

    @Test
    public void testFormatCategoryPerms() {
        checkFormatPerms("Test", "Test");
        checkFormatPerms("Test", "Test", "color", "magic", "format");

        checkFormatPerms("&1C&2o&3l&4o&5r&6m&7a&8t&9i&ac", "&1C&2o&3l&4o&5r&6m&7a&8t&9i&ac"); // Unchanged
        checkFormatPerms("§1C§2o§3l§4o§5r§6m§7a§8t§9i§ac", "Colormatic"); // Removed
        checkFormatPerms("&1C&2o&3l&4o&5r&6m&7a&8t&9i&ac", "§1C§2o§3l§4o§5r§6m§7a§8t§9i§ac", "color"); // Converted
        checkFormatPerms("§1C§2o§3l§4o§5r§6m§7a§8t§9i§ac", "§1C§2o§3l§4o§5r§6m§7a§8t§9i§ac", "color"); // Unchanged

        checkFormatPerms("&kFUNKY LOL", "&kFUNKY LOL"); // Unchanged
        checkFormatPerms("§kFUNKY LOL", "FUNKY LOL"); // Removed
        checkFormatPerms("&kFUNKY LOL", "&kFUNKY LOL", "format"); // Unchanged
        checkFormatPerms("§kFUNKY LOL", "FUNKY LOL", "format"); // Removed
        checkFormatPerms("&kFUNKY LOL", "§kFUNKY LOL", "magic"); // Converted
        checkFormatPerms("§kFUNKY LOL", "§kFUNKY LOL", "magic"); // Unchanged

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

        checkFormatPerms("&1Te&2st", "§1Te&2st", "code.1");
        checkFormatPerms("§1Te§2st", "§1Test", "code.1");

        checkFormatPerms("&1Te&2st", "&1Te§2st", "code.2");
        checkFormatPerms("§1Te§2st", "Te§2st", "code.2");

        checkFormatPerms("&1Te&2st", "§1Te§2st", "code.1", "code.2");
        checkFormatPerms("§1Te§2st", "§1Te§2st", "code.1", "code.2");
    }

    private void checkFormatPerms(String input, String expectedOutput, String... perms) {
        IUser user = mock(IUser.class);
        for (String perm : perms) {
            when(user.isAuthorized("essentials.chat." + perm)).thenReturn(true);
        }
        assertEquals(expectedOutput, FormatUtil.formatString(user, "essentials.chat", input));
    }
}
