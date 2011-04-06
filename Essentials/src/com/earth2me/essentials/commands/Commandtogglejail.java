package com.earth2me.essentials.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;


public class Commandtogglejail extends EssentialsCommand
{
	public Commandtogglejail()
	{
		super("togglejail");
	}

	@Override
	public void run(Server server, Essentials parent, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1 || args.length > 2)
		{
			sender.sendMessage("Usage: /" + commandLabel + " [player] [jailname]");
			return;
		}
		
		User p;
		try
		{
			p = User.get(server.matchPlayer(args[0]).get(0));
		}
		catch (Exception ex)
		{
			sender.sendMessage("§cThat player does not exist.");
			return;
		}

		if (p.isOp() || p.isAuthorized("essentials.jail.exempt"))
		{
			sender.sendMessage("§cYou may not jail that person");
			return;
		}
		
		if (args.length == 2 && !p.isJailed()) {
			User.charge(sender, this);
			sender.sendMessage("§7Player " + p.getName() + " " + (p.toggleJailed() ? "jailed." : "unjailed."));
			p.sendMessage("§7You have been jailed");
			p.currentJail = null;
			Essentials.getJail().sendToJail(p, args[1]);
			p.currentJail = (args[1]);
			return;
		}
		
		if (args.length == 2 && p.isJailed() && !args[1].equalsIgnoreCase(p.currentJail)) {
			sender.sendMessage("§cPerson is already in jail "+ p.currentJail);
			return;
		}
		
		if (args.length == 1 || (args.length == 2 && args[1].equalsIgnoreCase(p.currentJail))) {
			if (!p.isJailed()) {
				sender.sendMessage("Usage: /" + commandLabel + " [player] [jailname]");
				return;
			}
			sender.sendMessage("§7Player " + p.getName() + " " + (p.toggleJailed() ? "jailed." : "unjailed."));
			p.sendMessage("§7You have been released");
			p.currentJail = "";
			p.teleportBack();
		}
	}
}
