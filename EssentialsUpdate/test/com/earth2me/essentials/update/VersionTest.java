package com.earth2me.essentials.update;

import com.earth2me.essentials.update.Version.Type;
import java.util.TreeSet;
import junit.framework.TestCase;
import org.junit.Test;
import static org.junit.Assert.*;


public class VersionTest extends TestCase
{
	@Test
	public void testStable()
	{
		final Version instance = new Version("1.2.3");
		assertEquals("Testing Major", 1, instance.getMajor());
		assertEquals("Testing Minor", 2, instance.getMinor());
		assertEquals("Testing Build", 3, instance.getBuild());
		assertEquals("Testing Type", Type.STABLE, instance.getType());
	}

	@Test
	public void testDev()
	{
		final Version instance = new Version("Dev2.3.4");
		assertEquals("Testing Major", 2, instance.getMajor());
		assertEquals("Testing Minor", 3, instance.getMinor());
		assertEquals("Testing Build", 4, instance.getBuild());
		assertEquals("Testing Type", Type.DEVELOPER, instance.getType());
	}

	@Test
	public void testTeamCity()
	{
		final Version instance = new Version("Teamcity");
		assertEquals("Testing Type", Type.DEVELOPER, instance.getType());
	}

	@Test
	public void testPre()
	{
		final Version instance = new Version("Pre5.7.400.2");
		assertEquals("Testing Major", 5, instance.getMajor());
		assertEquals("Testing Minor", 7, instance.getMinor());
		assertEquals("Testing Build", 400, instance.getBuild());
		assertEquals("Testing Type", Type.PREVIEW, instance.getType());
	}

	@Test
	public void testCompareTo()
	{
		Version a = new Version("1.1.1");
		Version b = new Version("Dev1.1.2");
		Version c = new Version("1.1.2");
		Version d = new Version("1.2.0");
		Version e = new Version("2.0.0");
		Version f = new Version("Pre1.1.1.1");
		Version g = new Version("Dev1.2.2");
		assertTrue("Testing dev", a.compareTo(b) < 0);
		assertTrue("Testing dev", b.compareTo(a) > 0);
		assertTrue("Testing build", a.compareTo(c) < 0);
		assertTrue("Testing build", c.compareTo(a) > 0);
		assertTrue("Testing minor", a.compareTo(d) < 0);
		assertTrue("Testing minor", d.compareTo(a) > 0);
		assertTrue("Testing major", a.compareTo(e) < 0);
		assertTrue("Testing major", e.compareTo(a) > 0);
		assertTrue("Testing pre", f.compareTo(a) < 0);
		assertTrue("Testing pre", a.compareTo(f) > 0);
		assertTrue("Testing dev vs dev", b.compareTo(g) < 0);
		assertTrue("Testing dev vs dev", g.compareTo(b) > 0);
		final TreeSet<Version> set = new TreeSet<Version>();
		set.add(a);
		set.add(b);
		set.add(c);
		set.add(d);
		set.add(e);
		set.add(f);
		set.add(g);
		assertEquals("Testing sorting", f, set.pollFirst());
		assertEquals("Testing sorting", a, set.pollFirst());
		assertEquals("Testing sorting", c, set.pollFirst());
		assertEquals("Testing sorting", d, set.pollFirst());
		assertEquals("Testing sorting", e, set.pollFirst());
		assertEquals("Testing sorting", b, set.pollFirst());
		assertEquals("Testing sorting", g, set.pollFirst());
	}
}
