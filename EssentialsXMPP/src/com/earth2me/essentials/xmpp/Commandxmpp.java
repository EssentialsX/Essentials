package com.earth2me.essentials.xmpp;

import com.earth2me.essentials.Console;
import com.earth2me.essentials.commands.EssentialsCommand;
import com.earth2me.essentials.commands.NotEnoughArgumentsException;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Commandxmpp extends EssentialsCommand
{
	public Commandxmpp()
	{
		super("xmpp");
	}

	@Override
	protected void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 2)
		{
			throw new NotEnoughArgumentsException();
		}

		final String message = getFinalArg(args, 1);
		final String address = EssentialsXMPP.getInstance().getAddress(args[0]);
		if (address == null)
		{
			sender.sendMessage("§cThere are no players matching that name.");
		}
		else
		{
			final String senderName = sender instanceof Player ? ess.getUser(sender).getDisplayName() : Console.NAME;
			sender.sendMessage("[" + senderName + ">" + address + "] " + message);
			EssentialsXMPP.getInstance().sendMessage(address, "[" + senderName + "] " + message);
		}
	}
}
