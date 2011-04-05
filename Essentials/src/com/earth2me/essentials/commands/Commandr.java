package com.earth2me.essentials.commands;

import com.earth2me.essentials.*;
import com.earth2me.essentials.Console;
import com.earth2me.essentials.IReplyTo;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Commandr extends EssentialsCommand
{
	public Commandr()
	{
		super("r");
	}
	
	@Override
	public void run(Server server, Essentials parent, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			sender.sendMessage("§cUsage: /" + commandLabel + " [message]");
			return;
		}

		String message = getFinalArg(args, 0);
		IReplyTo replyTo = sender instanceof User?(User)sender:Console.getConsoleReplyTo();
		String senderName = sender instanceof User?((User)sender).getDisplayName():Console.NAME;
		CommandSender target = replyTo.getReplyTo();
		String targetName = target instanceof User?((User)target).getDisplayName():Console.NAME;

		if (target == null)
		{
			sender.sendMessage("§cYou have nobody to whom you can reply.");
		}

		charge(sender);
		sender.sendMessage("[Me -> " + targetName + "] " + message);
		target.sendMessage("[" + senderName  + " -> Me] " + message);
		replyTo.setReplyTo(target);
		if (target instanceof Player) {
			User.get((Player)target).setReplyTo(sender);
		} else {
			Console.getConsoleReplyTo().setReplyTo(sender);
		}
	}
}
