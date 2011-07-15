package com.earth2me.essentials;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.server.ServerConfigurationManager;
import org.bukkit.craftbukkit.CraftServer;


public class BanWorkaround implements IConf
{
	private transient final IEssentials ess;
	private transient final ServerConfigurationManager scm;
	private static final Logger LOGGER = Logger.getLogger("Minecraft");
	private transient final Set<String> bans = new HashSet<String>();
	private transient final Set<String> bannedIps = new HashSet<String>();

	public BanWorkaround(final IEssentials ess)
	{
		this.ess = ess;
		this.scm = ((CraftServer)ess.getServer()).getHandle();
	}
	
	public void banByName(final String name)
	{
		scm.a(name);
		reloadConfig();
	}
	
	public void unbanByName(String name)
	{
		scm.b(name);
		reloadConfig();
	}
	
	public void banByIp(final String ip)
	{
		scm.c(ip);
		reloadConfig();
	}
	
	public void unbanByIp(final String ip)
	{
		scm.d(ip);
		reloadConfig();
	}
	
	public boolean isNameBanned(final String name)
	{
		return bans.contains(name.toLowerCase());
	}
	
	public boolean isIpBanned(final String ip)
	{
		return bannedIps.contains(ip.toLowerCase());
	}
	
	public void reloadConfig()
	{
		//I don't like this but it needs to be done until CB fixors
		final File file = new File(ess.getDataFolder().getParentFile().getParentFile(), "banned-players.txt");
		try
		{
			if (!file.exists())
			{
				throw new FileNotFoundException(Util.i18n("bannedPlayersFileNotFound"));
			}

			final BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
			try
			{
				bans.clear();
				while (bufferedReader.ready())
				{

					final String line = bufferedReader.readLine().trim().toLowerCase();
					if (line.length() > 0 && line.charAt(0) == '#')
					{
						continue;
					}
					bans.add(line);

				}
			}
			catch (IOException io)
			{
				LOGGER.log(Level.SEVERE, Util.i18n("bannedPlayersFileError"), io);
			}
			finally
			{
				try
				{
					bufferedReader.close();
				}
				catch (IOException ex)
				{
					LOGGER.log(Level.SEVERE, Util.i18n("bannedPlayersFileError"), ex);
				}
			}
		}
		catch (FileNotFoundException ex)
		{
			LOGGER.log(Level.SEVERE, Util.i18n("bannedPlayersFileError"), ex);
		}

		final File ipFile = new File(ess.getDataFolder().getParentFile().getParentFile(), "banned-ips.txt");
		try
		{
			if (!ipFile.exists())
			{
				throw new FileNotFoundException(Util.i18n("bannedIpsFileNotFound"));
			}

			final BufferedReader bufferedReader = new BufferedReader(new FileReader(ipFile));
			try
			{
				bannedIps.clear();
				while (bufferedReader.ready())
				{

					final String line = bufferedReader.readLine().trim().toLowerCase();
					if (line.length() > 0 && line.charAt(0) == '#')
					{
						continue;
					}
					bannedIps.add(line);

				}
			}
			catch (IOException io)
			{
				LOGGER.log(Level.SEVERE, Util.i18n("bannedIpsFileError"), io);
			}
			finally
			{
				try
				{
					bufferedReader.close();
				}
				catch (IOException ex)
				{
					LOGGER.log(Level.SEVERE, Util.i18n("bannedIpsFileError"), ex);
				}
			}
		}
		catch (FileNotFoundException ex)
		{
			LOGGER.log(Level.SEVERE, Util.i18n("bannedIpsFileError"), ex);
		}
	}
}
