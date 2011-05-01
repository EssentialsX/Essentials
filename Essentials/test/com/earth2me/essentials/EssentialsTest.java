package com.earth2me.essentials;

import junit.framework.TestCase;


public class EssentialsTest extends TestCase
{
	public EssentialsTest(String testName)
	{
		super(testName);
	}

	private static void should(String what)
	{
		System.out.println("Essentials should " + what);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	public void testLoadClasses()
	{
		should("make all classes accessible");
		try
		{
			ItemDb itemDb = null;
			Mob mob = null;
			NetherPortal netherPortal = null;
			OfflinePlayer offlinePlayer = null;
			Settings settings = null;
			Spawn spawn = null;
			TargetBlock targetBlock = null;
			User user = null;
			assertNull(itemDb);
			assertNull(mob);
			assertNull(netherPortal);
			assertNull(offlinePlayer);
			assertNull(settings);
			assertNull(spawn);
			assertNull(targetBlock);
			assertNull(user);
		}
		catch (Throwable ex)
		{
			fail(ex.toString());
		}
	}
}
