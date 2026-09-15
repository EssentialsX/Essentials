package com.earth2me.essentials;

import com.earth2me.essentials.config.entities.CommandWarmup;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CommandWarmupTest {

    @Test
    public void testCommandWarmupCreation() {
        System.out.println("CommandWarmupTest: testCommandWarmupCreation");
        
        // Create a command warmup with 5 second delay
        final CommandWarmup warmup = new CommandWarmup();
        final Pattern pattern = Pattern.compile("home");
        warmup.pattern(pattern);
        warmup.value(5000L);
        
        assertNotNull(warmup);
        assertEquals(pattern.pattern(), warmup.pattern().pattern());
        assertEquals(5000L, warmup.value());
        assertFalse(warmup.isIncomplete());
    }

    @Test
    public void testCommandWarmupIncomplete() {
        System.out.println("CommandWarmupTest: testCommandWarmupIncomplete");
        
        // Create an incomplete warmup (null pattern)
        final CommandWarmup warmup = new CommandWarmup();
        warmup.value(5000L);
        // Don't set pattern, leaving it null
        
        assertNotNull(warmup);
        assertTrue(warmup.isIncomplete());
    }

    @Test
    public void testCommandWarmupWithRegexPattern() {
        System.out.println("CommandWarmupTest: testCommandWarmupWithRegexPattern");
        
        // Test regex pattern like /^home-.*/
        final Pattern regexPattern = Pattern.compile("^home-.*");
        final CommandWarmup warmup = new CommandWarmup();
        warmup.pattern(regexPattern);
        warmup.value(3000L);
        
        assertNotNull(warmup);
        assertEquals(regexPattern.pattern(), warmup.pattern().pattern());
        assertEquals(3000L, warmup.value());
        
        // Test that the pattern works as expected
        assertTrue(regexPattern.matcher("home-test").matches());
        assertTrue(regexPattern.matcher("home-main").matches());
        assertFalse(regexPattern.matcher("back").matches());
    }

    @Test
    public void testWarmupValueValidation() {
        System.out.println("CommandWarmupTest: testWarmupValueValidation");
        
        final Pattern pattern = Pattern.compile("home");
        
        // Test various warmup durations
        final CommandWarmup warmup1 = new CommandWarmup();
        warmup1.pattern(pattern);
        warmup1.value(1000L); // 1 second
        
        final CommandWarmup warmup5 = new CommandWarmup();
        warmup5.pattern(pattern);
        warmup5.value(5000L); // 5 seconds
        
        final CommandWarmup warmup10 = new CommandWarmup();
        warmup10.pattern(pattern);
        warmup10.value(10000L); // 10 seconds
        
        assertEquals(1000L, warmup1.value());
        assertEquals(5000L, warmup5.value());
        assertEquals(10000L, warmup10.value());
    }

    @Test
    public void testCommandWarmupEquality() {
        System.out.println("CommandWarmupTest: testCommandWarmupEquality");
        
        final Pattern pattern = Pattern.compile("home");
        
        final CommandWarmup warmup1 = new CommandWarmup();
        warmup1.pattern(pattern);
        warmup1.value(5000L);
        
        final CommandWarmup warmup2 = new CommandWarmup();
        warmup2.pattern(Pattern.compile("home"));
        warmup2.value(5000L);
        
        // Pattern objects with same regex should have same pattern() string
        assertEquals(warmup1.pattern().pattern(), warmup2.pattern().pattern());
        assertEquals(warmup1.value(), warmup2.value());
    }

    @Test
    public void testWarmupPatternMatching() {
        System.out.println("CommandWarmupTest: testWarmupPatternMatching");
        
        // Test various pattern types
        final Pattern exactPattern = Pattern.compile("home");
        final Pattern wildcardPattern = Pattern.compile("tp.*");
        final Pattern regexPattern = Pattern.compile("^warp-[a-z]+$");
        
        // Test exact match
        assertTrue(exactPattern.matcher("home").matches());
        assertFalse(exactPattern.matcher("home2").matches());
        
        // Test wildcard pattern
        assertTrue(wildcardPattern.matcher("tp").matches());
        assertTrue(wildcardPattern.matcher("tpa").matches());
        assertTrue(wildcardPattern.matcher("tpaccept").matches());
        assertFalse(wildcardPattern.matcher("home").matches());
        
        // Test regex pattern
        assertTrue(regexPattern.matcher("warp-spawn").matches());
        assertTrue(regexPattern.matcher("warp-pvp").matches());
        assertFalse(regexPattern.matcher("warp-").matches());
        assertFalse(regexPattern.matcher("warp-123").matches());
    }

    @Test
    public void testWarmupIncompleteness() {
        System.out.println("CommandWarmupTest: testWarmupIncompleteness");
        
        // Test incomplete warmup with no pattern
        final CommandWarmup noPattern = new CommandWarmup();
        noPattern.value(5000L);
        assertTrue(noPattern.isIncomplete());
        
        // Test incomplete warmup with no value
        final CommandWarmup noValue = new CommandWarmup();
        noValue.pattern(Pattern.compile("home"));
        assertTrue(noValue.isIncomplete());
        
        // Test complete warmup
        final CommandWarmup complete = new CommandWarmup();
        complete.pattern(Pattern.compile("home"));
        complete.value(5000L);
        assertFalse(complete.isIncomplete());
    }
}
