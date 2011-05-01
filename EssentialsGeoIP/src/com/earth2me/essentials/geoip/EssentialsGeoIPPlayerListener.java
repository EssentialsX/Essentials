package com.earth2me.essentials.geoip;

import com.earth2me.essentials.EssentialsConf;
import com.earth2me.essentials.IConf;
import com.earth2me.essentials.User;
import com.maxmind.geoip.Location;
import com.maxmind.geoip.LookupService;
import com.maxmind.geoip.regionName;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;


public class EssentialsGeoIPPlayerListener extends PlayerListener implements IConf
{
	LookupService ls = null;
	private static final Logger logger = Logger.getLogger("Minecraft");
	File databaseFile;
	File dataFolder;
	EssentialsConf config;

	public EssentialsGeoIPPlayerListener(File dataFolder)
	{
		this.dataFolder = dataFolder;
		this.config = new EssentialsConf(new File(dataFolder, "config.yml"));
		config.setTemplateName("/config.yml", EssentialsGeoIP.class);
		reloadConfig();
	}

	@Override
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		User u = User.get(event.getPlayer());
		if (u.isAuthorized("essentials.geoip.hide"))
		{
			return;
		}
		InetAddress address = event.getPlayer().getAddress().getAddress();
		StringBuilder sb = new StringBuilder();
		if (config.getBoolean("database.show-cities", false))
		{
			Location loc = ls.getLocation(address);
			if (loc == null)
			{
				return;
			}
			if (loc.city != null)
			{
				sb.append(loc.city).append(", ");
			}
			String region = regionName.regionNameByCode(loc.countryCode, loc.region);
			if (region != null)
			{
				sb.append(region).append(", ");
			}
			sb.append(loc.countryName);
		}
		else
		{
			sb.append(ls.getCountry(address).getName());
		}
		if (config.getBoolean("show-on-whois", true))
		{
			User.get(event.getPlayer()).setMetadata("location", sb.toString());
		}
		if (config.getBoolean("show-on-login", true))
		{
			event.getPlayer().getServer().broadcastMessage("Player " + event.getPlayer().getDisplayName() + " comes from " + sb.toString());
		}
	}

	@Override
	public final void reloadConfig()
	{
		config.load();

		if (config.getBoolean("database.show-cities", false))
		{
			databaseFile = new File(dataFolder, "GeoIPCity.dat");
		}
		else
		{
			databaseFile = new File(dataFolder, "GeoIP.dat");
		}
		if (!databaseFile.exists())
		{
			if (config.getBoolean("database.download-if-missing", true))
			{
				downloadDatabase();
			}
			else
			{
				logger.log(Level.SEVERE, "Can't find GeoIP database!");
				return;
			}
		}
		try
		{
			ls = new LookupService(databaseFile);
		}
		catch (IOException ex)
		{
			logger.log(Level.SEVERE, "Failed to read GeoIP database!", ex);
		}
	}

	private void downloadDatabase()
	{
		try
		{
			String url;
			if (config.getBoolean("database.show-cities", false))
			{
				url = config.getString("database.download-url-city");
			}
			else
			{
				url = config.getString("database.download-url");
			}
			if (url == null || url.isEmpty())
			{
				logger.log(Level.SEVERE, "GeoIP download url is empty.");
				return;
			}
			logger.log(Level.INFO, "Downloading GeoIP database ... this might take a while (country: 0.6 MB, city: 20MB)");
			URL downloadUrl = new URL(url);
			URLConnection conn = downloadUrl.openConnection();
			conn.setConnectTimeout(10000);
			conn.connect();
			InputStream input = conn.getInputStream();
			if (url.endsWith(".gz"))
			{
				input = new GZIPInputStream(input);
			}
			OutputStream output = new FileOutputStream(databaseFile);
			byte[] buffer = new byte[2048];
			int length = input.read(buffer);
			while (length >= 0)
			{
				output.write(buffer, 0, length);
				length = input.read(buffer);
			}
			output.close();
			input.close();
		}
		catch (MalformedURLException ex)
		{
			logger.log(Level.SEVERE, "GeoIP download url is invalid.", ex);
			return;
		}
		catch (IOException ex)
		{
			logger.log(Level.SEVERE, "Failed to open connection.", ex);
		}
	}
}
