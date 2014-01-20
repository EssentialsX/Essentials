package com.earth2me.essentials;

import com.earth2me.essentials.commands.IEssentialsCommand;
import com.earth2me.essentials.commands.NoChargeException;
import java.io.IOException;
import junit.framework.TestCase;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.InvalidDescriptionException;


public class ToggleTest extends TestCase
{
	private final OfflinePlayer base1;
	private final Essentials ess;
	private final FakeServer server;

	public ToggleTest(String testName)
	{
		super(testName);
		server = new FakeServer();
		server.createWorld("testWorld", Environment.NORMAL);
		ess = new Essentials(server);
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
		base1 = server.createPlayer("testPlayer1", ess);
		server.addPlayer(base1);
		ess.getUser(base1);
	}

	private void runCommand(String command, User user, String[] args) throws Exception
	{
		IEssentialsCommand cmd;

		try
		{
			cmd = (IEssentialsCommand)Essentials.class.getClassLoader().loadClass("com.earth2me.essentials.commands.Command" + command).newInstance();
			cmd.setEssentials(ess);
			cmd.run(server, user, command, null, args);
		}
		catch (NoChargeException ex)
		{
		}

	}

	private void runConsoleCommand(String command, String[] args) throws Exception
	{
		IEssentialsCommand cmd;

		CommandSender sender = server.getConsoleSender();

		try
		{
			cmd = (IEssentialsCommand)Essentials.class.getClassLoader().loadClass("com.earth2me.essentials.commands.Command" + command).newInstance();
			cmd.setEssentials(ess);
			cmd.run(server, new CommandSource(sender), command, null, args);
		}
		catch (NoChargeException ex)
		{
		}

	}

	public void testFlyToggle() throws Exception
	{
		User user = ess.getUser(base1);

		assertFalse(user.getAllowFlight());

		runCommand("fly", user, new String[]
				{
					"on"
				});
		assertTrue(user.getAllowFlight());

		runCommand("fly", user, new String[]
				{
					"on"
				});
		assertTrue(user.getAllowFlight());

		runCommand("fly", user, new String[]
				{
					"off"
				});
		assertFalse(user.getAllowFlight());

		runCommand("fly", user, new String[]
				{
					"off"
				});
		assertFalse(user.getAllowFlight());

		runCommand("fly", user, new String[]
				{
				});
		assertTrue(user.getAllowFlight());

		runCommand("fly", user, new String[]
				{
				});
		assertFalse(user.getAllowFlight());
	}

	public void testFlyDisOnToggle() throws Exception
	{
		User user = ess.getUser(base1);

		user.setAllowFlight(true);
		user.setFlying(true);
		assertTrue(user.isFlying());
		runCommand("fly", user, new String[]
				{
				});
		assertFalse(user.getAllowFlight());
		assertFalse(user.isFlying());
	}

	public void testGodToggle() throws Exception
	{
		User user = ess.getUser(base1);

		assertFalse(user.isGodModeEnabled());

		runCommand("god", user, new String[]
				{
					"on"
				});
		assertTrue(user.isGodModeEnabled());

		runCommand("god", user, new String[]
				{
					"on"
				});
		assertTrue(user.isGodModeEnabled());

		runCommand("god", user, new String[]
				{
					"off"
				});
		assertFalse(user.isGodModeEnabled());

		runCommand("god", user, new String[]
				{
					"off"
				});
		assertFalse(user.isGodModeEnabled());

		runCommand("god", user, new String[]
				{
				});
		assertTrue(user.isGodModeEnabled());

		runCommand("god", user, new String[]
				{
				});
		assertFalse(user.isGodModeEnabled());
	}

	public void testConsoleToggle() throws Exception
	{
		User user = ess.getUser(base1);

		assertFalse(user.getAllowFlight());

		runConsoleCommand("fly", new String[]
				{
					base1.getName(), "on"
				});
		assertTrue(user.getAllowFlight());

		runConsoleCommand("fly", new String[]
				{
					base1.getName(), "on"
				});
		assertTrue(user.getAllowFlight());

		runConsoleCommand("fly", new String[]
				{
					base1.getName(), "off"
				});
		assertFalse(user.getAllowFlight());

		runConsoleCommand("fly", new String[]
				{
					base1.getName(), "off"
				});
		assertFalse(user.getAllowFlight());

		runConsoleCommand("fly", new String[]
				{
					base1.getName()
				});
		assertTrue(user.getAllowFlight());

		runConsoleCommand("fly", new String[]
				{
					base1.getName()
				});
		assertFalse(user.getAllowFlight());
	}

	public void testAliasesToggle() throws Exception
	{
		User user = ess.getUser(base1);

		assertFalse(user.getAllowFlight());

		runConsoleCommand("fly", new String[]
				{
					base1.getName(), "enable"
				});
		assertTrue(user.getAllowFlight());

		runConsoleCommand("fly", new String[]
				{
					base1.getName(), "enable"
				});
		assertTrue(user.getAllowFlight());

		runConsoleCommand("fly", new String[]
				{
					base1.getName(), "disable"
				});
		assertFalse(user.getAllowFlight());

		runConsoleCommand("fly", new String[]
				{
					base1.getName(), "disable"
				});
		assertFalse(user.getAllowFlight());

		runConsoleCommand("fly", new String[]
				{
					base1.getName(), "1"
				});
		assertTrue(user.getAllowFlight());

		runConsoleCommand("fly", new String[]
				{
					base1.getName(), "1"
				});
		assertTrue(user.getAllowFlight());

		runConsoleCommand("fly", new String[]
				{
					base1.getName(), "0"
				});
		assertFalse(user.getAllowFlight());

		runConsoleCommand("fly", new String[]
				{
					base1.getName(), "0"
				});
		assertFalse(user.getAllowFlight());

	}
}
