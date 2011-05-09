package com.earth2me.essentials.commands;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.Util;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;

public class Commanddeljail extends EssentialsCommand {

	public Commanddeljail() {
		super("deljail");
	}

	@Override
	protected void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception {
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		charge(sender);
		Essentials.getJail().delJail(args[0]);
		sender.sendMessage(Util.format("deleteJail", args[0]));
	}
}
