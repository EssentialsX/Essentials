package com.earth2me.essentials;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.entity.Player;


class EssentialsUpdateTimer implements Runnable
{
	private transient URL url;
	private final transient IEssentials ess;
	private static final Logger LOGGER = Logger.getLogger("Minecraft");
	private final transient Pattern pattern = Pattern.compile("git-Bukkit-([0-9]+).([0-9]+).([0-9]+)-[0-9]+-[0-9a-z]+-b([0-9]+)jnks.*");

	public EssentialsUpdateTimer(final IEssentials ess)
	{
		this.ess = ess;
		try
		{
			url = new URL("http://essentialsupdate.appspot.com/check");
		}
		catch (MalformedURLException ex)
		{
			LOGGER.log(Level.SEVERE, "Invalid url!", ex);
		}
	}

	@Override
	public void run()
	{
		try
		{
			final StringBuilder builder = new StringBuilder();
			String bukkitVersion = ess.getServer().getVersion();
			final Matcher versionMatch = pattern.matcher(bukkitVersion);
			if (versionMatch.matches())
			{
				bukkitVersion = versionMatch.group(4);
			}
			builder.append("v=").append(URLEncoder.encode(ess.getDescription().getVersion(), "UTF-8"));
			builder.append("&b=").append(URLEncoder.encode(bukkitVersion, "UTF-8"));
			final URLConnection conn = url.openConnection();
			conn.setConnectTimeout(10000);
			conn.setDoOutput(true);
			conn.connect();
			final OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
			writer.write(builder.toString());
			writer.flush();
			final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			final String ret = reader.readLine();
			writer.close();
			reader.close();
			if (!ret.isEmpty() && !ret.equalsIgnoreCase("OK"))
			{
				LOGGER.log(Level.INFO, "Essentials Update-Check: " + ret);
				if (ret.startsWith("New Version"))
				{
					for (Player player : ess.getServer().getOnlinePlayers())
					{
						final User user = ess.getUser(player);
						if (user.isAuthorized("essentials.admin.notices.update"))
						{
							user.sendMessage(ret);
						}
					}
				}
			}
		}
		catch (IOException ex)
		{
			LOGGER.log(Level.SEVERE, "Failed to open connection", ex);
		}
	}
}
