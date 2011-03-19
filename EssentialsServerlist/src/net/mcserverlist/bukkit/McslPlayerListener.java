package net.mcserverlist.bukkit;

import com.earth2me.essentials.Essentials;
import java.io.*;
import java.net.*;
import java.util.logging.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.player.*;


public class McslPlayerListener extends PlayerListener
{
	private final static Logger logger = Logger.getLogger("Minecraft");
	private boolean running = true;
	private final Server server;
	private Thread thread;
	private Whitelist whitelist = null;
	private volatile boolean updateNeeded = true;

	@SuppressWarnings("CallToThreadStartDuringObjectConstruction")
	public McslPlayerListener(Mcsl parent)
	{
		this.server = parent.getServer();

		// Get the data from the server.properties file as the server sees it, rather than reading it manually
		try
		{
			this.whitelist = new Whitelist(server);
		}
		catch (Throwable ex)
		{
			// Disable the plugin
			logger.log(Level.WARNING, "Error encountered while initializing MCServerlist plugin.", ex);
			parent.getPluginLoader().disablePlugin(parent);
			return;
		}

		// Run Update on a set interval of 1 minute with an initial delay of 10 seconds
		thread = new Thread(new UpdateRunnable());
		thread.setDaemon(true);
		thread.start();
	}

	@Override
	@SuppressWarnings("FinalizeDeclaration")
	protected void finalize() throws Throwable
	{
		// Stop the timer
		if (thread != null && thread.isAlive())
		{
			running = false;
			thread.join();
		}

		super.finalize();
	}

	@Override
	public void onPlayerLogin(PlayerLoginEvent event)
	{
		if (event.getResult() != PlayerLoginEvent.Result.ALLOWED)
			return;
		
		Player player = event.getPlayer();

		// Check the whitelist
		if (!Essentials.getSettings().getWhitelistEnabled() || player.isOp() || whitelist == null || whitelist.isAllowed(player.getName()))
		{
			// Player is an op, there is no whitelist, or the player is whitelisted.
			return;
		}
		
		// Player is not whitelisted.
		event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "This server employs a whitelist.");
	}

	@Override
	public void onPlayerJoin(PlayerEvent event)
	{
		update();
	}

	@Override
	public void onPlayerQuit(PlayerEvent event)
	{
		update();
	}

	public boolean isWhitelistEnabled()
	{
		return whitelist != null;
	}

	public void whitelistReload()
	{
		whitelist.update();
	}

	public void update()
	{
		updateNeeded = true;
	}


	private class UpdateRunnable implements Runnable
	{
		@SuppressWarnings("SleepWhileInLoop")
		public void run()
		{
			do
			{
				if (updateNeeded)
				{
					updateNeeded = false;
					update();
				}
				
				try
				{
					Thread.sleep(60000);
				}
				catch (InterruptedException ex)
				{
					logger.info("Forcing MCServerlist update.");
				}
			}
			while (running);
		}

		@SuppressWarnings("CallToThreadDumpStack")
		public void update()
		{
			// Check that we aren't receiving an event inappropriately
			if (Essentials.getSettings().getMcslKey() == null || Essentials.getSettings().getMcslKey().equals("")) return;
			// Compile a comma-space-delimted list of players
			Player[] players = server.getOnlinePlayers();
			StringBuilder list = new StringBuilder();
			if (players.length > 0)
			{
				for (int i = 0; i < players.length; i++)
				{
					if (i > 0) list.append(", ");
					list.append(players[i].getName());
				}
			}

			try
			{
				// Compile POST data
				StringBuilder data = new StringBuilder();
				data.append("key=");
				data.append(URLEncoder.encode(Essentials.getSettings().getMcslKey(), "UTF-8"));
				data.append("&player_count=");
				data.append(Integer.toString(players.length));
				data.append("&max_players=");
				data.append(Integer.toString(server.getMaxPlayers()));
				data.append("&player_list=");
				data.append(URLEncoder.encode(list.toString(), "UTF-8"));

				OutputStreamWriter tx = null;
				BufferedReader rx = null;
				try
				{
					// Send POST request
					URL url = new URL("http://mcserverlist.net/api/update");
					// Swap line for testing purposes
					//URL url = new URL("http://localhost/mcsl/update.php");
					HttpURLConnection http = (HttpURLConnection)url.openConnection();
					http.setRequestMethod("POST");
					http.setUseCaches(false);
					http.setConnectTimeout(1000);
					http.setAllowUserInteraction(false);
					http.setInstanceFollowRedirects(true);
					http.setRequestProperty("User-Agent", "Java;Mcsl");
					http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
					http.setRequestProperty("X-Mcsl-Key", Essentials.getSettings().getMcslKey());
					http.setRequestProperty("X-Minecraft-Name", URLEncoder.encode(server.getName(), "UTF-8"));
					http.setRequestProperty("X-Minecraft-Version", server.getVersion());
					http.setDoInput(true);
					http.setDoOutput(true);
					tx = new OutputStreamWriter(http.getOutputStream());
					tx.write(data.toString());
					tx.flush();

					// Get the HTTP response
					rx = new BufferedReader(new InputStreamReader(http.getInputStream()));
					for (String l = ""; rx.ready(); l = rx.readLine())
					{
						if ("".equals(l)) continue;
						else if (l.startsWith("i:")) logger.info(l.substring(2));
						else if (l.startsWith("w:")) logger.warning(l.substring(2));
						else System.out.println(l);
					}
				}
				finally
				{
					if (tx != null) tx.close();
					if (rx != null) rx.close();
				}
			}
			catch (Exception ex)
			{
				logger.log(Level.WARNING, "Error communication with MCServerlist.", ex);
				ex.printStackTrace();
			}
		}
	}
}
