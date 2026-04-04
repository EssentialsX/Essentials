package com.earth2me.essentials;

import com.earth2me.essentials.utils.DateUtil;
import com.earth2me.essentials.utils.LocationUtil;
import com.earth2me.essentials.utils.VersionUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UtilTest {

    private Essentials ess;

    @BeforeEach
    public void setUp() {
        MockBukkit.mock();
        Essentials.TESTING = true;
        ess = MockBukkit.load(Essentials.class);
        ess.getI18n().updateLocale(Locale.ENGLISH.toLanguageTag());
    }

    @AfterEach
    public void afterEach() {
        MockBukkit.unmock();
    }

    @Test
    public void testSafeLocation() {
        final Set<String> testSet = new HashSet<>();
        int count = 0;
        int x;
        int y;
        int z;
        final int origX;
        final int origY;
        final int origZ;
        x = y = z = origX = origY = origZ = 0;
        int i = 0;
        while (true) {
            testSet.add(x + ":" + y + ":" + z);
            count++;
            i++;
            if (i >= LocationUtil.VOLUME.length) {
                break;
            }
            x = origX + LocationUtil.VOLUME[i].x;
            y = origY + LocationUtil.VOLUME[i].y;
            z = origZ + LocationUtil.VOLUME[i].z;
        }
        assertTrue(testSet.contains("0:0:0"));
        assertTrue(testSet.contains("3:3:3"));
        assertEquals(testSet.size(), count);
        final int diameter = LocationUtil.RADIUS * 2 + 1;
        assertEquals(diameter * diameter * diameter, count);
    }

    @Test
    public void testFDDnow() {
        final Calendar c = new GregorianCalendar();
        final String resp = DateUtil.formatDateDiff(c, c);
        assertEquals(resp, "now");
    }

    @Test
    public void testFDDfuture() {
        Calendar a, b;
        a = new GregorianCalendar(2010, Calendar.FEBRUARY, 1, 10, 0, 0);
        b = new GregorianCalendar(2010, Calendar.FEBRUARY, 1, 10, 0, 1);
        assertEquals("1 second", DateUtil.formatDateDiff(a, b));
        a = new GregorianCalendar(2010, Calendar.FEBRUARY, 1, 10, 0, 0);
        b = new GregorianCalendar(2010, Calendar.FEBRUARY, 1, 10, 0, 2);
        assertEquals("2 seconds", DateUtil.formatDateDiff(a, b));
        a = new GregorianCalendar(2010, Calendar.FEBRUARY, 1, 10, 0, 0);
        b = new GregorianCalendar(2010, Calendar.FEBRUARY, 1, 10, 0, 3);
        assertEquals("3 seconds", DateUtil.formatDateDiff(a, b));
        a = new GregorianCalendar(2010, Calendar.FEBRUARY, 1, 10, 0, 0);
        b = new GregorianCalendar(2010, Calendar.FEBRUARY, 1, 10, 1, 0);
        assertEquals("1 minute", DateUtil.formatDateDiff(a, b));
        a = new GregorianCalendar(2010, Calendar.FEBRUARY, 1, 10, 0, 0);
        b = new GregorianCalendar(2010, Calendar.FEBRUARY, 1, 10, 2, 0);
        assertEquals("2 minutes", DateUtil.formatDateDiff(a, b));
        a = new GregorianCalendar(2010, Calendar.FEBRUARY, 1, 10, 0, 0);
        b = new GregorianCalendar(2010, Calendar.FEBRUARY, 1, 10, 3, 0);
        assertEquals("3 minutes", DateUtil.formatDateDiff(a, b));
        a = new GregorianCalendar(2010, Calendar.FEBRUARY, 1, 10, 0, 0);
        b = new GregorianCalendar(2010, Calendar.FEBRUARY, 1, 11, 0, 0);
        assertEquals("1 hour", DateUtil.formatDateDiff(a, b));
        a = new GregorianCalendar(2010, Calendar.FEBRUARY, 1, 10, 0, 0);
        b = new GregorianCalendar(2010, Calendar.FEBRUARY, 1, 12, 0, 0);
        assertEquals("2 hours", DateUtil.formatDateDiff(a, b));
        a = new GregorianCalendar(2010, Calendar.FEBRUARY, 1, 10, 0, 0);
        b = new GregorianCalendar(2010, Calendar.FEBRUARY, 1, 13, 0, 0);
        assertEquals("3 hours", DateUtil.formatDateDiff(a, b));
        a = new GregorianCalendar(2010, Calendar.FEBRUARY, 1, 10, 0, 0);
        b = new GregorianCalendar(2010, Calendar.FEBRUARY, 2, 10, 0, 0);
        assertEquals("1 day", DateUtil.formatDateDiff(a, b));
        a = new GregorianCalendar(2010, Calendar.FEBRUARY, 1, 10, 0, 0);
        b = new GregorianCalendar(2010, Calendar.FEBRUARY, 3, 10, 0, 0);
        assertEquals("2 days", DateUtil.formatDateDiff(a, b));
        a = new GregorianCalendar(2010, Calendar.FEBRUARY, 1, 10, 0, 0);
        b = new GregorianCalendar(2010, Calendar.FEBRUARY, 4, 10, 0, 0);
        assertEquals("3 days", DateUtil.formatDateDiff(a, b));
        a = new GregorianCalendar(2010, Calendar.FEBRUARY, 1, 10, 0, 0);
        b = new GregorianCalendar(2010, Calendar.MARCH, 1, 10, 0, 0);
        assertEquals("1 month", DateUtil.formatDateDiff(a, b));
        a = new GregorianCalendar(2010, Calendar.FEBRUARY, 1, 10, 0, 0);
        b = new GregorianCalendar(2010, Calendar.APRIL, 1, 10, 0, 0);
        assertEquals("2 months", DateUtil.formatDateDiff(a, b));
        a = new GregorianCalendar(2010, Calendar.FEBRUARY, 1, 10, 0, 0);
        b = new GregorianCalendar(2010, Calendar.MAY, 1, 10, 0, 0);
        assertEquals("3 months", DateUtil.formatDateDiff(a, b));
        a = new GregorianCalendar(2010, Calendar.FEBRUARY, 1, 10, 0, 0);
        b = new GregorianCalendar(2011, Calendar.FEBRUARY, 1, 10, 0, 0);
        assertEquals("1 year", DateUtil.formatDateDiff(a, b));
        a = new GregorianCalendar(2010, Calendar.FEBRUARY, 1, 10, 0, 0);
        b = new GregorianCalendar(2012, Calendar.FEBRUARY, 1, 10, 0, 0);
        assertEquals("2 years", DateUtil.formatDateDiff(a, b));
        a = new GregorianCalendar(2010, Calendar.FEBRUARY, 1, 10, 0, 0);
        b = new GregorianCalendar(2013, Calendar.FEBRUARY, 1, 10, 0, 0);
        assertEquals("3 years", DateUtil.formatDateDiff(a, b));
        a = new GregorianCalendar(2010, Calendar.FEBRUARY, 1, 10, 0, 0);
        b = new GregorianCalendar(2011, Calendar.MAY, 5, 23, 38, 12);
        assertEquals("1 year 3 months 4 days", DateUtil.formatDateDiff(a, b));
        a = new GregorianCalendar(2010, Calendar.OCTOBER, 17, 23, 45, 45);
        b = new GregorianCalendar(2015, Calendar.APRIL, 7, 10, 0, 0);
        assertEquals("4 years 5 months 20 days", DateUtil.formatDateDiff(a, b));
        a = new GregorianCalendar(2011, Calendar.MAY, 31, 10, 0, 0);
        b = new GregorianCalendar(2011, Calendar.MAY, 31, 10, 5, 0);
        assertEquals("5 minutes", DateUtil.formatDateDiff(a, b));
    }

    @Test
    public void testFDDpast() {
        Calendar a, b;
        a = new GregorianCalendar(2010, Calendar.FEBRUARY, 1, 10, 0, 0);
        b = new GregorianCalendar(2010, Calendar.FEBRUARY, 1, 9, 59, 59);
        assertEquals("1 second", DateUtil.formatDateDiff(a, b));
        a = new GregorianCalendar(2010, Calendar.FEBRUARY, 1, 10, 0, 0);
        b = new GregorianCalendar(2010, Calendar.FEBRUARY, 1, 9, 59, 58);
        assertEquals("2 seconds", DateUtil.formatDateDiff(a, b));
        a = new GregorianCalendar(2010, Calendar.FEBRUARY, 1, 10, 0, 0);
        b = new GregorianCalendar(2010, Calendar.FEBRUARY, 1, 9, 59, 57);
        assertEquals("3 seconds", DateUtil.formatDateDiff(a, b));
        a = new GregorianCalendar(2010, Calendar.FEBRUARY, 1, 10, 0, 0);
        b = new GregorianCalendar(2010, Calendar.FEBRUARY, 1, 9, 59, 0);
        assertEquals("1 minute", DateUtil.formatDateDiff(a, b));
        a = new GregorianCalendar(2010, Calendar.FEBRUARY, 1, 10, 0, 0);
        b = new GregorianCalendar(2010, Calendar.FEBRUARY, 1, 9, 58, 0);
        assertEquals("2 minutes", DateUtil.formatDateDiff(a, b));
        a = new GregorianCalendar(2010, Calendar.FEBRUARY, 1, 10, 0, 0);
        b = new GregorianCalendar(2010, Calendar.FEBRUARY, 1, 9, 57, 0);
        assertEquals("3 minutes", DateUtil.formatDateDiff(a, b));
        a = new GregorianCalendar(2010, Calendar.FEBRUARY, 1, 10, 0, 0);
        b = new GregorianCalendar(2010, Calendar.FEBRUARY, 1, 9, 0, 0);
        assertEquals("1 hour", DateUtil.formatDateDiff(a, b));
        a = new GregorianCalendar(2010, Calendar.FEBRUARY, 1, 10, 0, 0);
        b = new GregorianCalendar(2010, Calendar.FEBRUARY, 1, 8, 0, 0);
        assertEquals("2 hours", DateUtil.formatDateDiff(a, b));
        a = new GregorianCalendar(2010, Calendar.FEBRUARY, 1, 10, 0, 0);
        b = new GregorianCalendar(2010, Calendar.FEBRUARY, 1, 7, 0, 0);
        assertEquals("3 hours", DateUtil.formatDateDiff(a, b));
        a = new GregorianCalendar(2010, Calendar.FEBRUARY, 5, 10, 0, 0);
        b = new GregorianCalendar(2010, Calendar.FEBRUARY, 4, 10, 0, 0);
        assertEquals("1 day", DateUtil.formatDateDiff(a, b));
        a = new GregorianCalendar(2010, Calendar.FEBRUARY, 5, 10, 0, 0);
        b = new GregorianCalendar(2010, Calendar.FEBRUARY, 3, 10, 0, 0);
        assertEquals("2 days", DateUtil.formatDateDiff(a, b));
        a = new GregorianCalendar(2010, Calendar.FEBRUARY, 5, 10, 0, 0);
        b = new GregorianCalendar(2010, Calendar.FEBRUARY, 2, 10, 0, 0);
        assertEquals("3 days", DateUtil.formatDateDiff(a, b));
        a = new GregorianCalendar(2010, Calendar.JUNE, 1, 10, 0, 0);
        b = new GregorianCalendar(2010, Calendar.MAY, 1, 10, 0, 0);
        assertEquals("1 month", DateUtil.formatDateDiff(a, b));
        a = new GregorianCalendar(2010, Calendar.JUNE, 1, 10, 0, 0);
        b = new GregorianCalendar(2010, Calendar.APRIL, 1, 10, 0, 0);
        assertEquals("2 months", DateUtil.formatDateDiff(a, b));
        a = new GregorianCalendar(2010, Calendar.JUNE, 1, 10, 0, 0);
        b = new GregorianCalendar(2010, Calendar.MARCH, 1, 10, 0, 0);
        assertEquals("3 months", DateUtil.formatDateDiff(a, b));
        a = new GregorianCalendar(2010, Calendar.FEBRUARY, 1, 10, 0, 0);
        b = new GregorianCalendar(2009, Calendar.FEBRUARY, 1, 10, 0, 0);
        assertEquals("1 year", DateUtil.formatDateDiff(a, b));
        a = new GregorianCalendar(2010, Calendar.FEBRUARY, 1, 10, 0, 0);
        b = new GregorianCalendar(2008, Calendar.FEBRUARY, 1, 10, 0, 0);
        assertEquals("2 years", DateUtil.formatDateDiff(a, b));
        a = new GregorianCalendar(2010, Calendar.FEBRUARY, 1, 10, 0, 0);
        b = new GregorianCalendar(2007, Calendar.FEBRUARY, 1, 10, 0, 0);
        assertEquals("3 years", DateUtil.formatDateDiff(a, b));
        a = new GregorianCalendar(2010, Calendar.FEBRUARY, 1, 10, 0, 0);
        b = new GregorianCalendar(2009, Calendar.MAY, 5, 23, 38, 12);
        assertEquals("8 months 26 days 10 hours", DateUtil.formatDateDiff(a, b));
        a = new GregorianCalendar(2010, Calendar.OCTOBER, 17, 23, 45, 45);
        b = new GregorianCalendar(2000, Calendar.APRIL, 7, 10, 0, 0);
        assertEquals("10 years 6 months 10 days", DateUtil.formatDateDiff(a, b));
    }

    @Test
    public void testVer() {
        VersionUtil.BukkitVersion v;
        v = VersionUtil.BukkitVersion.fromString("1.13.2-R0.1");
        assertEquals(v.getMajor(), 1);
        assertEquals(v.getMinor(), 13);
        assertEquals(v.getPatch(), 2);
        assertEquals(v.getRevision(), 0.1);
        assertEquals(v.getPrerelease(), -1);
        assertEquals(v.getReleaseCandidate(), -1);
        v = VersionUtil.BukkitVersion.fromString("1.9-R1.4"); // not real
        assertEquals(v.getMajor(), 1);
        assertEquals(v.getMinor(), 9);
        assertEquals(v.getPatch(), 0);
        assertEquals(v.getRevision(), 1.4);
        assertEquals(v.getPrerelease(), -1);
        assertEquals(v.getReleaseCandidate(), -1);
        v = VersionUtil.BukkitVersion.fromString("1.14-pre5");
        assertEquals(v.getMajor(), 1);
        assertEquals(v.getMinor(), 14);
        assertEquals(v.getPatch(), 0);
        assertEquals(v.getRevision(), 0.0);
        assertEquals(v.getPrerelease(), 5);
        assertEquals(v.getReleaseCandidate(), -1);
        v = VersionUtil.BukkitVersion.fromString("1.13.2-pre1-R0.1"); // not real
        assertEquals(v.getMajor(), 1);
        assertEquals(v.getMinor(), 13);
        assertEquals(v.getPatch(), 2);
        assertEquals(v.getRevision(), 0.1);
        assertEquals(v.getPrerelease(), 1);
        assertEquals(v.getReleaseCandidate(), -1);
        v = VersionUtil.BukkitVersion.fromString("1.14.3-SNAPSHOT");
        assertEquals(v.getMajor(), 1);
        assertEquals(v.getMinor(), 14);
        assertEquals(v.getPatch(), 3);
        assertEquals(v.getRevision(), 0.0);
        assertEquals(v.getPrerelease(), -1);
        assertEquals(v.getReleaseCandidate(), -1);
        v = VersionUtil.BukkitVersion.fromString("1.18-rc3-R0.1-SNAPSHOT");
        assertEquals(v.getMajor(), 1);
        assertEquals(v.getMinor(), 18);
        assertEquals(v.getPatch(), 0);
        assertEquals(v.getRevision(), 0.1);
        assertEquals(v.getPrerelease(), -1);
        assertEquals(v.getReleaseCandidate(), 3);
        // New versioning format (26.x)
        v = VersionUtil.BukkitVersion.fromString("26.1-R0.1-SNAPSHOT");
        assertEquals(v.getMajor(), 26);
        assertEquals(v.getMinor(), 1);
        assertEquals(v.getPatch(), 0);
        assertEquals(v.getRevision(), 0.1);
        assertEquals(v.getSnapshotRelease(), -1);
        assertEquals(v.getPrerelease(), -1);
        assertEquals(v.getReleaseCandidate(), -1);
        v = VersionUtil.BukkitVersion.fromString("26.1.1-R0.1-SNAPSHOT");
        assertEquals(v.getMajor(), 26);
        assertEquals(v.getMinor(), 1);
        assertEquals(v.getPatch(), 1);
        assertEquals(v.getRevision(), 0.1);
        assertEquals(v.getSnapshotRelease(), -1);
        assertEquals(v.getPrerelease(), -1);
        assertEquals(v.getReleaseCandidate(), -1);
        v = VersionUtil.BukkitVersion.fromString("26.1-snapshot-11-R0.1-SNAPSHOT");
        assertEquals(v.getMajor(), 26);
        assertEquals(v.getMinor(), 1);
        assertEquals(v.getPatch(), 0);
        assertEquals(v.getRevision(), 0.1);
        assertEquals(v.getSnapshotRelease(), 11);
        assertEquals(v.getPrerelease(), -1);
        assertEquals(v.getReleaseCandidate(), -1);
        v = VersionUtil.BukkitVersion.fromString("26.1-pre-3-R0.1-SNAPSHOT");
        assertEquals(v.getMajor(), 26);
        assertEquals(v.getMinor(), 1);
        assertEquals(v.getPatch(), 0);
        assertEquals(v.getRevision(), 0.1);
        assertEquals(v.getSnapshotRelease(), -1);
        assertEquals(v.getPrerelease(), 3);
        assertEquals(v.getReleaseCandidate(), -1);
        v = VersionUtil.BukkitVersion.fromString("26.1-rc-2-R0.1-SNAPSHOT");
        assertEquals(v.getMajor(), 26);
        assertEquals(v.getMinor(), 1);
        assertEquals(v.getPatch(), 0);
        assertEquals(v.getRevision(), 0.1);
        assertEquals(v.getSnapshotRelease(), -1);
        assertEquals(v.getPrerelease(), -1);
        assertEquals(v.getReleaseCandidate(), 2);
        // Old format versions are always lower than new format versions
        assertTrue(VersionUtil.BukkitVersion.fromString("1.21.11-R0.1-SNAPSHOT")
            .isLowerThan(VersionUtil.BukkitVersion.fromString("26.1-R0.1-SNAPSHOT")));
        assertTrue(VersionUtil.BukkitVersion.fromString("26.1-R0.1-SNAPSHOT")
            .isHigherThan(VersionUtil.BukkitVersion.fromString("1.21.11-R0.1-SNAPSHOT")));
        assertTrue(VersionUtil.BukkitVersion.fromString("1.21.11-R0.1-SNAPSHOT")
            .isLowerThan(VersionUtil.BukkitVersion.fromString("26.1-snapshot-1-R0.1-SNAPSHOT")));
        assertTrue(VersionUtil.BukkitVersion.fromString("1.21.11-R0.1-SNAPSHOT")
            .isLowerThan(VersionUtil.BukkitVersion.fromString("26.1-pre-1-R0.1-SNAPSHOT")));
        assertTrue(VersionUtil.BukkitVersion.fromString("1.21.11-R0.1-SNAPSHOT")
            .isLowerThan(VersionUtil.BukkitVersion.fromString("26.1-rc-1-R0.1-SNAPSHOT")));
        // Dev variants are considered higher than their base version (for feature checks)
        assertTrue(VersionUtil.BukkitVersion.fromString("26.1-snapshot-1-R0.1-SNAPSHOT")
            .isHigherThan(VersionUtil.BukkitVersion.fromString("26.1-R0.1-SNAPSHOT")));
        assertTrue(VersionUtil.BukkitVersion.fromString("26.1-pre-1-R0.1-SNAPSHOT")
            .isHigherThan(VersionUtil.BukkitVersion.fromString("26.1-R0.1-SNAPSHOT")));
        assertTrue(VersionUtil.BukkitVersion.fromString("26.1-rc-1-R0.1-SNAPSHOT")
            .isHigherThan(VersionUtil.BukkitVersion.fromString("26.1-R0.1-SNAPSHOT")));
        // Dev variants of 26.1 are lower than 26.2
        assertTrue(VersionUtil.BukkitVersion.fromString("26.1-snapshot-99-R0.1-SNAPSHOT")
            .isLowerThan(VersionUtil.BukkitVersion.fromString("26.2-R0.1-SNAPSHOT")));
        assertTrue(VersionUtil.BukkitVersion.fromString("26.1-pre-99-R0.1-SNAPSHOT")
            .isLowerThan(VersionUtil.BukkitVersion.fromString("26.2-R0.1-SNAPSHOT")));
        assertTrue(VersionUtil.BukkitVersion.fromString("26.1-rc-99-R0.1-SNAPSHOT")
            .isLowerThan(VersionUtil.BukkitVersion.fromString("26.2-R0.1-SNAPSHOT")));
        // equalsBaseVersion: dev variants match their base version
        assertTrue(VersionUtil.BukkitVersion.fromString("26.1-snapshot-11-R0.1-SNAPSHOT")
            .equalsBaseVersion(VersionUtil.BukkitVersion.fromString("26.1-R0.1-SNAPSHOT")));
        assertTrue(VersionUtil.BukkitVersion.fromString("26.1-pre-3-R0.1-SNAPSHOT")
            .equalsBaseVersion(VersionUtil.BukkitVersion.fromString("26.1-R0.1-SNAPSHOT")));
        assertTrue(VersionUtil.BukkitVersion.fromString("26.1-rc-2-R0.1-SNAPSHOT")
            .equalsBaseVersion(VersionUtil.BukkitVersion.fromString("26.1-R0.1-SNAPSHOT")));
        // equalsBaseVersion: different minor versions don't match
        assertFalse(VersionUtil.BukkitVersion.fromString("26.2-snapshot-1-R0.1-SNAPSHOT")
            .equalsBaseVersion(VersionUtil.BukkitVersion.fromString("26.1-R0.1-SNAPSHOT")));
        // equalsBaseVersion: different patch versions don't match
        assertFalse(VersionUtil.BukkitVersion.fromString("26.1.1-R0.1-SNAPSHOT")
            .equalsBaseVersion(VersionUtil.BukkitVersion.fromString("26.1-R0.1-SNAPSHOT")));
        // toString roundtrip with new PaperMC format
        assertEquals("26.1-snapshot-11-R0.1",
            VersionUtil.BukkitVersion.fromString("26.1-snapshot-11-R0.1-SNAPSHOT").toString());
        assertEquals("26.1-pre-3-R0.1",
            VersionUtil.BukkitVersion.fromString("26.1-pre-3-R0.1-SNAPSHOT").toString());
        assertEquals("26.1-rc-2-R0.1",
            VersionUtil.BukkitVersion.fromString("26.1-rc-2-R0.1-SNAPSHOT").toString());
        // Paper build metadata format (e.g. 26.1-rc-3.build.8-alpha)
        // Paper versions do NOT include -R0.1-SNAPSHOT suffix
        v = VersionUtil.BukkitVersion.fromString("26.1-rc-3.build.8-alpha");
        assertEquals(v.getMajor(), 26);
        assertEquals(v.getMinor(), 1);
        assertEquals(v.getPatch(), 0);
        assertEquals(v.getRevision(), 0.0);
        assertEquals(v.getSnapshotRelease(), -1);
        assertEquals(v.getPrerelease(), -1);
        assertEquals(v.getReleaseCandidate(), 3);
        assertEquals(v.getPaperBuild(), 8);
        assertEquals(v.getReleaseChannel(), "alpha");
        v = VersionUtil.BukkitVersion.fromString("26.1-pre-3.build.5-alpha");
        assertEquals(v.getMajor(), 26);
        assertEquals(v.getMinor(), 1);
        assertEquals(v.getPatch(), 0);
        assertEquals(v.getRevision(), 0.0);
        assertEquals(v.getSnapshotRelease(), -1);
        assertEquals(v.getPrerelease(), 3);
        assertEquals(v.getReleaseCandidate(), -1);
        assertEquals(v.getPaperBuild(), 5);
        assertEquals(v.getReleaseChannel(), "alpha");
        v = VersionUtil.BukkitVersion.fromString("26.1-snapshot-11.build.3-alpha");
        assertEquals(v.getMajor(), 26);
        assertEquals(v.getMinor(), 1);
        assertEquals(v.getPatch(), 0);
        assertEquals(v.getRevision(), 0.0);
        assertEquals(v.getSnapshotRelease(), 11);
        assertEquals(v.getPrerelease(), -1);
        assertEquals(v.getReleaseCandidate(), -1);
        assertEquals(v.getPaperBuild(), 3);
        assertEquals(v.getReleaseChannel(), "alpha");
        // Paper build of a base release (no Mojang specifier)
        v = VersionUtil.BukkitVersion.fromString("26.1.build.5-alpha");
        assertEquals(v.getMajor(), 26);
        assertEquals(v.getMinor(), 1);
        assertEquals(v.getPatch(), 0);
        assertEquals(v.getRevision(), 0.0);
        assertEquals(v.getSnapshotRelease(), -1);
        assertEquals(v.getPrerelease(), -1);
        assertEquals(v.getReleaseCandidate(), -1);
        assertEquals(v.getPaperBuild(), 5);
        assertEquals(v.getReleaseChannel(), "alpha");
        // Paper builds with same Mojang version: equalsBaseVersion matches
        assertTrue(VersionUtil.BukkitVersion.fromString("26.1-rc-3.build.8-alpha")
            .equalsBaseVersion(VersionUtil.BukkitVersion.fromString("26.1-R0.1-SNAPSHOT")));
        assertTrue(VersionUtil.BukkitVersion.fromString("26.1.build.5-alpha")
            .equalsBaseVersion(VersionUtil.BukkitVersion.fromString("26.1-R0.1-SNAPSHOT")));
        assertTrue(VersionUtil.BukkitVersion.fromString("26.1")
            .equalsBaseVersion(VersionUtil.BukkitVersion.fromString("26.1-R0.1-SNAPSHOT")));
        // Paper build ordering: higher build number is higher
        assertTrue(VersionUtil.BukkitVersion.fromString("26.1-rc-3.build.12-alpha")
            .isHigherThan(VersionUtil.BukkitVersion.fromString("26.1-rc-3.build.8-alpha")));
        // Paper build is higher than bare Mojang version (paperBuild -1 < 1)
        assertTrue(VersionUtil.BukkitVersion.fromString("26.1-rc-3.build.1-alpha")
            .isHigherThan(VersionUtil.BukkitVersion.fromString("26.1-rc-3-R0.1-SNAPSHOT")));
        // Paper versions: ordering still works against Bukkit versions
        assertTrue(VersionUtil.BukkitVersion.fromString("26.1-rc-3.build.8-alpha")
            .isHigherThan(VersionUtil.BukkitVersion.fromString("1.21.11-R0.1-SNAPSHOT")));
        assertTrue(VersionUtil.BukkitVersion.fromString("1.21.11-R0.1-SNAPSHOT")
            .isLowerThan(VersionUtil.BukkitVersion.fromString("26.1-rc-3.build.8-alpha")));
        // Release channel: different channels parsed correctly
        v = VersionUtil.BukkitVersion.fromString("26.1-rc-3.build.8-beta");
        assertEquals(v.getReleaseCandidate(), 3);
        assertEquals(v.getPaperBuild(), 8);
        assertEquals(v.getReleaseChannel(), "beta");
        v = VersionUtil.BukkitVersion.fromString("26.1.build.10-recommended");
        assertEquals(v.getMajor(), 26);
        assertEquals(v.getMinor(), 1);
        assertEquals(v.getPatch(), 0);
        assertEquals(v.getPaperBuild(), 10);
        assertEquals(v.getReleaseChannel(), "recommended");
        // Release channel: Bukkit/Mojang versions have no release channel
        assertNull(VersionUtil.BukkitVersion.fromString("26.1-R0.1-SNAPSHOT").getReleaseChannel());
        assertNull(VersionUtil.BukkitVersion.fromString("26.1-rc-3-R0.1-SNAPSHOT").getReleaseChannel());
        assertNull(VersionUtil.BukkitVersion.fromString("1.21.11-R0.1-SNAPSHOT").getReleaseChannel());
        // Release channel: Paper build without channel suffix
        v = VersionUtil.BukkitVersion.fromString("26.1-rc-3.build.8");
        assertEquals(v.getPaperBuild(), 8);
        assertNull(v.getReleaseChannel());
        // toString roundtrip preserves release channel
        assertEquals("26.1-rc-3.build.8-alpha-R0.0",
            VersionUtil.BukkitVersion.fromString("26.1-rc-3.build.8-alpha").toString());
        assertEquals("26.1.build.5-recommended-R0.0",
            VersionUtil.BukkitVersion.fromString("26.1.build.5-recommended").toString());
        assertEquals("26.1-pre-1.build.3-beta-R0.0",
            VersionUtil.BukkitVersion.fromString("26.1-pre-1.build.3-beta").toString());
        assertEquals("26.1-snapshot-11.build.3-alpha-R0.0",
            VersionUtil.BukkitVersion.fromString("26.1-snapshot-11.build.3-alpha").toString());
        // toString: paper build without channel omits channel suffix
        assertEquals("26.1-rc-3.build.8-R0.0",
            VersionUtil.BukkitVersion.fromString("26.1-rc-3.build.8").toString());
        // Release channel does not affect equalsBaseVersion
        assertTrue(VersionUtil.BukkitVersion.fromString("26.1-rc-3.build.8-alpha")
            .equalsBaseVersion(VersionUtil.BukkitVersion.fromString("26.1-rc-3.build.8-beta")));
        assertTrue(VersionUtil.BukkitVersion.fromString("26.1.build.5-recommended")
            .equalsBaseVersion(VersionUtil.BukkitVersion.fromString("26.1.build.5-alpha")));
        // Paper build metadata does not affect equality
        assertEquals(VersionUtil.BukkitVersion.fromString("26.1-rc-3.build.8-alpha"),
            VersionUtil.BukkitVersion.fromString("26.1-rc-3.build.12-beta"));
        assertEquals(VersionUtil.BukkitVersion.fromString("26.1.build.5-alpha"),
            VersionUtil.BukkitVersion.fromString("26.1.build.99-recommended"));
        assertEquals(VersionUtil.BukkitVersion.fromString("26.1-rc-3.build.8-alpha"),
            VersionUtil.BukkitVersion.fromString("26.1-rc-3-R0.0"));
    }
}
