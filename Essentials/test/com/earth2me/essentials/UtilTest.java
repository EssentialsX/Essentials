package com.earth2me.essentials;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;
import junit.framework.TestCase;
import org.bukkit.World.Environment;
import org.bukkit.plugin.InvalidDescriptionException;


public class UtilTest extends TestCase
{
	private final Essentials ess;
	private final FakeServer server;

	public UtilTest()
	{
		ess = new Essentials();
		server = new FakeServer();
		server.createWorld("testWorld", Environment.NORMAL);
		try
		{
			ess.setupForTesting(server);
		}
		catch (InvalidDescriptionException ex)
		{
			fail("InvalidDescriptionException");
		}
		catch (IOException ex)
		{
			fail("IOException");
		}
	}

	public void testSafeLocation()
	{
		Set<String> testSet = new HashSet<String>();
		int count = 0;
		int x, y, z, origX, origY, origZ;
		x = y = z = origX = origY = origZ = 0;
		int i = 0;
		while (true)
		{
			testSet.add(x + ":" + y + ":" + z);
			count++;
			i++;
			if (i >= Util.VOLUME.length)
			{
				break;
			}
			x = origX + Util.VOLUME[i].x;
			y = origY + Util.VOLUME[i].y;
			z = origZ + Util.VOLUME[i].z;
		}
		assertTrue(testSet.contains("0:0:0"));
		assertTrue(testSet.contains("3:3:3"));
		assertEquals(testSet.size(), count);
		int diameter = Util.RADIUS * 2 + 1;
		assertEquals(diameter * diameter * diameter, count);
	}

	public void testFDDnow()
	{
		Calendar c = new GregorianCalendar();
		String resp = Util.formatDateDiff(c, c);
		assertEquals(resp, "now");
	}

	public void testFDDfuture()
	{
		Calendar a, b;
		a = new GregorianCalendar(2010, 1, 1, 10, 0, 0);
		b = new GregorianCalendar(2010, 1, 1, 10, 0, 1);
		assertEquals("1 second", Util.formatDateDiff(a, b));
		a = new GregorianCalendar(2010, 1, 1, 10, 0, 0);
		b = new GregorianCalendar(2010, 1, 1, 10, 0, 2);
		assertEquals("2 seconds", Util.formatDateDiff(a, b));
		a = new GregorianCalendar(2010, 1, 1, 10, 0, 0);
		b = new GregorianCalendar(2010, 1, 1, 10, 0, 3);
		assertEquals("3 seconds", Util.formatDateDiff(a, b));
		a = new GregorianCalendar(2010, 1, 1, 10, 0, 0);
		b = new GregorianCalendar(2010, 1, 1, 10, 1, 0);
		assertEquals("1 minute", Util.formatDateDiff(a, b));
		a = new GregorianCalendar(2010, 1, 1, 10, 0, 0);
		b = new GregorianCalendar(2010, 1, 1, 10, 2, 0);
		assertEquals("2 minutes", Util.formatDateDiff(a, b));
		a = new GregorianCalendar(2010, 1, 1, 10, 0, 0);
		b = new GregorianCalendar(2010, 1, 1, 10, 3, 0);
		assertEquals("3 minutes", Util.formatDateDiff(a, b));
		a = new GregorianCalendar(2010, 1, 1, 10, 0, 0);
		b = new GregorianCalendar(2010, 1, 1, 11, 0, 0);
		assertEquals("1 hour", Util.formatDateDiff(a, b));
		a = new GregorianCalendar(2010, 1, 1, 10, 0, 0);
		b = new GregorianCalendar(2010, 1, 1, 12, 0, 0);
		assertEquals("2 hours", Util.formatDateDiff(a, b));
		a = new GregorianCalendar(2010, 1, 1, 10, 0, 0);
		b = new GregorianCalendar(2010, 1, 1, 13, 0, 0);
		assertEquals("3 hours", Util.formatDateDiff(a, b));
		a = new GregorianCalendar(2010, 1, 1, 10, 0, 0);
		b = new GregorianCalendar(2010, 1, 2, 10, 0, 0);
		assertEquals("1 day", Util.formatDateDiff(a, b));
		a = new GregorianCalendar(2010, 1, 1, 10, 0, 0);
		b = new GregorianCalendar(2010, 1, 3, 10, 0, 0);
		assertEquals("2 days", Util.formatDateDiff(a, b));
		a = new GregorianCalendar(2010, 1, 1, 10, 0, 0);
		b = new GregorianCalendar(2010, 1, 4, 10, 0, 0);
		assertEquals("3 days", Util.formatDateDiff(a, b));
		a = new GregorianCalendar(2010, 1, 1, 10, 0, 0);
		b = new GregorianCalendar(2010, 2, 1, 10, 0, 0);
		assertEquals("1 month", Util.formatDateDiff(a, b));
		a = new GregorianCalendar(2010, 1, 1, 10, 0, 0);
		b = new GregorianCalendar(2010, 3, 1, 10, 0, 0);
		assertEquals("2 months", Util.formatDateDiff(a, b));
		a = new GregorianCalendar(2010, 1, 1, 10, 0, 0);
		b = new GregorianCalendar(2010, 4, 1, 10, 0, 0);
		assertEquals("3 months", Util.formatDateDiff(a, b));
		a = new GregorianCalendar(2010, 1, 1, 10, 0, 0);
		b = new GregorianCalendar(2011, 1, 1, 10, 0, 0);
		assertEquals("1 year", Util.formatDateDiff(a, b));
		a = new GregorianCalendar(2010, 1, 1, 10, 0, 0);
		b = new GregorianCalendar(2012, 1, 1, 10, 0, 0);
		assertEquals("2 years", Util.formatDateDiff(a, b));
		a = new GregorianCalendar(2010, 1, 1, 10, 0, 0);
		b = new GregorianCalendar(2013, 1, 1, 10, 0, 0);
		assertEquals("3 years", Util.formatDateDiff(a, b));
		a = new GregorianCalendar(2010, 1, 1, 10, 0, 0);
		b = new GregorianCalendar(2011, 4, 5, 23, 38, 12);
		assertEquals("1 year 3 months 4 days", Util.formatDateDiff(a, b));
		a = new GregorianCalendar(2010, 9, 17, 23, 45, 45);
		b = new GregorianCalendar(2015, 3, 7, 10, 0, 0);
		assertEquals("4 years 5 months 20 days", Util.formatDateDiff(a, b));
		a = new GregorianCalendar(2011, 4, 31, 10, 0, 0);
		b = new GregorianCalendar(2011, 4, 31, 10, 5, 0);
		assertEquals("5 minutes", Util.formatDateDiff(a, b));
	}

