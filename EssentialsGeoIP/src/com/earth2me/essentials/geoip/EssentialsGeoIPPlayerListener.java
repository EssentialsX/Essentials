package com.earth2me.essentials.geoip;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.api.IEssentials;
import com.earth2me.essentials.api.IReload;
import com.earth2me.essentials.api.IUser;
import com.earth2me.essentials.perm.Permissions;
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
import org.bukkit.plugin.Plugin;


public class EssentialsGeoIPPlayerListener implements Listener, IReload
{
	private transient LookupService ls = null;
	private static final Logger LOGGER = Logger.getLogger("Minecraft");
	private transient File databaseFile;
	private final transient ConfigHolder config;
	private final transient IEssentials ess;
	private final transient Plugin geoip;

	public EssentialsGeoIPPlayerListener(final Plugin geoip, final IEssentials ess)
	{
		super();
		this.ess = ess;
		this.geoip = geoip;
		this.config = new ConfigHolder(ess, geoip);
		onReload();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(final PlayerJoinEvent event)
	{
		final IUser u = ess.getUser(event.getPlayer());
		if (Permissions.GEOIP_HIDE.isAuthorized(u))
		{
			return;
		}
		config.acquireReadLock();
		try
		{
			final InetAddress address = event.getPlayer().getAddress().getAddress();
			final StringBuilder builder = new StringBuilder();
			if (config.getData().getDatabase().isShowCities())
			{
				final Location loc = ls.getLocation(address);
				if (loc == null)
				{
					return;
				}
				if (loc.city != null)
				{
					builder.append(loc.city).append(", ");
				}
				final String region = regionName.regionNameByCode(loc.countryCode, loc.region);
				if (region != null)
				{
					builder.append(region).append(", ");
				}
				builder.append(loc.countryName);
			}
			else
			{
				builder.append(ls.getCountry(address).getName());
			}
			if (config.getData().isShowOnWhois())
			{
				u.acquireWriteLock();
				try
				{
					u.getData().setGeolocation(builder.toString());
				}
				finally
				{
					u.unlock();
				}
			}
			if (config.getData().isShowOnLogin() && !u.isHidden())
			{
				for (Player player : event.getPlayer().getServer().getOnlinePlayers())
				{
					final IUser user = ess.getUser(player);
					if (Permissions.GEOIP_SHOW.isAuthorized(user))
					{
						user.sendMessage(_("geoipJoinFormat", user.getDisplayName(), builder.toString()));
					}
				}
			}
		}
		finally
		{
			config.unlock();
		}
	}

	@Override
	public final void onReload()
	{
		config.onReload();
		config.acquireReadLock();
		try
		{
			if (config.getData().getDatabase().isShowCities())
			{
				databaseFile = new File(geoip.getDataFolder(), "GeoIPCity.dat");
			}
			else
			{
				databaseFile = new File(geoip.getDataFolder(), "GeoIP.dat");
			}
			if (!databaseFile.exists())
			{
				if (config.getData().getDatabase().isDownloadIfMissing())
				{
					if (config.getData().getDatabase().isShowCities())
					{
						downloadDatabase(config.getData().getDatabase().getDownloadUrlCity());
					}
					else
					{
						downloadDatabase(config.getData().getDatabase().getDownloadUrl());
					}
				}
				else
				{
					LOGGER.log(Level.SEVERE, _("cantFindGeoIpDB"));
					return;
				}
			}
			try
			{
				ls = new LookupService(databaseFile);
			}
			catch (IOException ex)
			{
				LOGGER.log(Level.SEVERE, _("cantReadGeoIpDB"), ex);
			}
		}
		finally
		{
			config.unlock();
		}
	}

	private void downloadDatabase(final String url)
	{
		if (url == null || url.isEmpty())
		{
			LOGGER.log(Level.SEVERE, _("geoIpUrlEmpty"));
			return;
		}
		InputStream input = null;
		OutputStream output = null;
		try
		{
			LOGGER.log(Level.INFO, _("downloadingGeoIp"));
			final URL downloadUrl = new URL(url);
			final URLConnection conn = downloadUrl.openConnection();
			conn.setConnectTimeout(10000);
			conn.connect();
			input = conn.getInputStream();
			if (url.endsWith(".gz"))
			{
				input = new GZIPInputStream(input);
			}
			output = new FileOutputStream(databaseFile);
			final byte[] buffer = new byte[2048];
			int length = input.read(buffer);
			while (length >= 0)
			{
				output.write(buffer, 0, length);
				length = input.read(buffer);
			}
			input.close();
			output.close();
		}
		catch (MalformedURLException ex)
		{
			LOGGER.log(Level.SEVERE, _("geoIpUrlInvalid"), ex);
		}
		catch (IOException ex)
		{
			LOGGER.log(Level.SEVERE, _("connectionFailed"), ex);
		}
		finally
		{
			if (output != null)
			{
				try
				{
					output.close();
				}
				catch (IOException ex)
				{
					LOGGER.log(Level.SEVERE, _("connectionFailed"), ex);
				}
			}
			if (input != null)
			{
				try
				{
					input.close();
				}
				catch (IOException ex)
				{
					LOGGER.log(Level.SEVERE, _("connectionFailed"), ex);
				}
			}
		}
	}
}
