package com.earth2me.essentials.commands;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;

public class Commanddeljail extends EssentialsCommand {

	public Commanddeljail() {
		super("deljail");
	}

	@Override
	protected void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception {
		if (args.length < 1)
		{
			user.sendMessage("§cUsage: /" + commandLabel  + " [jailname]");
			return;
		}
		user.charge(this);
		Essentials.getJail().delJail(args[0]);
		user.sendMessage("§7Jail " + args[0] + " has been removed");
	}

	@Override
	protected void run(Server server, Essentials parent, CommandSender sender, String commandLabel, String[] args) throws Exception {
		super.run(server, parent, sender, commandLabel, args);
	}
	
	
	
}
