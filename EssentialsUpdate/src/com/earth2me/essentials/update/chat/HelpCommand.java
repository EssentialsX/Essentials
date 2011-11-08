package com.earth2me.essentials.update.chat;

import org.bukkit.entity.Player;


public class HelpCommand implements Command
{
	@Override
	public void run(final IrcBot ircBot, final Player player)
	{
		player.sendMessage("Commands: (Note: Files send to the chat will be public viewable.)");
		player.sendMessage("!errors - Send the last server errors to the chat.");
		player.sendMessage("!startup - Send the last startup messages to the chat.");
		player.sendMessage("!config - Sends your Essentials config to the chat.");
		player.sendMessage("!list - List all players in chat.");
		player.sendMessage("!quit - Leave chat.");
	}
}
