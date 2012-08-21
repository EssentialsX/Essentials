package com.earth2me.essentials;

import java.io.*;
import junit.framework.TestCase;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.plugin.InvalidDescriptionException;
import org.junit.Test;


public class StorageTest extends TestCase
{
	Essentials ess;
	FakeServer server;
	World world;

	public StorageTest()
	{
		ess = new Essentials();
		server = new FakeServer();
		world = server.createWorld("testWorld", Environment.NORMAL);
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

	@Test
	public void testOldUserdata()
	{
		ExecuteTimer ext = new ExecuteTimer();
		ext.start();
		OfflinePlayer base1 = server.createPlayer("testPlayer1", ess);
		server.addPlayer(base1);
		ext.mark("fake user created");
		UserData user = (UserData)ess.getUser(base1);
		ext.mark("load empty user");
		for (int j = 0; j < 1; j++)
		{
			user.setHome("home", new Location(world, j, j, j));
		}
		ext.mark("change home 1 times");
		user.save();
		ext.mark("write user");
		user.save();
		ext.mark("write user (cached)");
		user.reloadConfig();
		ext.mark("reloaded file");
		user.reloadConfig();
		ext.mark("reloaded file (cached)");
		System.out.println(ext.end());
	}
}
