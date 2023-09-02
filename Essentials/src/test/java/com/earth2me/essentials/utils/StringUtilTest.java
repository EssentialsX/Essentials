package com.earth2me.essentials.utils;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StringUtilTest {
    static final String ABBREVIATE = "The quick brown fox jumps over the lazy dog";
    static final String STRIP_1 = "  \"JRoy\" ";
    static final String STRIP_2 = "mdcfe";
    static final String STRIP_3 = "\"     \"";
    static final String STRIP_4 = "     ";

    @Test
    public void testAbbreviate() {
        assertEquals("The quick brown fox jumps over the lazy dog", StringUtil.abbreviate(ABBREVIATE, ABBREVIATE.length() + 5), "Should not abbreviate when input string shorter than length");
        assertEquals("The quick brown fox jumps over the lazy dog", StringUtil.abbreviate(ABBREVIATE, ABBREVIATE.length()), "Should not abbreviate when input string equal to length");
        assertEquals("The quick brown fox jumps over the laz...", StringUtil.abbreviate(ABBREVIATE, ABBREVIATE.length() - 2), "Should abbreviate when input string longer than length");
        assertEquals("T...", StringUtil.abbreviate(ABBREVIATE, 4), "Should abbreviate to single letter");
        assertEquals("", StringUtil.abbreviate("", 4), "Should return empty string");
        assertThrows(IllegalArgumentException.class, () -> StringUtil.abbreviate(ABBREVIATE, 3), "Should throw exception when length is less than 4");
        assertNull(StringUtil.abbreviate(null, 10), "Should return null when input is null");
    }

    @Test
    public void testStrip() {
        assertEquals("\"JRoy\"", StringUtil.strip(STRIP_1), "Should strip whitespace");
        assertEquals("", StringUtil.strip(STRIP_4), "Should strip whitespace to empty string");
        assertEquals(STRIP_2, StringUtil.strip(STRIP_2), "Should not strip non-whitespace");

        assertEquals("Roy", StringUtil.strip(STRIP_1, " \"J"), "Should strip characters");
        assertEquals("  \"JRoy\" ", StringUtil.strip(STRIP_1, "abc"), "Should not strip any characters");
        assertEquals("     ", StringUtil.strip(STRIP_3, "\""), "Should strip characters only");

        assertEquals("", StringUtil.strip(STRIP_1, c -> true), "Should strip all characters to empty string");
        assertEquals(STRIP_1, StringUtil.strip(STRIP_1, c -> false), "Should not strip any characters");
    }

    @Test
    public void testStripToNull() {
        assertEquals("\"JRoy\"", StringUtil.stripToNull(STRIP_1), "Should strip whitespace");
        assertNotNull(StringUtil.stripToNull(STRIP_3), "Should not strip string to null");
        assertNull(StringUtil.stripToNull(STRIP_4), "Should strip whitespace-only string to null");
    }
}
