package com.earth2me.essentials;

import com.earth2me.essentials.utils.DateUtil;
import com.earth2me.essentials.utils.LocationUtil;
import com.earth2me.essentials.utils.VersionUtil;
import junit.framework.TestCase;
import org.bukkit.plugin.InvalidDescriptionException;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

public class UtilTest extends TestCase {

    public UtilTest() {
        final FakeServer server = FakeServer.getServer();
        final Essentials ess = new Essentials(server);
        try {
            ess.setupForTesting(server);
        } catch (final InvalidDescriptionException ex) {
            fail("InvalidDescriptionException");
        } catch (final IOException ex) {
            fail("IOException");
        }
    }

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

    public void testFDDnow() {
        final Calendar c = new GregorianCalendar();
        final String resp = DateUtil.formatDateDiff(c, c);
        assertEquals(resp, "now");
    }

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
    }
}
