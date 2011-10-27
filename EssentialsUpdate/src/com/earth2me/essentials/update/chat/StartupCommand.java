package com.earth2me.essentials.update.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;


public class StartupCommand extends AbstractFileCommand implements Command
{
	private final transient Pattern patternStart = Pattern.compile("^[0-9 :-]+\\[INFO\\] Starting minecraft server version.*");
	private final transient Pattern patternEnd = Pattern.compile("^[0-9 :-]+\\[INFO\\] Done \\([0-9.,]+s\\)! For help, type \"help\".*");

	public StartupCommand(final Plugin plugin)
	{
		super(plugin);
	}

	@Override
	public void run(final IrcBot ircBot, final Player player)
	{
		BufferedReader page = null;
		try
		{
			page = getServerLogReader();
			final StringBuilder input = new StringBuilder();
			String line;
			boolean log = false;
			while ((line = page.readLine()) != null)
			{
				if (patternStart.matcher(line).matches())
				{
					if (input.length() > 0)
					{
						input.delete(0, input.length());
					}
					log = true;
				}
				if (log)
				{
					input.append(line).append("\n");
				}
				if (patternEnd.matcher(line).matches())
				{
					log = false;
				}
			}
			page.close();
			final String message = "Startup: " + uploadToPastie(input);
			player.sendMessage("§6" + ircBot.getNick() + ": §7" + message);
			ircBot.sendMessage(message);
		}
		catch (IOException ex)
		{
			Bukkit.getLogger().log(Level.SEVERE, null, ex);
			player.sendMessage(ex.getMessage());
		}
		finally
		{
			try
			{
				if (page != null)
				{
					page.close();
				}
			}
			catch (IOException ex)
			{
				Bukkit.getLogger().log(Level.SEVERE, null, ex);
				player.sendMessage(ex.getMessage());
			}
		}
	}
}
