package com.earth2me.essentials.update.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;


public class ErrorsCommand extends AbstractFileCommand implements Command
{
	private final transient Pattern pattern = Pattern.compile("^[0-9 :-]+\\[INFO\\].*");

	public ErrorsCommand(final Plugin plugin)
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
			do
			{
				final String line = page.readLine();
				if (line == null)
				{
					break;
				}
				else
				{
					if (!pattern.matcher(line).matches())
					{
						input.append(line).append("\n");
					}
				}
			}
			while (true);
			page.close();
			final String message = "Errors: " + uploadToPastie(input);
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
