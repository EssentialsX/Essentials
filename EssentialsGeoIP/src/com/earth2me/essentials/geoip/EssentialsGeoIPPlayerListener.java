package com.earth2me.essentials.geoip;

import com.earth2me.essentials.EssentialsConf;
import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.IConf;
import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.User;
import com.maxmind.geoip.Location;
import com.maxmind.geoip.LookupService;
import com.maxmind.geoip.regionName;
import java.io.*;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;


public class EssentialsGeoIPPlayerListener implements Listener, IConf
{
	LookupService ls = null;
	private static final Logger logger = Logger.getLogger("Minecraft");
	File databaseFile;
	File dataFolder;
	EssentialsConf config;
	private final transient IEssentials ess;

	public EssentialsGeoIPPlayerListener(File dataFolder, IEssentials ess)
	{
		this.ess = ess;
		this.dataFolder = dataFolder;
		this.config = new EssentialsConf(new File(dataFolder, "config.yml"));
		config.setTemplateName("/config.yml", EssentialsGeoIP.class);
		reloadConfig();
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(final PlayerJoinEvent event)
	{
		ess.scheduleAsyncDelayedTask(new Runnable()
		{
			@Override
			public void run()
			{
				delayedJoin(event.getPlayer());
			}
		});
	}

	public void delayedJoin(Player player)
	{
		User u = ess.getUser(player);
		if (u.isAuthorized("essentials.geoip.hide") || player.getAddress() == null)
		{
			return;
		}
		InetAddress address = player.getAddress().getAddress();
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
			u.setGeoLocation(sb.toString());
		}
		if (config.getBoolean("show-on-login", true) && !u.isHidden())
		{
			for (Player onlinePlayer : player.getServer().getOnlinePlayers())
			{
				User user = ess.getUser(onlinePlayer);
				if (user.isAuthorized("essentials.geoip.show"))
				{
					user.sendMessage(_("geoipJoinFormat", u.getDisplayName(), sb.toString()));
				}
			}
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
				logger.log(Level.SEVERE, _("cantFindGeoIpDB"));
				return;
			}
		}
		try
		{
			ls = new LookupService(databaseFile);
		}
		catch (IOException ex)
		{
			logger.log(Level.SEVERE, _("cantReadGeoIpDB"), ex);
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
				logger.log(Level.SEVERE, _("geoIpUrlEmpty"));
				return;
			}
			logger.log(Level.INFO, _("downloadingGeoIp"));
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
			logger.log(Level.SEVERE, _("geoIpUrlInvalid"), ex);
			return;
		}
		catch (IOException ex)
		{
			logger.log(Level.SEVERE, _("connectionFailed"), ex);
		}
	}
}
