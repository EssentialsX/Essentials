package net.mcserverlist.bukkit;

import com.earth2me.essentials.Essentials;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Server;


public class Whitelist
{
	private static final Logger logger = Logger.getLogger("Minecraft");
	private List<String> allowed = new ArrayList<String>();
	private final Object allowedLock = new Object();
	private Server server;

	public Whitelist(Server server)
	{
		this.server = server;
	}

	public void update()
	{
		Thread thread = new Thread(new UpdateRunnable());
		thread.setDaemon(true);
		thread.start();
	}

	public boolean isAllowed(String player)
	{
		String p = player.toLowerCase();
		synchronized (allowedLock)
		{
			return allowed.contains(p);
		}
	}

	private class UpdateRunnable implements Runnable
	{
		@SuppressWarnings("CallToThreadDumpStack")
		public void run()
		{
			// Check that we aren't receiving an event inappropriately
			if (Essentials.getSettings().getMcslKey() == null || Essentials.getSettings().getMcslKey().equals("")) return;

			try
			{
				OutputStreamWriter tx = null;
				BufferedReader rx = null;
				try
				{
					// Send GET request
					URL url = new URL("http://mcserverlist.net/api/whitelist");
					// Swap line for testing purposes
					//URL url = new URL("http://localhost/mcsl/whitelist.php");
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
					http.setDoOutput(false);

					// Get the HTTP response
					rx = new BufferedReader(new InputStreamReader(http.getInputStream()));
					List<String> allowed = new ArrayList<String>();
					for (String l = ""; rx.ready(); l = rx.readLine())
					{
						if ("".equals(l)) continue;
						else if (l.startsWith("i:")) logger.info(l.substring(2));
						else if (l.startsWith("w:")) logger.warning(l.substring(2));
						else allowed.add(l.toLowerCase()); // Add to whitelist
					}
					
					synchronized (Whitelist.this.allowedLock)
					{
						Whitelist.this.allowed = allowed;
						allowed = null; // Remove our reference so that we don't accidentally use it
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
