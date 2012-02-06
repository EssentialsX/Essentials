package com.earth2me.essentials;

import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;
import com.earth2me.essentials.craftbukkit.DummyOfflinePlayer;
import com.earth2me.essentials.user.User;
import java.io.IOException;
import junit.framework.TestCase;
import org.bukkit.World.Environment;
import org.bukkit.plugin.InvalidDescriptionException;
import org.junit.Test;


public class EconomyTest extends TestCase
{
	private final transient Essentials ess;
	private final static String NPCNAME = "npc1";
	private final static String PLAYERNAME = "TestPlayer1";

	public EconomyTest(final String testName)
	{
		super(testName);
		ess = new Essentials();
		final FakeServer server = new FakeServer();
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
		server.addPlayer(new User(new DummyOfflinePlayer(PLAYERNAME), ess));
	}

	// only one big test, since we use static instances
	@Test
	public void testEconomy()
	{
		// test NPC
		assertFalse("NPC does not exists", ess.getEconomy().playerExists(NPCNAME));
		assertTrue("Create NPC", ess.getEconomy().createNPC(NPCNAME));
		assertTrue("NPC exists", ess.getEconomy().playerExists(NPCNAME));
		assertNull("NPC can not be accessed", ess.getUser(NPCNAME));
		try
		{
			ess.getEconomy().removeNPC(NPCNAME);
		}
		catch (UserDoesNotExistException ex)
		{
			fail(ex.getMessage());
		}
		assertFalse("NPC can be removed",ess.getEconomy().playerExists(NPCNAME));

		//test Math
		try
		{

			assertTrue("Player exists", ess.getEconomy().playerExists(PLAYERNAME));
			ess.getEconomy().resetBalance(PLAYERNAME);
			assertEquals("Player has no money", 0.0, ess.getEconomy().getMoney(PLAYERNAME));
			ess.getEconomy().setMoney(PLAYERNAME, 10.0);
			assertEquals("Set money", 10.0, ess.getEconomy().getMoney(PLAYERNAME));
		}
		catch (NoLoanPermittedException ex)
		{
			fail(ex.getMessage());
		}
		catch (UserDoesNotExistException ex)
		{
			fail(ex.getMessage());
		}

		//test Format
		assertEquals("Format $1000", "$1000", ess.getEconomy().format(1000.0));
		assertEquals("Format $10", "$10", ess.getEconomy().format(10.0));
		assertEquals("Format $10.10", "$10.10", ess.getEconomy().format(10.10));
		assertEquals("Format $10.10", "$10.10", ess.getEconomy().format(10.102));
		assertEquals("Format $10.11", "$10.11", ess.getEconomy().format(10.109));


		//test Exceptions
		try
		{
			assertTrue("Player exists", ess.getEconomy().playerExists(PLAYERNAME));
			ess.getEconomy().resetBalance(PLAYERNAME);
			assertEquals("Reset balance", 0.0, ess.getEconomy().getMoney(PLAYERNAME));
			ess.getEconomy().setMoney(PLAYERNAME, -5.0);
			fail("Did not throw exception");
		}
		catch (NoLoanPermittedException ex)
		{
		}
		catch (UserDoesNotExistException ex)
		{
			fail(ex.getMessage());
		}

		try
		{
			ess.getEconomy().resetBalance("UnknownPlayer");
			fail("Did not throw exception");
		}
		catch (NoLoanPermittedException ex)
		{
			fail(ex.getMessage());
		}
		catch (UserDoesNotExistException ex)
		{
		}
	}
}
