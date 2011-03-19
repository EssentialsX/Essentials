package com.earth2me.essentials.commands;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;

public class Commandjails extends EssentialsCommand {

	public Commandjails() {
		super("jails");
	}

	@Override
	protected void run(Server server, Essentials parent, CommandSender sender, String commandLabel, String[] args) throws Exception {
		StringBuilder jailList = new StringBuilder();
		for (String j : Essentials.getJail().getJails())
		{
			jailList.append(j);
			jailList.append(' ');
		}
		sender.sendMessage("§7" + jailList);
	}

	@Override
	protected void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception {
		StringBuilder jailList = new StringBuilder();
		for (String j : Essentials.getJail().getJails())
		{
			jailList.append(j);
			jailList.append(' ');
		}
		user.sendMessage("§7" + jailList);
	}
	
	
}
