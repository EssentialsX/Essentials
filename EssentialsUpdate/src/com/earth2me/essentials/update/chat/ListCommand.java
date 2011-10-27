package com.earth2me.essentials.update.chat;

import org.bukkit.entity.Player;
import org.jibble.pircbot.User;


public class ListCommand implements Command
{
	@Override
	public void run(final IrcBot ircBot, final Player player)
	{
		final User[] members = ircBot.getUsers();
		final StringBuilder message = new StringBuilder();
		for (User user : members)
		{
			if (message.length() > 0)
			{
				message.append("§f, ");
			}
			if (user.isOp() || user.hasVoice())
			{
				message.append("§6");
			}
			else
			{
				message.append("§7");
			}
			message.append(user.getPrefix()).append(user.getNick());
		}
		player.sendMessage(message.toString());
	}
}
