package com.earth2me.essentials;

import org.bukkit.command.CommandSender;

@Deprecated
public interface IReplyTo {
	public void setReplyTo(CommandSender user);

	public CommandSender getReplyTo();
}
