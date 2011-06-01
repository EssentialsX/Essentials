package com.earth2me.essentials;

import com.earth2me.essentials.api.Economy;
import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;
import org.bukkit.plugin.InvalidDescriptionException;


public class EconomyTest extends TestCase
{
	private final OfflinePlayer base1;
	private final Essentials ess;

	public EconomyTest(String testName)
	{
		super(testName);
		ess = new Essentials();
		FakeServer server = new FakeServer();
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
		base1 = new OfflinePlayer("TestPlayer1");
		server.addPlayer(base1);
	}
	
	// only one big test, since we use static instances
	public void testEconomy()
	{
		// test NPC
		String npcName = "npc1";
		assertFalse(Economy.playerExists(npcName));
		assertTrue(Economy.createNPC(npcName));
		assertTrue(Economy.playerExists(npcName));
		assertNotNull(ess.getOfflineUser(npcName));
		try
		{
			Economy.removeNPC(npcName);
		}
		catch (UserDoesNotExistException ex)
		{
			fail(ex.getMessage());
		}
		assertFalse(Economy.playerExists(npcName));
		
		//test Math
		try
		{
			String playerName = "TestPlayer1";
			assertTrue(Economy.playerExists(playerName));
			Economy.resetBalance(playerName);
			assertEquals(0.0, Economy.getMoney(playerName));
			Economy.add(playerName, 10.0);
			assertEquals(10.0, Economy.getMoney(playerName));
			Economy.subtract(playerName, 5.0);
			assertEquals(5.0, Economy.getMoney(playerName));
			Economy.multiply(playerName, 2.0);
			assertEquals(10.0, Economy.getMoney(playerName));
			Economy.divide(playerName, 2.0);
			assertEquals(5.0, Economy.getMoney(playerName));
			Economy.setMoney(playerName, 10.0);
			assertEquals(10.0, Economy.getMoney(playerName));
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
		assertEquals("$1000", Economy.format(1000.0));
		assertEquals("$10", Economy.format(10.0));
		assertEquals("$10.10", Economy.format(10.10));
		assertEquals("$10.10", Economy.format(10.102));
		assertEquals("$10.11", Economy.format(10.109));
		
		
		//test Exceptions
		try
		{
			String playerName = "TestPlayer1";
			assertTrue(Economy.playerExists(playerName));
			Economy.resetBalance(playerName);
			assertEquals(0.0, Economy.getMoney(playerName));
			Economy.subtract(playerName, 5.0);
			fail();
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
			String playerName = "UnknownPlayer";
			Economy.resetBalance(playerName);
			fail();
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