	public void testFDDpast()
	{
		Calendar a, b;
		a = new GregorianCalendar(2010, 1, 1, 10, 0, 0);
		b = new GregorianCalendar(2010, 1, 1, 9, 59, 59);
		assertEquals("1 second", Util.formatDateDiff(a, b));
		a = new GregorianCalendar(2010, 1, 1, 10, 0, 0);
		b = new GregorianCalendar(2010, 1, 1, 9, 59, 58);
		assertEquals("2 seconds", Util.formatDateDiff(a, b));
		a = new GregorianCalendar(2010, 1, 1, 10, 0, 0);
		b = new GregorianCalendar(2010, 1, 1, 9, 59, 57);
		assertEquals("3 seconds", Util.formatDateDiff(a, b));
		a = new GregorianCalendar(2010, 1, 1, 10, 0, 0);
		b = new GregorianCalendar(2010, 1, 1, 9, 59, 0);
		assertEquals("1 minute", Util.formatDateDiff(a, b));
		a = new GregorianCalendar(2010, 1, 1, 10, 0, 0);
		b = new GregorianCalendar(2010, 1, 1, 9, 58, 0);
		assertEquals("2 minutes", Util.formatDateDiff(a, b));
		a = new GregorianCalendar(2010, 1, 1, 10, 0, 0);
		b = new GregorianCalendar(2010, 1, 1, 9, 57, 0);
		assertEquals("3 minutes", Util.formatDateDiff(a, b));
		a = new GregorianCalendar(2010, 1, 1, 10, 0, 0);
		b = new GregorianCalendar(2010, 1, 1, 9, 0, 0);
		assertEquals("1 hour", Util.formatDateDiff(a, b));
		a = new GregorianCalendar(2010, 1, 1, 10, 0, 0);
		b = new GregorianCalendar(2010, 1, 1, 8, 0, 0);
		assertEquals("2 hours", Util.formatDateDiff(a, b));
		a = new GregorianCalendar(2010, 1, 1, 10, 0, 0);
		b = new GregorianCalendar(2010, 1, 1, 7, 0, 0);
		assertEquals("3 hours", Util.formatDateDiff(a, b));
		a = new GregorianCalendar(2010, 1, 5, 10, 0, 0);
		b = new GregorianCalendar(2010, 1, 4, 10, 0, 0);
		assertEquals("1 day", Util.formatDateDiff(a, b));
		a = new GregorianCalendar(2010, 1, 5, 10, 0, 0);
		b = new GregorianCalendar(2010, 1, 3, 10, 0, 0);
		assertEquals("2 days", Util.formatDateDiff(a, b));
		a = new GregorianCalendar(2010, 1, 5, 10, 0, 0);
		b = new GregorianCalendar(2010, 1, 2, 10, 0, 0);
		assertEquals("3 days", Util.formatDateDiff(a, b));
		a = new GregorianCalendar(2010, 5, 1, 10, 0, 0);
		b = new GregorianCalendar(2010, 4, 1, 10, 0, 0);
		assertEquals("1 month", Util.formatDateDiff(a, b));
		a = new GregorianCalendar(2010, 5, 1, 10, 0, 0);
		b = new GregorianCalendar(2010, 3, 1, 10, 0, 0);
		assertEquals("2 months", Util.formatDateDiff(a, b));
		a = new GregorianCalendar(2010, 5, 1, 10, 0, 0);
		b = new GregorianCalendar(2010, 2, 1, 10, 0, 0);
		assertEquals("3 months", Util.formatDateDiff(a, b));
		a = new GregorianCalendar(2010, 1, 1, 10, 0, 0);
		b = new GregorianCalendar(2009, 1, 1, 10, 0, 0);
		assertEquals("1 year", Util.formatDateDiff(a, b));
		a = new GregorianCalendar(2010, 1, 1, 10, 0, 0);
		b = new GregorianCalendar(2008, 1, 1, 10, 0, 0);
		assertEquals("2 years", Util.formatDateDiff(a, b));
		a = new GregorianCalendar(2010, 1, 1, 10, 0, 0);
		b = new GregorianCalendar(2007, 1, 1, 10, 0, 0);
		assertEquals("3 years", Util.formatDateDiff(a, b));
		a = new GregorianCalendar(2010, 1, 1, 10, 0, 0);
		b = new GregorianCalendar(2009, 4, 5, 23, 38, 12);
		assertEquals("8 months 26 days 10 hours", Util.formatDateDiff(a, b));
		a = new GregorianCalendar(2010, 9, 17, 23, 45, 45);
		b = new GregorianCalendar(2000, 3, 7, 10, 0, 0);
		assertEquals("10 years 6 months 10 days", Util.formatDateDiff(a, b));
	}
}
