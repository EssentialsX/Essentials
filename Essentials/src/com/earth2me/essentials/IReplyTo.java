package com.earth2me.essentials;

import org.bukkit.command.CommandSender;

public interface IReplyTo
{
	/**
	 * Sets the user to reply to
	 * @param user
	 */
	public void setReplyTo(CommandSender user);

	/**
	 * Gets the user the sender should reply to
	 * @return
	 */
	public CommandSender getReplyTo();
}
