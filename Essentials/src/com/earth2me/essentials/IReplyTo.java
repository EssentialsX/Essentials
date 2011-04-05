package com.earth2me.essentials;

import org.bukkit.command.CommandSender;

public interface IReplyTo {
	public void setReplyTo(CommandSender user);

	public CommandSender getReplyTo();
}
