package com.earth2me.essentials;

import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.Bukkit;


public class EssentialsUserConf extends EssentialsConf
{
	final String username;
	final UUID uuid;

	public EssentialsUserConf(final String username, final UUID uuid, final File configFile)
	{
		super(configFile);
		this.username = username;
		this.uuid = uuid;
	}

	@Override
	public boolean legacyFileExists()
	{
		File file = new File(configFile.getParentFile(), username + ".yml");
		return file.exists();
	}

	@Override
	public void convertLegacyFile()
	{
		File file = new File(configFile.getParentFile(), username + ".yml");
		try
		{
			Files.move(file, new File(configFile.getParentFile(), uuid + ".yml"));
		}
		catch (IOException ex)
		{
			Bukkit.getLogger().log(Level.WARNING, "Failed to migrate user: " + username, ex);
		}

		setProperty("lastAccountName", username);
	}
}
