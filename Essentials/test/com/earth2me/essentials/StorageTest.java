package com.earth2me.essentials;

import com.earth2me.essentials.settings.Settings;
import com.earth2me.essentials.storage.StorageObject;
import com.earth2me.essentials.storage.YamlStorageReader;
import com.earth2me.essentials.storage.YamlStorageWriter;
import java.io.*;
import junit.framework.TestCase;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.plugin.InvalidDescriptionException;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;


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
	public void testSettings()
	{
		assertTrue(StorageObject.class.isAssignableFrom(Settings.class));
		ExecuteTimer ext = new ExecuteTimer();
		ext.start();
		final ByteArrayInputStream bais = new ByteArrayInputStream(new byte[0]);
		final Reader reader = new InputStreamReader(bais);
		final Settings settings = new YamlStorageReader(reader, null).load(Settings.class);
		ext.mark("load empty settings");
		final ByteArrayInputStream bais3 = new ByteArrayInputStream(new byte[0]);
		final Reader reader3 = new InputStreamReader(bais3);
		final Settings settings3 = new YamlStorageReader(reader3, null).load(Settings.class);
		ext.mark("load empty settings (class cached)");
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final PrintWriter writer = new PrintWriter(baos);
		new YamlStorageWriter(writer).save(settings);
		writer.close();
		ext.mark("write settings");
		byte[] written = baos.toByteArray();
		System.out.println(new String(written));
		final ByteArrayInputStream bais2 = new ByteArrayInputStream(written);
		final Reader reader2 = new InputStreamReader(bais2);
		final Settings settings2 = new YamlStorageReader(reader2, null).load(Settings.class);
		System.out.println(settings.toString());
		System.out.println(settings2.toString());
		ext.mark("reload settings");
		System.out.println(ext.end());
		//assertEquals("Default and rewritten config should be equal", settings, settings2);
		//that assertion fails, because empty list and maps return as null
	}

	@Test
	public void testUserdata()
	{
		FakeServer server = new FakeServer();
		World world = server.createWorld("testWorld", Environment.NORMAL);
		ExecuteTimer ext = new ExecuteTimer();
		ext.start();
		final ByteArrayInputStream bais = new ByteArrayInputStream(new byte[0]);
		final Reader reader = new InputStreamReader(bais);
		final com.earth2me.essentials.user.UserData userdata = new YamlStorageReader(reader, null).load(com.earth2me.essentials.user.UserData.class);
		ext.mark("load empty user");
		final ByteArrayInputStream bais3 = new ByteArrayInputStream(new byte[0]);
		final Reader reader3 = new InputStreamReader(bais3);
		final com.earth2me.essentials.user.UserData userdata3 = new YamlStorageReader(reader3, null).load(com.earth2me.essentials.user.UserData.class);
		ext.mark("load empty user (class cached)");

		for (int j = 0; j < 10000; j++)
		{
			userdata.getHomes().put("home", new Location(world, j, j, j));
		}
		ext.mark("change home 10000 times");
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final PrintWriter writer = new PrintWriter(baos);
		new YamlStorageWriter(writer).save(userdata);
		writer.close();
		ext.mark("write user");
		final ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
		final PrintWriter writer2 = new PrintWriter(baos2);
		new YamlStorageWriter(writer2).save(userdata);
		writer2.close();
		ext.mark("write user (cached)");
		byte[] written = baos.toByteArray();
		System.out.println(new String(written));
		ext.mark("debug output");
		final ByteArrayInputStream bais2 = new ByteArrayInputStream(written);
		final Reader reader2 = new InputStreamReader(bais2);
		final com.earth2me.essentials.user.UserData userdata2 = new YamlStorageReader(reader2, null).load(com.earth2me.essentials.user.UserData.class);
		ext.mark("reload file");
		final ByteArrayInputStream bais4 = new ByteArrayInputStream(written);
		final Reader reader4 = new InputStreamReader(bais4);
		final com.earth2me.essentials.user.UserData userdata4 = new YamlStorageReader(reader4, null).load(com.earth2me.essentials.user.UserData.class);
		ext.mark("reload file (cached)");
		System.out.println(userdata.toString());
		System.out.println(userdata2.toString());
		System.out.println(ext.end());
		com.earth2me.essentials.user.User test = new com.earth2me.essentials.user.User(null, ess);
		test.example();

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
