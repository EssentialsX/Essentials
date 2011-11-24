package com.earth2me.essentials.update;

import com.earth2me.essentials.update.Version.Type;
import java.util.TreeSet;
import junit.framework.TestCase;
import org.junit.Test;


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
		final Version verA = new Version("1.1.1");
		final Version verB = new Version("Dev1.1.2");
		final Version verC = new Version("1.1.2");
		final Version verD = new Version("1.2.0");
		final Version verE = new Version("2.0.0");
		final Version verF = new Version("Pre1.1.1.1");
		final Version verG = new Version("Dev1.2.2");
		assertTrue("Testing dev", verA.compareTo(verB) < 0);
		assertTrue("Testing dev", verB.compareTo(verA) > 0);
		assertTrue("Testing build", verA.compareTo(verC) < 0);
		assertTrue("Testing build", verC.compareTo(verA) > 0);
		assertTrue("Testing minor", verA.compareTo(verD) < 0);
		assertTrue("Testing minor", verD.compareTo(verA) > 0);
		assertTrue("Testing major", verA.compareTo(verE) < 0);
		assertTrue("Testing major", verE.compareTo(verA) > 0);
		assertTrue("Testing pre", verF.compareTo(verA) < 0);
		assertTrue("Testing pre", verA.compareTo(verF) > 0);
		assertTrue("Testing dev vs dev", verB.compareTo(verG) < 0);
		assertTrue("Testing dev vs dev", verG.compareTo(verB) > 0);
		final TreeSet<Version> set = new TreeSet<Version>();
		set.add(verA);
		set.add(verB);
		set.add(verC);
		set.add(verD);
		set.add(verE);
		set.add(verF);
		set.add(verG);
		assertEquals("Testing sorting", verF, set.pollFirst());
		assertEquals("Testing sorting", verA, set.pollFirst());
		assertEquals("Testing sorting", verC, set.pollFirst());
		assertEquals("Testing sorting", verD, set.pollFirst());
		assertEquals("Testing sorting", verE, set.pollFirst());
		assertEquals("Testing sorting", verB, set.pollFirst());
		assertEquals("Testing sorting", verG, set.pollFirst());
	}
}
