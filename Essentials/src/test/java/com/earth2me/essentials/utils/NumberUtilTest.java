package com.earth2me.essentials.utils;

import com.earth2me.essentials.commands.InvalidModifierException;
import org.junit.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;

public class NumberUtilTest {

    @Test
    public void testStringParseBDecimal() throws ParseException, InvalidModifierException {

        final BigDecimal decimal = NumberUtil.parseStringToBDecimal("10,000,000.5");
        assertEquals("10000000.5", decimal.toString());

        final BigDecimal decimal2 = NumberUtil.parseStringToBDecimal("10.000.000,5");
        assertNotEquals("10000000.5", decimal2.toString());

        final BigDecimal decimal3 = NumberUtil.parseStringToBDecimal("10000000,5");
        assertNotEquals("10000000.5", decimal3.toString());

        final BigDecimal decimal4 = NumberUtil.parseStringToBDecimal("10000000.5");
        assertEquals("10000000.5", decimal4.toString());

        final BigDecimal decimal5 = NumberUtil.parseStringToBDecimal("10000000.50000");
        assertEquals("10000000.5", decimal5.toString());

        final BigDecimal decimal6 = NumberUtil.parseStringToBDecimal(".50000");
        assertEquals("0.5", decimal6.toString());

        final BigDecimal decimal7 = NumberUtil.parseStringToBDecimal("00000.50000");
        assertEquals("0.5", decimal7.toString());

        final BigDecimal decimal8 = NumberUtil.parseStringToBDecimal(",50000");
        assertEquals("50000", decimal8.toString());

        assertThrows(InvalidModifierException.class, ()-> NumberUtil.parseStringToBDecimal("abc"));

        assertThrows(IllegalArgumentException.class, ()-> NumberUtil.parseStringToBDecimal(""));

        assertThrows(ParseException.class, ()-> NumberUtil.parseStringToBDecimal("M"));
    }

    @Test
    public void testStringParseBDecimalLocale() throws ParseException, InvalidModifierException {

        final Locale locale = Locale.GERMANY;

        final BigDecimal decimal = NumberUtil.parseStringToBDecimal("10,000,000.5", locale);
        assertNotEquals("10000000.5", decimal.toString());

        final BigDecimal decimal2 = NumberUtil.parseStringToBDecimal("10.000.000,5", locale);
        assertEquals("10000000.5", decimal2.toString());

        final BigDecimal decimal3 = NumberUtil.parseStringToBDecimal("10000000,5", locale);
        assertEquals("10000000.5", decimal3.toString());

        final BigDecimal decimal4 = NumberUtil.parseStringToBDecimal("10000000.5", locale);
        assertNotEquals("10000000.5", decimal4.toString());

        final BigDecimal decimal5 = NumberUtil.parseStringToBDecimal(",5", locale);
        assertEquals("0.5", decimal5.toString());

        final BigDecimal decimal6 = NumberUtil.parseStringToBDecimal(".50000", locale);
        assertEquals("50000", decimal6.toString());
    }
}
