package com.earth2me.essentials.update.chat;

import org.bukkit.entity.Player;


public interface Command
{
	void run(final IrcBot ircBot, final Player player);
}
